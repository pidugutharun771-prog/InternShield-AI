package com.internshield.dto;

import java.util.List;

public record UrlAnalysisResponse(
        int score,
        String riskLevel,
        String url,
        String domain,
        List<String> indicators,
        List<String> recommendations
) {
}