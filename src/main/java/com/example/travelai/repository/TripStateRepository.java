package com.example.travelai.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.travelai.model.TripState;

public interface TripStateRepository extends MongoRepository<TripState, String> {
}
