package com.example.travelai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.travelai.model.TravelEvent;

public interface TravelEventRepository extends MongoRepository<TravelEvent, String> {

    List<TravelEvent> findByTripIdOrderByTimestampDesc(String tripId);

    Optional<TravelEvent> findFirstByTripIdOrderByTimestampDesc(String tripId);
}
