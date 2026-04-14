package com.hrms.db.repositories.attrition;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AttritionDTOs — all data transfer objects for the Attrition Analysis subsystem.
 *
 * These are plain Java classes (no Hibernate annotations) — they are the
 * "contract objects" passed across the interface boundary. The implementation
 * maps from Hibernate entities to these objects internally.
 *
 * DESIGN: All in one file since they are small helper classes tightly
 * coupled to the attrition domain. Each could be its own file — but
 * grouping them here prevents the package from becoming 15 tiny files.
 */
public final class AttritionDTOs {
    private AttritionDTOs() {} // utility class, no instantiation
}

// ── Core Employee View (Attrition subset) ──────────────────────────────────

class AttritionEmployee {
    public String  employeeId;
    public String  name;
    public String  department;
    public String  role;
    public String  employmentStatus;   // "ACTIVE", "RESIGNED", "TERMINATED"
    public double  tenureYears;
    public double  performanceScore;
    public double  attendanceRate;
    public int     monthsSincePromotion;
    public LocalDate dateOfJoining;
}

// ── Filter Specs ────────────────────────────────────────────────────────────

class EmployeeFilter {
    public String  department;
    public String  role;
    public String  employmentStatus;
    public LocalDate joinedAfter;
    public LocalDate joinedBefore;
}

class FilterSpec {
    public String department;
    public String ageGroup;   // e.g. "25-30"
    public String role;
    public String tenureBand; // e.g. "0-2 years"
}

class DashboardFilter {
    public LocalDate from;
    public LocalDate to;
    public String    department; // null = all departments
}

// ── Attrition Rate ──────────────────────────────────────────────────────────

enum PeriodType { MONTHLY, QUARTERLY, ANNUAL }

class AttritionRecord {
    public String  periodLabel;   // e.g. "2025-Q1" or "2025-03"
    public LocalDate startDate;
    public LocalDate endDate;
    public int     totalEmployeesAtStart;
    public int     separationsInPeriod;
    public double  attritionRatePercent; // = (separations / start) * 100
}

// ── Segment Comparison ──────────────────────────────────────────────────────

class SegmentComparison {
    public FilterSpec  segmentA;
    public FilterSpec  segmentB;
    public double      attritionRateA;
    public double      attritionRateB;
    public double      difference;         // rateA - rateB
}

// ── Risk Classification ─────────────────────────────────────────────────────

enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

class RiskAssessment {
    public String    employeeId;
    public String    employeeName;
    public RiskLevel riskLevel;
    public double    riskScore;           // 0.0 – 1.0
    public List<String> flagReasons;      // e.g. ["No promotion in 24 months", "Low attendance"]
}

// ── Exit Records ────────────────────────────────────────────────────────────

class ExitRecord {
    public String    employeeId;
    public String    primaryReason;
    public String    feedbackText;
    public int       satisfactionRating;  // 1–5
    public LocalDate exitDate;
}

// ── Correlation Report ──────────────────────────────────────────────────────

class CorrelationReport {
    public LocalDate      startDate;
    public LocalDate      endDate;
    public List<CorrelationFactor> factors;
    public String         summary;
}

class CorrelationFactor {
    public String  factorName;      // e.g. "performanceScore", "monthsSincePromotion"
    public double  correlationCoef; // -1.0 to +1.0 (Pearson)
}

class RootCauseFinding {
    public String  causeName;
    public double  impactScore;     // how much this cause contributes to attrition
    public String  recommendation;
}

// ── Heat Map ────────────────────────────────────────────────────────────────

class HeatMapData {
    public List<HeatMapCell> cells;
    public String  xAxisLabel;  // e.g. "Department"
    public String  yAxisLabel;  // e.g. "Tenure Band"
}

class HeatMapCell {
    public String xValue;  // e.g. "Engineering"
    public String yValue;  // e.g. "0-2 years"
    public double value;   // e.g. attrition rate or headcount
}

// ── Cohort Analysis ─────────────────────────────────────────────────────────

class CohortAnalysisReport {
    public PeriodType  periodType;
    public List<CohortRow> cohorts;
}

class CohortRow {
    public String  cohortLabel;          // e.g. "2023 Joiners"
    public int     initialSize;
    public int     remainingAfter1Period;
    public int     remainingAfter2Periods;
    public double  retentionRatePercent;
}

// ── Risk Report ─────────────────────────────────────────────────────────────

class RiskReport {
    public RiskLevel      reportLevel;
    public List<RiskAssessment> employees;
    public int            totalFlagged;
    public double         avgRiskScore;
}

// ── Executive Dashboard ──────────────────────────────────────────────────────

class DashboardSnapshot {
    public LocalDate      generatedOn;
    public int            totalHeadcount;
    public int            separationsThisPeriod;
    public double         overallAttritionRate;
    public List<RiskAssessment>  topRiskEmployees;
    public List<AttritionRecord> monthlyTrend;
    public Map<String, Long>     exitReasonBreakdown;
    public Map<String, Double>   attritionByDepartment;
}
