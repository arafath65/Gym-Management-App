package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ManagerApproval;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerApprovalRepository
        extends JpaRepository<ManagerApproval, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT approval
            FROM ManagerApproval approval
            WHERE approval.tokenHash = :tokenHash
            """)
    Optional<ManagerApproval> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );
}