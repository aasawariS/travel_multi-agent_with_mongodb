package com.example.travelai.agent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.travelai.model.IncidentMemory;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;
import com.example.travelai.service.DecisionService;
import com.example.travelai.vector.VectorSearchService;

@Service
public class MemoryAgentService implements MemoryAgent {

    private static final Logger logger = LoggerFactory.getLogger(MemoryAgentService.class);

    private final VectorSearchService vectorSearchService;
    private final DecisionService decisionService;

    public MemoryAgentService(VectorSearchService vectorSearchService, DecisionService decisionService) {
        this.vectorSearchService = vectorSearchService;
        this.decisionService = decisionService;
    }

    @Override
    public List<IncidentMemory> retrieveSimilarIncidents(TripState tripState, TravelEvent event) {
        String query = event.getType() + " from " + event.getMetadata().get("from")
                + " to " + event.getMetadata().get("to")
                + " with delay " + event.getMetadata().get("delayMinutes") + " minutes";

        List<IncidentMemory> results = vectorSearchService.findSimilar(query);
        logger.info("MemoryAgent retrieved {} similar incidents for trip {}", results.size(), tripState.getId());

        decisionService.record(
                tripState.getId(),
                "MemoryAgent",
                java.util.Map.of("query", query),
                "Retrieved similar incidents",
                results.isEmpty() ? "No similar memories found" : "Top memory: " + results.getFirst().getResolution(),
                results.isEmpty() ? 0.35 : 0.83
        );
        return results;
    }
}
