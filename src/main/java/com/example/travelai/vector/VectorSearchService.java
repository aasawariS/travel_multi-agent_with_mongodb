package com.example.travelai.vector;

import java.util.List;

import com.example.travelai.model.IncidentMemory;

public interface VectorSearchService {

    List<IncidentMemory> findSimilar(String query);
}
