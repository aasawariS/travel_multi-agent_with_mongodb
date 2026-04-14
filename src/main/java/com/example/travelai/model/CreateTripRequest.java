package com.example.travelai.model;

import java.util.List;
import java.util.Map;

public record CreateTripRequest(
        String userId,
        List<ItinerarySegment> itinerary,
        Map<String, Object> preferences
) {
}
