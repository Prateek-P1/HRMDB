package com.hrms.db.repositories.recruitment_management;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Time;

public class RecruitmentInterfaces {

    // --- Entity Interfaces (Model) ---

    // Module 1: Represents a Job Posting entity
    public interface IJobPosting {
        String getTitle();
        String getDepartment();
        String getDescription();
        BigDecimal getSalary();
        String getStatus();
        String getPlatformName();
        String getChannelType();
    }

    // Module 2: Represents a Candidate entity
    public interface ICandidate {
        String getCandidateId();
        String getName();
        String getContactInfo();
        String getResume();
        String getSkills();
        String getSource();
    }

    // Module 3: Represents an Application (Date Applied) entity
    public interface IApplication {
        String getApplicationId();
        String getCandidateId();
        String getJobId();
        Date getDateApplied();
    }

    // Module 4: Represents Application Status entity
    public interface IApplicationStatus {
        String getApplicationId();
        String getCurrentStage();
        String getHistory();
        Date getTimestamp();
    }

    // Module 5: Represents Screening Results entity
    public interface IScreeningResult {
        String getApplicationId();
        Integer getScore();
        Integer getRanking();
        String getShortlistStatus();
    }

    // Module 6: Represents Interviewer Profile entity
    public interface IInterviewerProfile {
        String getInterviewerId();
        String getName();
        String getDepartment();
        String getExpertise();
        String getContact();
    }

    // Module 7: Represents Interviewer Availability entity
    public interface IInterviewerAvailability {
        String getInterviewerId();
        Date getAvailableDate();
        Time getAvailableTime();
        Integer getSlotDuration();
    }

    // Module 8: Represents Interview Schedule entity
    public interface IInterviewSchedule {
        String getScheduleId();
        String getCandidateId();
        String getInterviewerId();
        Date getInterviewDate();
        Time getInterviewTime();
        String getInterviewType();
    }

    // Module 9: Represents Interview Results entity
    public interface IInterviewResult {
        String getScheduleId();
        String getFeedback();
        Integer getScore();
        String getPassFailOutcome();
    }

    // Module 10: Represents Offer entity
    public interface IOffer {
        String getOfferId();
        String getCandidateId();
        String getOfferDetails();
        BigDecimal getSalary();
        Date getStartDate();
        String getStatus();
    }

    // Module 11: Represents Employee Record entity
    public interface IEmployeeRecord {
        String getEmployeeId();
        String getCandidateId();
        String getEmployeeName();
        String getDepartment();
        String getDesignation();
        Date getJoiningDate();
    }

    // Module 12: Represents Notification Log entity
    public interface INotificationLog {
        String getNotificationId();
        String getNotificationType();
        String getSentTo();
        String getStatusAlert();
        String getContactInfoUsed();
        Date getSentTimestamp();
    }

    // --- Repository Interfaces ---

    // Module 1: Handles Job Posting database operations
    public interface IJobPostingRepository {
        void save(IJobPosting jobPosting);
        IJobPosting findByTitle(String title);
        List<IJobPosting> findAll();
        void update(IJobPosting jobPosting);
        void delete(String title);
        List<IJobPosting> findByDepartment(String department);
        List<IJobPosting> findByStatus(String status);
    }

    // Module 2: Handles Candidate database operations
    public interface ICandidateRepository {
        void save(ICandidate candidate);
        ICandidate findByCandidateId(String candidateId);
        List<ICandidate> findAll();
        void update(ICandidate candidate);
        void delete(String candidateId);
        List<ICandidate> findBySkill(String skill);
        List<ICandidate> findBySource(String source);
    }

    // Module 3: Handles Application database operations
    public interface IApplicationRepository {
        void save(IApplication application);
        IApplication findByApplicationId(String applicationId);
        List<IApplication> findByCandidateId(String candidateId);
        List<IApplication> findByJobId(String jobId);
        void update(IApplication application);
        void delete(String applicationId);
    }

    // Module 4: Handles Application Status database operations
    public interface IApplicationStatusRepository {
        void save(IApplicationStatus applicationStatus);
        IApplicationStatus findByApplicationId(String applicationId);
        List<IApplicationStatus> getStatusHistory(String applicationId);
        List<IApplicationStatus> findByCurrentStage(String currentStage);
        void update(IApplicationStatus applicationStatus);
        void delete(String applicationId);
    }

    // Module 5: Handles Screening Results database operations
    public interface IScreeningResultRepository {
        void save(IScreeningResult screeningResult);
        IScreeningResult findByApplicationId(String applicationId);
        List<IScreeningResult> getShortlistedCandidates();
        List<IScreeningResult> getTopRankedCandidates(int limit);
        void update(IScreeningResult screeningResult);
        void delete(String applicationId);
    }

    // Module 6: Handles Interviewer Profile database operations
    public interface IInterviewerProfileRepository {
        void save(IInterviewerProfile interviewerProfile);
        IInterviewerProfile findByInterviewerId(String interviewerId);
        List<IInterviewerProfile> findAll();
        List<IInterviewerProfile> findByDepartment(String department);
        List<IInterviewerProfile> findByExpertise(String expertise);
        void update(IInterviewerProfile interviewerProfile);
        void delete(String interviewerId);
    }

    // Module 7: Handles Interviewer Availability database operations
    public interface IInterviewerAvailabilityRepository {
        void save(IInterviewerAvailability availability);
        List<IInterviewerAvailability> findByInterviewerId(String interviewerId);
        List<IInterviewerAvailability> findByAvailableDate(Date date);
        List<IInterviewerAvailability> findByDateRange(Date startDate, Date endDate);
        void delete(String interviewerId, Date availableDate, Time availableTime);
        boolean isAvailable(String interviewerId, Date date, Time time);
    }

    // Module 8: Handles Interview Schedule database operations
    public interface IInterviewScheduleRepository {
        void save(IInterviewSchedule schedule);
        IInterviewSchedule findByScheduleId(String scheduleId);
        List<IInterviewSchedule> findByCandidateId(String candidateId);
        List<IInterviewSchedule> findByInterviewerId(String interviewerId);
        List<IInterviewSchedule> findByInterviewDate(Date date);
        List<IInterviewSchedule> getUpcomingInterviews();
        void update(IInterviewSchedule schedule);
        void delete(String scheduleId);
    }

    // Module 9: Handles Interview Results database operations
    public interface IInterviewResultRepository {
        void save(IInterviewResult result);
        IInterviewResult findByScheduleId(String scheduleId);
        List<IInterviewResult> findByCandidateId(String candidateId);
        List<IInterviewResult> findByInterviewerId(String interviewerId);
        List<IInterviewResult> getPassedResults();
        List<IInterviewResult> getFailedResults();
        Double getAverageScoreByInterviewer(String interviewerId);
        void update(IInterviewResult result);
        void delete(String scheduleId);
    }

    // Module 10: Handles Offer database operations
    public interface IOfferRepository {
        void save(IOffer offer);
        IOffer findByOfferId(String offerId);
        List<IOffer> findByCandidateId(String candidateId);
        List<IOffer> findByStatus(String status);
        List<IOffer> getPendingOffers();
        void update(IOffer offer);
        void delete(String offerId);
        void acceptOffer(String offerId);
        void rejectOffer(String offerId);
    }

    // Module 11: Handles Employee Record database operations
    public interface IEmployeeRecordRepository {
        void save(IEmployeeRecord employeeRecord);
        IEmployeeRecord findByEmployeeId(String employeeId);
        IEmployeeRecord findByCandidateId(String candidateId);
        void update(IEmployeeRecord employeeRecord);
        void delete(String employeeId);
    }

    // Module 12: Handles Notification Log database operations
    public interface INotificationLogRepository {
        void save(INotificationLog notificationLog);
        INotificationLog findByNotificationId(String notificationId);
        List<INotificationLog> findByDateRange(Date startDate, Date endDate);
        List<INotificationLog> getFailedNotifications();
        void updateNotificationStatus(String notificationId, String status);
        void delete(String notificationId);
    }
}
