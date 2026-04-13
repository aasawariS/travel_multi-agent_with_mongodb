package com.example.travelai.agent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.TripState;
import com.example.travelai.service.DecisionService;

@Service
public class PreferenceAgentService implements PreferenceAgent {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceAgentService.class);

    private final DecisionService decisionService;

    public PreferenceAgentService(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @Override
    public List<AlternativeRoute> applyPreferences(TripState tripState, List<AlternativeRoute> options) {
        boolean avoidRedEye = Boolean.TRUE.equals(tripState.getPreferences().get("avoidRedEye"));
        String airlinePreference = String.valueOf(tripState.getPreferences().getOrDefault("airlinePreference", "SkyJet"));

        List<AlternativeRoute> ranked = options.stream()
                .peek(option -> {
                    if (avoidRedEye && !option.isRedEye()) {
                        option.setScore(option.getScore() + 20);
                    }
                    if (option.isPreferredCarrier() && "SkyJet".equalsIgnoreCase(airlinePreference)) {
                        option.setScore(option.getScore() + 15);
                    }
                })
                .sorted(Comparator.comparingDouble(AlternativeRoute::getScore).reversed())
                .toList();

        logger.info("PreferenceAgent ranked {} options for trip {}", ranked.size(), tripState.getId());
        decisionService.record(
                tripState.getId(),
                "PreferenceAgent",
                Map.of("avoidRedEye", avoidRedEye, "airlinePreference", airlinePreference),
                "Applied traveler preferences",
                "Raised scores for options that match airline preference and avoid red-eye travel",
                0.9
        );
        return ranked;
    }
}
