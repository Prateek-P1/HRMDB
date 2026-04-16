package com.hrms.db.repositories.performance.interfaces;

import com.hrms.db.repositories.performance.models.Feedback;
import com.hrms.db.repositories.performance.models.FeedbackRequest;
import java.util.List;

/**
 * IFeedbackRepository
 * Component: Feedback & 360-Degree Review Component
 *
 * Manages multi-rater feedback collection and storage.
 * DB Team must implement this interface and provide the concrete class.
 */
public interface IFeedbackRepository {

    /**
     * Raise a feedback request from one employee to another for a cycle.
     * @param fromEmployeeId employee requesting the feedback
     * @param toEmployeeId   reviewer being asked to give feedback
     * @param cycleId        the performance cycle this request belongs to
     * @return generated requestId on success, -1 on failure
     */
    int requestFeedback(int fromEmployeeId, int toEmployeeId, int cycleId);

    /**
     * Store a submitted feedback response from a reviewer.
     * @param feedback Feedback object with all fields populated
     * @return true if stored successfully, false otherwise
     */
    boolean submitFeedback(Feedback feedback);

    /**
     * Retrieve all feedback given TO an employee in a cycle (360-degree view).
     * @param employeeId employee receiving the feedback
     * @param cycleId    cycle to filter by
     * @return list of Feedback objects; empty list if none
     */
    List<Feedback> getFeedbackForEmployee(int employeeId, int cycleId);

    /**
     * Fetch all feedback submitted BY a specific reviewer in a cycle.
     * @param reviewerId employee who gave the feedback
     * @param cycleId    cycle to filter by
     * @return list of Feedback objects; empty list if none
     */
    List<Feedback> getFeedbackByReviewer(int reviewerId, int cycleId);

    /**
     * Get all outstanding (PENDING) feedback requests assigned to a reviewer.
     * @param reviewerId employee who needs to give feedback
     * @return list of FeedbackRequest objects with status PENDING; empty list if none
     */
    List<FeedbackRequest> getPendingRequests(int reviewerId);
}
