package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    Optional<User> findByTenantTenantIdAndUsername(
            Integer tenantId,
            String username
    );

    boolean existsByTenantTenantIdAndUsername(
            Integer tenantId,
            String username
    );

    Optional<User> findByUserIdAndTenantTenantId(
            Integer userId,
            Integer tenantId
    );
}