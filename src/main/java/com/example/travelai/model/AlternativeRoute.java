package com.example.travelai.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlternativeRoute {

    private String routeId;
    private String summary;

    @Builder.Default
    private List<ItinerarySegment> segments = new ArrayList<>();

    private double totalCost;
    private boolean redEye;
    private boolean preferredCarrier;
    private double score;
}
