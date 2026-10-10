package com.internshield.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlAnalysisRequest(
        @NotBlank(message = "URL is required")
        String url,

        String email
) {
}