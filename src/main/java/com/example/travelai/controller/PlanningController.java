package com.example.travelai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelai.agent.PlannerAgent;
import com.example.travelai.model.AgentDecision;
import com.example.travelai.model.ReplanRequest;
import com.example.travelai.model.TripState;
import com.example.travelai.service.DecisionService;

@RestController
@RequestMapping
public class PlanningController {

    private final PlannerAgent plannerAgent;
    private final DecisionService decisionService;

    public PlanningController(PlannerAgent plannerAgent, DecisionService decisionService) {
        this.plannerAgent = plannerAgent;
        this.decisionService = decisionService;
    }

    @PostMapping("/plan/replan")
    public TripState replan(@RequestBody ReplanRequest request) {
        return plannerAgent.replan(request.tripId());
    }

    @GetMapping("/decisions")
    public List<AgentDecision> decisions() {
        return decisionService.allDecisions();
    }
}
