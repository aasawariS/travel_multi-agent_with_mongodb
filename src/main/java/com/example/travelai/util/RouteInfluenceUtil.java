package com.example.travelai.util;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.travelai.model.IncidentMemory;

@Component
public class RouteInfluenceUtil {

    public Optional<String> preferredHubFromMemories(List<IncidentMemory> memories) {
        return memories.stream()
                .map(IncidentMemory::getResolution)
                .filter(resolution -> resolution != null && resolution.toLowerCase(Locale.ROOT).contains("via "))
                .map(this::extractHub)
                .filter(hub -> !hub.isBlank())
                .findFirst();
    }

    private String extractHub(String resolution) {
        String[] parts = resolution.split("via ", 2);
        if (parts.length < 2) {
            return "";
        }
        String tail = parts[1].trim();
        int end = tail.indexOf(' ');
        return end > 0 ? tail.substring(0, end) : tail;
    }
}
