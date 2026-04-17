package com.hrms.db.repositories.security;

import com.hrms.db.repositories.security.models.AccessDecision;

public interface IAuthorizationService {
    AccessDecision authorize(String userId, String role, String resource, String action);
}
