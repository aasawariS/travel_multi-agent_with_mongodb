package com.example.travelai.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.travelai.model.IncidentMemory;
import com.example.travelai.repository.IncidentMemoryRepository;
import com.example.travelai.vector.EmbeddingService;

@Configuration
public class SeedDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(SeedDataLoader.class);

    @Bean
    ApplicationRunner seedIncidentMemory(IncidentMemoryRepository incidentMemoryRepository,
                                         EmbeddingService embeddingService) {
        return args -> {
            if (incidentMemoryRepository.count() > 0) {
                return;
            }

            List<IncidentMemory> seedData = List.of(
                    createMemory("Flight delay in NYC caused missed connection", "Rebook via Chicago", 0.92, embeddingService),
                    createMemory("Snowstorm in Boston disrupted westbound routes", "Shift to Atlanta connection and push departure by 2 hours", 0.84, embeddingService),
                    createMemory("Late inbound aircraft in San Francisco cascaded delays", "Protect traveler on direct evening flight with same carrier", 0.79, embeddingService)
            );
            incidentMemoryRepository.saveAll(seedData);
            logger.info("Seeded {} incident_memory documents", seedData.size());
        };
    }

    private IncidentMemory createMemory(String description,
                                        String resolution,
                                        double successScore,
                                        EmbeddingService embeddingService) {
        return IncidentMemory.builder()
                .description(description)
                .resolution(resolution)
                .successScore(successScore)
                .embedding(embeddingService.generateEmbedding(description))
                .build();
    }
}
