package com.example.travelai.agent;

import java.util.List;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.IncidentMemory;
import com.example.travelai.model.TravelEvent;
import com.example.travelai.model.TripState;

public interface BookingAgent {

    List<AlternativeRoute> generateOptions(TripState tripState, TravelEvent event, List<IncidentMemory> memories);
}
