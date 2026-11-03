package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MemberMembershipBenefitRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipBenefitResponse;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipBenefitService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-memberships")
public class MemberMembershipBenefitController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MemberMembershipBenefitService
            memberMembershipBenefitService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipBenefitController(
            MemberMembershipBenefitService
                    memberMembershipBenefitService,

            TenantRepository tenantRepository,

            RegisteredDeviceRepository
                    registeredDeviceRepository) {

        this.memberMembershipBenefitService =
                memberMembershipBenefitService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // ADD BENEFIT TO MEMBER MEMBERSHIP
    // =====================================================

    @PostMapping("/{memberMembershipId}/benefits")
    public ResponseEntity<
            ApiResponse<MemberMembershipBenefitResponse>>
    addBenefitToMembership(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipId,

            @Valid
            @RequestBody
            MemberMembershipBenefitRequest request) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );

        MemberMembershipBenefitResponse response =
                memberMembershipBenefitService
                        .addBenefitToMembership(
                                tenant.getTenantId(),
                                memberMembershipId,
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Benefit added to member membership successfully.",
                                response
                        )
                );
    }


    // =====================================================
    // GET MEMBERSHIP BENEFITS
    // =====================================================

    @GetMapping("/{memberMembershipId}/benefits")
    public ResponseEntity<
            ApiResponse<List<MemberMembershipBenefitResponse>>>
    getMembershipBenefits(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );

        List<MemberMembershipBenefitResponse> response =
                memberMembershipBenefitService
                        .getMembershipBenefits(
                                tenant.getTenantId(),
                                memberMembershipId
                        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member membership benefits loaded successfully.",
                        response
                )
        );
    }

    // =====================================================
// GET ALL BENEFIT HISTORY OF MEMBER
// =====================================================

    @GetMapping("/benefits/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MemberMembershipBenefitResponse>>>
    getMemberBenefitHistory(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MemberMembershipBenefitResponse> response =
                memberMembershipBenefitService
                        .getBenefitHistory(
                                tenant.getTenantId(),
                                memberId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member benefit history loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ONE
    // =====================================================

    @GetMapping("/benefits/{memberMembershipBenefitId}")
    public ResponseEntity<
            ApiResponse<MemberMembershipBenefitResponse>>
    getMembershipBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipBenefitId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );

        MemberMembershipBenefitResponse response =
                memberMembershipBenefitService
                        .getMembershipBenefit(
                                tenant.getTenantId(),
                                memberMembershipBenefitId
                        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member membership benefit loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @PutMapping(
            "/benefits/{memberMembershipBenefitId}/deactivate"
    )
    public ResponseEntity<ApiResponse<Void>>
    deactivateMembershipBenefit(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipBenefitId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );

        memberMembershipBenefitService
                .deactivateMembershipBenefit(
                        tenant.getTenantId(),
                        memberMembershipBenefitId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member membership benefit deactivated successfully.",
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

        if (device.getTenant() == null
                || device.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        return tenant;
    }
}