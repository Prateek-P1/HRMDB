package com.hrms.db.repositories.attrition;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * IAttritionRepository — unified interface for the Attrition Analysis subsystem.
 *
 * ORIGINAL NOTE: The Attrition team sent a file (IAttritionAnalysis.java) that
 * contained 10 separate public interface declarations in one file. Java does not
 * allow this. We have reorganised them here as methods in one clean interface.
 *
 * DB Team: Implement AttritionRepositoryImpl to satisfy this contract.
 */
public interface IAttritionRepository {

    // ── Employee Dataset ─────────────────────────────────────────────
    AttritionEmployee getEmployeeById(String employeeId);
    List<AttritionEmployee> getAllEmployees();
    List<AttritionEmployee> searchEmployees(EmployeeFilter filter);

    // ── Attrition Rate ───────────────────────────────────────────────
    AttritionRecord calculateAttritionRate(PeriodType periodType, LocalDate startDate, LocalDate endDate);
    List<AttritionRecord> generateTrendData(PeriodType periodType, LocalDate startDate, LocalDate endDate);

    // ── Segmented Reports ────────────────────────────────────────────
    List<AttritionEmployee> applySegmentation(FilterSpec filterSpec);
    SegmentComparison compareSegments(FilterSpec first, FilterSpec second);

    // ── Risk Classification ──────────────────────────────────────────
    RiskAssessment evaluateRisk(String employeeId);
    List<RiskAssessment> getFlaggedEmployees(RiskLevel level);

    // ── Exit Reason Database ─────────────────────────────────────────
    ExitRecord recordExit(ExitRecord exitRecord);
    java.util.Map<String, Long> getAggregatedReasons(LocalDate startDate, LocalDate endDate);

    // ── Correlation Report ───────────────────────────────────────────
    CorrelationReport runCorrelationAnalysis(LocalDate startDate, LocalDate endDate);
    List<RootCauseFinding> identifyRootCauses(LocalDate startDate, LocalDate endDate);

    // ── Heat Maps ────────────────────────────────────────────────────
    HeatMapData generateHeatMap(FilterSpec filterSpec);

    // ── Cohort Analysis ──────────────────────────────────────────────
    CohortAnalysisReport generateCohortAnalysis(PeriodType periodType);

    // ── Risk Report ──────────────────────────────────────────────────
    RiskReport generateRiskReport(RiskLevel level);

    // ── Executive Dashboard ──────────────────────────────────────────
    DashboardSnapshot buildDashboard(DashboardFilter filter);
}
