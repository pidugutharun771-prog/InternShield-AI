package com.internshield.controller;

import com.internshield.dto.UrlAnalysisRequest;
import com.internshield.dto.UrlAnalysisResponse;
import com.internshield.service.UrlAnalysisService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/url")
@CrossOrigin(origins = {"http://localhost:5173","https://internshield-frontend-ygy9.onrender.com","https://intern-shield-ai-five.vercel.app"})
public class UrlAnalysisController {

    private final UrlAnalysisService urlAnalysisService;

    public UrlAnalysisController(UrlAnalysisService urlAnalysisService) {
        this.urlAnalysisService = urlAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<UrlAnalysisResponse> analyzeUrl(
            @Valid @RequestBody UrlAnalysisRequest request) {

        return ResponseEntity.ok(
                urlAnalysisService.analyze(request)
        );
    }
}