package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
