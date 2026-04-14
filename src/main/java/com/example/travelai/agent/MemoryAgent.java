package com.example.travelai.agent;

import java.util.List;

import com.example.travelai.model.IncidentMemory;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;

public interface MemoryAgent {

    List<IncidentMemory> retrieveSimilarIncidents(TripState tripState, TravelEvent event);
}
