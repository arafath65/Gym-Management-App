package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.*;
import com.arafath65.gymManagement.repository.UserSessionRepository;
import com.arafath65.gymManagement.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class UserSessionService {
    private static final int TOKEN_BYTES = 32;
    private static final long SESSION_HOURS = 12;

    private final UserSessionRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserSessionService(UserSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IssuedSession create(User user) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setTokenHash(sha256(rawToken));
        session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_HOURS));
        repository.save(session);

        return new IssuedSession(rawToken, session.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> authenticate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return Optional.empty();

        return repository.findByTokenHashAndRevokedAtIsNull(sha256(rawToken))
                .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(s -> Boolean.TRUE.equals(s.getUser().getStatus()))
                .map(s -> {
                    User u = s.getUser();
                    Tenant t = u.getTenant();
                    Branch b = u.getBranch();
                    Role r = u.getRole();
                    return new AuthenticatedUser(
                            u.getUserId(),
                            t.getTenantId(),
                            b == null ? null : b.getBranchId(),
                            u.getUsername(),
                            u.getFullName(),
                            r == null ? null : r.getRoleId(),
                            r == null ? null : r.getRoleName()
                    );
                });
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        repository.findByTokenHashAndRevokedAtIsNull(sha256(rawToken))
                .ifPresent(s -> {
                    s.setRevokedAt(LocalDateTime.now());
                    repository.save(s);
                });
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash session token.", e);
        }
    }

    public record IssuedSession(String token, LocalDateTime expiresAt) {}
}
