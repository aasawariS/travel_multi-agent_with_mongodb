package com.example.travelai.agent;

import java.util.List;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.TripState;

public interface PreferenceAgent {

    List<AlternativeRoute> applyPreferences(TripState tripState, List<AlternativeRoute> options);
}
