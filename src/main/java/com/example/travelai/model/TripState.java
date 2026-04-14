package com.example.travelai.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "trip_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripState {

    @Id
    private String id;

    private String userId;

    @Builder.Default
    private List<ItinerarySegment> itinerary = new ArrayList<>();

    private TripStatus status;
    private Instant lastUpdated;

    @Builder.Default
    private Map<String, Object> preferences = new HashMap<>();
}
