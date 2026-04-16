package com.hrms.db.repositories.performance.models;

public class FeedbackRequest {
    private int requestId;
    private int fromEmployeeId;
    private int toEmployeeId;
    private int cycleId;
    private String status; // PENDING, COMPLETED

    public FeedbackRequest() {}

    public FeedbackRequest(int requestId, int fromEmployeeId, int toEmployeeId, int cycleId) {
        this.requestId = requestId;
        this.fromEmployeeId = fromEmployeeId;
        this.toEmployeeId = toEmployeeId;
        this.cycleId = cycleId;
        this.status = "PENDING";
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getFromEmployeeId() { return fromEmployeeId; }
    public void setFromEmployeeId(int fromEmployeeId) { this.fromEmployeeId = fromEmployeeId; }
    public int getToEmployeeId() { return toEmployeeId; }
    public void setToEmployeeId(int toEmployeeId) { this.toEmployeeId = toEmployeeId; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "FeedbackRequest{id=" + requestId + ", from=" + fromEmployeeId + ", to=" + toEmployeeId + ", status='" + status + "'}";
    }
}
