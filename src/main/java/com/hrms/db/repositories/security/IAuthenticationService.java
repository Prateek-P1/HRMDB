package com.hrms.db.repositories.security;

import com.hrms.db.repositories.security.models.AuthResult;
import com.hrms.db.repositories.security.models.UserSession;

public interface IAuthenticationService {
    AuthResult authenticate(String username, String password);

    UserSession validateToken(String token);

    boolean logout(String token);
}
