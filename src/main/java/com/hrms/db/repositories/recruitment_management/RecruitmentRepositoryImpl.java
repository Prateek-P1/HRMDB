package com.hrms.db.repositories.recruitment_management;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.*;
import com.hrms.db.handlers.*;
import com.hrms.db.handlers.ErrorHandler.ErrorLevel;
import com.hrms.db.interfaces.DatabaseException;
import com.hrms.db.logging.*;
import com.hrms.db.repositories.recruitment_management.RecruitmentDTOs.*;
import com.hrms.db.repositories.recruitment_management.RecruitmentInterfaces.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class RecruitmentRepositoryImpl implements IRecruitmentRepository {

    private static final String REPO = "RecruitmentRepositoryImpl";
    private final ErrorHandler errorChain = new ConsoleErrorLogger(
            new DatabaseErrorLogger(new CriticalErrorEscalator(null)));
    private final LogHandler log = new ConsoleLogHandler(new DatabaseLogHandler(null));

    // --- Helper execution wrappers for Hibernate logic ---

    @FunctionalInterface
    private interface HibernateAction<T> {
        T execute(Session session) throws Exception;
    }

    @FunctionalInterface
    private interface HibernateVoidAction {
        void execute(Session session) throws Exception;
    }

    private <T> T executeQuery(HibernateAction<T> action, String operation) {
        log.log(LogHandler.LogLevel.INFO, REPO, operation, "Running database query.");
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return action.execute(session);
        } catch (Exception e) {
            DatabaseException dbe = new DatabaseException(operation, "Query failed", e);
            errorChain.handle(dbe, ErrorLevel.ERROR);
            throw dbe;
        }
    }

    private void executeTx(HibernateVoidAction action, String operation) {
        log.log(LogHandler.LogLevel.INFO, REPO, operation, "Running database transaction.");
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.execute(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ignored) {
                }
            }
            DatabaseException dbe = new DatabaseException(operation, "Transaction failed", e);
            errorChain.handle(dbe, ErrorLevel.CRITICAL);
            throw dbe;
        }
    }

    // --- Accessors for the 12 Sub-Repositories ---

    @Override
    public IJobPostingRepository getJobPostingRepository() {
        return jobPostingRepo;
    }

    @Override
    public ICandidateRepository getCandidateRepository() {
        return candidateRepo;
    }

    @Override
    public IApplicationRepository getApplicationRepository() {
        return applicationRepo;
    }

    @Override
    public IApplicationStatusRepository getApplicationStatusRepository() {
        return applicationStatusRepo;
    }

    @Override
    public IScreeningResultRepository getScreeningResultRepository() {
        return screeningResultRepo;
    }

    @Override
    public IInterviewerProfileRepository getInterviewerProfileRepository() {
        return interviewerProfileRepo;
    }

    @Override
    public IInterviewerAvailabilityRepository getInterviewerAvailabilityRepository() {
        return interviewerAvailabilityRepo;
    }

    @Override
    public IInterviewScheduleRepository getInterviewScheduleRepository() {
        return interviewScheduleRepo;
    }

    @Override
    public IInterviewResultRepository getInterviewResultRepository() {
        return interviewResultRepo;
    }

    @Override
    public IOfferRepository getOfferRepository() {
        return offerRepo;
    }

    @Override
    public IEmployeeRecordRepository getEmployeeRecordRepository() {
        return employeeRecordRepo;
    }

    @Override
    public INotificationLogRepository getNotificationLogRepository() {
        return notificationLogRepo;
    }

    private Date toDate(LocalDate date) {
        if (date == null)
            return null;
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date toDate(LocalDateTime date) {
        if (date == null)
            return null;
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // ==========================================================
    // 1. Job Posting implementation
    // ==========================================================
    private final IJobPostingRepository jobPostingRepo = new IJobPostingRepository() {
        @Override
        public void save(IJobPosting jp) {
            executeTx(session -> {
                JobPosting entity = new JobPosting();
                entity.setJobId(UUID.randomUUID().toString());
                entity.setTitle(jp.getTitle());
                entity.setDepartment(jp.getDepartment());
                entity.setDescription(jp.getDescription());
                entity.setSalary(jp.getSalary() != null ? jp.getSalary().doubleValue() : null);
                entity.setStatus(jp.getStatus());
                entity.setPlatformName(jp.getPlatformName());
                entity.setChannelType(jp.getChannelType());
                session.persist(entity);
            }, "JobPosting.save");
        }

        @Override
        public IJobPosting findByTitle(String title) {
            return executeQuery(session -> {
                JobPosting jp = session
                        .createQuery("FROM JobPosting WHERE title = :t AND status != 'DELETED'", JobPosting.class)
                        .setParameter("t", title).uniqueResult();
                return mapJobPosting(jp);
            }, "JobPosting.findByTitle");
        }

        @Override
        public List<IJobPosting> findAll() {
            return executeQuery(session -> session
                    .createQuery("FROM JobPosting WHERE status != 'DELETED'", JobPosting.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapJobPosting).collect(Collectors.toList()),
                    "JobPosting.findAll");
        }

        @Override
        public void update(IJobPosting jp) {
            executeTx(session -> {
                JobPosting entity = session
                        .createQuery("FROM JobPosting WHERE title = :t AND status != 'DELETED'", JobPosting.class)
                        .setParameter("t", jp.getTitle()).uniqueResult();
                if (entity != null) {
                    entity.setDepartment(jp.getDepartment());
                    entity.setDescription(jp.getDescription());
                    entity.setSalary(jp.getSalary() != null ? jp.getSalary().doubleValue() : null);
                    entity.setStatus(jp.getStatus());
                    entity.setPlatformName(jp.getPlatformName());
                    entity.setChannelType(jp.getChannelType());
                    session.merge(entity);
                }
            }, "JobPosting.update");
        }

        @Override
        public void delete(String title) {
            executeTx(session -> {
                JobPosting entity = session.createQuery("FROM JobPosting WHERE title = :t", JobPosting.class)
                        .setParameter("t", title).uniqueResult();
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "JobPosting.delete");
        }

        @Override
        public List<IJobPosting> findByDepartment(String department) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM JobPosting WHERE department = :d AND status != 'DELETED'",
                                    JobPosting.class)
                            .setParameter("d", department).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapJobPosting).collect(Collectors.toList()),
                    "JobPosting.findByDepartment");
        }

        @Override
        public List<IJobPosting> findByStatus(String status) {
            return executeQuery(session -> session.createQuery("FROM JobPosting WHERE status = :s", JobPosting.class)
                    .setParameter("s", status).getResultStream().map(RecruitmentRepositoryImpl.this::mapJobPosting)
                    .collect(Collectors.toList()), "JobPosting.findByStatus");
        }
    };

    private JobPostingDTO mapJobPosting(JobPosting entity) {
        if (entity == null)
            return null;
        JobPostingDTO dto = new JobPostingDTO();
        dto.title = entity.getTitle();
        dto.department = entity.getDepartment();
        dto.description = entity.getDescription();
        dto.salary = entity.getSalary() != null ? java.math.BigDecimal.valueOf(entity.getSalary()) : null;
        dto.status = entity.getStatus();
        dto.platformName = entity.getPlatformName();
        dto.channelType = entity.getChannelType();
        return dto;
    }

    // ==========================================================
    // 2. Candidate implementation
    // ==========================================================
    private final ICandidateRepository candidateRepo = new ICandidateRepository() {
        @Override
        public void save(ICandidate c) {
            executeTx(session -> {
                Candidate entity = new Candidate();
                entity.setCandidateId(c.getCandidateId() != null ? c.getCandidateId() : UUID.randomUUID().toString());
                entity.setName(c.getName());
                entity.setContactInfo(c.getContactInfo());
                entity.setResumePath(c.getResume());
                entity.setSkills(c.getSkills());
                entity.setSource(c.getSource());
                session.persist(entity);
            }, "Candidate.save");
        }

        @Override
        public ICandidate findByCandidateId(String id) {
            return executeQuery(session -> mapCandidate(session
                    .createQuery("FROM Candidate WHERE candidateId = :id AND status != 'DELETED'", Candidate.class)
                    .setParameter("id", id).uniqueResult()), "Candidate.findByCandidateId");
        }

        @Override
        public List<ICandidate> findAll() {
            return executeQuery(session -> session
                    .createQuery("FROM Candidate WHERE status != 'DELETED'", Candidate.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapCandidate).collect(Collectors.toList()),
                    "Candidate.findAll");
        }

        @Override
        public void update(ICandidate c) {
            executeTx(session -> {
                Candidate entity = session.get(Candidate.class, c.getCandidateId());
                if (entity != null) {
                    entity.setName(c.getName());
                    entity.setContactInfo(c.getContactInfo());
                    entity.setResumePath(c.getResume());
                    entity.setSkills(c.getSkills());
                    entity.setSource(c.getSource());
                    session.merge(entity);
                }
            }, "Candidate.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                Candidate c = session.get(Candidate.class, id);
                if (c != null) {
                    c.setStatus("DELETED");
                    session.merge(c);
                }
            }, "Candidate.delete");
        }

        @Override
        public List<ICandidate> findBySkill(String skill) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM Candidate WHERE skills LIKE :s AND status != 'DELETED'", Candidate.class)
                            .setParameter("s", "%" + skill + "%").getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapCandidate).collect(Collectors.toList()),
                    "Candidate.findBySkill");
        }

        @Override
        public List<ICandidate> findBySource(String source) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM Candidate WHERE source = :s AND status != 'DELETED'", Candidate.class)
                            .setParameter("s", source).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapCandidate).collect(Collectors.toList()),
                    "Candidate.findBySource");
        }
    };

    private CandidateDTO mapCandidate(Candidate entity) {
        if (entity == null)
            return null;
        CandidateDTO dto = new CandidateDTO();
        dto.candidateId = entity.getCandidateId();
        dto.name = entity.getName();
        dto.contactInfo = entity.getContactInfo();
        dto.resume = entity.getResumePath();
        dto.skills = entity.getSkills();
        dto.source = entity.getSource();
        return dto;
    }

    // ==========================================================
    // 3. Application implementation
    // ==========================================================
    private final IApplicationRepository applicationRepo = new IApplicationRepository() {
        @Override
        public void save(IApplication app) {
            executeTx(session -> {
                Application entity = new Application();
                entity.setApplicationId(
                        app.getApplicationId() != null ? app.getApplicationId() : UUID.randomUUID().toString());
                entity.setCandidateId(app.getCandidateId());
                entity.setJobId(app.getJobId());
                entity.setDateApplied(toLocalDate(app.getDateApplied()));
                session.persist(entity);
            }, "Application.save");
        }

        @Override
        public IApplication findByApplicationId(String id) {
            return executeQuery(session -> mapApplication(session
                    .createQuery("FROM Application WHERE applicationId = :id AND status != 'DELETED'",
                            Application.class)
                    .setParameter("id", id).uniqueResult()), "Application.findByApplicationId");
        }

        @Override
        public List<IApplication> findByCandidateId(String candidateId) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM Application WHERE candidateId = :c AND status != 'DELETED'",
                                    Application.class)
                            .setParameter("c", candidateId).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapApplication).collect(Collectors.toList()),
                    "Application.findByCandidateId");
        }

        @Override
        public List<IApplication> findByJobId(String jobId) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM Application WHERE jobId = :j AND status != 'DELETED'", Application.class)
                            .setParameter("j", jobId).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapApplication).collect(Collectors.toList()),
                    "Application.findByJobId");
        }

        @Override
        public void update(IApplication app) {
            executeTx(session -> {
                Application entity = session.get(Application.class, app.getApplicationId());
                if (entity != null) {
                    entity.setCandidateId(app.getCandidateId());
                    entity.setJobId(app.getJobId());
                    entity.setDateApplied(toLocalDate(app.getDateApplied()));
                    session.merge(entity);
                }
            }, "Application.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                Application entity = session.get(Application.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "Application.delete");
        }
    };

    private ApplicationDTO mapApplication(Application entity) {
        if (entity == null)
            return null;
        ApplicationDTO dto = new ApplicationDTO();
        dto.applicationId = entity.getApplicationId();
        dto.candidateId = entity.getCandidateId();
        dto.jobId = entity.getJobId();
        dto.dateApplied = toDate(entity.getDateApplied());
        return dto;
    }

    // ==========================================================
    // 4. Application Status implementation (using Application table)
    // ==========================================================
    private final IApplicationStatusRepository applicationStatusRepo = new IApplicationStatusRepository() {
        @Override
        public void save(IApplicationStatus as) {
            executeTx(session -> {
                Application entity = session.get(Application.class, as.getApplicationId());
                if (entity != null) {
                    entity.setCurrentStage(as.getCurrentStage());
                    entity.setHistory(as.getHistory());
                    entity.setTimestamp(toLocalDateTime(as.getTimestamp()));
                    session.merge(entity);
                }
            }, "ApplicationStatus.save");
        }

        @Override
        public IApplicationStatus findByApplicationId(String id) {
            return executeQuery(session -> mapApplicationStatus(session
                    .createQuery("FROM Application WHERE applicationId = :id AND status != 'DELETED'",
                            Application.class)
                    .setParameter("id", id).uniqueResult()), "ApplicationStatus.findByApplicationId");
        }

        @Override
        public List<IApplicationStatus> getStatusHistory(String id) {
            return executeQuery(session -> {
                Application entity = session
                        .createQuery("FROM Application WHERE applicationId = :id AND status != 'DELETED'",
                                Application.class)
                        .setParameter("id", id).uniqueResult();
                List<IApplicationStatus> result = new ArrayList<>();
                if (entity != null)
                    result.add(mapApplicationStatus(entity));
                return result; // Since history is stored as a TEXT block, we just return the full block
            }, "ApplicationStatus.getStatusHistory");
        }

        @Override
        public List<IApplicationStatus> findByCurrentStage(String stage) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM Application WHERE currentStage = :s AND status != 'DELETED'",
                                    Application.class)
                            .setParameter("s", stage).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapApplicationStatus).collect(Collectors.toList()),
                    "ApplicationStatus.findByCurrentStage");
        }

        @Override
        public void update(IApplicationStatus as) {
            save(as); // Merging is the same conceptually
        }

        @Override
        public void delete(String id) {
            // Because Status and Application are merged, we shouldn't "delete" the
            // application just to delete status.
            executeTx(session -> {
                Application entity = session.get(Application.class, id);
                if (entity != null) {
                    entity.setCurrentStage(null);
                    entity.setHistory(null);
                    session.merge(entity);
                }
            }, "ApplicationStatus.delete");
        }
    };

    private ApplicationStatusDTO mapApplicationStatus(Application entity) {
        if (entity == null)
            return null;
        ApplicationStatusDTO dto = new ApplicationStatusDTO();
        dto.applicationId = entity.getApplicationId();
        dto.currentStage = entity.getCurrentStage();
        dto.history = entity.getHistory();
        dto.timestamp = toDate(entity.getTimestamp());
        return dto;
    }

    // ==========================================================
    // 5. Screening Results implementation
    // ==========================================================
    private final IScreeningResultRepository screeningResultRepo = new IScreeningResultRepository() {
        @Override
        public void save(IScreeningResult res) {
            executeTx(session -> {
                ScreeningResult entity = new ScreeningResult();
                entity.setApplicationId(res.getApplicationId());
                entity.setScore(res.getScore());
                entity.setRanking(res.getRanking());
                entity.setShortlistStatus(res.getShortlistStatus());
                session.persist(entity);
            }, "ScreeningResult.save");
        }

        @Override
        public IScreeningResult findByApplicationId(String id) {
            return executeQuery(session -> mapScreeningResult(session
                    .createQuery("FROM ScreeningResult WHERE applicationId = :id AND status != 'DELETED'",
                            ScreeningResult.class)
                    .setParameter("id", id).uniqueResult()), "ScreeningResult.findByApplicationId");
        }

        @Override
        public List<IScreeningResult> getShortlistedCandidates() {
            return executeQuery(session -> session
                    .createQuery("FROM ScreeningResult WHERE shortlistStatus = 'SHORTLISTED' AND status != 'DELETED'",
                            ScreeningResult.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapScreeningResult)
                    .collect(Collectors.toList()), "ScreeningResult.getShortlistedCandidates");
        }

        @Override
        public List<IScreeningResult> getTopRankedCandidates(int limit) {
            return executeQuery(session -> session
                    .createQuery("FROM ScreeningResult WHERE status != 'DELETED' ORDER BY ranking ASC",
                            ScreeningResult.class)
                    .setMaxResults(limit)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapScreeningResult)
                    .collect(Collectors.toList()), "ScreeningResult.getTopRanked");
        }

        @Override
        public void update(IScreeningResult res) {
            executeTx(session -> {
                ScreeningResult entity = session.get(ScreeningResult.class, res.getApplicationId());
                if (entity != null) {
                    entity.setScore(res.getScore());
                    entity.setRanking(res.getRanking());
                    entity.setShortlistStatus(res.getShortlistStatus());
                    session.merge(entity);
                }
            }, "ScreeningResult.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                ScreeningResult entity = session.get(ScreeningResult.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "ScreeningResult.delete");
        }
    };

    private ScreeningResultDTO mapScreeningResult(ScreeningResult entity) {
        if (entity == null)
            return null;
        ScreeningResultDTO dto = new ScreeningResultDTO();
        dto.applicationId = entity.getApplicationId();
        dto.score = entity.getScore();
        dto.ranking = entity.getRanking();
        dto.shortlistStatus = entity.getShortlistStatus();
        return dto;
    }

    // ==========================================================
    // 6. Interviewer Profile implementation
    // ==========================================================
    private final IInterviewerProfileRepository interviewerProfileRepo = new IInterviewerProfileRepository() {
        @Override
        public void save(IInterviewerProfile ip) {
            executeTx(session -> {
                InterviewerProfile entity = new InterviewerProfile();
                entity.setInterviewerId(
                        ip.getInterviewerId() != null ? ip.getInterviewerId() : UUID.randomUUID().toString());
                entity.setName(ip.getName());
                entity.setDepartment(ip.getDepartment());
                entity.setExpertise(ip.getExpertise());
                entity.setContact(ip.getContact());
                session.persist(entity);
            }, "InterviewerProfile.save");
        }

        @Override
        public IInterviewerProfile findByInterviewerId(String id) {
            return executeQuery(session -> mapInterviewerProfile(session
                    .createQuery("FROM InterviewerProfile WHERE interviewerId = :id AND status != 'DELETED'",
                            InterviewerProfile.class)
                    .setParameter("id", id).uniqueResult()), "InterviewerProfile.findByInterviewerId");
        }

        @Override
        public List<IInterviewerProfile> findAll() {
            return executeQuery(session -> session
                    .createQuery("FROM InterviewerProfile WHERE status != 'DELETED'", InterviewerProfile.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapInterviewerProfile)
                    .collect(Collectors.toList()), "InterviewerProfile.findAll");
        }

        @Override
        public List<IInterviewerProfile> findByDepartment(String dept) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM InterviewerProfile WHERE department = :d AND status != 'DELETED'",
                                    InterviewerProfile.class)
                            .setParameter("d", dept).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapInterviewerProfile).collect(Collectors.toList()),
                    "InterviewerProfile.findByDepartment");
        }

        @Override
        public List<IInterviewerProfile> findByExpertise(String exp) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM InterviewerProfile WHERE expertise LIKE :e AND status != 'DELETED'",
                                    InterviewerProfile.class)
                            .setParameter("e", "%" + exp + "%").getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapInterviewerProfile).collect(Collectors.toList()),
                    "InterviewerProfile.findByExpertise");
        }

        @Override
        public void update(IInterviewerProfile ip) {
            executeTx(session -> {
                InterviewerProfile entity = session.get(InterviewerProfile.class, ip.getInterviewerId());
                if (entity != null) {
                    entity.setName(ip.getName());
                    entity.setDepartment(ip.getDepartment());
                    entity.setExpertise(ip.getExpertise());
                    entity.setContact(ip.getContact());
                    session.merge(entity);
                }
            }, "InterviewerProfile.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                InterviewerProfile entity = session.get(InterviewerProfile.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "InterviewerProfile.delete");
        }
    };

    private InterviewerProfileDTO mapInterviewerProfile(InterviewerProfile entity) {
        if (entity == null)
            return null;
        InterviewerProfileDTO dto = new InterviewerProfileDTO();
        dto.interviewerId = entity.getInterviewerId();
        dto.name = entity.getName();
        dto.department = entity.getDepartment();
        dto.expertise = entity.getExpertise();
        dto.contact = entity.getContact();
        return dto;
    }

    // ==========================================================
    // 7. Interviewer Availability implementation
    // ==========================================================
    private final IInterviewerAvailabilityRepository interviewerAvailabilityRepo = new IInterviewerAvailabilityRepository() {
        @Override
        public void save(IInterviewerAvailability ia) {
            executeTx(session -> {
                InterviewerAvailability entity = new InterviewerAvailability();
                entity.setAvailabilityId(UUID.randomUUID().toString());
                entity.setInterviewerId(ia.getInterviewerId());
                entity.setAvailableDate(toLocalDate(ia.getAvailableDate()));
                if (ia.getAvailableTime() != null)
                    entity.setAvailableTime(ia.getAvailableTime().toLocalTime());
                entity.setSlotDuration(ia.getSlotDuration());
                session.persist(entity);
            }, "InterviewerAvailability.save");
        }

        @Override
        public List<IInterviewerAvailability> findByInterviewerId(String id) {
            return executeQuery(session -> session
                    .createQuery("FROM InterviewerAvailability WHERE interviewerId = :id AND status != 'DELETED'",
                            InterviewerAvailability.class)
                    .setParameter("id", id).getResultStream()
                    .map(RecruitmentRepositoryImpl.this::mapInterviewerAvailability).collect(Collectors.toList()),
                    "InterviewerAvailability.findByInterviewerId");
        }

        @Override
        public List<IInterviewerAvailability> findByAvailableDate(Date date) {
            LocalDate ld = toLocalDate(date);
            return executeQuery(session -> session
                    .createQuery("FROM InterviewerAvailability WHERE availableDate = :d AND status != 'DELETED'",
                            InterviewerAvailability.class)
                    .setParameter("d", ld).getResultStream()
                    .map(RecruitmentRepositoryImpl.this::mapInterviewerAvailability).collect(Collectors.toList()),
                    "InterviewerAvailability.findByAvailableDate");
        }

        @Override
        public List<IInterviewerAvailability> findByDateRange(Date start, Date end) {
            LocalDate sd = toLocalDate(start);
            LocalDate ed = toLocalDate(end);
            return executeQuery(session -> session.createQuery(
                    "FROM InterviewerAvailability WHERE availableDate BETWEEN :s AND :e AND status != 'DELETED'",
                    InterviewerAvailability.class)
                    .setParameter("s", sd).setParameter("e", ed).getResultStream()
                    .map(RecruitmentRepositoryImpl.this::mapInterviewerAvailability).collect(Collectors.toList()),
                    "InterviewerAvailability.findByDateRange");
        }

        @Override
        public void delete(String id, Date date, Time time) {
            LocalDate ld = toLocalDate(date);
            LocalTime lt = time != null ? time.toLocalTime() : null;
            executeTx(session -> {
                InterviewerAvailability entity = session.createQuery(
                        "FROM InterviewerAvailability WHERE interviewerId = :id AND availableDate = :d AND availableTime = :t",
                        InterviewerAvailability.class)
                        .setParameter("id", id).setParameter("d", ld).setParameter("t", lt).uniqueResult();
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "InterviewerAvailability.delete");
        }

        @Override
        public boolean isAvailable(String id, Date date, Time time) {
            LocalDate ld = toLocalDate(date);
            LocalTime lt = time != null ? time.toLocalTime() : null;
            return executeQuery(session -> {
                Long count = session.createQuery(
                        "SELECT count(*) FROM InterviewerAvailability WHERE interviewerId = :id AND availableDate = :d AND availableTime <= :t AND status != 'DELETED'",
                        Long.class)
                        .setParameter("id", id).setParameter("d", ld).setParameter("t", lt).uniqueResult();
                return count > 0;
            }, "InterviewerAvailability.isAvailable");
        }
    };

    private InterviewerAvailabilityDTO mapInterviewerAvailability(InterviewerAvailability entity) {
        if (entity == null)
            return null;
        InterviewerAvailabilityDTO dto = new InterviewerAvailabilityDTO();
        dto.interviewerId = entity.getInterviewerId();
        dto.availableDate = toDate(entity.getAvailableDate());
        if (entity.getAvailableTime() != null)
            dto.availableTime = Time.valueOf(entity.getAvailableTime());
        dto.slotDuration = entity.getSlotDuration();
        return dto;
    }

    // ==========================================================
    // 8. Interview Schedule implementation
    // ==========================================================
    private final IInterviewScheduleRepository interviewScheduleRepo = new IInterviewScheduleRepository() {
        @Override
        public void save(IInterviewSchedule sc) {
            executeTx(session -> {
                InterviewSchedule entity = new InterviewSchedule();
                entity.setScheduleId(sc.getScheduleId() != null ? sc.getScheduleId() : UUID.randomUUID().toString());
                entity.setCandidateId(sc.getCandidateId());
                entity.setInterviewerId(sc.getInterviewerId());
                entity.setInterviewDate(toLocalDate(sc.getInterviewDate()));
                if (sc.getInterviewTime() != null)
                    entity.setInterviewTime(sc.getInterviewTime().toLocalTime());
                entity.setInterviewType(sc.getInterviewType());
                session.persist(entity);
            }, "InterviewSchedule.save");
        }

        @Override
        public IInterviewSchedule findByScheduleId(String id) {
            return executeQuery(session -> mapInterviewSchedule(session
                    .createQuery("FROM InterviewSchedule WHERE scheduleId = :id AND status != 'DELETED'",
                            InterviewSchedule.class)
                    .setParameter("id", id).uniqueResult()), "InterviewSchedule.findByScheduleId");
        }

        @Override
        public List<IInterviewSchedule> findByCandidateId(String id) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM InterviewSchedule WHERE candidateId = :id AND status != 'DELETED'",
                                    InterviewSchedule.class)
                            .setParameter("id", id).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapInterviewSchedule).collect(Collectors.toList()),
                    "InterviewSchedule.findByCandidateId");
        }

        @Override
        public List<IInterviewSchedule> findByInterviewerId(String id) {
            return executeQuery(
                    session -> session
                            .createQuery("FROM InterviewSchedule WHERE interviewerId = :id AND status != 'DELETED'",
                                    InterviewSchedule.class)
                            .setParameter("id", id).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapInterviewSchedule).collect(Collectors.toList()),
                    "InterviewSchedule.findByInterviewerId");
        }

        @Override
        public List<IInterviewSchedule> findByInterviewDate(Date date) {
            LocalDate ld = toLocalDate(date);
            return executeQuery(
                    session -> session
                            .createQuery("FROM InterviewSchedule WHERE interviewDate = :d AND status != 'DELETED'",
                                    InterviewSchedule.class)
                            .setParameter("d", ld).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapInterviewSchedule).collect(Collectors.toList()),
                    "InterviewSchedule.findByInterviewDate");
        }

        @Override
        public List<IInterviewSchedule> getUpcomingInterviews() {
            return executeQuery(session -> session.createQuery(
                    "FROM InterviewSchedule WHERE interviewDate >= :d AND status != 'DELETED' ORDER BY interviewDate ASC, interviewTime ASC",
                    InterviewSchedule.class)
                    .setParameter("d", LocalDate.now()).getResultStream()
                    .map(RecruitmentRepositoryImpl.this::mapInterviewSchedule).collect(Collectors.toList()),
                    "InterviewSchedule.getUpcoming");
        }

        @Override
        public void update(IInterviewSchedule sc) {
            executeTx(session -> {
                InterviewSchedule entity = session.get(InterviewSchedule.class, sc.getScheduleId());
                if (entity != null) {
                    entity.setCandidateId(sc.getCandidateId());
                    entity.setInterviewerId(sc.getInterviewerId());
                    entity.setInterviewDate(toLocalDate(sc.getInterviewDate()));
                    if (sc.getInterviewTime() != null)
                        entity.setInterviewTime(sc.getInterviewTime().toLocalTime());
                    entity.setInterviewType(sc.getInterviewType());
                    session.merge(entity);
                }
            }, "InterviewSchedule.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                InterviewSchedule entity = session.get(InterviewSchedule.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "InterviewSchedule.delete");
        }
    };

    private InterviewScheduleDTO mapInterviewSchedule(InterviewSchedule entity) {
        if (entity == null)
            return null;
        InterviewScheduleDTO dto = new InterviewScheduleDTO();
        dto.scheduleId = entity.getScheduleId();
        dto.candidateId = entity.getCandidateId();
        dto.interviewerId = entity.getInterviewerId();
        dto.interviewDate = toDate(entity.getInterviewDate());
        if (entity.getInterviewTime() != null)
            dto.interviewTime = Time.valueOf(entity.getInterviewTime());
        dto.interviewType = entity.getInterviewType();
        return dto;
    }

    // ==========================================================
    // 9. Interview Results implementation
    // ==========================================================
    private final IInterviewResultRepository interviewResultRepo = new IInterviewResultRepository() {
        @Override
        public void save(IInterviewResult r) {
            executeTx(session -> {
                InterviewResult entity = new InterviewResult();
                entity.setScheduleId(r.getScheduleId());
                entity.setFeedback(r.getFeedback());
                entity.setScore(r.getScore());
                entity.setPassFailOutcome(r.getPassFailOutcome());
                session.persist(entity);
            }, "InterviewResult.save");
        }

        @Override
        public IInterviewResult findByScheduleId(String id) {
            return executeQuery(session -> mapInterviewResult(session
                    .createQuery("FROM InterviewResult WHERE scheduleId = :id AND status != 'DELETED'",
                            InterviewResult.class)
                    .setParameter("id", id).uniqueResult()), "InterviewResult.findByScheduleId");
        }

        @Override
        public List<IInterviewResult> findByCandidateId(String id) {
            return executeQuery(session -> session.createQuery(
                    "FROM InterviewResult r JOIN InterviewSchedule s ON r.scheduleId = s.scheduleId WHERE s.candidateId = :id AND r.status != 'DELETED'",
                    InterviewResult.class)
                    .setParameter("id", id).getResultStream().map(RecruitmentRepositoryImpl.this::mapInterviewResult)
                    .collect(Collectors.toList()), "InterviewResult.findByCandidateId");
        }

        @Override
        public List<IInterviewResult> findByInterviewerId(String id) {
            return executeQuery(session -> session.createQuery(
                    "FROM InterviewResult r JOIN InterviewSchedule s ON r.scheduleId = s.scheduleId WHERE s.interviewerId = :id AND r.status != 'DELETED'",
                    InterviewResult.class)
                    .setParameter("id", id).getResultStream().map(RecruitmentRepositoryImpl.this::mapInterviewResult)
                    .collect(Collectors.toList()), "InterviewResult.findByInterviewerId");
        }

        @Override
        public List<IInterviewResult> getPassedResults() {
            return executeQuery(session -> session
                    .createQuery("FROM InterviewResult WHERE passFailOutcome = 'PASS' AND status != 'DELETED'",
                            InterviewResult.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapInterviewResult)
                    .collect(Collectors.toList()), "InterviewResult.getPassed");
        }

        @Override
        public List<IInterviewResult> getFailedResults() {
            return executeQuery(session -> session
                    .createQuery("FROM InterviewResult WHERE passFailOutcome = 'FAIL' AND status != 'DELETED'",
                            InterviewResult.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapInterviewResult)
                    .collect(Collectors.toList()), "InterviewResult.getFailed");
        }

        @Override
        public Double getAverageScoreByInterviewer(String id) {
            return executeQuery(session -> session.createQuery(
                    "SELECT avg(r.score) FROM InterviewResult r JOIN InterviewSchedule s ON r.scheduleId = s.scheduleId WHERE s.interviewerId = :id AND r.status != 'DELETED'",
                    Double.class)
                    .setParameter("id", id).uniqueResult(), "InterviewResult.getAverageByInterviewer");
        }

        @Override
        public void update(IInterviewResult r) {
            executeTx(session -> {
                InterviewResult entity = session.get(InterviewResult.class, r.getScheduleId());
                if (entity != null) {
                    entity.setFeedback(r.getFeedback());
                    entity.setScore(r.getScore());
                    entity.setPassFailOutcome(r.getPassFailOutcome());
                    session.merge(entity);
                }
            }, "InterviewResult.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                InterviewResult entity = session.get(InterviewResult.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "InterviewResult.delete");
        }
    };

    private InterviewResultDTO mapInterviewResult(InterviewResult entity) {
        if (entity == null)
            return null;
        InterviewResultDTO dto = new InterviewResultDTO();
        dto.scheduleId = entity.getScheduleId();
        dto.feedback = entity.getFeedback();
        dto.score = entity.getScore();
        dto.passFailOutcome = entity.getPassFailOutcome();
        return dto;
    }

    // ==========================================================
    // 10. Offer implementation
    // ==========================================================
    private final IOfferRepository offerRepo = new IOfferRepository() {
        @Override
        public void save(IOffer o) {
            executeTx(session -> {
                Offer entity = new Offer();
                entity.setOfferId(o.getOfferId() != null ? o.getOfferId() : UUID.randomUUID().toString());
                entity.setCandidateId(o.getCandidateId());
                entity.setOfferDetails(o.getOfferDetails());
                entity.setSalary(o.getSalary() != null ? o.getSalary().doubleValue() : null);
                entity.setStartDate(toLocalDate(o.getStartDate()));
                entity.setStatus(o.getStatus() != null ? o.getStatus() : "EXTENDED");
                session.persist(entity);
            }, "Offer.save");
        }

        @Override
        public IOffer findByOfferId(String id) {
            return executeQuery(session -> mapOffer(
                    session.createQuery("FROM Offer WHERE offerId = :id AND status != 'DELETED'", Offer.class)
                            .setParameter("id", id).uniqueResult()),
                    "Offer.findByOfferId");
        }

        @Override
        public List<IOffer> findByCandidateId(String id) {
            return executeQuery(session -> session
                    .createQuery("FROM Offer WHERE candidateId = :id AND status != 'DELETED'", Offer.class)
                    .setParameter("id", id).getResultStream().map(RecruitmentRepositoryImpl.this::mapOffer)
                    .collect(Collectors.toList()), "Offer.findByCandidateId");
        }

        @Override
        public List<IOffer> findByStatus(String status) {
            return executeQuery(session -> session.createQuery("FROM Offer WHERE status = :s", Offer.class)
                    .setParameter("s", status).getResultStream().map(RecruitmentRepositoryImpl.this::mapOffer)
                    .collect(Collectors.toList()), "Offer.findByStatus");
        }

        @Override
        public List<IOffer> getPendingOffers() {
            return executeQuery(session -> session.createQuery("FROM Offer WHERE status = 'EXTENDED'", Offer.class)
                    .getResultStream().map(RecruitmentRepositoryImpl.this::mapOffer).collect(Collectors.toList()),
                    "Offer.getPendingOffers");
        }

        @Override
        public void update(IOffer o) {
            executeTx(session -> {
                Offer entity = session.get(Offer.class, o.getOfferId());
                if (entity != null) {
                    entity.setCandidateId(o.getCandidateId());
                    entity.setOfferDetails(o.getOfferDetails());
                    entity.setSalary(o.getSalary() != null ? o.getSalary().doubleValue() : null);
                    entity.setStartDate(toLocalDate(o.getStartDate()));
                    entity.setStatus(o.getStatus());
                    session.merge(entity);
                }
            }, "Offer.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                Offer entity = session.get(Offer.class, id);
                if (entity != null) {
                    entity.setStatus("DELETED");
                    session.merge(entity);
                }
            }, "Offer.delete");
        }

        @Override
        public void acceptOffer(String id) {
            executeTx(session -> {
                Offer entity = session.get(Offer.class, id);
                if (entity != null) {
                    entity.setStatus("ACCEPTED");
                    session.merge(entity);
                }
            }, "Offer.accept");
        }

        @Override
        public void rejectOffer(String id) {
            executeTx(session -> {
                Offer entity = session.get(Offer.class, id);
                if (entity != null) {
                    entity.setStatus("REJECTED");
                    session.merge(entity);
                }
            }, "Offer.reject");
        }
    };

    private OfferDTO mapOffer(Offer entity) {
        if (entity == null)
            return null;
        OfferDTO dto = new OfferDTO();
        dto.offerId = entity.getOfferId();
        dto.candidateId = entity.getCandidateId();
        dto.offerDetails = entity.getOfferDetails();
        dto.salary = entity.getSalary() != null ? java.math.BigDecimal.valueOf(entity.getSalary()) : null;
        dto.startDate = toDate(entity.getStartDate());
        dto.status = entity.getStatus();
        return dto;
    }

    // ==========================================================
    // 11. Employee Record implementation (Maps to Employee entity)
    // ==========================================================
    private final IEmployeeRecordRepository employeeRecordRepo = new IEmployeeRecordRepository() {
        @Override
        public void save(IEmployeeRecord er) {
            executeTx(session -> {
                Employee entity = new Employee();
                entity.setEmpId(er.getEmployeeId());
                entity.setCandidateId(er.getCandidateId());
                entity.setName(er.getEmployeeName());
                entity.setDepartment(er.getDepartment());
                entity.setDesignation(er.getDesignation());
                entity.setDateOfJoining(toLocalDate(er.getJoiningDate()));
                session.persist(entity);
            }, "EmployeeRecord.save");
        }

        @Override
        public IEmployeeRecord findByEmployeeId(String id) {
            return executeQuery(session -> mapEmployee(session
                    .createQuery("FROM Employee WHERE empId = :id AND employmentStatus != 'DELETED'", Employee.class)
                    .setParameter("id", id).uniqueResult()), "EmployeeRecord.findByEmployeeId");
        }

        @Override
        public IEmployeeRecord findByCandidateId(String id) {
            return executeQuery(session -> mapEmployee(session
                    .createQuery("FROM Employee WHERE candidateId = :id AND employmentStatus != 'DELETED'",
                            Employee.class)
                    .setParameter("id", id).uniqueResult()), "EmployeeRecord.findByCandidateId");
        }

        @Override
        public void update(IEmployeeRecord er) {
            executeTx(session -> {
                Employee entity = session.get(Employee.class, er.getEmployeeId());
                if (entity != null) {
                    entity.setCandidateId(er.getCandidateId());
                    entity.setName(er.getEmployeeName());
                    entity.setDepartment(er.getDepartment());
                    entity.setDesignation(er.getDesignation());
                    entity.setDateOfJoining(toLocalDate(er.getJoiningDate()));
                    session.merge(entity);
                }
            }, "EmployeeRecord.update");
        }

        @Override
        public void delete(String id) {
            executeTx(session -> {
                Employee entity = session.get(Employee.class, id);
                if (entity != null) {
                    entity.setEmploymentStatus("DELETED"); // Soft delete
                    session.merge(entity);
                }
            }, "EmployeeRecord.delete");
        }
    };

    private EmployeeRecordDTO mapEmployee(Employee entity) {
        if (entity == null)
            return null;
        EmployeeRecordDTO dto = new EmployeeRecordDTO();
        dto.employeeId = entity.getEmpId();
        dto.candidateId = entity.getCandidateId();
        dto.employeeName = entity.getName();
        dto.department = entity.getDepartment();
        dto.designation = entity.getDesignation();
        dto.joiningDate = toDate(entity.getDateOfJoining());
        return dto;
    }

    // ==========================================================
    // 12. Notification Log implementation (Maps to Notification entity)
    // ==========================================================
    private final INotificationLogRepository notificationLogRepo = new INotificationLogRepository() {
        @Override
        public void save(INotificationLog nl) {
            executeTx(session -> {
                Notification entity = new Notification();
                // Since NotificationId is numeric generated, we don't set it unless it's a
                // specific logic.
                entity.setNotificationType(nl.getNotificationType());
                entity.setRecipientId(nl.getSentTo());
                entity.setNotificationMessage(nl.getStatusAlert());
                // We map contactInfoUsed roughly or leave it to payload details
                // Notification uses createdAt as timestamp
                session.persist(entity);
            }, "NotificationLog.save");
        }

        @Override
        public INotificationLog findByNotificationId(String id) {
            try {
                Long numericId = Long.parseLong(id);
                return executeQuery(session -> mapNotification(session
                        .createQuery("FROM Notification WHERE notificationId = :id AND status != 'DELETED'",
                                Notification.class)
                        .setParameter("id", numericId).uniqueResult()), "NotificationLog.findById");
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<INotificationLog> findByDateRange(Date start, Date end) {
            LocalDateTime sd = toLocalDateTime(start);
            LocalDateTime ed = toLocalDateTime(end);
            return executeQuery(
                    session -> session
                            .createQuery("FROM Notification WHERE createdAt BETWEEN :s AND :e AND status != 'DELETED'",
                                    Notification.class)
                            .setParameter("s", sd).setParameter("e", ed).getResultStream()
                            .map(RecruitmentRepositoryImpl.this::mapNotification).collect(Collectors.toList()),
                    "NotificationLog.findByDateRange");
        }

        @Override
        public List<INotificationLog> getFailedNotifications() {
            return executeQuery(
                    session -> session.createQuery("FROM Notification WHERE status = 'FAILED'", Notification.class)
                            .getResultStream().map(RecruitmentRepositoryImpl.this::mapNotification)
                            .collect(Collectors.toList()),
                    "NotificationLog.getFailed");
        }

        @Override
        public void updateNotificationStatus(String id, String status) {
            try {
                Long numericId = Long.parseLong(id);
                executeTx(session -> {
                    Notification entity = session.get(Notification.class, numericId);
                    if (entity != null) {
                        entity.setStatus(status);
                        session.merge(entity);
                    }
                }, "NotificationLog.updateStatus");
            } catch (NumberFormatException ignored) {
            }
        }

        @Override
        public void delete(String id) {
            try {
                Long numericId = Long.parseLong(id);
                executeTx(session -> {
                    Notification entity = session.get(Notification.class, numericId);
                    if (entity != null) {
                        entity.setStatus("DELETED");
                        session.merge(entity);
                    }
                }, "NotificationLog.delete");
            } catch (NumberFormatException ignored) {
            }
        }
    };

    private NotificationLogDTO mapNotification(Notification entity) {
        if (entity == null)
            return null;
        NotificationLogDTO dto = new NotificationLogDTO();
        dto.notificationId = String.valueOf(entity.getNotificationId());
        dto.notificationType = entity.getNotificationType();
        dto.sentTo = entity.getRecipientId();
        dto.statusAlert = entity.getNotificationMessage();
        dto.contactInfoUsed = "User Profile"; // Placeholder since it's not directly tracked
        dto.sentTimestamp = toDate(entity.getCreatedAt());
        return dto;
    }
}
