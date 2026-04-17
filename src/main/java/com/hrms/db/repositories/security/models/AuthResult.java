package com.hrms.db.repositories.security.models;

public class AuthResult {
    private boolean success;
    private String token;
    private String userId;
    private String message;

    public AuthResult() {}

    public AuthResult(boolean success, String token, String userId, String message) {
        this.success = success;
        this.token = token;
        this.userId = userId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "AuthResult{success=" + success + ", userId='" + userId + "', token=" + (token != null ? "<present>" : "<null>") + ", message='" + message + "'}";
    }
}
