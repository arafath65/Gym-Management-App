package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipPlanRequest;
import com.arafath65.gymManagement.dto.response.MembershipPlanResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
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

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanController(
            MembershipPlanService membershipPlanService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository) {

        this.membershipPlanService =
                membershipPlanService;

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
        // BUILD ENTITY
        // -------------------------------------------------

        MembershipPlan plan =
                new MembershipPlan();

        plan.setTenant(tenant);

        plan.setPlanCode(
                request.getPlanCode()
        );

        plan.setPlanName(
                request.getPlanName()
        );

        plan.setDescription(
                request.getDescription()
        );

        plan.setDurationType(
                request.getDurationType()
        );

        plan.setDurationValue(
                request.getDurationValue()
        );

        plan.setPrice(
                request.getPrice()
        );

        plan.setJoiningFee(
                request.getJoiningFee()
        );

        plan.setRenewalFee(
                request.getRenewalFee()
        );

        plan.setBillingType(
                request.getBillingType()
        );

        plan.setBillingFrequency(
                request.getBillingFrequency()
        );

        plan.setBillingFrequencyValue(
                request.getBillingFrequencyValue()
        );

        plan.setFreezeAllowed(
                request.getFreezeAllowed()
        );

        plan.setMaxFreezeDays(
                request.getMaxFreezeDays()
        );

        plan.setFreezeFee(
                request.getFreezeFee()
        );

        plan.setAccessType(
                request.getAccessType()
        );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (request.getBranchId() != null) {

            Branch branch =
                    branchRepository.findById(
                            request.getBranchId()
                    ).orElseThrow(() ->
                            new BadRequestException(
                                    "Branch not found."
                            )
                    );

            if (branch.getTenant()
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

            plan.setBranch(branch);
        }


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        MembershipPlan savedPlan =
                membershipPlanService.save(
                        plan
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MembershipPlanResponse response =
                toResponse(savedPlan);


        ApiResponse<MembershipPlanResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plan created successfully.",
                        response
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET MEMBERSHIP PLANS
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

        List<MembershipPlanResponse> responses =
                plans.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipPlanResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plans loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        List<MembershipPlanResponse> responses =
                plans.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipPlanResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Active membership plans loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET MEMBERSHIP PLAN BY ID
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


        if (plan.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership plan does not belong to this tenant."
            );
        }


        MembershipPlanResponse response =
                toResponse(plan);


        ApiResponse<MembershipPlanResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plan loaded successfully.",
                        response
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // ACTIVATE PLAN
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


        MembershipPlan updated =
                membershipPlanService.activate(
                        membershipPlanId
                );


        ApiResponse<MembershipPlanResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plan activated successfully.",
                        toResponse(updated)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // DEACTIVATE PLAN
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


        membershipPlanService.deactivate(
                membershipPlanId
        );


        ApiResponse<Void>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plan deactivated successfully.",
                        null
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // DELETE PLAN
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


        membershipPlanService.delete(
                membershipPlanId
        );


        ApiResponse<Void>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership plan deleted successfully.",
                        null
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipPlanResponse toResponse(
            MembershipPlan plan) {

        MembershipPlanResponse response =
                new MembershipPlanResponse();

        response.setMembershipPlanId(
                plan.getMembershipPlanId()
        );

        response.setTenantId(
                plan.getTenant().getTenantId()
        );

        if (plan.getBranch() != null) {

            response.setBranchId(
                    plan.getBranch().getBranchId()
            );

            response.setBranchName(
                    plan.getBranch().getBranchName()
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

        response.setDurationType(
                plan.getDurationType()
        );

        response.setDurationValue(
                plan.getDurationValue()
        );

        response.setPrice(
                plan.getPrice()
        );

        response.setJoiningFee(
                plan.getJoiningFee()
        );

        response.setRenewalFee(
                plan.getRenewalFee()
        );

        response.setBillingType(
                plan.getBillingType()
        );

        response.setBillingFrequency(
                plan.getBillingFrequency()
        );

        response.setBillingFrequencyValue(
                plan.getBillingFrequencyValue()
        );

        response.setFreezeAllowed(
                plan.getFreezeAllowed()
        );

        response.setMaxFreezeDays(
                plan.getMaxFreezeDays()
        );

        response.setFreezeFee(
                plan.getFreezeFee()
        );

        response.setAccessType(
                plan.getAccessType()
        );

        response.setStatus(
                plan.getStatus()
        );

        response.setCreatedAt(
                plan.getCreatedAt()
        );

        response.setUpdatedAt(
                plan.getUpdatedAt()
        );

        return response;
    }


    // =====================================================
    // VALIDATE PLAN TENANT
    // =====================================================

    private void validatePlanTenant(
            MembershipPlan plan,
            Tenant tenant) {

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

        Branch branch =
                branchRepository.findById(
                        branchId
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Branch not found."
                        )
                );

        if (branch.getTenant()
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