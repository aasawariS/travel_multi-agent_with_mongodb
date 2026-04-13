package com.example.travelai.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "incident_memory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentMemory {

    @Id
    private String id;

    private String description;

    @Builder.Default
    private List<Double> embedding = new ArrayList<>();

    private String resolution;
    private double successScore;
}
