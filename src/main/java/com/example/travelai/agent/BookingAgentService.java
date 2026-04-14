package com.example.travelai.agent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.IncidentMemory;
import com.example.travelai.model.ItinerarySegment;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;
import com.example.travelai.service.DecisionService;
import com.example.travelai.util.RouteInfluenceUtil;

@Service
public class BookingAgentService implements BookingAgent {

    private static final Logger logger = LoggerFactory.getLogger(BookingAgentService.class);

    private final DecisionService decisionService;
    private final RouteInfluenceUtil routeInfluenceUtil;

    public BookingAgentService(DecisionService decisionService, RouteInfluenceUtil routeInfluenceUtil) {
        this.decisionService = decisionService;
        this.routeInfluenceUtil = routeInfluenceUtil;
    }

    @Override
    public List<AlternativeRoute> generateOptions(TripState tripState, TravelEvent event, List<IncidentMemory> memories) {
        String from = String.valueOf(event.getMetadata().get("from"));
        String to = String.valueOf(event.getMetadata().get("to"));
        Optional<String> preferredHub = routeInfluenceUtil.preferredHubFromMemories(memories);

        List<AlternativeRoute> options = new ArrayList<>();
        options.add(route("OPT-CHI", from, "ORD", to, 640.0, false, true));
        options.add(route("OPT-ATL", from, "ATL", to, 590.0, false, false));
        options.add(route("OPT-BOS", from, "BOS", to, 520.0, true, true));

        preferredHub.ifPresent(hub -> options.stream()
                .filter(option -> option.getSummary().contains(hub))
                .forEach(option -> option.setScore(option.getScore() + 30)));

        logger.info("BookingAgent generated {} options for trip {}", options.size(), tripState.getId());
        decisionService.record(
                tripState.getId(),
                "BookingAgent",
                Map.of("preferredHub", preferredHub.orElse("none")),
                "Generated mock flight alternatives",
                "Created three rerouting options and boosted any route aligned with prior incident memory",
                0.81
        );

        return options;
    }

    private AlternativeRoute route(String routeId, String from, String hub, String to, double cost, boolean redEye, boolean preferredCarrier) {
        Instant departure = Instant.now().plusSeconds(2 * 3600);
        List<ItinerarySegment> segments = List.of(
                ItinerarySegment.builder()
                        .segmentId(routeId + "-1")
                        .type("FLIGHT")
                        .provider(preferredCarrier ? "SkyJet" : "OpenSky")
                        .fromLocation(from)
                        .toLocation(hub)
                        .departureTime(departure)
                        .arrivalTime(departure.plusSeconds(2 * 3600))
                        .bookingReference(routeId + "-A")
                        .cost(cost / 2)
                        .attributes(Map.of("redEye", redEye))
                        .build(),
                ItinerarySegment.builder()
                        .segmentId(routeId + "-2")
                        .type("FLIGHT")
                        .provider(preferredCarrier ? "SkyJet" : "OpenSky")
                        .fromLocation(hub)
                        .toLocation(to)
                        .departureTime(departure.plusSeconds(3 * 3600))
                        .arrivalTime(departure.plusSeconds(6 * 3600))
                        .bookingReference(routeId + "-B")
                        .cost(cost / 2)
                        .attributes(Map.of("redEye", redEye))
                        .build()
        );

        return AlternativeRoute.builder()
                .routeId(routeId)
                .summary("Rebook via " + hub)
                .segments(segments)
                .totalCost(cost)
                .redEye(redEye)
                .preferredCarrier(preferredCarrier)
                .score(100 - cost / 10)
                .build();
    }
}
