package com.example.travelai.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.travelai.event.EventSeverity;
import com.example.travelai.event.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelEvent {

    @Id
    private String id;

    private String tripId;
    private String userId;
    private EventType type;
    private EventSeverity severity;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    private Instant timestamp;
}
