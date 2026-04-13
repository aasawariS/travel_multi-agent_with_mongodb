package com.example.travelai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelai.model.CreateTripRequest;
import com.example.travelai.model.TripState;
import com.example.travelai.service.TripService;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/create")
    public TripState createTrip(@RequestBody(required = false) CreateTripRequest request) {
        CreateTripRequest normalized = request == null ? new CreateTripRequest("demo-user", null, null) : request;
        return tripService.createTrip(normalized);
    }

    @GetMapping("/{id}")
    public TripState getTrip(@PathVariable String id) {
        return tripService.getTrip(id);
    }
}
