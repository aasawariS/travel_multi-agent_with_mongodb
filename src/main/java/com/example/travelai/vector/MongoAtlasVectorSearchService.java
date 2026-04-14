package com.example.travelai.vector;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.example.travelai.model.IncidentMemory;
import com.example.travelai.repository.IncidentMemoryRepository;
import com.example.travelai.util.DeterministicEmbeddingUtil;

@Service
public class MongoAtlasVectorSearchService implements VectorSearchService {

    private final MongoTemplate mongoTemplate;
    private final IncidentMemoryRepository incidentMemoryRepository;
    private final EmbeddingService embeddingService;
    private final DeterministicEmbeddingUtil deterministicEmbeddingUtil;
    private final String indexName;
    private final int dimensions;

    public MongoAtlasVectorSearchService(MongoTemplate mongoTemplate,
                                         IncidentMemoryRepository incidentMemoryRepository,
                                         EmbeddingService embeddingService,
                                         DeterministicEmbeddingUtil deterministicEmbeddingUtil,
                                         @Value("${mongodb.vector-search.index-name:incident_memory_vector_index}") String indexName,
                                         @Value("${voyage.api.dimensions:1024}") int dimensions) {
        this.mongoTemplate = mongoTemplate;
        this.incidentMemoryRepository = incidentMemoryRepository;
        this.embeddingService = embeddingService;
        this.deterministicEmbeddingUtil = deterministicEmbeddingUtil;
        this.indexName = indexName;
        this.dimensions = dimensions;
    }

    @Override
    public List<IncidentMemory> findSimilar(String query) {
        List<Double> queryEmbedding = embeddingService.generateEmbedding(query);
        try {
            List<Document> pipeline = List.of(
                    new Document("$vectorSearch", new Document("index", indexName)
                            .append("path", "embedding")
                            .append("queryVector", queryEmbedding)
                            .append("numCandidates", 25)
                            .append("limit", 3)),
                    new Document("$addFields", new Document("vectorScore", new Document("$meta", "vectorSearchScore")))
            );
            return mongoTemplate.getCollection("incident_memory")
                    .aggregate(pipeline, IncidentMemory.class)
                    .into(new ArrayList<>());
        } catch (Exception ignored) {
            List<Double> fallback = deterministicEmbeddingUtil.generate(query, dimensions);
            return incidentMemoryRepository.findAll().stream()
                    .sorted((left, right) -> Double.compare(
                            cosineSimilarity(fallback, right.getEmbedding()),
                            cosineSimilarity(fallback, left.getEmbedding())))
                    .limit(3)
                    .toList();
        }
    }

    private double cosineSimilarity(List<Double> left, List<Double> right) {
        if (left == null || right == null || left.isEmpty() || right.isEmpty()) {
            return 0.0;
        }
        int dimensions = Math.min(left.size(), right.size());
        double dot = 0.0;
        double leftMagnitude = 0.0;
        double rightMagnitude = 0.0;
        for (int i = 0; i < dimensions; i++) {
            double lv = left.get(i);
            double rv = right.get(i);
            dot += lv * rv;
            leftMagnitude += lv * lv;
            rightMagnitude += rv * rv;
        }
        if (leftMagnitude == 0.0 || rightMagnitude == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }
}
