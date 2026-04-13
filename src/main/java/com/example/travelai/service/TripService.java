package com.example.travelai.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.travelai.model.CreateTripRequest;
import com.example.travelai.model.ItinerarySegment;
import com.example.travelai.model.TripState;
import com.example.travelai.model.TripStatus;
import com.example.travelai.repository.TripStateRepository;

@Service
public class TripService {

    private final TripStateRepository tripStateRepository;
    private final Clock clock;

    public TripService(TripStateRepository tripStateRepository, Clock clock) {
        this.tripStateRepository = tripStateRepository;
        this.clock = clock;
    }

    public TripState createTrip(CreateTripRequest request) {
        List<ItinerarySegment> itinerary = request.itinerary() == null || request.itinerary().isEmpty()
                ? defaultItinerary()
                : request.itinerary();
        TripState tripState = TripState.builder()
                .userId(request.userId() == null || request.userId().isBlank() ? "demo-user" : request.userId())
                .itinerary(itinerary)
                .status(TripStatus.ON_TRACK)
                .lastUpdated(Instant.now(clock))
                .preferences(request.preferences() == null ? Map.of(
                        "airlinePreference", "SkyJet",
                        "avoidRedEye", true,
                        "maxAdditionalBudget", 250
                ) : request.preferences())
                .build();
        return tripStateRepository.save(tripState);
    }

    public TripState getTrip(String tripId) {
        return tripStateRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));
    }

    public TripState saveTrip(TripState tripState) {
        tripState.setLastUpdated(Instant.now(clock));
        return tripStateRepository.save(tripState);
    }

    private List<ItinerarySegment> defaultItinerary() {
        return List.of(
                ItinerarySegment.builder()
                        .segmentId("SEG-1")
                        .type("FLIGHT")
                        .provider("SkyJet")
                        .fromLocation("JFK")
                        .toLocation("SFO")
                        .departureTime(Instant.now(clock).plusSeconds(4 * 3600))
                        .arrivalTime(Instant.now(clock).plusSeconds(10 * 3600))
                        .bookingReference("BK-1001")
                        .cost(420.0)
                        .attributes(Map.of("cabin", "ECONOMY"))
                        .build()
        );
    }
}
