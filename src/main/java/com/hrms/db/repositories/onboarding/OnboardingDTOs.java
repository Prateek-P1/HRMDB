package com.hrms.db.repositories.onboarding;

import java.time.LocalDate;

/**
 * OnboardingDTOs — all data transfer objects for the Onboarding & Offboarding subsystem.
 *
 * These replace the undefined types (UserAccount, Asset, Training, Clearance, etc.)
 * that were referenced in the original interface file without being defined.
 */
public final class OnboardingDTOs {
    private OnboardingDTOs() {}
}

// ── Employee View (Onboarding subset) ─────────────────────────────────────

class OnboardingEmployee {
    public String   employeeID;
    public String   name;
    public String   email;
    public String   department;
    public String   designation;
    public String   employmentStatus;  // ACTIVE, ON_NOTICE, OFFBOARDED
    public LocalDate dateOfJoining;
}

// ── Candidate (Pre-Onboarding) ─────────────────────────────────────────────

class OnboardingCandidate {
    public String   candidateID;
    public String   name;
    public String   email;
    public String   phone;
    public String   onboardingStatus;  // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    public String   jobPostingId;
    public LocalDate offerDate;
    public LocalDate joiningDate;
}

// ── Document ───────────────────────────────────────────────────────────────

class OnboardingDocument {
    public String   documentID;
    public String   employeeID;
    public String   documentType;        // e.g. "PAN Card", "Offer Letter"
    public String   filePath;
    public String   verificationStatus;  // PENDING, VERIFIED, REJECTED
    public LocalDate uploadedOn;
}

// ── Compliance Policy ──────────────────────────────────────────────────────

class CompliancePolicy {
    public String   policyID;
    public String   policyName;
    public String   policyText;
    public String   complianceStatus;    // NOT_ACKNOWLEDGED, ACKNOWLEDGED
    public String   applicableToType;    // EMPLOYEE, CONTRACTOR, ALL
}

// ── Onboarding Task ────────────────────────────────────────────────────────

class OnboardingTaskDTO {
    public String   taskID;
    public String   employeeID;
    public String   taskName;
    public String   taskType;       // SYSTEM, MANUAL, DOCUMENT
    public String   assignedTo;     // HR or manager emp_id
    public String   status;         // PENDING, IN_PROGRESS, DONE
    public LocalDate dueDate;
}

// ── Exit Request ───────────────────────────────────────────────────────────

class ExitRequestDTO {
    public String   requestID;
    public String   employeeID;
    public String   exitType;       // RESIGNATION, TERMINATION, RETIREMENT
    public LocalDate noticePeriodStart;
    public LocalDate lastWorkingDay;
    public String   status;         // SUBMITTED, APPROVED, REJECTED, COMPLETED
    public String   reason;
}

// ── Exit Interview ─────────────────────────────────────────────────────────

class ExitInterviewDTO {
    public String   interviewID;
    public String   employeeID;
    public String   feedback;
    public String   primaryReason;
    public int      satisfactionRating;  // 1–5
    public LocalDate conductedOn;
}

// ── Clearance Settlement ───────────────────────────────────────────────────

class ClearanceDTO {
    public String   clearanceID;
    public String   employeeID;
    public String   clearanceStatus;    // INITIATED, IN_PROGRESS, CLEARED, BLOCKED
    public double   finalSettlementAmount;
    public LocalDate settlementDate;
    public String   remarks;
}

// ── Notification ───────────────────────────────────────────────────────────

class OnboardingNotification {
    public String   notificationID;
    public String   recipientEmployeeID;
    public String   notificationType;   // EMAIL, SMS, IN_APP, SYSTEM
    public String   subject;
    public String   body;
    public String   status;             // PENDING, SENT, READ, FAILED
    public java.time.LocalDateTime scheduledAt;
}

// ── Progress ───────────────────────────────────────────────────────────────

class ProgressDTO {
    public String   processID;
    public String   employeeID;
    public String   processType;        // ONBOARDING, OFFBOARDING
    public int      totalSteps;
    public int      completedSteps;
    public String   overallStatus;      // NOT_STARTED, IN_PROGRESS, COMPLETED
    public double   completionPercent;
}
