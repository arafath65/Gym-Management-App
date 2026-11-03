package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipBillingCycleRequest;
import com.arafath65.gymManagement.dto.response.MembershipBillingCycleResponse;
import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MembershipBillingCycleService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-billing-cycles")
public class MembershipBillingCycleController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipBillingCycleService
            billingCycleService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipBillingCycleController(
            MembershipBillingCycleService billingCycleService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.billingCycleService =
                billingCycleService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE BILLING CYCLE
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MembershipBillingCycleResponse>>
    createCycle(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MembershipBillingCycleRequest request) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipBillingCycle cycle =
                billingCycleService.createCycle(
                        tenant.getTenantId(),
                        request
                );


        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Membership billing cycle created successfully.",
                        mapToResponse(cycle)
                ),
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{billingCycleId}")
    public ResponseEntity<
            ApiResponse<MembershipBillingCycleResponse>>
    getById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer billingCycleId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipBillingCycle cycle =
                billingCycleService
                        .findById(
                                tenant.getTenantId(),
                                billingCycleId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Billing cycle not found."
                                )
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership billing cycle loaded successfully.",
                        mapToResponse(cycle)
                )
        );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipBillingCycleResponse>>>
    getByMembership(

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


        List<MembershipBillingCycle> cycles =
                billingCycleService
                        .findByMembership(
                                tenant.getTenantId(),
                                memberMembershipId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership billing cycles loaded successfully.",
                        cycles.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList())
                )
        );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MembershipBillingCycleResponse>>>
    getByMember(

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


        List<MembershipBillingCycle> cycles =
                billingCycleService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member billing cycles loaded successfully.",
                        cycles.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList())
                )
        );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<
            ApiResponse<List<MembershipBillingCycleResponse>>>
    getByBranch(

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


        List<MembershipBillingCycle> cycles =
                billingCycleService
                        .findByBranch(
                                tenant.getTenantId(),
                                branchId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Branch billing cycles loaded successfully.",
                        cycles.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList())
                )
        );
    }


    // =====================================================
    // CURRENT CYCLE
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}/current")
    public ResponseEntity<
            ApiResponse<MembershipBillingCycleResponse>>
    getCurrentCycle(

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


        LocalDate today =
                LocalDate.now();


        MembershipBillingCycle cycle =
                billingCycleService
                        .findCurrentCycle(
                                tenant.getTenantId(),
                                memberMembershipId,
                                today
                        )
                        .orElse(null);


        if (cycle == null) {

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "No current billing cycle found.",
                            null
                    )
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Current billing cycle loaded successfully.",
                        mapToResponse(cycle)
                )
        );
    }


    // =====================================================
    // OUTSTANDING CYCLES
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}/outstanding")
    public ResponseEntity<
            ApiResponse<List<MembershipBillingCycleResponse>>>
    getOutstandingCycles(

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


        List<MembershipBillingCycle> cycles =
                billingCycleService
                        .findOutstandingCycles(
                                tenant.getTenantId(),
                                memberMembershipId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Outstanding billing cycles loaded successfully.",
                        cycles.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList())
                )
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @PatchMapping("/{billingCycleId}/activate")
    public ResponseEntity<
            ApiResponse<MembershipBillingCycleResponse>>
    activateCycle(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer billingCycleId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipBillingCycle cycle =
                billingCycleService.activateCycle(
                        tenant.getTenantId(),
                        billingCycleId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership billing cycle activated successfully.",
                        mapToResponse(cycle)
                )
        );
    }


    // =====================================================
    // CANCEL
    // =====================================================

    @PatchMapping("/{billingCycleId}/cancel")
    public ResponseEntity<
            ApiResponse<Void>>
    cancelCycle(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer billingCycleId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        billingCycleService.cancelCycle(
                tenant.getTenantId(),
                billingCycleId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership billing cycle cancelled successfully.",
                        null
                )
        );
    }


    // =====================================================
    // REFRESH STATUS
    // =====================================================

    @PatchMapping("/{billingCycleId}/refresh-status")
    public ResponseEntity<
            ApiResponse<MembershipBillingCycleResponse>>
    refreshStatus(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer billingCycleId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipBillingCycle cycle =
                billingCycleService.refreshStatus(
                        tenant.getTenantId(),
                        billingCycleId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Billing cycle status refreshed successfully.",
                        mapToResponse(cycle)
                )
        );
    }


    // =====================================================
    // EXPIRE COMPLETED CYCLES
    // =====================================================

    @PostMapping("/expire-completed")
    public ResponseEntity<
            ApiResponse<Integer>>
    expireCompletedCycles(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        int count =
                billingCycleService
                        .expireCompletedCycles(
                                tenant.getTenantId(),
                                LocalDate.now()
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Completed billing cycles processed successfully.",
                        count
                )
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{billingCycleId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deleteCycle(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer billingCycleId) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        billingCycleService.delete(
                tenant.getTenantId(),
                billingCycleId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership billing cycle deleted successfully.",
                        null
                )
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipBillingCycleResponse mapToResponse(
            MembershipBillingCycle cycle) {

        MembershipBillingCycleResponse response =
                new MembershipBillingCycleResponse();


        // -------------------------------------------------
        // ID
        // -------------------------------------------------

        response.setMembershipBillingCycleId(
                cycle.getMembershipBillingCycleId()
        );


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (cycle.getTenant() != null) {

            response.setTenantId(
                    cycle.getTenant().getTenantId()
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (cycle.getBranch() != null) {

            response.setBranchId(
                    cycle.getBranch().getBranchId()
            );

            response.setBranchName(
                    cycle.getBranch().getBranchName()
            );
        }


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        if (cycle.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    cycle.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    cycle.getMemberMembership()
                            .getMembershipNumber()
            );


            if (cycle.getMemberMembership()
                    .getMember() != null) {

                response.setMemberId(
                        cycle.getMemberMembership()
                                .getMember()
                                .getMemberId()
                );
            }
        }


        // -------------------------------------------------
        // CYCLE
        // -------------------------------------------------

        response.setCycleNumber(
                cycle.getCycleNumber()
        );

        response.setBillingMode(
                cycle.getBillingMode()
        );

        response.setPeriodStart(
                cycle.getPeriodStart()
        );

        response.setPeriodEnd(
                cycle.getPeriodEnd()
        );

        response.setActivatedDate(
                cycle.getActivatedDate()
        );


        // -------------------------------------------------
        // FINANCIAL
        // -------------------------------------------------

        response.setBaseAmount(
                cycle.getBaseAmount()
        );

        response.setDiscountAmount(
                cycle.getDiscountAmount()
        );

        response.setTotalAmount(
                cycle.getTotalAmount()
        );

        response.setPaidAmount(
                cycle.getPaidAmount()
        );

        response.setBalanceAmount(
                cycle.getBalanceAmount()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        response.setStatus(
                cycle.getStatus()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                cycle.getNotes()
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                cycle.getCreatedAt()
        );

        response.setUpdatedAt(
                cycle.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {

        if (tenantNo == null
                || tenantNo.isBlank()) {

            throw new BadRequestException(
                    "Tenant number is required."
            );
        }


        if (deviceToken == null
                || deviceToken.isBlank()) {

            throw new BadRequestException(
                    "Device token is required."
            );
        }


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