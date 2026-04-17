package com.hrms.db.repositories.security;

import com.hrms.db.config.DatabaseConnection;
import com.hrms.db.entities.AccessPermission;
import com.hrms.db.entities.Employee;
import com.hrms.db.entities.SecurityAuditLog;
import com.hrms.db.repositories.security.models.AccessDecision;
import com.hrms.db.repositories.security.models.AuditEntry;
import com.hrms.db.repositories.security.models.AuthResult;
import com.hrms.db.repositories.security.models.UserSession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SecurityRepositoryImpl — Unified implementation for the Security team's interfaces.
 *
 * Notes:
 * - Authentication in this module is DB-backed for sessions, but does not currently validate passwords
 *   (there is no password/credential store entity in this repository).
 * - Authorization uses the {@link AccessPermission} table.
 * - Audit logging uses the {@link SecurityAuditLog} table.
 */
public class SecurityRepositoryImpl
        implements IAuditService, IAuthenticationService, IAuthorizationService, IEncryptionService {

    private static final String ENV_ENCRYPTION_KEY = "HRMS_ENCRYPTION_KEY";

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public SecurityRepositoryImpl() {
        this.secretKey = new SecretKeySpec(deriveAesKey(System.getenv(ENV_ENCRYPTION_KEY)), "AES");
    }

    // ─────────────────────────────────────────────────────────────
    // IEncryptionService
    // ─────────────────────────────────────────────────────────────

    @Override
    public String encrypt(String plainText) {
        if (plainText == null) return null;

        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null) return null;

        try {
            String[] parts = cipherText.split(":", 2);
            if (parts.length != 2) return null;

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String mask(String plainText) {
        if (plainText == null) return null;
        if (plainText.isEmpty()) return "";

        int visible = Math.min(4, plainText.length());
        int maskedLen = plainText.length() - visible;
        return "*".repeat(Math.max(0, maskedLen)) + plainText.substring(plainText.length() - visible);
    }

    private static byte[] deriveAesKey(String keyMaterial) {
        try {
            String material = (keyMaterial != null && !keyMaterial.isBlank())
                    ? keyMaterial
                    : "HRMS_DEV_DEFAULT_KEY_CHANGE_ME";

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(material.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to derive AES key", ex);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // IAuditService
    // ─────────────────────────────────────────────────────────────

    @Override
    public void logAccess(String userId, String resource, String action, String status, LocalDateTime timestamp) {
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            SecurityAuditLog log = new SecurityAuditLog();
            log.setUserId(userId);
            log.setActionType("ACCESS");
            log.setAction(action);
            log.setOperation(resource);
            log.setOutcome(status);
            log.setDetails("resource=" + resource + ", action=" + action);
            log.setTimestamp(timestamp != null ? timestamp : LocalDateTime.now());

            session.persist(log);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public List<AuditEntry> getAuditLogs(LocalDateTime fromDate, LocalDateTime toDate, String userId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM SecurityAuditLog a WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (fromDate != null) {
                hql.append(" AND a.timestamp >= :from");
                params.put("from", fromDate);
            }
            if (toDate != null) {
                hql.append(" AND a.timestamp <= :to");
                params.put("to", toDate);
            }
            if (userId != null && !userId.isBlank()) {
                hql.append(" AND a.userId = :uid");
                params.put("uid", userId);
            }

            hql.append(" ORDER BY a.timestamp DESC");

            var query = session.createQuery(hql.toString(), SecurityAuditLog.class);
            for (var entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList().stream()
                    .map(a -> new AuditEntry(
                            a.getUserId(),
                            a.getOperation(),
                            a.getAction(),
                            a.getOutcome(),
                            a.getTimestamp()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // IAuthenticationService
    // ─────────────────────────────────────────────────────────────

    @Override
    public AuthResult authenticate(String username, String password) {
        if (username == null || username.isBlank()) {
            return new AuthResult(false, null, null, "Username is required");
        }
        if (password == null || password.isBlank()) {
            return new AuthResult(false, null, null, "Password is required");
        }

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Employee employee = session.get(Employee.class, username);
            if (employee == null) {
                employee = session.createQuery("FROM Employee e WHERE e.email = :u", Employee.class)
                        .setParameter("u", username)
                        .setMaxResults(1)
                        .uniqueResult();
            }
            if (employee == null) {
                logAccess(null, "AUTH", "authenticate", "FAIL", LocalDateTime.now());
                return new AuthResult(false, null, null, "User not found");
            }

            // NOTE: This repository has no credential/password table.
            // For now we only verify that the user exists, then create a DB-backed session.
            String sessionId = UUID.randomUUID().toString();
            String token = UUID.randomUUID().toString();

            tx = session.beginTransaction();

            com.hrms.db.entities.UserSession sessionEntity = new com.hrms.db.entities.UserSession();
            sessionEntity.setSessionId(sessionId);
            sessionEntity.setUserId(employee.getEmpId());
            sessionEntity.setUsername(username);
            sessionEntity.setSessionToken(token);
            sessionEntity.setLoginTimestamp(LocalDateTime.now());
            sessionEntity.setIsActive(true);
            session.persist(sessionEntity);

            SecurityAuditLog audit = new SecurityAuditLog();
            audit.setUserId(employee.getEmpId());
            audit.setActionType("LOGIN");
            audit.setAction("AUTHENTICATE");
            audit.setOperation("authenticate");
            audit.setOutcome("SUCCESS");
            audit.setDetails("Created session for username=" + username);
            audit.setTimestamp(LocalDateTime.now());
            session.persist(audit);

            tx.commit();

            return new AuthResult(true, token, employee.getEmpId(), "OK");
        } catch (Exception ex) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception ignored) {}
            }
            return new AuthResult(false, null, null, "Authentication failed");
        }
    }

    @Override
    public UserSession validateToken(String token) {
        if (token == null || token.isBlank()) return null;

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.UserSession entity = session.createQuery(
                            "FROM UserSession s WHERE s.sessionToken = :t AND (s.isActive = true OR s.isActive IS NULL)",
                            com.hrms.db.entities.UserSession.class)
                    .setParameter("t", token)
                    .setMaxResults(1)
                    .uniqueResult();

            if (entity == null) return null;

            boolean active = entity.getIsActive() == null || Boolean.TRUE.equals(entity.getIsActive());
            return new UserSession(
                    entity.getSessionId(),
                    entity.getUserId(),
                    entity.getUsername(),
                    entity.getSessionToken(),
                    entity.getLoginTimestamp(),
                    active);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean logout(String token) {
        if (token == null || token.isBlank()) return false;

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int updated = session.createMutationQuery(
                            "UPDATE UserSession s SET s.isActive = false WHERE s.sessionToken = :t")
                    .setParameter("t", token)
                    .executeUpdate();

            tx.commit();
            return updated > 0;
        } catch (Exception ex) {
            if (tx != null) {
                try { tx.rollback(); } catch (Exception ignored) {}
            }
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // IAuthorizationService
    // ─────────────────────────────────────────────────────────────

    @Override
    public AccessDecision authorize(String userId, String role, String resource, String action) {
        if (userId == null || userId.isBlank()) {
            return new AccessDecision(false, "userId is required");
        }

        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            return new AccessDecision(true, "ADMIN role");
        }

        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            AccessPermission perm = session.createQuery(
                            "FROM AccessPermission p WHERE p.userId = :uid", AccessPermission.class)
                    .setParameter("uid", userId)
                    .setMaxResults(1)
                    .uniqueResult();

            if (perm == null) {
                return new AccessDecision(false, "No AccessPermission assigned");
            }

            if (perm.getComplianceStatus() != null && perm.getComplianceStatus().equalsIgnoreCase("BLOCKED")) {
                return new AccessDecision(false, "User is blocked by compliance");
            }

            String raw = perm.getAccessPermissions();
            if (raw == null || raw.isBlank()) {
                return new AccessDecision(false, "No permissions configured");
            }

            Set<String> tokens = Arrays.stream(raw.split("[,;\\s]+"))
                    .filter(s -> !s.isBlank())
                    .map(s -> s.trim().toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            if (tokens.contains("*") || tokens.contains("all")) {
                return new AccessDecision(true, "Wildcard permission");
            }

            String res = resource != null ? resource.trim() : "";
            String act = action != null ? action.trim() : "";

            List<String> candidates = List.of(
                    (res + ":" + act).toLowerCase(Locale.ROOT),
                    (res + ":*").toLowerCase(Locale.ROOT),
                    ("*:" + act).toLowerCase(Locale.ROOT)
            );

            for (String c : candidates) {
                if (tokens.contains(c)) {
                    return new AccessDecision(true, "Matched permission: " + c);
                }
            }

            // Fallback: if the token list contains the resource name or action name alone.
            if (!res.isBlank() && tokens.contains(res.toLowerCase(Locale.ROOT))) {
                return new AccessDecision(true, "Matched resource token");
            }
            if (!act.isBlank() && tokens.contains(act.toLowerCase(Locale.ROOT))) {
                return new AccessDecision(true, "Matched action token");
            }

            return new AccessDecision(false, "Denied");
        } catch (Exception ex) {
            return new AccessDecision(false, "Authorization error");
        }
    }
}
