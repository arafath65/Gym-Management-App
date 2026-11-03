package com.arafath65.gymManagement.dto.response;

import java.util.List;

public record RoleResponse(Integer roleId, Integer tenantId, String roleName, String description, Boolean status, boolean systemRole, List<String> permissionCodes) {}
