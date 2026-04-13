package com.example.travelai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.travelai.agent.MonitoringAgent;
import com.example.travelai.agent.PlannerAgent;
import com.example.travelai.event.EventSeverity;
import com.example.travelai.model.CreateTripRequest;
import com.example.travelai.model.ReplanRequest;
import com.example.travelai.model.SimulateDelayRequest;
import com.example.travelai.model.TripState;

@Configuration
public class DemoRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoRunner.class);

    @Bean
    ApplicationRunner demoTravelScenario(@Value("${travel.demo.enabled:false}") boolean enabled,
                                         TripService tripService,
                                         MonitoringAgent monitoringAgent,
                                         PlannerAgent plannerAgent) {
        return args -> {
            if (!enabled) {
                return;
            }

            TripState tripState = tripService.createTrip(new CreateTripRequest("demo-user", null, null));
            logger.info("Demo created trip {}", tripState.getId());

            monitoringAgent.simulateDelay(new SimulateDelayRequest(tripState.getId(), null, 180, EventSeverity.HIGH));
            TripState replanned = plannerAgent.replan(tripState.getId());

            logger.info("Demo replanned itinerary for trip {} with status {}", replanned.getId(), replanned.getStatus());
        };
    }
}
