package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.repository.RolePermissionRepository;
import com.arafath65.gymManagement.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/**
 * Central backend authorization check for role-assigned permissions.
 * Gym owners and system administrators retain administrative access.
 */
@Service
public class PermissionAuthorizationService {

    private final RolePermissionRepository rolePermissionRepository;

    public PermissionAuthorizationService(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public void requirePermission(String permissionCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser actor)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "A valid user session is required."
            );
        }

        String roleName = actor.roleName() == null
                ? ""
                : actor.roleName().trim().toUpperCase(Locale.ROOT).replace(' ', '_');

        // The gym owner administers the tenant; the platform administrator administers the system.
        if ("GYM_OWNER".equals(roleName) || "SUPER_ADMIN".equals(roleName)) {
            return;
        }

        if (actor.roleId() == null
                || !rolePermissionRepository.existsByRoleRoleIdAndPermissionPermissionCodeAndPermissionStatusTrue(
                        actor.roleId(),
                        permissionCode
                )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to perform this action: " + permissionCode
            );
        }
    }

    public boolean hasPermission(String permissionCode) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser actor)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "A valid user session is required."
            );
        }

        String roleName = actor.roleName() == null
                ? ""
                : actor.roleName().trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');

        if ("GYM_OWNER".equals(roleName)
                || "SUPER_ADMIN".equals(roleName)) {
            return true;
        }

        return actor.roleId() != null
                && rolePermissionRepository
                .existsByRoleRoleIdAndPermissionPermissionCodeAndPermissionStatusTrue(
                        actor.roleId(),
                        permissionCode
                );
    }
}
