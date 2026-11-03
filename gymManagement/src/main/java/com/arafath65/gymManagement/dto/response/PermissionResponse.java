package com.arafath65.gymManagement.dto.response;

public record PermissionResponse(Integer permissionId, String permissionCode, String module, String screen, String action, String description) {}
