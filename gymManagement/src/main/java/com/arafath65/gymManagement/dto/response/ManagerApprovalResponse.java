package com.arafath65.gymManagement.dto.response;

import java.time.LocalDateTime;

public record ManagerApprovalResponse(
        String authorizationToken,
        String operationCode,
        Integer memberId,
        String approvedBy,
        LocalDateTime expiresAt
) {}