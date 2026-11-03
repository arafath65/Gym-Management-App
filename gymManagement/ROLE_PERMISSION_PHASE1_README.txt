FITFLOOR - ROLE & PERMISSION FOUNDATION (PHASE 1)

This patch adds the first backend foundation for custom tenant roles and selectable permissions.

Included:
- Role.tenant relationship. NULL tenant_id is reserved for system template roles.
- Tenant-scoped role name uniqueness (tenant_id + role_name).
- Global permission catalogue, grouped by module/screen/action.
- Role-permission join table.
- GET /api/roles/permissions
- GET /api/roles
- POST /api/roles
- PUT /api/roles/{roleId}/permissions
- Audit records for role creation and permission assignment changes.

Security in this phase:
- Role management endpoints require an authenticated X-User-Session.
- Only GYM_OWNER and SUPER_ADMIN can manage roles in this first foundation.
- Custom role records are tenant-scoped; users cannot update another tenant's role by ID.
- System template roles are read-only through these endpoints.
- Existing general API authorization is NOT globally changed in this patch, to avoid breaking existing app screens before each API is migrated to permission checks.

Important limitations:
- This is Phase 1 only. It does NOT yet enforce each permission on all existing business APIs.
- It does NOT yet include Android Role Management UI or Audit Logs UI.
- It does NOT yet return a user's permission set at login. That will be added with the Android permission UI integration.
- Existing AuditService is used for role and permission change audit events.

Database:
- Development database only. Back up if you need the current test data.
- The roles table mapping now expects nullable tenant_id and a unique key on (tenant_id, role_name).
- Run ROLE_PERMISSION_PHASE1_MIGRATION_DEV.sql once against the development database BEFORE starting the patched backend. It preserves existing roles as system templates (tenant_id = NULL).
- Hibernate ddl-auto=update creates the new permissions and role_permissions tables.
- Do not run this development migration against a production database.

API request examples:
POST /api/roles
Headers: X-User-Session: <session-token>
Content-Type: application/json
Body: {"roleName":"Front Desk","description":"Front desk staff"}

PUT /api/roles/ROLE_ID/permissions
Headers: X-User-Session: <session-token>
Content-Type: application/json
Body: {"permissionCodes":["MEMBERS_LIST_VIEW","MEMBERS_LIST_CREATE","PAYMENTS_COLLECTION_VIEW"]}

All permission codes are returned by GET /api/roles/permissions.
