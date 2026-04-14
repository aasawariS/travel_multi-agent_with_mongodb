package com.example.travelai.vector;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.travelai.util.DeterministicEmbeddingUtil;

@Service
public class VoyageEmbeddingService implements EmbeddingService {

    private final WebClient voyageWebClient;
    private final DeterministicEmbeddingUtil fallbackEmbeddingUtil;
    private final String apiKey;
    private final String model;
    private final int dimensions;

    public VoyageEmbeddingService(WebClient voyageWebClient,
                                  DeterministicEmbeddingUtil fallbackEmbeddingUtil,
                                  @Value("${voyage.api.key:}") String apiKey,
                                  @Value("${voyage.api.model:voyage-3-large}") String model,
                                  @Value("${voyage.api.dimensions:1024}") int dimensions) {
        this.voyageWebClient = voyageWebClient;
        this.fallbackEmbeddingUtil = fallbackEmbeddingUtil;
        this.apiKey = apiKey;
        this.model = model;
        this.dimensions = dimensions;
    }

    @Override
    public List<Double> generateEmbedding(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackEmbeddingUtil.generate(text, dimensions);
        }

        try {
            VoyageResponse response = voyageWebClient.post()
                    .uri("/v1/embeddings")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new VoyageRequest(List.of(text), model, "document", dimensions))
                    .retrieve()
                    .bodyToMono(VoyageResponse.class)
                    .block(Duration.ofSeconds(20));

            if (response == null || response.data() == null || response.data().isEmpty()) {
                return fallbackEmbeddingUtil.generate(text, dimensions);
            }
            return response.data().getFirst().embedding();
        } catch (Exception ignored) {
            return fallbackEmbeddingUtil.generate(text, dimensions);
        }
    }

    private record VoyageRequest(
            List<String> input,
            String model,
            @JsonProperty("input_type") String inputType,
            @JsonProperty("output_dimension") Integer outputDimension
    ) {
    }

    private record VoyageResponse(List<VoyageData> data) {
    }

    private record VoyageData(List<Double> embedding) {
    }
}
