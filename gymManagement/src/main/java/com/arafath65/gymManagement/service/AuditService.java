package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.AuditLog;
import com.arafath65.gymManagement.repository.AuditLogRepository;
import com.arafath65.gymManagement.security.AuthenticatedUser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditService {
    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AuditLog record(
            Integer tenantId,
            Integer branchId,
            String module,
            String action,
            String entityType,
            Object entityId,
            String description,
            Object oldValues,
            Object newValues
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser actor = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AuthenticatedUser current) {
            actor = current;
        }

        AuditLog log = new AuditLog();
        log.setTenantId(tenantId != null ? tenantId : actor == null ? null : actor.tenantId());
        if (log.getTenantId() == null) {
            throw new IllegalArgumentException("Tenant ID is required for an audit record.");
        }
        log.setBranchId(branchId != null ? branchId : actor == null ? null : actor.branchId());
        if (actor != null) {
            log.setUserId(actor.userId());
            log.setUsername(actor.username());
        }
        log.setModule(module);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId == null ? null : String.valueOf(entityId));
        log.setDescription(description);
        log.setOldValues(toJson(oldValues));
        log.setNewValues(toJson(newValues));
        log.setIpAddress(resolveIpAddress());
        return repository.save(log);
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Unable to serialize audit values.", e);
        }
    }

    private String resolveIpAddress() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
