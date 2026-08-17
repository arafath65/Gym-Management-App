package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.BranchCreateRequest;
import com.arafath65.gymManagement.dto.response.BranchResponse;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.BranchService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository registeredDeviceRepository;


    public BranchController(
            BranchService branchService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.branchService =
                branchService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>>
    createBranch(
            @Valid
            @RequestBody
            BranchCreateRequest request) {

        BranchResponse response =
                branchService.save(
                        request
                );


        ApiResponse<BranchResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branch created successfully.",
                        response
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET CURRENT TENANT BRANCHES
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<BranchResponse>>>
    getCurrentTenantBranches(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {


        // -------------------------------------------------
        // FIND TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository
                        .findByTenantNo(tenantNo)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid tenant number."
                                )
                        );


        // -------------------------------------------------
        // TENANT STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // -------------------------------------------------
        // FIND DEVICE
        // -------------------------------------------------

        RegisteredDevice device =
                registeredDeviceRepository
                        .findByDeviceToken(
                                deviceToken
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Device is not registered."
                                )
                        );


        // -------------------------------------------------
        // DEVICE STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                device.getStatus())) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


        // -------------------------------------------------
        // DEVICE → TENANT
        // -------------------------------------------------

        if (device.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // GET ACTIVE BRANCHES
        // -------------------------------------------------

        List<BranchResponse> branches =
                branchService.findByTenant(
                        tenant.getTenantId()
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<List<BranchResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branches loaded successfully.",
                        branches
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET ALL TENANT BRANCHES
    // =====================================================

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<
            ApiResponse<List<BranchResponse>>>
    getTenantBranches(
            @PathVariable
            Integer tenantId) {

        List<BranchResponse> branches =
                branchService.findByTenant(
                        tenantId
                );


        ApiResponse<List<BranchResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branches loaded successfully.",
                        branches
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET BRANCH BY ID
    // =====================================================

    @GetMapping("/{branchId}")
    public ResponseEntity<ApiResponse<BranchResponse>>
    getBranch(
            @PathVariable
            Integer branchId) {

        BranchResponse response =
                branchService.findById(
                        branchId
                );


        ApiResponse<BranchResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branch loaded successfully.",
                        response
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }
}