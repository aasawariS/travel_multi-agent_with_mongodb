package com.example.travelai.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "agent_decisions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDecision {

    @Id
    private String id;

    private String tripId;
    private String agentName;

    @Builder.Default
    private Map<String, Object> context = new HashMap<>();

    private String decision;
    private String reasoning;
    private Double confidenceScore;
    private Instant timestamp;
}
