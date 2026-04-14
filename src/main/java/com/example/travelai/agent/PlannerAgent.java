package com.example.travelai.agent;

import com.example.travelai.model.TripState;

public interface PlannerAgent {

    TripState replan(String tripId);
}
