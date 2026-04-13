package com.example.travelai.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DeterministicEmbeddingUtil {

    public List<Double> generate(String text, int dimensions) {
        List<Double> embedding = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            embedding.add(0.0);
        }

        if (text == null || text.isBlank()) {
            return embedding;
        }

        char[] chars = text.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int bucket = Math.floorMod((chars[i] * 17) + i, dimensions);
            embedding.set(bucket, embedding.get(bucket) + ((chars[i] % 11) + 1));
        }

        double magnitude = Math.sqrt(embedding.stream().mapToDouble(v -> v * v).sum());
        if (magnitude == 0.0) {
            return embedding;
        }

        for (int i = 0; i < embedding.size(); i++) {
            embedding.set(i, embedding.get(i) / magnitude);
        }
        return embedding;
    }
}
