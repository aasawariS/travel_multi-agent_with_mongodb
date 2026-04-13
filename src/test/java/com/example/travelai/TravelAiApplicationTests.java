package com.example.travelai;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.example.travelai.model.CreateTripRequest;
import com.example.travelai.service.TripService;
import com.example.travelai.util.DeterministicEmbeddingUtil;

class TravelAiApplicationTests {

    @Test
    void deterministicEmbeddingHasExpectedDimensions() {
        DeterministicEmbeddingUtil util = new DeterministicEmbeddingUtil();
        assertThat(util.generate("flight delay in new york", 16)).hasSize(16);
    }

    @Test
    void createTripRequestAllowsNulls() {
        CreateTripRequest request = new CreateTripRequest("user-1", null, null);
        assertThat(request.userId()).isEqualTo("user-1");
    }
}
