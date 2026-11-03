FITFLOOR AUDIT FOUNDATION - BACKEND PATCH

This patch adds the first backend foundation only:
- User session tokens issued after successful login.
- Raw session tokens are returned once; only SHA-256 hashes are stored in user_sessions.
- Session expiry: 12 hours.
- Optional X-User-Session header authentication (staged rollout; requests without it continue to work).
- POST /api/auth/logout revokes the supplied session token.
- AuditLog entity/table and AuditService.record(...) for use by module services.

IMPORTANT:
1. This is not the complete audit rollout. Existing business services have not yet been wired to AuditService.
2. Android has not yet been updated to save/send X-User-Session. That is the next integration step.
3. All /api/** routes remain permitAll during compatibility rollout. Do not treat this as endpoint authorization.
4. The audit foundation expects tenantId to be provided when no authenticated session exists.
5. Database tables are created by the project's current spring.jpa.hibernate.ddl-auto=update setting.

AuditService usage example:
auditService.record(
    tenantId, branchId,
    "MEMBERS", "MEMBER_CREATED",
    "Member", savedMember.getMemberId(),
    "Created member " + savedMember.getMemberNo(),
    null, savedMemberSnapshot
);

Apply the files under the existing gymManagement project root, preserving the relative paths.
