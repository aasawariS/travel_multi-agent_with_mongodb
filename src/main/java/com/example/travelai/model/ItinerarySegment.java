package com.example.travelai.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarySegment {

    private String segmentId;
    private String type;
    private String provider;
    private String fromLocation;
    private String toLocation;
    private Instant departureTime;
    private Instant arrivalTime;
    private String bookingReference;
    private double cost;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
}
