package com.example.travelai.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.travelai.model.AgentDecision;
import com.example.travelai.repository.AgentDecisionRepository;

@Service
public class DecisionService {

    private final AgentDecisionRepository agentDecisionRepository;
    private final Clock clock;

    public DecisionService(AgentDecisionRepository agentDecisionRepository, Clock clock) {
        this.agentDecisionRepository = agentDecisionRepository;
        this.clock = clock;
    }

    public AgentDecision record(String tripId,
                                String agentName,
                                Map<String, Object> context,
                                String decision,
                                String reasoning,
                                Double confidenceScore) {
        AgentDecision agentDecision = AgentDecision.builder()
                .tripId(tripId)
                .agentName(agentName)
                .context(context)
                .decision(decision)
                .reasoning(reasoning)
                .confidenceScore(confidenceScore)
                .timestamp(Instant.now(clock))
                .build();
        return agentDecisionRepository.save(agentDecision);
    }

    public List<AgentDecision> allDecisions() {
        return agentDecisionRepository.findAllByOrderByTimestampDesc();
    }
}
