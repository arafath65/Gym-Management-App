package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipFreezeRequest;
import com.arafath65.gymManagement.dto.response.MembershipFreezeResponse;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipFreeze;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipService;
import com.arafath65.gymManagement.service.MembershipFreezeService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-freezes")
public class MembershipFreezeController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipFreezeService membershipFreezeService;

    private final MemberMembershipService memberMembershipService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipFreezeController(
            MembershipFreezeService membershipFreezeService,
            MemberMembershipService memberMembershipService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.membershipFreezeService =
                membershipFreezeService;

        this.memberMembershipService =
                memberMembershipService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE FREEZE
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MembershipFreezeResponse>>
    createFreeze(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MembershipFreezeRequest request) {

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
        // BUILD FREEZE
        // -------------------------------------------------

        MembershipFreeze freeze =
                new MembershipFreeze();

        freeze.setTenant(
                tenant
        );

        freeze.setBranch(
                membership.getBranch()
        );

        freeze.setMember(
                membership.getMember()
        );

        freeze.setMemberMembership(
                membership
        );

        freeze.setFreezeStartDate(
                request.getFreezeStartDate()
        );

        freeze.setFreezeEndDate(
                request.getFreezeEndDate()
        );

        freeze.setReason(
                request.getReason()
        );

        freeze.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        MembershipFreeze savedFreeze =
                membershipFreezeService.createFreeze(
                        freeze
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<MembershipFreezeResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freeze created successfully.",
                        toResponse(savedFreeze)
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET FREEZE BY ID
    // =====================================================

    @GetMapping("/{freezeId}")
    public ResponseEntity<
            ApiResponse<MembershipFreezeResponse>>
    getFreezeById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer freezeId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipFreeze freeze =
                membershipFreezeService
                        .findById(
                                freezeId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership freeze not found."
                                )
                        );


        if (freeze.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership freeze does not belong to this tenant."
            );
        }


        ApiResponse<MembershipFreezeResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freeze loaded successfully.",
                        toResponse(freeze)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET FREEZES BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{membershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipFreezeResponse>>>
    getMembershipFreezes(

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


        List<MembershipFreeze> freezes =
                membershipFreezeService
                        .findByMembership(
                                tenant.getTenantId(),
                                membershipId
                        );


        List<MembershipFreezeResponse> responses =
                freezes.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipFreezeResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freezes loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET FREEZES BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MembershipFreezeResponse>>>
    getMemberFreezes(

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


        List<MembershipFreeze> freezes =
                membershipFreezeService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        List<MembershipFreezeResponse> responses =
                freezes.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipFreezeResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Member freezes loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET FREEZES BY STATUS
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<
            ApiResponse<List<MembershipFreezeResponse>>>
    getFreezesByStatus(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            MembershipFreeze.FreezeStatus status) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MembershipFreeze> freezes =
                membershipFreezeService
                        .findByStatus(
                                tenant.getTenantId(),
                                status
                        );


        List<MembershipFreezeResponse> responses =
                freezes.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipFreezeResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freezes loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET FREEZES BY DATE RANGE
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<
            ApiResponse<List<MembershipFreezeResponse>>>
    getFreezesByDateRange(

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


        if (endDate.isBefore(startDate)) {

            throw new BadRequestException(
                    "End date cannot be before start date."
            );
        }


        List<MembershipFreeze> freezes =
                membershipFreezeService
                        .findByDateRange(
                                tenant.getTenantId(),
                                startDate,
                                endDate
                        );


        List<MembershipFreezeResponse> responses =
                freezes.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipFreezeResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freezes loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // COMPLETE FREEZE
    // =====================================================

    @PutMapping("/{freezeId}/complete")
    public ResponseEntity<
            ApiResponse<MembershipFreezeResponse>>
    completeFreeze(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer freezeId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipFreeze freeze =
                membershipFreezeService
                        .findById(
                                freezeId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership freeze not found."
                                )
                        );


        if (freeze.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership freeze does not belong to this tenant."
            );
        }


        MembershipFreeze completed =
                membershipFreezeService.completeFreeze(
                        freezeId
                );


        ApiResponse<MembershipFreezeResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freeze completed successfully.",
                        toResponse(completed)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // CANCEL FREEZE
    // =====================================================

    @PutMapping("/{freezeId}/cancel")
    public ResponseEntity<
            ApiResponse<MembershipFreezeResponse>>
    cancelFreeze(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer freezeId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipFreeze freeze =
                membershipFreezeService
                        .findById(
                                freezeId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership freeze not found."
                                )
                        );


        if (freeze.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership freeze does not belong to this tenant."
            );
        }


        MembershipFreeze cancelled =
                membershipFreezeService.cancelFreeze(
                        freezeId
                );


        ApiResponse<MembershipFreezeResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership freeze cancelled successfully.",
                        toResponse(cancelled)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipFreezeResponse toResponse(
            MembershipFreeze freeze) {

        MembershipFreezeResponse response =
                new MembershipFreezeResponse();

        response.setMembershipFreezeId(
                freeze.getMembershipFreezeId()
        );

        response.setTenantId(
                freeze.getTenant()
                        .getTenantId()
        );

        if (freeze.getBranch() != null) {

            response.setBranchId(
                    freeze.getBranch()
                            .getBranchId()
            );

            response.setBranchName(
                    freeze.getBranch()
                            .getBranchName()
            );
        }

        if (freeze.getMember() != null) {

            response.setMemberId(
                    freeze.getMember()
                            .getMemberId()
            );
        }

        if (freeze.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    freeze.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    freeze.getMemberMembership()
                            .getMembershipNumber()
            );
        }

        response.setFreezeStartDate(
                freeze.getFreezeStartDate()
        );

        response.setFreezeEndDate(
                freeze.getFreezeEndDate()
        );

        response.setFreezeDays(
                freeze.getFreezeDays()
        );

        response.setFreezeFee(
                freeze.getFreezeFee()
        );

        response.setReason(
                freeze.getReason()
        );

        response.setStatus(
                freeze.getStatus()
        );

        response.setNotes(
                freeze.getNotes()
        );

        response.setCreatedAt(
                freeze.getCreatedAt()
        );

        response.setUpdatedAt(
                freeze.getUpdatedAt()
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

    // =====================================================
// PROCESS EXPIRED FREEZES
// =====================================================

    @PutMapping("/process-expired")
    public ResponseEntity<
            ApiResponse<Integer>>
    processExpiredFreezes(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        int completedCount =
                membershipFreezeService
                        .processExpiredFreezes();


        ApiResponse<Integer>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Expired freezes processed successfully.",
                        completedCount
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }
}