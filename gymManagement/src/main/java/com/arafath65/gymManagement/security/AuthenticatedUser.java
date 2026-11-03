package com.arafath65.gymManagement.security;

public record AuthenticatedUser(
        Integer userId,
        Integer tenantId,
        Integer branchId,
        String username,
        String fullName,
        Integer roleId,
        String roleName
) {}
