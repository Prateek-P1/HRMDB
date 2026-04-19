package com.hrms.db.repositories.recruitment_management;

import com.hrms.db.repositories.recruitment_management.RecruitmentInterfaces.*;

public interface IRecruitmentRepository {
    IJobPostingRepository getJobPostingRepository();
    ICandidateRepository getCandidateRepository();
    IApplicationRepository getApplicationRepository();
    IApplicationStatusRepository getApplicationStatusRepository();
    IScreeningResultRepository getScreeningResultRepository();
    IInterviewerProfileRepository getInterviewerProfileRepository();
    IInterviewerAvailabilityRepository getInterviewerAvailabilityRepository();
    IInterviewScheduleRepository getInterviewScheduleRepository();
    IInterviewResultRepository getInterviewResultRepository();
    IOfferRepository getOfferRepository();
    IEmployeeRecordRepository getEmployeeRecordRepository();
    INotificationLogRepository getNotificationLogRepository();
}
