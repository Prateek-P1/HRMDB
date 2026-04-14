package com.hrms.db.repositories.onboarding;

import java.util.List;

/**
 * IOnboardingRepository — unified interface for the Onboarding & Offboarding subsystem.
 *
 * ORIGINAL NOTE: The Onboarding team sent a file with 14 separate interface declarations,
 * no package declaration, and no imports. We have consolidated them into this one
 * clean interface with correct types sourced from OnboardingDTOs.
 *
 * DB Team: Implement OnboardingRepositoryImpl to satisfy this contract.
 */
public interface IOnboardingRepository {

    // ── Employee Profile ─────────────────────────────────────────────
    OnboardingEmployee getEmployeeById(String employeeID);
    List<OnboardingEmployee> getAllEmployees();
    void updateEmployeeStatus(String employeeID, String status);

    // ── Pre-Onboarding (Candidate) ───────────────────────────────────
    OnboardingCandidate getCandidateById(String candidateID);
    void updateOnboardingStatus(String candidateID, String status);

    // ── Document Management ──────────────────────────────────────────
    void uploadDocument(OnboardingDocument doc);
    List<OnboardingDocument> getDocumentsByEmployee(String employeeID);
    void updateDocumentVerificationStatus(String documentID, String status);

    // ── Policy Compliance ────────────────────────────────────────────
    List<CompliancePolicy> getAllPolicies();
    void updateComplianceStatus(String policyID, String status);

    // ── Onboarding Tasks ─────────────────────────────────────────────
    void assignTask(OnboardingTaskDTO task);
    List<OnboardingTaskDTO> getTasksByEmployee(String employeeID);
    void updateTaskStatus(String taskID, String status);

    // ── Exit Management ──────────────────────────────────────────────
    void createExitRequest(ExitRequestDTO request);
    ExitRequestDTO getExitDetails(String employeeID);
    void updateExitStatus(String employeeID, String status);

    // ── Exit Interview ───────────────────────────────────────────────
    void recordExitInterview(ExitInterviewDTO interview);
    ExitInterviewDTO getInterviewByEmployee(String employeeID);
    void updateInterviewDetails(String interviewID, String feedback, String reason);

    // ── Clearance Settlement ─────────────────────────────────────────
    void createSettlement(ClearanceDTO clearance);
    ClearanceDTO getSettlement(String employeeID);
    void updateClearanceStatus(String clearanceID, String status);

    // ── Notifications ────────────────────────────────────────────────
    void sendNotification(OnboardingNotification notification);
    List<OnboardingNotification> getNotifications(String employeeID);
    void updateNotificationStatus(String notificationID, String status);

    // ── Progress Tracking ────────────────────────────────────────────
    ProgressDTO getProgress(String employeeID, String processType);
    void updateProgressStatus(String processID, String status);
}
