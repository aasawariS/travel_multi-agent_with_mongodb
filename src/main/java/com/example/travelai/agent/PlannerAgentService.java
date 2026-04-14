package com.example.travelai.agent;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.IncidentMemory;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;
import com.example.travelai.model.TripStatus;
import com.example.travelai.repository.TravelEventRepository;
import com.example.travelai.service.DecisionService;
import com.example.travelai.service.TripService;

@Service
public class PlannerAgentService implements PlannerAgent {

    private static final Logger logger = LoggerFactory.getLogger(PlannerAgentService.class);

    private final TripService tripService;
    private final TravelEventRepository travelEventRepository;
    private final MemoryAgent memoryAgent;
    private final BookingAgent bookingAgent;
    private final BudgetAgent budgetAgent;
    private final PreferenceAgent preferenceAgent;
    private final DecisionService decisionService;
    private final Clock clock;

    public PlannerAgentService(TripService tripService,
                               TravelEventRepository travelEventRepository,
                               MemoryAgent memoryAgent,
                               BookingAgent bookingAgent,
                               BudgetAgent budgetAgent,
                               PreferenceAgent preferenceAgent,
                               DecisionService decisionService,
                               Clock clock) {
        this.tripService = tripService;
        this.travelEventRepository = travelEventRepository;
        this.memoryAgent = memoryAgent;
        this.bookingAgent = bookingAgent;
        this.budgetAgent = budgetAgent;
        this.preferenceAgent = preferenceAgent;
        this.decisionService = decisionService;
        this.clock = clock;
    }

    @Override
    public TripState replan(String tripId) {
        TripState tripState = tripService.getTrip(tripId);
        TravelEvent latestEvent = travelEventRepository.findFirstByTripIdOrderByTimestampDesc(tripId)
                .orElseThrow(() -> new IllegalStateException("No disruption event found for trip " + tripId));

        logger.info("PlannerAgent starting replanning for trip {}", tripId);
        List<IncidentMemory> memories = memoryAgent.retrieveSimilarIncidents(tripState, latestEvent);
        List<AlternativeRoute> options = bookingAgent.generateOptions(tripState, latestEvent, memories);
        List<AlternativeRoute> budgeted = budgetAgent.filterOptions(tripState, options);
        List<AlternativeRoute> refined = preferenceAgent.applyPreferences(tripState, budgeted);

        AlternativeRoute best = refined.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No replanning options were produced"));

        tripState.setItinerary(best.getSegments());
        tripState.setStatus(TripStatus.REPLANNED);
        tripState.setLastUpdated(Instant.now(clock));
        TripState saved = tripService.saveTrip(tripState);

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("eventType", latestEvent.getType());
        context.put("matchedMemories", memories.stream().map(IncidentMemory::getResolution).toList());
        context.put("selectedRoute", best.getSummary());
        context.put("score", best.getScore());

        String reasoning = "Selected " + best.getSummary()
                + " because it best balanced budget, preferences, and prior incident memory. "
                + (memories.isEmpty() ? "No past memory influenced the route." :
                "Top memory suggested: " + memories.getFirst().getResolution());

        decisionService.record(
                saved.getId(),
                "PlannerAgent",
                context,
                "Committed replanned itinerary",
                reasoning,
                0.93
        );

        logger.info("PlannerAgent selected route {} for trip {}", best.getSummary(), saved.getId());
        return saved;
    }
}
