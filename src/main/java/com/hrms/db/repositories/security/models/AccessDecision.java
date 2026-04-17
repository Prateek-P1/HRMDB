package com.hrms.db.repositories.security.models;

public class AccessDecision {
    private boolean allowed;
    private String reason;

    public AccessDecision() {}

    public AccessDecision(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "AccessDecision{allowed=" + allowed + ", reason='" + reason + "'}";
    }
}
