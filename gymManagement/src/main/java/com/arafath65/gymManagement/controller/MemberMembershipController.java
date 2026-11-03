package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MemberMembershipPlanRequest;
import com.arafath65.gymManagement.dto.request.MemberMembershipRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MemberRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipService;
import com.arafath65.gymManagement.service.MembershipPlanService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member-memberships")
public class MemberMembershipController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MemberMembershipService memberMembershipService;

    private final MembershipPlanService membershipPlanService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository registeredDeviceRepository;

    private final BranchRepository branchRepository;

    private final MemberRepository memberRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipController(
            MemberMembershipService memberMembershipService,
            MembershipPlanService membershipPlanService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository,
            MemberRepository memberRepository) {

        this.memberMembershipService =
                memberMembershipService;

        this.membershipPlanService =
                membershipPlanService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.branchRepository =
                branchRepository;

        this.memberRepository =
                memberRepository;
    }


    // =====================================================
    // CREATE MEMBERSHIP
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MemberMembershipResponse>>
    createMembership(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MemberMembershipRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // VALIDATE BRANCH
        // -------------------------------------------------

        Branch branch =
                branchRepository.findById(
                        request.getBranchId()
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Branch not found."
                        )
                );


        validateBranch(
                tenant,
                branch
        );


        // -------------------------------------------------
        // VALIDATE MEMBER
        // -------------------------------------------------

        Member member =
                memberRepository.findById(
                        request.getMemberId()
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Member not found."
                        )
                );


        if (member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        if (member.getBranch() == null
                || member.getBranch()
                .getBranchId()
                != branch.getBranchId()) {

            throw new BadRequestException(
                    "Member does not belong to the selected branch."
            );
        }


        // -------------------------------------------------
        // VALIDATE MEMBERSHIP PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                membershipPlanService
                        .findById(
                                request.getMembershipPlanId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan not found."
                                )
                        );


        // -------------------------------------------------
        // PLAN → TENANT
        // -------------------------------------------------

        if (plan.getTenant() == null
                || plan.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership plan does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // PLAN STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                plan.getStatus()
        )) {

            throw new BadRequestException(
                    "Membership plan is inactive."
            );
        }


        // -------------------------------------------------
        // PLAN → BRANCH
        // -------------------------------------------------

        MembershipPlanBranch planBranch =
                membershipPlanService
                        .findBranchAssignment(
                                tenant.getTenantId(),
                                plan.getMembershipPlanId(),
                                branch.getBranchId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership plan is not available for this branch."
                                )
                        );


        // -------------------------------------------------
        // PLAN BRANCH STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                planBranch.getStatus()
        )) {

            throw new BadRequestException(
                    "Membership plan is inactive for this branch."
            );
        }


        // -------------------------------------------------
        // BUILD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                new MemberMembership();


        membership.setTenant(
                tenant
        );


        membership.setBranch(
                branch
        );


        membership.setMember(
                member
        );


        membership.setMembershipPlan(
                plan
        );


        membership.setStartDate(
                request.getStartDate()
        );


        // -------------------------------------------------
        // BRANCH-SPECIFIC PRICING
        // -------------------------------------------------

        membership.setPlanPrice(
                planBranch.getPrice()
        );


        membership.setJoiningFee(
                planBranch.getJoiningFee()
        );


        membership.setRenewalFee(
                planBranch.getRenewalFee()
        );


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        membership.setDiscountAmount(
                request.getDiscountAmount()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        membership.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        MemberMembership savedMembership =
                memberMembershipService.create(
                        membership
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MemberMembershipResponse response =
                toResponse(
                        savedMembership
                );


        ApiResponse<MemberMembershipResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership created successfully.",
                        response
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET MEMBERSHIPS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MemberMembershipResponse>>>
    getMemberships(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(required = false)
            Integer memberId,

            @RequestParam(required = false)
            Integer branchId,

            @RequestParam(required = false)
            Boolean status) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MemberMembership> memberships;


        // -------------------------------------------------
        // MEMBER + BRANCH
        // -------------------------------------------------

        if (memberId != null
                && branchId != null) {

            validateBranch(
                    tenant,
                    branchId
            );


            memberships =
                    memberMembershipService
                            .findByMemberAndBranch(
                                    tenant.getTenantId(),
                                    branchId,
                                    memberId
                            );
        }


        // -------------------------------------------------
        // MEMBER ONLY
        // -------------------------------------------------

        else if (memberId != null) {

            memberships =
                    memberMembershipService
                            .findByMember(
                                    tenant.getTenantId(),
                                    memberId
                            );
        }


        // -------------------------------------------------
        // BRANCH + STATUS
        // -------------------------------------------------

        else if (branchId != null
                && status != null) {

            validateBranch(
                    tenant,
                    branchId
            );


            memberships =
                    memberMembershipService
                            .findByBranchAndStatus(
                                    tenant.getTenantId(),
                                    branchId,
                                    status
                            );
        }


        // -------------------------------------------------
        // STATUS ONLY
        // -------------------------------------------------

        else if (status != null) {

            memberships =
                    memberMembershipService
                            .findByStatus(
                                    tenant.getTenantId(),
                                    status
                            );
        }


        // -------------------------------------------------
        // BRANCH ONLY
        // -------------------------------------------------

        else if (branchId != null) {

            validateBranch(
                    tenant,
                    branchId
            );


            memberships =
                    memberMembershipService
                            .findByBranchAndStatus(
                                    tenant.getTenantId(),
                                    branchId,
                                    true
                            );
        }


        // -------------------------------------------------
        // ALL
        // -------------------------------------------------

        else {

            memberships =
                    memberMembershipService
                            .findByTenant(
                                    tenant.getTenantId()
                            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        List<MemberMembershipResponse> responses =
                memberships.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MemberMembershipResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Memberships loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET MEMBERSHIPS BY STATE
    // =====================================================

    @GetMapping("/state/{membershipState}")
    public ResponseEntity<
            ApiResponse<List<MemberMembershipResponse>>>
    getMembershipsByState(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            MemberMembership.MembershipState membershipState) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<MemberMembership> memberships =
                memberMembershipService
                        .findByMembershipState(
                                tenant.getTenantId(),
                                membershipState
                        );


        List<MemberMembershipResponse> responses =
                memberships.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MemberMembershipResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Memberships loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET MEMBERSHIPS BY BRANCH AND STATE
    // =====================================================

    @GetMapping(
            "/branch/{branchId}/state/{membershipState}"
    )
    public ResponseEntity<
            ApiResponse<List<MemberMembershipResponse>>>
    getBranchMembershipsByState(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer branchId,

            @PathVariable
            MemberMembership.MembershipState membershipState) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        validateBranch(
                tenant,
                branchId
        );


        List<MemberMembership> memberships =
                memberMembershipService
                        .findByBranchAndMembershipState(
                                tenant.getTenantId(),
                                branchId,
                                membershipState
                        );


        List<MemberMembershipResponse> responses =
                memberships.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MemberMembershipResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branch memberships loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET MEMBERSHIP BY ID
    // =====================================================

    @GetMapping("/{membershipId}")
    public ResponseEntity<
            ApiResponse<MemberMembershipResponse>>
    getMembershipById(

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


        if (membership.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }


        ApiResponse<MemberMembershipResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership loaded successfully.",
                        toResponse(membership)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET MEMBER MEMBERSHIPS
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MemberMembershipResponse>>>
    getMemberMemberships(

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


        List<MemberMembership> memberships =
                memberMembershipService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        List<MemberMembershipResponse> responses =
                memberships.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MemberMembershipResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Member memberships loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }

    // =====================================================
// ASSIGN / CHANGE MEMBERSHIP PLAN
// =====================================================

    @PutMapping("/{membershipId}/plan")
    public ResponseEntity<
            ApiResponse<MemberMembershipResponse>>
    assignMembershipPlan(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipId,

            @Valid
            @RequestBody
            MemberMembershipPlanRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // ASSIGN PLAN
        // -------------------------------------------------

        MemberMembership updatedMembership =
                memberMembershipService
                        .assignMembershipPlan(
                                tenant.getTenantId(),
                                membershipId,
                                request.getMembershipPlanId(),
                                request.getStartDate(),
                                request.getSettlementType(),
                                request.getSettlementAmount(),
                                request.getSettlementReason(),
                                request.getNotes()
                        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership plan assigned successfully.",
                        toResponse(
                                updatedMembership
                        )
                )
        );
    }


    // =====================================================
    // DEACTIVATE MEMBERSHIP
    // =====================================================

    @PutMapping("/{membershipId}/deactivate")
    public ResponseEntity<
            ApiResponse<MemberMembershipResponse>>
    deactivateMembership(

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


        if (membership.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }


        MemberMembership updated =
                memberMembershipService.deactivate(
                        membershipId
                );


        ApiResponse<MemberMembershipResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership deactivated successfully.",
                        toResponse(updated)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // PROCESS EXPIRED MEMBERSHIPS
    // =====================================================

    @PutMapping("/process-expired")
    public ResponseEntity<
            ApiResponse<Integer>>
    processExpiredMemberships(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken) {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        int updatedCount =
                memberMembershipService
                        .updateExpiredMemberships(
                                tenant.getTenantId()
                        );


        ApiResponse<Integer>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Expired memberships processed successfully.",
                        updatedCount
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // CANCEL MEMBERSHIP
    // =====================================================

    @PutMapping("/{membershipId}/cancel")
    public ResponseEntity<
            ApiResponse<MemberMembershipResponse>>
    cancelMembership(

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


        if (membership.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }


        MemberMembership cancelled =
                memberMembershipService.cancel(
                        membershipId
                );


        ApiResponse<MemberMembershipResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership cancelled successfully.",
                        toResponse(cancelled)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MemberMembershipResponse toResponse(
            MemberMembership membership) {

        MemberMembershipResponse response =
                new MemberMembershipResponse();


        response.setMemberMembershipId(
                membership.getMemberMembershipId()
        );


        response.setTenantId(
                membership.getTenant()
                        .getTenantId()
        );


        if (membership.getBranch() != null) {

            response.setBranchId(
                    membership.getBranch()
                            .getBranchId()
            );

            response.setBranchName(
                    membership.getBranch()
                            .getBranchName()
            );
        }


        if (membership.getMember() != null) {

            response.setMemberId(
                    membership.getMember()
                            .getMemberId()
            );
        }


        if (membership.getMembershipPlan() != null) {

            response.setMembershipPlanId(
                    membership.getMembershipPlan()
                            .getMembershipPlanId()
            );

            response.setPlanCode(
                    membership.getMembershipPlan()
                            .getPlanCode()
            );

            response.setPlanName(
                    membership.getMembershipPlan()
                            .getPlanName()
            );
        }


        response.setMembershipNumber(
                membership.getMembershipNumber()
        );


        response.setStartDate(
                membership.getStartDate()
        );


        response.setEndDate(
                membership.getEndDate()
        );


        response.setPlanPrice(
                membership.getPlanPrice()
        );


        response.setJoiningFee(
                membership.getJoiningFee()
        );


        response.setRenewalFee(
                membership.getRenewalFee()
        );


        response.setDiscountAmount(
                membership.getDiscountAmount()
        );


        response.setTotalAmount(
                membership.getTotalAmount()
        );


        response.setPaidAmount(
                membership.getPaidAmount()
        );


        response.setBalanceAmount(
                membership.getBalanceAmount()
        );


        response.setStatus(
                membership.getStatus()
        );


        response.setMembershipState(
                membership.getMembershipState()
        );


        response.setTotalFreezeDays(
                membership.getTotalFreezeDays()
        );


        response.setRenewalCount(
                membership.getRenewalCount()
        );


        response.setNotes(
                membership.getNotes()
        );


        response.setCreatedAt(
                membership.getCreatedAt()
        );


        response.setUpdatedAt(
                membership.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // VALIDATE BRANCH BY ID
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


        validateBranch(
                tenant,
                branch
        );
    }


    // =====================================================
    // VALIDATE BRANCH
    // =====================================================

    private void validateBranch(
            Tenant tenant,
            Branch branch) {

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