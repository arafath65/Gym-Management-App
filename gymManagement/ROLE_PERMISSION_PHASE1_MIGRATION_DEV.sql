-- FITFLOOR development database migration for tenant-specific custom roles.
-- Run once against the development database BEFORE starting the patched backend.
-- This migration intentionally preserves existing role records as system templates (tenant_id = NULL).

ALTER TABLE roles
    DROP INDEX uk_roles_role_name;

ALTER TABLE roles
    ADD COLUMN tenant_id INT NULL AFTER role_id;

ALTER TABLE roles
    ADD CONSTRAINT fk_roles_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

ALTER TABLE roles
    ADD UNIQUE KEY uk_roles_tenant_role_name (tenant_id, role_name);

-- permissions and role_permissions tables are created by Hibernate ddl-auto=update.
