package com.internshield.controller;

import com.internshield.dto.AnalyticsResponse;
import com.internshield.model.RiskLevel;
import com.internshield.repository.AnalysisRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    private final AnalysisRepository analysisRepository;

    public AnalyticsController(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    @GetMapping
    public AnalyticsResponse getAnalytics() {

        long total = analysisRepository.count();

        long low = analysisRepository.countByRiskLevel(RiskLevel.LOW);
        long medium = analysisRepository.countByRiskLevel(RiskLevel.MEDIUM);
        long high = analysisRepository.countByRiskLevel(RiskLevel.HIGH);
        long critical = analysisRepository.countByRiskLevel(RiskLevel.CRITICAL);

        return new AnalyticsResponse(
                total,
                low,
                medium,
                high,
                critical
        );
    }
}