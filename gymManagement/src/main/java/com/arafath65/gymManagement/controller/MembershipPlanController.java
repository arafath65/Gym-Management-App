package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipPlanRequest;
import com.arafath65.gymManagement.dto.response.MembershipPlanBenefitResponse;
import com.arafath65.gymManagement.dto.response.MembershipPlanBranchResponse;
import com.arafath65.gymManagement.dto.response.MembershipPlanResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MembershipPlanBenefitService;
import com.arafath65.gymManagement.service.MembershipPlanService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipPlanService membershipPlanService;

    private final MembershipPlanBenefitService
            membershipPlanBenefitService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanController(
            MembershipPlanService membershipPlanService,
            MembershipPlanBenefitService membershipPlanBenefitService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository) {

        this.membershipPlanService =
                membershipPlanService;

        this.membershipPlanBenefitService =
                membershipPlanBenefitService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE MEMBERSHIP PLAN
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MembershipPlanResponse>>
    createPlan(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MembershipPlanRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CREATE THROUGH SERVICE
        // -------------------------------------------------

        MembershipPlan savedPlan;

        try {

            savedPlan =
                    membershipPlanService.create(
                            request,
                            tenant
                    );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MembershipPlanResponse response =
                toResponse(
                        savedPlan
                );


        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Membership plan created successfully.",
                        response
                ),
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // UPDATE MEMBERSHIP PLAN
    // =====================================================

    @PutMapping("/{membershipPlanId}")
    public ResponseEntity<
            ApiResponse<MembershipPlanResponse>>
    updatePlan(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanId,

            @Valid
            @RequestBody
            MembershipPlanRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // UPDATE THROUGH SERVICE
        // -------------------------------------------------

        MembershipPlan updatedPlan;

        try {

            updatedPlan =
                    membershipPlanService.update(
                            membershipPlanId,
                            request,
                            tenant
                    );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MembershipPlanResponse response =
                toResponse(
                        updatedPlan
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan updated successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ALL MEMBERSHIP PLANS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MembershipPlanResponse>>>
    getPlans(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(required = false)
            Integer branchId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // GET PLANS
        // -------------------------------------------------

        List<MembershipPlan> plans;

        if (branchId == null) {

            plans =
                    membershipPlanService.findByTenant(
                            tenant.getTenantId()
                    );

        } else {

            validateBranch(
                    tenant,
                    branchId
            );

            plans =
                    membershipPlanService.findByBranch(
                            tenant.getTenantId(),
                            branchId
                    );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        List<MembershipPlanResponse>
                responses =
                plans.stream()
                        .map(this::toResponse)
                        .collect(
                                Collectors.toList()
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plans loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET ACTIVE MEMBERSHIP PLANS
    // =====================================================

    @GetMapping("/active")
    public ResponseEntity<
            ApiResponse<List<MembershipPlanResponse>>>
    getActivePlans(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(required = false)
            Integer branchId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // GET ACTIVE PLANS
        // -------------------------------------------------

        List<MembershipPlan> plans;

        if (branchId == null) {

            plans =
                    membershipPlanService
                            .findActiveByTenant(
                                    tenant.getTenantId()
                            );

        } else {

            validateBranch(
                    tenant,
                    branchId
            );

            plans =
                    membershipPlanService
                            .findActiveByBranch(
                                    tenant.getTenantId(),
                                    branchId
                            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        List<MembershipPlanResponse>
                responses =
                plans.stream()
                        .map(this::toResponse)
                        .collect(
                                Collectors.toList()
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active membership plans loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET PLAN BY ID
    // =====================================================

    @GetMapping("/{membershipPlanId}")
    public ResponseEntity<
            ApiResponse<MembershipPlanResponse>>
    getPlanById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipPlanId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                membershipPlanService
                        .findById(
                                membershipPlanId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan not found."
                                )
                        );


        // -------------------------------------------------
        // TENANT SECURITY
        // -------------------------------------------------

        validatePlanTenant(
                plan,
                tenant
        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan loaded successfully.",
                        toResponse(plan)
                )
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @PutMapping("/{membershipPlanId}/activate")
    public ResponseEntity<
            ApiResponse<MembershipPlanResponse>>
    activatePlan(

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


        MembershipPlan plan =
                membershipPlanService
                        .findById(
                                membershipPlanId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan not found."
                                )
                        );


        validatePlanTenant(
                plan,
                tenant
        );


        MembershipPlan updatedPlan;

        try {

            updatedPlan =
                    membershipPlanService.activate(
                            membershipPlanId
                    );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan activated successfully.",
                        toResponse(updatedPlan)
                )
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @PutMapping("/{membershipPlanId}/deactivate")
    public ResponseEntity<
            ApiResponse<Void>>
    deactivatePlan(

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


        MembershipPlan plan =
                membershipPlanService
                        .findById(
                                membershipPlanId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan not found."
                                )
                        );


        validatePlanTenant(
                plan,
                tenant
        );


        try {

            membershipPlanService.deactivate(
                    membershipPlanId
            );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan deactivated successfully.",
                        null
                )
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{membershipPlanId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deletePlan(

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


        MembershipPlan plan =
                membershipPlanService
                        .findById(
                                membershipPlanId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan not found."
                                )
                        );


        validatePlanTenant(
                plan,
                tenant
        );


        try {

            membershipPlanService.delete(
                    membershipPlanId
            );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan deleted successfully.",
                        null
                )
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipPlanResponse toResponse(
            MembershipPlan plan) {

        MembershipPlanResponse response =
                new MembershipPlanResponse();


        // -------------------------------------------------
        // BASIC
        // -------------------------------------------------

        response.setMembershipPlanId(
                plan.getMembershipPlanId()
        );


        if (plan.getTenant() != null) {

            response.setTenantId(
                    plan.getTenant()
                            .getTenantId()
            );
        }


        response.setPlanCode(
                plan.getPlanCode()
        );

        response.setPlanName(
                plan.getPlanName()
        );

        response.setDescription(
                plan.getDescription()
        );


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        response.setDurationType(
                plan.getDurationType()
        );

        response.setDurationValue(
                plan.getDurationValue()
        );


        // -------------------------------------------------
        // BILLING
        // -------------------------------------------------

        response.setBillingType(
                plan.getBillingType()
        );

        response.setBillingFrequency(
                plan.getBillingFrequency()
        );

        response.setBillingFrequencyValue(
                plan.getBillingFrequencyValue()
        );


        // -------------------------------------------------
        // ACCESS BILLING
        // -------------------------------------------------

        response.setAccessBillingMode(
                plan.getAccessBillingMode()
        );


        // -------------------------------------------------
        // MONTHLY / DAILY
        // -------------------------------------------------

        response.setMonthlyActivationRequired(
                plan.getMonthlyActivationRequired()
        );

        response.setDailyChargeEnabled(
                plan.getDailyChargeEnabled()
        );


        // -------------------------------------------------
        // FREEZE
        // -------------------------------------------------

        response.setFreezeAllowed(
                plan.getFreezeAllowed()
        );

        response.setMaxFreezeDays(
                plan.getMaxFreezeDays()
        );

        response.setFreezeFee(
                plan.getFreezeFee()
        );


        // -------------------------------------------------
        // ACCESS
        // -------------------------------------------------

        response.setAccessType(
                plan.getAccessType()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        response.setStatus(
                plan.getStatus()
        );


        // -------------------------------------------------
        // BRANCH ASSIGNMENTS
        // -------------------------------------------------

        if (plan.getBranchAssignments() != null) {

            List<MembershipPlanBranchResponse>
                    branchResponses =
                    plan.getBranchAssignments()
                            .stream()
                            .map(
                                    this::toBranchResponse
                            )
                            .collect(
                                    Collectors.toList()
                            );

            response.setBranchAssignments(
                    branchResponses
            );
        }


        // -------------------------------------------------
        // INCLUDED BENEFITS
        // -------------------------------------------------

        if (plan.getMembershipPlanId() != null
                && plan.getTenant() != null) {

            List<MembershipPlanBenefitResponse>
                    benefitResponses =
                    membershipPlanBenefitService
                            .getPlanBenefits(
                                    plan.getTenant()
                                            .getTenantId(),
                                    plan.getMembershipPlanId()
                            )
                            .stream()
                            .filter(
                                    benefit ->
                                            benefit != null
                                                    && Boolean.TRUE.equals(
                                                    benefit.getStatus()
                                            )
                            )
                            .collect(
                                    Collectors.toList()
                            );


            response.setBenefits(
                    benefitResponses
            );
        }


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                plan.getCreatedAt()
        );

        response.setUpdatedAt(
                plan.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // BRANCH ASSIGNMENT → RESPONSE
    // =====================================================

    private MembershipPlanBranchResponse
    toBranchResponse(
            MembershipPlanBranch assignment) {

        MembershipPlanBranchResponse response =
                new MembershipPlanBranchResponse();


        response.setMembershipPlanBranchId(
                assignment
                        .getMembershipPlanBranchId()
        );


        if (assignment.getBranch() != null) {

            Branch branch =
                    assignment.getBranch();


            response.setBranchId(
                    branch.getBranchId()
            );

            response.setBranchNo(
                    branch.getBranchNo()
            );

            response.setBranchName(
                    branch.getBranchName()
            );
        }


        response.setPrice(
                assignment.getPrice()
        );

        response.setJoiningFee(
                assignment.getJoiningFee()
        );

        response.setRenewalFee(
                assignment.getRenewalFee()
        );

        response.setStatus(
                assignment.getStatus()
        );

        response.setCreatedAt(
                assignment.getCreatedAt()
        );

        response.setUpdatedAt(
                assignment.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // VALIDATE PLAN TENANT
    // =====================================================

    private void validatePlanTenant(
            MembershipPlan plan,
            Tenant tenant) {

        if (plan.getTenant() == null) {

            throw new BadRequestException(
                    "Membership plan tenant is missing."
            );
        }


        if (plan.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership plan does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // VALIDATE BRANCH
    // =====================================================

    private void validateBranch(
            Tenant tenant,
            Integer branchId) {

        if (branchId == null) {

            throw new BadRequestException(
                    "Branch ID is required."
            );
        }


        Branch branch =
                branchRepository
                        .findById(
                                branchId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Branch not found."
                                )
                        );


        if (branch.getTenant() == null
                || branch.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                branch.getStatus()
        )) {

            throw new BadRequestException(
                    "Branch is inactive."
            );
        }
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            throw new BadRequestException(
                    "Tenant number is required."
            );
        }


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