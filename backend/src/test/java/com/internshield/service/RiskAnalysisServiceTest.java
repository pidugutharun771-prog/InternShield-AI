package com.internshield.service;

import com.internshield.dto.AnalyzeRequest;
import com.internshield.dto.AnalysisResponse;
import com.internshield.model.RiskLevel;
import com.internshield.repository.AnalysisRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAnalysisServiceTest {

    @Mock
    private AnalysisRepository repository;

    @InjectMocks
    private RiskAnalysisService service;

    @Test
    void registrationFeeAndNoInterviewShouldBeHighRisk() {

        AnalyzeRequest request = new AnalyzeRequest(
            "Congratulations! You are selected for a remote internship. " +
            "Pay a registration fee of Rs 1500 to confirm your offer. " +
            "No interview required.",
            "hr@company-example.com"
        );

        AnalysisResponse response = service.analyze(request);

        assertEquals(45, response.score());
        assertEquals(RiskLevel.HIGH, response.riskLevel());

        assertTrue(response.indicators().stream()
            .anyMatch(i -> i.contains("+25")));

        assertTrue(response.indicators().stream()
            .anyMatch(i -> i.contains("+20")));

        verify(repository).save(any());
    }

    @Test
    void explicitlyDeniedRegistrationFeeShouldNotAddFeePoints() {

        AnalyzeRequest request = new AnalyzeRequest(
            "Our company offers a software engineering internship. " +
            "No registration fee is required. Candidates can review " +
            "the job responsibilities and selection process on our " +
            "official careers website.",
            "recruitment@sampletech.com"
        );

        AnalysisResponse response = service.analyze(request);

        assertFalse(response.indicators().stream()
            .anyMatch(i -> i.toLowerCase().contains("fee")));

        assertEquals(0, response.score());
        assertEquals(RiskLevel.LOW, response.riskLevel());
    }

    @Test
    void publicEmailProviderShouldAddTenPoints() {

        AnalyzeRequest request = new AnalyzeRequest(
            "This software engineering internship has a clear " +
            "description of responsibilities and a documented " +
            "selection process. Please review the complete role " +
            "details before applying.",
            "recruiter@gmail.com"
        );

        AnalysisResponse response = service.analyze(request);

        assertEquals(10, response.score());
        assertTrue(response.freeEmail());
        assertEquals(RiskLevel.LOW, response.riskLevel());
    }

    @Test
    void shortenedUrlShouldBeFlagged() {

        AnalyzeRequest request = new AnalyzeRequest(
            "Review the full details for this software engineering " +
            "internship and contact the recruitment team before " +
            "submitting your application. More details: " +
            "https://bit.ly/demo123",
            "hr@sampletech.com"
        );

        AnalysisResponse response = service.analyze(request);

        assertEquals(12, response.score());

        assertTrue(response.indicators().stream()
            .anyMatch(i -> i.toLowerCase().contains("shortener")));
    }

    @Test
    void unrelatedLinkDomainShouldProduceAdvisory() {

        AnalyzeRequest request = new AnalyzeRequest(
            "Please review the Software Engineering Internship " +
            "responsibilities and selection process on the recruitment " +
            "page before applying. More details are available at " +
            "https://careers.othercompany.com/role",
            "hr@sampletech.com"
        );

        AnalysisResponse response = service.analyze(request);

        // A domain mismatch should not automatically add risk points.
        assertEquals(0, response.score());

        // The mismatch should appear in the explanatory indicators.
        assertTrue(response.indicators().stream()
            .anyMatch(i -> i.toLowerCase()
                .contains("differs from recruiter email domain")));
    }
}