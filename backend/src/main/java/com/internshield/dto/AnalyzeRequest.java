package com.internshield.dto;
import jakarta.validation.constraints.NotBlank;
public record AnalyzeRequest(@NotBlank(message="Internship content is required") String content, String email) {}
