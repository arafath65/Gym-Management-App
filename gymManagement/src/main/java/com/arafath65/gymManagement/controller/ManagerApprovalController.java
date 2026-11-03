
package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ManagerApprovalRequest;
import com.arafath65.gymManagement.dto.response.ManagerApprovalResponse;
import com.arafath65.gymManagement.service.ManagerApprovalService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class ManagerApprovalController {

    private final ManagerApprovalService managerApprovalService;

    public ManagerApprovalController(
            ManagerApprovalService managerApprovalService) {

        this.managerApprovalService = managerApprovalService;
    }

    @PostMapping("/{memberId}/manager-approval")
    public ResponseEntity<ApiResponse<ManagerApprovalResponse>> requestApproval(
            @PathVariable Integer memberId,
            @Valid @RequestBody ManagerApprovalRequest request) {

        ManagerApprovalResponse response =
                managerApprovalService.requestApproval(
                        memberId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Manager authorization approved.",
                        response
                )
        );
    }
}
