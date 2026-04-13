package com.example.travelai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.travelai.model.AgentDecision;

public interface AgentDecisionRepository extends MongoRepository<AgentDecision, String> {

    List<AgentDecision> findAllByOrderByTimestampDesc();

    List<AgentDecision> findByTripIdOrderByTimestampDesc(String tripId);
}
