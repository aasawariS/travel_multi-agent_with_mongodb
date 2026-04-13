package com.example.travelai.model;

import com.example.travelai.event.EventSeverity;

public record SimulateDelayRequest(
        String tripId,
        String segmentId,
        Integer delayMinutes,
        EventSeverity severity
) {
}
