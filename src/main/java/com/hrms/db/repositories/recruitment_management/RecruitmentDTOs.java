package com.hrms.db.repositories.recruitment_management;

import java.util.Date;
import java.math.BigDecimal;
import java.sql.Time;
import com.hrms.db.repositories.recruitment_management.RecruitmentInterfaces.*;

public class RecruitmentDTOs {

    public static class JobPostingDTO implements IJobPosting {
        public String title;
        public String department;
        public String description;
        public BigDecimal salary;
        public String status;
        public String platformName;
        public String channelType;

        @Override public String getTitle() { return title; }
        @Override public String getDepartment() { return department; }
        @Override public String getDescription() { return description; }
        @Override public BigDecimal getSalary() { return salary; }
        @Override public String getStatus() { return status; }
        @Override public String getPlatformName() { return platformName; }
        @Override public String getChannelType() { return channelType; }
    }

    public static class CandidateDTO implements ICandidate {
        public String candidateId;
        public String name;
        public String contactInfo;
        public String resume;
        public String skills;
        public String source;

        @Override public String getCandidateId() { return candidateId; }
        @Override public String getName() { return name; }
        @Override public String getContactInfo() { return contactInfo; }
        @Override public String getResume() { return resume; }
        @Override public String getSkills() { return skills; }
        @Override public String getSource() { return source; }
    }

    public static class ApplicationDTO implements IApplication {
        public String applicationId;
        public String candidateId;
        public String jobId;
        public Date dateApplied;

        @Override public String getApplicationId() { return applicationId; }
        @Override public String getCandidateId() { return candidateId; }
        @Override public String getJobId() { return jobId; }
        @Override public Date getDateApplied() { return dateApplied; }
    }

    public static class ApplicationStatusDTO implements IApplicationStatus {
        public String applicationId;
        public String currentStage;
        public String history;
        public Date timestamp;

        @Override public String getApplicationId() { return applicationId; }
        @Override public String getCurrentStage() { return currentStage; }
        @Override public String getHistory() { return history; }
        @Override public Date getTimestamp() { return timestamp; }
    }

    public static class ScreeningResultDTO implements IScreeningResult {
        public String applicationId;
        public Integer score;
        public Integer ranking;
        public String shortlistStatus;

        @Override public String getApplicationId() { return applicationId; }
        @Override public Integer getScore() { return score; }
        @Override public Integer getRanking() { return ranking; }
        @Override public String getShortlistStatus() { return shortlistStatus; }
    }

    public static class InterviewerProfileDTO implements IInterviewerProfile {
        public String interviewerId;
        public String name;
        public String department;
        public String expertise;
        public String contact;

        @Override public String getInterviewerId() { return interviewerId; }
        @Override public String getName() { return name; }
        @Override public String getDepartment() { return department; }
        @Override public String getExpertise() { return expertise; }
        @Override public String getContact() { return contact; }
    }

    public static class InterviewerAvailabilityDTO implements IInterviewerAvailability {
        public String interviewerId;
        public Date availableDate;
        public Time availableTime;
        public Integer slotDuration;

        @Override public String getInterviewerId() { return interviewerId; }
        @Override public Date getAvailableDate() { return availableDate; }
        @Override public Time getAvailableTime() { return availableTime; }
        @Override public Integer getSlotDuration() { return slotDuration; }
    }

    public static class InterviewScheduleDTO implements IInterviewSchedule {
        public String scheduleId;
        public String candidateId;
        public String interviewerId;
        public Date interviewDate;
        public Time interviewTime;
        public String interviewType;

        @Override public String getScheduleId() { return scheduleId; }
        @Override public String getCandidateId() { return candidateId; }
        @Override public String getInterviewerId() { return interviewerId; }
        @Override public Date getInterviewDate() { return interviewDate; }
        @Override public Time getInterviewTime() { return interviewTime; }
        @Override public String getInterviewType() { return interviewType; }
    }

    public static class InterviewResultDTO implements IInterviewResult {
        public String scheduleId;
        public String feedback;
        public Integer score;
        public String passFailOutcome;

        @Override public String getScheduleId() { return scheduleId; }
        @Override public String getFeedback() { return feedback; }
        @Override public Integer getScore() { return score; }
        @Override public String getPassFailOutcome() { return passFailOutcome; }
    }

    public static class OfferDTO implements IOffer {
        public String offerId;
        public String candidateId;
        public String offerDetails;
        public BigDecimal salary;
        public Date startDate;
        public String status;

        @Override public String getOfferId() { return offerId; }
        @Override public String getCandidateId() { return candidateId; }
        @Override public String getOfferDetails() { return offerDetails; }
        @Override public BigDecimal getSalary() { return salary; }
        @Override public Date getStartDate() { return startDate; }
        @Override public String getStatus() { return status; }
    }

    public static class EmployeeRecordDTO implements IEmployeeRecord {
        public String employeeId;
        public String candidateId;
        public String employeeName;
        public String department;
        public String designation;
        public Date joiningDate;

        @Override public String getEmployeeId() { return employeeId; }
        @Override public String getCandidateId() { return candidateId; }
        @Override public String getEmployeeName() { return employeeName; }
        @Override public String getDepartment() { return department; }
        @Override public String getDesignation() { return designation; }
        @Override public Date getJoiningDate() { return joiningDate; }
    }

    public static class NotificationLogDTO implements INotificationLog {
        public String notificationId;
        public String notificationType;
        public String sentTo;
        public String statusAlert;
        public String contactInfoUsed;
        public Date sentTimestamp;

        @Override public String getNotificationId() { return notificationId; }
        @Override public String getNotificationType() { return notificationType; }
        @Override public String getSentTo() { return sentTo; }
        @Override public String getStatusAlert() { return statusAlert; }
        @Override public String getContactInfoUsed() { return contactInfoUsed; }
        @Override public Date getSentTimestamp() { return sentTimestamp; }
    }
}
