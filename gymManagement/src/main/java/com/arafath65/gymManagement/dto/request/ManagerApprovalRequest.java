
package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManagerApprovalRequest(

        @NotBlank(message = "Manager username is required.")
        @Size(max = 100)
        String username,

        @NotBlank(message = "Manager password is required.")
        @Size(max = 100)
        String password,

        @NotBlank(message = "Operation code is required.")
        @Size(max = 50)
        String operationCode

) {}
