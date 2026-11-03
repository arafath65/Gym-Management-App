package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}
