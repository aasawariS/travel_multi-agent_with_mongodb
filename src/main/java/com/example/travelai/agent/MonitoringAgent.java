package com.example.travelai.agent;

import com.example.travelai.model.SimulateDelayRequest;
import com.example.travelai.model.TravelEvent;

public interface MonitoringAgent {

    TravelEvent simulateDelay(SimulateDelayRequest request);
}
