package com.example.travelai.vector;

import java.util.List;

public interface EmbeddingService {

    List<Double> generateEmbedding(String text);
}
