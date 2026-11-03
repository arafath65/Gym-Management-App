package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipRenewalRequest;
import com.arafath65.gymManagement.dto.response.MembershipRenewalResponse;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.service.MembershipRenewalService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-renewals")
public class MembershipRenewalController {

    private final MembershipRenewalService
            membershipRenewalService;


    public MembershipRenewalController(
            MembershipRenewalService membershipRenewalService) {

        this.membershipRenewalService =
                membershipRenewalService;
    }


    // =====================================================
    // CREATE RENEWAL
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MembershipRenewalResponse>>
    createRenewal(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MembershipRenewalRequest request) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipRenewal renewal =
                membershipRenewalService.createRenewal(
                        tenant.getTenantId(),
                        request
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership renewed successfully.",
                        toResponse(renewal)
                )
        );
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{renewalId}")
    public ResponseEntity<
            ApiResponse<MembershipRenewalResponse>>
    getById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer renewalId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipRenewal renewal =
                membershipRenewalService
                        .findById(
                                renewalId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Membership renewal not found."
                                )
                        );


        if (renewal.getTenant() == null ||
                renewal.getTenant().getTenantId()
                        != tenant.getTenantId()) {

            throw new RuntimeException(
                    "Membership renewal does not belong to this tenant."
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership renewal loaded successfully.",
                        toResponse(renewal)
                )
        );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    findByMembership(

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


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByMembership(
                                tenant.getTenantId(),
                                memberMembershipId
                        );


        List<MembershipRenewalResponse> response =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership renewals loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    findByMember(

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


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        List<MembershipRenewalResponse> response =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member renewals loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    findByBranch(

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


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByBranch(
                                tenant.getTenantId(),
                                branchId
                        );


        List<MembershipRenewalResponse> response =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Branch renewals loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    findByDateRange(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByDateRange(
                                tenant.getTenantId(),
                                startDate,
                                endDate
                        );


        List<MembershipRenewalResponse> response =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership renewals loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private MembershipRenewalResponse toResponse(
            MembershipRenewal renewal) {

        MembershipRenewalResponse response =
                new MembershipRenewalResponse();


        // -------------------------------------------------
        // ID
        // -------------------------------------------------

        response.setMembershipRenewalId(
                renewal.getMembershipRenewalId()
        );


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (renewal.getTenant() != null) {

            response.setTenantId(
                    renewal.getTenant()
                            .getTenantId()
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (renewal.getBranch() != null) {

            response.setBranchId(
                    renewal.getBranch()
                            .getBranchId()
            );

            response.setBranchName(
                    renewal.getBranch()
                            .getBranchName()
            );
        }


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        if (renewal.getMember() != null) {

            response.setMemberId(
                    renewal.getMember()
                            .getMemberId()
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------

        if (renewal.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    renewal.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    renewal.getMemberMembership()
                            .getMembershipNumber()
            );
        }


        // -------------------------------------------------
        // RENEWAL
        // -------------------------------------------------

        response.setRenewalNumber(
                renewal.getRenewalNumber()
        );

        response.setRenewalDate(
                renewal.getRenewalDate()
        );


        // -------------------------------------------------
        // PREVIOUS PERIOD
        // -------------------------------------------------

        response.setPreviousStartDate(
                renewal.getPreviousStartDate()
        );

        response.setPreviousEndDate(
                renewal.getPreviousEndDate()
        );


        // -------------------------------------------------
        // NEW PERIOD
        // -------------------------------------------------

        response.setNewStartDate(
                renewal.getNewStartDate()
        );

        response.setNewEndDate(
                renewal.getNewEndDate()
        );


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        response.setDurationType(
                renewal.getDurationType()
        );

        response.setDurationValue(
                renewal.getDurationValue()
        );


        // -------------------------------------------------
        // FINANCIAL
        // -------------------------------------------------

        response.setRenewalFee(
                renewal.getRenewalFee()
        );

        response.setDiscountAmount(
                renewal.getDiscountAmount()
        );

        response.setTotalAmount(
                renewal.getTotalAmount()
        );

        response.setPaidAmount(
                renewal.getPaidAmount()
        );

        response.setBalanceAmount(
                renewal.getBalanceAmount()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        response.setStatus(
                renewal.getStatus()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                renewal.getNotes()
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                renewal.getCreatedAt()
        );

        response.setUpdatedAt(
                renewal.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {

        /*
         * IMPORTANT:
         *
         * Keep the SAME FIT002 tenant/device
         * validation logic used by your other
         * working controllers.
         *
         * Do not create another authentication system.
         */

        throw new UnsupportedOperationException(
                "Use existing FIT002 tenant/device validation."
        );
    }
}