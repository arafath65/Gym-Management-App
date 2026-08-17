package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.TenantCreateRequest;
import com.arafath65.gymManagement.dto.response.TenantResponse;
import com.arafath65.gymManagement.service.TenantService;
import com.arafath65.gymManagement.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody TenantCreateRequest request) {

        TenantResponse response = tenantService.save(request);

        ApiResponse<TenantResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Tenant created successfully.",
                        response);

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED);

    }

}