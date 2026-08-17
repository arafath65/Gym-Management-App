package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipRenewalRequest;
import com.arafath65.gymManagement.dto.response.MembershipRenewalResponse;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipService;
import com.arafath65.gymManagement.service.MembershipRenewalService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-renewals")
public class MembershipRenewalController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipRenewalService membershipRenewalService;

    private final MemberMembershipService memberMembershipService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipRenewalController(
            MembershipRenewalService membershipRenewalService,
            MemberMembershipService memberMembershipService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.membershipRenewalService =
                membershipRenewalService;

        this.memberMembershipService =
                memberMembershipService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
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


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipService
                        .findById(
                                request.getMemberMembershipId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        // -------------------------------------------------
        // VALIDATE MEMBERSHIP
        // -------------------------------------------------

        validateMembership(
                tenant,
                membership
        );


        // -------------------------------------------------
        // CREATE RENEWAL
        // -------------------------------------------------

        MembershipRenewal renewal =
                membershipRenewalService.renew(
                        membership.getMemberMembershipId(),
                        request.getDiscountAmount(),
                        request.getRenewalStartDate(),
                        request.getNotes()
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<MembershipRenewalResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership renewed successfully.",
                        toResponse(renewal)
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET RENEWAL BY ID
    // =====================================================

    @GetMapping("/{renewalId}")
    public ResponseEntity<
            ApiResponse<MembershipRenewalResponse>>
    getRenewalById(

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
                                new BadRequestException(
                                        "Membership renewal not found."
                                )
                        );


        if (renewal.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership renewal does not belong to this tenant."
            );
        }


        ApiResponse<MembershipRenewalResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership renewal loaded successfully.",
                        toResponse(renewal)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET RENEWALS BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{membershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    getMembershipRenewals(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MemberMembership membership =
                memberMembershipService
                        .findById(
                                membershipId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        validateMembership(
                tenant,
                membership
        );


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByMembership(
                                tenant.getTenantId(),
                                membershipId
                        );


        List<MembershipRenewalResponse> responses =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipRenewalResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership renewals loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET RENEWALS BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    getMemberRenewals(

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


        List<MembershipRenewalResponse> responses =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipRenewalResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Member renewals loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET RENEWALS BY BRANCH
    // =====================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    getBranchRenewals(

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


        List<MembershipRenewalResponse> responses =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipRenewalResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branch renewals loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET RENEWALS BY DATE RANGE
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    getRenewalsByDateRange(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam
            LocalDateTime startDate,

            @RequestParam
            LocalDateTime endDate) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        if (endDate.isBefore(startDate)) {

            throw new BadRequestException(
                    "End date cannot be before start date."
            );
        }


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findByDateRange(
                                tenant.getTenantId(),
                                startDate,
                                endDate
                        );


        List<MembershipRenewalResponse> responses =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipRenewalResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership renewals loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET RENEWAL HISTORY
    // =====================================================

    @GetMapping("/membership/{membershipId}/history")
    public ResponseEntity<
            ApiResponse<List<MembershipRenewalResponse>>>
    getRenewalHistory(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MemberMembership membership =
                memberMembershipService
                        .findById(
                                membershipId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        validateMembership(
                tenant,
                membership
        );


        List<MembershipRenewal> renewals =
                membershipRenewalService
                        .findHistory(
                                tenant.getTenantId(),
                                membershipId
                        );


        List<MembershipRenewalResponse> responses =
                renewals.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipRenewalResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership renewal history loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipRenewalResponse toResponse(
            MembershipRenewal renewal) {

        MembershipRenewalResponse response =
                new MembershipRenewalResponse();

        response.setMembershipRenewalId(
                renewal.getMembershipRenewalId()
        );

        response.setTenantId(
                renewal.getTenant()
                        .getTenantId()
        );

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

        if (renewal.getMember() != null) {

            response.setMemberId(
                    renewal.getMember()
                            .getMemberId()
            );
        }

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

        response.setRenewalNumber(
                renewal.getRenewalNumber()
        );

        response.setPreviousStartDate(
                renewal.getPreviousStartDate()
        );

        response.setPreviousEndDate(
                renewal.getPreviousEndDate()
        );

        response.setNewStartDate(
                renewal.getNewStartDate()
        );

        response.setNewEndDate(
                renewal.getNewEndDate()
        );

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

        response.setRenewalDate(
                renewal.getRenewalDate()
        );

        response.setNotes(
                renewal.getNotes()
        );

        response.setCreatedAt(
                renewal.getCreatedAt()
        );

        response.setUpdatedAt(
                renewal.getUpdatedAt()
        );

        return response;
    }


    // =====================================================
    // VALIDATE MEMBERSHIP
    // =====================================================

    private void validateMembership(
            Tenant tenant,
            MemberMembership membership) {

        if (membership.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(

            String tenantNo,

            String deviceToken) {

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


        if (!Boolean.TRUE.equals(
                tenant.getStatus()
        )) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


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


        if (!Boolean.TRUE.equals(
                device.getStatus()
        )) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


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