package com.example.travelai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelai.agent.MonitoringAgent;
import com.example.travelai.event.EventSeverity;
import com.example.travelai.model.SimulateDelayRequest;
import com.example.travelai.model.TravelEvent;

@RestController
@RequestMapping("/event")
public class EventController {

    private final MonitoringAgent monitoringAgent;

    public EventController(MonitoringAgent monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    @PostMapping("/simulate-delay")
    public TravelEvent simulateDelay(@RequestBody SimulateDelayRequest request) {
        SimulateDelayRequest normalized = request == null
                ? new SimulateDelayRequest(null, null, 180, EventSeverity.HIGH)
                : request;
        return monitoringAgent.simulateDelay(normalized);
    }
}
