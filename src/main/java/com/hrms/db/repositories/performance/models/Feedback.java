package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class Feedback {
    private int feedbackId;
    private int fromEmployeeId;
    private int toEmployeeId;
    private int cycleId;
    private String comments;
    private int rating;            // 1-5
    private Date submittedDate;

    public Feedback() {}

    public Feedback(int feedbackId, int fromEmployeeId, int toEmployeeId, int cycleId, String comments, int rating) {
        this.feedbackId = feedbackId;
        this.fromEmployeeId = fromEmployeeId;
        this.toEmployeeId = toEmployeeId;
        this.cycleId = cycleId;
        this.comments = comments;
        this.rating = rating;
        this.submittedDate = new Date();
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }
    public int getFromEmployeeId() { return fromEmployeeId; }
    public void setFromEmployeeId(int fromEmployeeId) { this.fromEmployeeId = fromEmployeeId; }
    public int getToEmployeeId() { return toEmployeeId; }
    public void setToEmployeeId(int toEmployeeId) { this.toEmployeeId = toEmployeeId; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public Date getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Date submittedDate) { this.submittedDate = submittedDate; }

    @Override
    public String toString() {
        return "Feedback{id=" + feedbackId + ", from=" + fromEmployeeId + ", to=" + toEmployeeId + ", rating=" + rating + "}";
    }
}
