package com.internshield.dto;

public record AnalyticsResponse(
        long totalScans,
        long lowRisk,
        long mediumRisk,
        long highRisk,
        long criticalRisk
) {
}