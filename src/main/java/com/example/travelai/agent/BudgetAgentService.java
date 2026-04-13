package com.example.travelai.agent;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.TripState;
import com.example.travelai.service.DecisionService;

@Service
public class BudgetAgentService implements BudgetAgent {

    private static final Logger logger = LoggerFactory.getLogger(BudgetAgentService.class);

    private final DecisionService decisionService;
    private final double softCap;

    public BudgetAgentService(DecisionService decisionService,
                              @Value("${travel.budget.soft-cap:950}") double softCap) {
        this.decisionService = decisionService;
        this.softCap = softCap;
    }

    @Override
    public List<AlternativeRoute> filterOptions(TripState tripState, List<AlternativeRoute> options) {
        List<AlternativeRoute> filtered = options.stream()
                .filter(option -> option.getTotalCost() <= softCap)
                .toList();
        logger.info("BudgetAgent filtered {} options down to {}", options.size(), filtered.size());
        decisionService.record(
                tripState.getId(),
                "BudgetAgent",
                Map.of("softCap", softCap),
                "Filtered options by budget",
                "Retained routes with total cost at or below the configured budget threshold",
                0.88
        );
        return filtered.isEmpty() ? options : filtered;
    }
}
