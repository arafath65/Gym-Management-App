package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.RoleCreateRequest;
import com.arafath65.gymManagement.dto.request.RolePermissionsRequest;
import com.arafath65.gymManagement.dto.response.PermissionResponse;
import com.arafath65.gymManagement.dto.response.RoleResponse;
import com.arafath65.gymManagement.entity.*;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.*;
import com.arafath65.gymManagement.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleManagementService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final TenantRepository tenantRepository;
    private final AuditService auditService;

    public RoleManagementService(RoleRepository roleRepository, PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository, TenantRepository tenantRepository, AuditService auditService) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.tenantRepository = tenantRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissions() {
        requireRoleManager();
        return permissionRepository.findAllByStatusTrueOrderByModuleAscScreenAscActionAsc().stream()
                .map(p -> new PermissionResponse(p.getPermissionId(), p.getPermissionCode(), p.getModule(), p.getScreen(), p.getAction(), p.getDescription())).toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getRoles() {
        AuthenticatedUser actor = requireRoleManager();
        return roleRepository.findAllByTenantTenantIdOrTenantIsNullOrderByRoleNameAsc(actor.tenantId()).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        AuthenticatedUser actor = requireRoleManager();
        String name = request.roleName().trim();
        Set<String> reservedNames = Set.of("SUPER_ADMIN", "GYM_OWNER", "BRANCH_MANAGER", "RECEPTION", "CASHIER", "TRAINER", "ACCOUNTANT");
        if (reservedNames.contains(name.toUpperCase(Locale.ROOT).replace(' ', '_'))) {
            throw new BadRequestException("This role name is reserved for a system role.");
        }
        if (roleRepository.existsByTenantTenantIdAndRoleNameIgnoreCase(actor.tenantId(), name)) {
            throw new BadRequestException("A role with this name already exists.");
        }
        Tenant tenant = tenantRepository.findById(actor.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found."));
        Role role = new Role();
        role.setTenant(tenant);
        role.setRoleName(name);
        role.setDescription(request.description());
        role.setStatus(true);
        Role saved = roleRepository.save(role);
        auditService.record(actor.tenantId(), actor.branchId(), "ROLES", "ROLE_CREATED", "Role", saved.getRoleId(),
                "Created role " + saved.getRoleName(), null, Map.of("roleName", saved.getRoleName(), "description", Objects.toString(saved.getDescription(), "")));
        return toResponse(saved);
    }

    @Transactional
    public RoleResponse replacePermissions(Integer roleId, RolePermissionsRequest request) {
        AuthenticatedUser actor = requireRoleManager();
        Role role = roleRepository.findByRoleIdAndTenantTenantId(roleId, actor.tenantId())
                .orElseThrow(() -> new BadRequestException("Custom role not found in your gym."));
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        for (String code : request.permissionCodes()) {
            if (code == null || code.isBlank()) throw new BadRequestException("Permission code cannot be blank.");
            codes.add(code.trim().toUpperCase(Locale.ROOT));
        }
        List<String> unknown = codes.stream().filter(code -> permissionRepository.findByPermissionCode(code).isEmpty()).toList();
        if (!unknown.isEmpty()) throw new BadRequestException("Unknown permission codes: " + String.join(", ", unknown));
        List<String> oldCodes = rolePermissionRepository.findAllByRoleRoleId(roleId).stream()
                .map(rp -> rp.getPermission().getPermissionCode()).sorted().toList();
        rolePermissionRepository.deleteAllByRoleRoleId(roleId);
        for (String code : codes) {
            Permission permission = permissionRepository.findByPermissionCode(code).orElseThrow();
            RolePermission link = new RolePermission();
            link.setRole(role);
            link.setPermission(permission);
            rolePermissionRepository.save(link);
        }
        auditService.record(actor.tenantId(), actor.branchId(), "ROLES", "ROLE_PERMISSIONS_UPDATED", "Role", roleId,
                "Updated permissions for role " + role.getRoleName(), oldCodes, codes.stream().sorted().toList());
        return toResponse(role);
    }

    private RoleResponse toResponse(Role role) {
        List<String> codes = rolePermissionRepository.findAllByRoleRoleId(role.getRoleId()).stream()
                .map(rp -> rp.getPermission().getPermissionCode()).sorted().toList();
        Integer tenantId = role.getTenant() == null ? null : role.getTenant().getTenantId();
        return new RoleResponse(role.getRoleId(), tenantId, role.getRoleName(), role.getDescription(), role.getStatus(),
                role.getTenant() == null, codes);
    }

    private AuthenticatedUser requireRoleManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser actor)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "A valid user session is required.");
        }
        String role = actor.roleName() == null ? "" : actor.roleName().toUpperCase(Locale.ROOT);
        if (!role.equals("GYM_OWNER") && !role.equals("SUPER_ADMIN")) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Only the gym owner or system administrator can manage roles.");
        }
        return actor;
    }
}
