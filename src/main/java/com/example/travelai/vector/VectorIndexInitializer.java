package com.example.travelai.vector;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class VectorIndexInitializer {

    private static final Logger logger = LoggerFactory.getLogger(VectorIndexInitializer.class);

    private final MongoTemplate mongoTemplate;
    private final boolean autoCreate;
    private final String indexName;
    private final int dimensions;

    public VectorIndexInitializer(MongoTemplate mongoTemplate,
                                  @Value("${mongodb.vector-search.auto-create-index:false}") boolean autoCreate,
                                  @Value("${mongodb.vector-search.index-name:incident_memory_vector_index}") String indexName,
                                  @Value("${voyage.api.dimensions:1024}") int dimensions) {
        this.mongoTemplate = mongoTemplate;
        this.autoCreate = autoCreate;
        this.indexName = indexName;
        this.dimensions = dimensions;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createVectorIndexIfRequested() {
        if (!autoCreate) {
            return;
        }

        try {
            Document command = new Document("createSearchIndexes", "incident_memory")
                    .append("indexes", List.of(new Document("name", indexName)
                            .append("type", "vectorSearch")
                            .append("definition", new Document("fields", List.of(
                                    new Document("type", "vector")
                                            .append("path", "embedding")
                                            .append("numDimensions", dimensions)
                                            .append("similarity", "cosine")
                            )))));
            mongoTemplate.getDb().runCommand(command);
            logger.info("Requested creation of Atlas Vector Search index {}", indexName);
        } catch (Exception exception) {
            logger.warn("Vector Search index creation skipped: {}", exception.getMessage());
        }
    }
}
