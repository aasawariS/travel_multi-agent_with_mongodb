package com.example.travelai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.travelai.model.IncidentMemory;

public interface IncidentMemoryRepository extends MongoRepository<IncidentMemory, String> {

    List<IncidentMemory> findTop10ByOrderBySuccessScoreDesc();
}
