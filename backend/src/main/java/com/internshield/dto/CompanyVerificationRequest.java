package com.internshield.dto;

import jakarta.validation.constraints.NotBlank;

public record CompanyVerificationRequest(

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Company website is required")
        String website,

        @NotBlank(message = "Recruiter email is required")
        String email
) {
}