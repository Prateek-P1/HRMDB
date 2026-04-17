package com.hrms.db.repositories.security.models;

import java.time.LocalDateTime;

public class UserSession {
    private String sessionId;
    private String userId;
    private String username;
    private String token;
    private LocalDateTime loginTimestamp;
    private boolean active;

    public UserSession() {}

    public UserSession(String sessionId, String userId, String username, String token, LocalDateTime loginTimestamp, boolean active) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.loginTimestamp = loginTimestamp;
        this.active = active;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getLoginTimestamp() { return loginTimestamp; }
    public void setLoginTimestamp(LocalDateTime loginTimestamp) { this.loginTimestamp = loginTimestamp; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "UserSession{sessionId='" + sessionId + "', userId='" + userId + "', username='" + username + "', active=" + active + ", loginTimestamp=" + loginTimestamp + "}";
    }
}
