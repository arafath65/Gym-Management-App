package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RolePermissionsRequest(@NotNull List<String> permissionCodes) {}
