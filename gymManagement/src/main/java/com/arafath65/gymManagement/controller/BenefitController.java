package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.BenefitRequest;
import com.arafath65.gymManagement.dto.response.BenefitResponse;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.BenefitService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benefits")
public class BenefitController {


    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final BenefitService benefitService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitController(
            BenefitService benefitService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.benefitService =
                benefitService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE BENEFIT
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<BenefitResponse>>
    createBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            BenefitRequest request) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        BenefitResponse response =
                benefitService.createBenefit(
                        tenant.getTenantId(),
                        request
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Benefit created successfully.",
                                response
                        )
                );
    }


    // =====================================================
    // GET ALL BENEFITS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<BenefitResponse>>>
    getAllBenefits(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<BenefitResponse> response =
                benefitService.getAllBenefits(
                        tenant.getTenantId()
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Benefits loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ACTIVE BENEFITS
    // =====================================================

    @GetMapping("/active")
    public ResponseEntity<
            ApiResponse<List<BenefitResponse>>>
    getActiveBenefits(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<BenefitResponse> response =
                benefitService.getActiveBenefits(
                        tenant.getTenantId()
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active benefits loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ACTIVE BENEFITS BY BRANCH
    //
    // Used by:
    // Add Membership → Benefit
    //
    // Returns benefits that are:
    // - assigned to this branch
    // - branch assignment active
    // - benefit itself active
    // =====================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<
            ApiResponse<List<BenefitResponse>>>
    getActiveBenefitsByBranch(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer branchId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<BenefitResponse> response =
                benefitService.getActiveBenefitsByBranch(
                        tenant.getTenantId(),
                        branchId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Branch benefits loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ONE BENEFIT
    // =====================================================

    @GetMapping("/{benefitId}")
    public ResponseEntity<
            ApiResponse<BenefitResponse>>
    getBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer benefitId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        BenefitResponse response =
                benefitService.getBenefit(
                        tenant.getTenantId(),
                        benefitId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Benefit loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // UPDATE BENEFIT
    // =====================================================

    @PutMapping("/{benefitId}")
    public ResponseEntity<
            ApiResponse<BenefitResponse>>
    updateBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer benefitId,

            @Valid
            @RequestBody
            BenefitRequest request) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        BenefitResponse response =
                benefitService.updateBenefit(
                        tenant.getTenantId(),
                        benefitId,
                        request
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Benefit updated successfully.",
                        response
                )
        );
    }


    // =====================================================
    // ACTIVATE BENEFIT
    // =====================================================

    @PutMapping("/{benefitId}/activate")
    public ResponseEntity<ApiResponse<Void>>
    activateBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer benefitId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        benefitService.activateBenefit(
                tenant.getTenantId(),
                benefitId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Benefit activated successfully.",
                        null
                )
        );
    }


    // =====================================================
    // DEACTIVATE BENEFIT
    // =====================================================

    @PutMapping("/{benefitId}/deactivate")
    public ResponseEntity<ApiResponse<Void>>
    deactivateBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer benefitId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        benefitService.deactivateBenefit(
                tenant.getTenantId(),
                benefitId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Benefit deactivated successfully.",
                        null
                )
        );
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {


        // -------------------------------------------------
        // VALIDATE TENANT NUMBER
        // -------------------------------------------------

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            throw new BadRequestException(
                    "Tenant number is required."
            );
        }


        // -------------------------------------------------
        // VALIDATE DEVICE TOKEN
        // -------------------------------------------------

        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            throw new BadRequestException(
                    "Device token is required."
            );
        }


        // -------------------------------------------------
        // FIND TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository
                        .findByTenantNo(
                                tenantNo.trim()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid tenant number."
                                )
                        );


        // -------------------------------------------------
        // TENANT STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus()
        )) {

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
                                deviceToken.trim()
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
                device.getStatus()
        )) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


        // -------------------------------------------------
        // DEVICE → TENANT
        // -------------------------------------------------

        if (device.getTenant() == null
                || device.getTenant().getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        return tenant;
    }
}