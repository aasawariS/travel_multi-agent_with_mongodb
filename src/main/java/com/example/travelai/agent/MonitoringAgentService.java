package com.example.travelai.agent;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.travelai.event.EventSeverity;
import com.example.travelai.event.EventType;
import com.example.travelai.model.ItinerarySegment;
import com.example.travelai.model.SimulateDelayRequest;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;
import com.example.travelai.model.TripStatus;
import com.example.travelai.repository.TravelEventRepository;
import com.example.travelai.service.DecisionService;
import com.example.travelai.service.TripService;

@Service
public class MonitoringAgentService implements MonitoringAgent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgentService.class);

    private final TripService tripService;
    private final TravelEventRepository travelEventRepository;
    private final DecisionService decisionService;
    private final Clock clock;

    public MonitoringAgentService(TripService tripService,
                                  TravelEventRepository travelEventRepository,
                                  DecisionService decisionService,
                                  Clock clock) {
        this.tripService = tripService;
        this.travelEventRepository = travelEventRepository;
        this.decisionService = decisionService;
        this.clock = clock;
    }

    @Override
    public TravelEvent simulateDelay(SimulateDelayRequest request) {
        TripState tripState = tripService.getTrip(request.tripId());
        ItinerarySegment segment = tripState.getItinerary().stream()
                .filter(item -> request.segmentId() == null || request.segmentId().equals(item.getSegmentId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Segment not found for trip " + request.tripId()));

        int delayMinutes = request.delayMinutes() == null ? 180 : request.delayMinutes();
        EventSeverity severity = request.severity() == null ? EventSeverity.HIGH : request.severity();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("segmentId", segment.getSegmentId());
        metadata.put("provider", segment.getProvider());
        metadata.put("from", segment.getFromLocation());
        metadata.put("to", segment.getToLocation());
        metadata.put("delayMinutes", delayMinutes);

        TravelEvent event = TravelEvent.builder()
                .tripId(tripState.getId())
                .userId(tripState.getUserId())
                .type(EventType.FLIGHT_DELAY)
                .severity(severity)
                .metadata(metadata)
                .timestamp(Instant.now(clock))
                .build();

        tripState.setStatus(TripStatus.DISRUPTED);
        tripService.saveTrip(tripState);
        TravelEvent saved = travelEventRepository.save(event);

        logger.info("MonitoringAgent detected event {} for trip {}", saved.getType(), saved.getTripId());
        decisionService.record(
                tripState.getId(),
                "MonitoringAgent",
                metadata,
                "Persisted disruption event",
                "Detected a delay of " + delayMinutes + " minutes on segment " + segment.getSegmentId(),
                0.96
        );
        return saved;
    }
}
