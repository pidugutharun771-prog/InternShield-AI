package com.internshield.controller;

import com.internshield.dto.CompanyVerificationRequest;
import com.internshield.dto.CompanyVerificationResponse;
import com.internshield.service.CompanyVerificationService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = {"http://localhost:5173","https://internshield-frontend-ygy9.onrender.com","https://intern-shield-ai-five.vercel.app"})
public class CompanyVerificationController {

    private final CompanyVerificationService companyVerificationService;

    public CompanyVerificationController(
            CompanyVerificationService companyVerificationService) {

        this.companyVerificationService =
                companyVerificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<CompanyVerificationResponse> verifyCompany(
            @Valid @RequestBody CompanyVerificationRequest request) {

        return ResponseEntity.ok(
                companyVerificationService.verify(request)
        );
    }
}