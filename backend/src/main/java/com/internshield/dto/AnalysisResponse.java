package com.internshield.dto;
import com.internshield.model.RiskLevel;
import java.time.LocalDateTime;
import java.util.List;
public record AnalysisResponse(Long id,int score,RiskLevel riskLevel,String emailDomain,boolean freeEmail,List<String> indicators,List<String> recommendations,LocalDateTime createdAt) {}
