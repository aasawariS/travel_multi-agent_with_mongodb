package com.example.travelai.agent;

import java.util.List;

import com.example.travelai.model.AlternativeRoute;
import com.example.travelai.model.TripState;

public interface BudgetAgent {

    List<AlternativeRoute> filterOptions(TripState tripState, List<AlternativeRoute> options);
}
