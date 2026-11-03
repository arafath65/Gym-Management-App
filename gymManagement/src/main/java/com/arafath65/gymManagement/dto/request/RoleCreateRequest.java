package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleCreateRequest(
        @NotBlank @Size(max = 100) String roleName,
        @Size(max = 255) String description
) {}
