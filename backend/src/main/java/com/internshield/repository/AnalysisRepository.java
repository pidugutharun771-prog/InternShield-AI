package com.internshield.repository;

import com.internshield.model.Analysis;
import com.internshield.model.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findTop20ByOrderByCreatedAtDesc();

    long countByRiskLevel(RiskLevel riskLevel);
}