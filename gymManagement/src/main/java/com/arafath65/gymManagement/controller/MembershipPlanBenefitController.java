package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipPlanBenefitRequest;
import com.arafath65.gymManagement.dto.response.MembershipPlanBenefitResponse;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MembershipPlanBenefitService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanBenefitController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipPlanBenefitService
            membershipPlanBenefitService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanBenefitController(
            MembershipPlanBenefitService
                    membershipPlanBenefitService,

            TenantRepository tenantRepository,

            RegisteredDeviceRepository
                    registeredDeviceRepository) {

        this.membershipPlanBenefitService =
                membershipPlanBenefitService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // ADD BENEFIT TO MEMBERSHIP PLAN
    // =====================================================

    @PostMapping("/{membershipPlanId}/benefits")
    public ResponseEntity<
            ApiResponse<MembershipPlanBenefitResponse>>
    addBenefitToPlan(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanId,

            @Valid
            @RequestBody
            MembershipPlanBenefitRequest request) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipPlanBenefitResponse response =
                membershipPlanBenefitService
                        .addBenefitToPlan(
                                tenant.getTenantId(),
                                membershipPlanId,
                                request
                        );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Benefit added to membership plan successfully.",
                                response
                        )
                );
    }


    // =====================================================
    // GET PLAN BENEFITS
    // =====================================================

    @GetMapping("/{membershipPlanId}/benefits")
    public ResponseEntity<
            ApiResponse<List<MembershipPlanBenefitResponse>>>
    getPlanBenefits(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MembershipPlanBenefitResponse> response =
                membershipPlanBenefitService
                        .getPlanBenefits(
                                tenant.getTenantId(),
                                membershipPlanId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan benefits loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ONE PLAN BENEFIT
    // =====================================================

    @GetMapping("/benefits/{membershipPlanBenefitId}")
    public ResponseEntity<
            ApiResponse<MembershipPlanBenefitResponse>>
    getPlanBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanBenefitId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipPlanBenefitResponse response =
                membershipPlanBenefitService
                        .getPlanBenefit(
                                tenant.getTenantId(),
                                membershipPlanBenefitId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan benefit loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // UPDATE PLAN BENEFIT
    // =====================================================

    @PutMapping("/benefits/{membershipPlanBenefitId}")
    public ResponseEntity<
            ApiResponse<MembershipPlanBenefitResponse>>
    updatePlanBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanBenefitId,

            @Valid
            @RequestBody
            MembershipPlanBenefitRequest request) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipPlanBenefitResponse response =
                membershipPlanBenefitService
                        .updatePlanBenefit(
                                tenant.getTenantId(),
                                membershipPlanBenefitId,
                                request
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan benefit updated successfully.",
                        response
                )
        );
    }


    // =====================================================
    // DEACTIVATE PLAN BENEFIT
    // =====================================================

    @PutMapping(
            "/benefits/{membershipPlanBenefitId}/deactivate"
    )
    public ResponseEntity<
            ApiResponse<Void>>
    deactivatePlanBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanBenefitId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        membershipPlanBenefitService
                .deactivatePlanBenefit(
                        tenant.getTenantId(),
                        membershipPlanBenefitId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan benefit deactivated successfully.",
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
        // TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository
                        .findByTenantNo(
                                tenantNo
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
        // DEVICE
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
                device.getStatus()
        )) {

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


        return tenant;
    }
}