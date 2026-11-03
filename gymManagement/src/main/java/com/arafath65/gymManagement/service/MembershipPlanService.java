package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MembershipPlanBenefitRequest;
import com.arafath65.gymManagement.dto.request.MembershipPlanBranchRequest;
import com.arafath65.gymManagement.dto.request.MembershipPlanRequest;
import com.arafath65.gymManagement.entity.Benefit;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBenefit;
import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.repository.BenefitRepository;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBenefitRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBranchRepository;
import com.arafath65.gymManagement.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    private final MembershipPlanBranchRepository
            membershipPlanBranchRepository;

    private final MembershipPlanBenefitRepository
            membershipPlanBenefitRepository;

    private final BenefitRepository
            benefitRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanService(
            MembershipPlanRepository membershipPlanRepository,
            MembershipPlanBranchRepository membershipPlanBranchRepository,
            MembershipPlanBenefitRepository membershipPlanBenefitRepository,
            BenefitRepository benefitRepository,
            BranchRepository branchRepository) {

        this.membershipPlanRepository =
                membershipPlanRepository;

        this.membershipPlanBranchRepository =
                membershipPlanBranchRepository;

        this.membershipPlanBenefitRepository =
                membershipPlanBenefitRepository;

        this.benefitRepository =
                benefitRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE
    // =====================================================

    public MembershipPlan create(
            MembershipPlanRequest request,
            Tenant tenant) {

        validateTenant(tenant);

        validateRequest(request);

        Integer tenantId =
                tenant.getTenantId();

        String planCode =
                normalizeCode(
                        request.getPlanCode()
                );


        // -------------------------------------------------
        // DUPLICATE PLAN CODE
        // -------------------------------------------------

        if (membershipPlanRepository
                .existsByTenant_TenantIdAndPlanCode(
                        tenantId,
                        planCode
                )) {

            throw new IllegalArgumentException(
                    "Membership plan code already exists."
            );
        }


        // -------------------------------------------------
        // CREATE PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                new MembershipPlan();

        plan.setTenant(tenant);

        plan.setPlanCode(planCode);

        plan.setPlanName(
                request.getPlanName().trim()
        );

        plan.setDescription(
                request.getDescription()
        );

        plan.setDurationType(
                MembershipPlan.DurationType.valueOf(
                        request.getDurationType()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setDurationValue(
                request.getDurationValue()
        );

        plan.setBillingType(
                MembershipPlan.BillingType.valueOf(
                        request.getBillingType()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setBillingFrequency(
                request.getBillingFrequency() == null
                        ? null
                        : MembershipPlan.BillingFrequency.valueOf(
                        request.getBillingFrequency()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setBillingFrequencyValue(
                request.getBillingFrequencyValue()
        );

        plan.setAccessBillingMode(
                MembershipPlan.AccessBillingMode.valueOf(
                        request.getAccessBillingMode()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setMonthlyActivationRequired(
                Boolean.TRUE.equals(
                        request.getMonthlyActivationRequired()
                )
        );

        plan.setDailyChargeEnabled(
                Boolean.TRUE.equals(
                        request.getDailyChargeEnabled()
                )
        );

        plan.setFreezeAllowed(
                Boolean.TRUE.equals(
                        request.getFreezeAllowed()
                )
        );

        plan.setMaxFreezeDays(
                request.getMaxFreezeDays()
        );

        plan.setFreezeFee(
                request.getFreezeFee()
        );

        plan.setAccessType(
                MembershipPlan.AccessType.valueOf(
                        request.getAccessType()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setStatus(true);


        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        validateDuration(plan);

        validateBilling(plan);

        validateFreeze(plan);


        // -------------------------------------------------
        // SAVE PLAN
        // -------------------------------------------------

        MembershipPlan savedPlan =
                membershipPlanRepository.save(
                        plan
                );


        // -------------------------------------------------
        // CREATE BRANCH ASSIGNMENTS
        // -------------------------------------------------

        createBranchAssignments(
                savedPlan,
                tenant,
                request
        );


        // -------------------------------------------------
        // CREATE INCLUDED BENEFITS
        // -------------------------------------------------

        createPlanBenefits(
                savedPlan,
                tenant,
                request.getBenefits()
        );


        return savedPlan;
    }


    // =====================================================
    // UPDATE
    // =====================================================

    public MembershipPlan update(
            Integer membershipPlanId,
            MembershipPlanRequest request,
            Tenant tenant) {

        validateTenant(tenant);

        validateRequest(request);


        if (membershipPlanId == null) {

            throw new IllegalArgumentException(
                    "Membership plan ID is required."
            );
        }


        // -------------------------------------------------
        // FIND PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                membershipPlanRepository.findById(
                        membershipPlanId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership plan not found."
                        )
                );


        // -------------------------------------------------
        // TENANT SECURITY
        // -------------------------------------------------

        if (plan.getTenant() == null
                || tenant.getTenantId()
                != plan.getTenant().getTenantId()) {

            throw new IllegalArgumentException(
                    "Membership plan does not belong to this tenant."
            );
        }


        String planCode =
                normalizeCode(
                        request.getPlanCode()
                );


        // -------------------------------------------------
        // DUPLICATE CODE
        // -------------------------------------------------

        if (!planCode.equalsIgnoreCase(
                plan.getPlanCode()
        )) {

            if (membershipPlanRepository
                    .existsByTenant_TenantIdAndPlanCode(
                            tenant.getTenantId(),
                            planCode
                    )) {

                throw new IllegalArgumentException(
                        "Membership plan code already exists."
                );
            }
        }


        // -------------------------------------------------
        // UPDATE PLAN
        // -------------------------------------------------

        plan.setPlanCode(planCode);

        plan.setPlanName(
                request.getPlanName().trim()
        );

        plan.setDescription(
                request.getDescription()
        );

        plan.setDurationType(
                MembershipPlan.DurationType.valueOf(
                        request.getDurationType()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setDurationValue(
                request.getDurationValue()
        );

        plan.setBillingType(
                MembershipPlan.BillingType.valueOf(
                        request.getBillingType()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setBillingFrequency(
                request.getBillingFrequency() == null
                        ? null
                        : MembershipPlan.BillingFrequency.valueOf(
                        request.getBillingFrequency()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setBillingFrequencyValue(
                request.getBillingFrequencyValue()
        );

        plan.setAccessBillingMode(
                MembershipPlan.AccessBillingMode.valueOf(
                        request.getAccessBillingMode()
                                .trim()
                                .toUpperCase()
                )
        );

        plan.setMonthlyActivationRequired(
                Boolean.TRUE.equals(
                        request.getMonthlyActivationRequired()
                )
        );

        plan.setDailyChargeEnabled(
                Boolean.TRUE.equals(
                        request.getDailyChargeEnabled()
                )
        );

        plan.setFreezeAllowed(
                Boolean.TRUE.equals(
                        request.getFreezeAllowed()
                )
        );

        plan.setMaxFreezeDays(
                request.getMaxFreezeDays()
        );

        plan.setFreezeFee(
                request.getFreezeFee()
        );

        plan.setAccessType(
                MembershipPlan.AccessType.valueOf(
                        request.getAccessType()
                                .trim()
                                .toUpperCase()
                )
        );


        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        validateDuration(plan);

        validateBilling(plan);

        validateFreeze(plan);


        // -------------------------------------------------
        // SAVE PLAN
        // -------------------------------------------------

        MembershipPlan savedPlan =
                membershipPlanRepository.save(
                        plan
                );


        // -------------------------------------------------
        // REPLACE BRANCH ASSIGNMENTS
        // -------------------------------------------------

        replaceBranchAssignments(
                savedPlan,
                tenant,
                request
        );


        // -------------------------------------------------
        // SYNCHRONIZE PLAN BENEFITS
        // -------------------------------------------------

        synchronizePlanBenefits(
                savedPlan,
                tenant,
                request.getBenefits()
        );


        return savedPlan;
    }


    // =====================================================
    // CREATE BRANCH ASSIGNMENTS
    // =====================================================

    private void createBranchAssignments(
            MembershipPlan plan,
            Tenant tenant,
            MembershipPlanRequest request) {

        Integer tenantId =
                tenant.getTenantId();

        String branchMode =
                normalizeBranchMode(
                        request.getBranchMode()
                );


        // =================================================
        // ALL BRANCHES
        // =================================================

        if ("ALL_BRANCHES".equals(branchMode)) {

            List<Branch> branches =
                    branchRepository
                            .findByTenant_TenantIdAndStatusTrue(
                                    tenantId
                            );


            if (branches == null
                    || branches.isEmpty()) {

                throw new IllegalArgumentException(
                        "No active branches are available for this tenant."
                );
            }


            for (Branch branch : branches) {

                MembershipPlanBranch assignment =
                        buildAssignment(
                                plan,
                                branch,
                                request.getDefaultPrice(),
                                request.getDefaultJoiningFee(),
                                request.getDefaultRenewalFee()
                        );


                plan.addBranchAssignment(
                        assignment
                );
            }
        }


        // =================================================
        // SELECTED BRANCHES
        // =================================================

        else if ("SELECTED_BRANCHES"
                .equals(branchMode)) {

            List<MembershipPlanBranchRequest>
                    branchRequests =
                    request.getBranches();


            validateSelectedBranches(
                    branchRequests
            );


            Set<Integer> branchIds =
                    new HashSet<>();


            for (
                    MembershipPlanBranchRequest
                            branchRequest
                    : branchRequests
            ) {

                Integer branchId =
                        branchRequest.getBranchId();


                if (branchId == null) {

                    throw new IllegalArgumentException(
                            "Branch ID is required."
                    );
                }


                if (!branchIds.add(branchId)) {

                    throw new IllegalArgumentException(
                            "Duplicate branch selected: "
                                    + branchId
                    );
                }


                Branch branch =
                        findTenantBranch(
                                tenantId,
                                branchId
                        );


                MembershipPlanBranch assignment =
                        buildAssignment(
                                plan,
                                branch,
                                branchRequest.getPrice(),
                                branchRequest.getJoiningFee(),
                                branchRequest.getRenewalFee()
                        );


                plan.addBranchAssignment(
                        assignment
                );
            }
        }


        // =================================================
        // INVALID MODE
        // =================================================

        else {

            throw new IllegalArgumentException(
                    "Invalid branch mode. Use ALL_BRANCHES or SELECTED_BRANCHES."
            );
        }


        membershipPlanRepository.save(
                plan
        );
    }


    // =====================================================
    // REPLACE BRANCH ASSIGNMENTS
    // =====================================================

    private void replaceBranchAssignments(
            MembershipPlan plan,
            Tenant tenant,
            MembershipPlanRequest request) {

        Integer tenantId =
                tenant.getTenantId();


        // -------------------------------------------------
        // FIND EXISTING ASSIGNMENTS
        // -------------------------------------------------

        List<MembershipPlanBranch>
                existingAssignments =
                membershipPlanBranchRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanId(
                                tenantId,
                                plan.getMembershipPlanId()
                        );


        // -------------------------------------------------
        // DELETE EXISTING ASSIGNMENTS
        // -------------------------------------------------

        if (existingAssignments != null
                && !existingAssignments.isEmpty()) {

            membershipPlanBranchRepository
                    .deleteAll(
                            existingAssignments
                    );

            membershipPlanBranchRepository.flush();
        }


        // -------------------------------------------------
        // CLEAR CURRENT ENTITY COLLECTION
        // -------------------------------------------------

        if (plan.getBranchAssignments() != null) {

            plan.getBranchAssignments().clear();
        }


        // -------------------------------------------------
        // CREATE NEW ASSIGNMENTS
        // -------------------------------------------------

        createBranchAssignments(
                plan,
                tenant,
                request
        );
    }


    // =====================================================
    // CREATE PLAN BENEFITS
    // =====================================================

    private void createPlanBenefits(
            MembershipPlan plan,
            Tenant tenant,
            List<MembershipPlanBenefitRequest>
                    benefitRequests) {

        if (benefitRequests == null
                || benefitRequests.isEmpty()) {

            return;
        }


        Integer tenantId =
                tenant.getTenantId();


        Set<Integer> benefitIds =
                new HashSet<>();


        for (
                MembershipPlanBenefitRequest
                        request
                : benefitRequests
        ) {

            if (request == null) {

                throw new IllegalArgumentException(
                        "Invalid membership plan benefit."
                );
            }


            Integer benefitId =
                    request.getBenefitId();


            if (benefitId == null) {

                throw new IllegalArgumentException(
                        "Benefit ID is required."
                );
            }


            // -------------------------------------------------
            // DUPLICATE BENEFIT
            // -------------------------------------------------

            if (!benefitIds.add(benefitId)) {

                throw new IllegalArgumentException(
                        "Duplicate benefit selected: "
                                + benefitId
                );
            }


            // -------------------------------------------------
            // LOAD BENEFIT
            // -------------------------------------------------

            Benefit benefit =
                    benefitRepository
                            .findById(
                                    benefitId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Benefit not found: "
                                                    + benefitId
                                    )
                            );


            // -------------------------------------------------
            // TENANT VALIDATION
            // -------------------------------------------------

            if (benefit.getTenant() == null
                    || benefit.getTenant()
                    .getTenantId()
                    != tenantId) {

                throw new IllegalArgumentException(
                        "Benefit does not belong to this tenant."
                );
            }


            // -------------------------------------------------
            // ACTIVE VALIDATION
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    benefit.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "Benefit is inactive: "
                                + benefit.getBenefitName()
                );
            }


            // -------------------------------------------------
            // PLAN BENEFIT
            // -------------------------------------------------

            MembershipPlanBenefit
                    planBenefit =
                    new MembershipPlanBenefit();


            planBenefit.setTenant(
                    tenant
            );

            planBenefit.setMembershipPlan(
                    plan
            );

            planBenefit.setBenefit(
                    benefit
            );

            /*
             * Benefits selected from the Membership Plan
             * Benefits tab are INCLUDED benefits.
             */
            planBenefit.setBenefitType(
                    MembershipPlanBenefit.BenefitType.INCLUDED
            );

            /*
             * Included benefits do not add a separate
             * charge to the membership plan.
             */
            planBenefit.setFee(
                    BigDecimal.ZERO
            );

            planBenefit.setStatus(
                    true
            );


            membershipPlanBenefitRepository.save(
                    planBenefit
            );
        }
    }


    // =====================================================
    // SYNCHRONIZE PLAN BENEFITS
    // =====================================================

    private void synchronizePlanBenefits(
            MembershipPlan plan,
            Tenant tenant,
            List<MembershipPlanBenefitRequest>
                    benefitRequests) {

        Integer tenantId =
                tenant.getTenantId();


        // -------------------------------------------------
        // LOAD EXISTING ACTIVE BENEFITS
        // -------------------------------------------------

        List<MembershipPlanBenefit>
                existingBenefits =
                membershipPlanBenefitRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndStatusTrue(
                                tenantId,
                                plan.getMembershipPlanId()
                        );


        Set<Integer> requestedBenefitIds =
                new HashSet<>();


        if (benefitRequests != null) {

            for (
                    MembershipPlanBenefitRequest
                            request
                    : benefitRequests
            ) {

                if (request == null
                        || request.getBenefitId() == null) {

                    throw new IllegalArgumentException(
                            "Benefit ID is required."
                    );
                }


                if (!requestedBenefitIds.add(
                        request.getBenefitId()
                )) {

                    throw new IllegalArgumentException(
                            "Duplicate benefit selected: "
                                    + request.getBenefitId()
                    );
                }
            }
        }


        // -------------------------------------------------
        // DEACTIVATE REMOVED BENEFITS
        // -------------------------------------------------

        for (
                MembershipPlanBenefit existing
                : existingBenefits
        ) {

            Integer existingBenefitId =
                    existing.getBenefit()
                            .getBenefitId();


            if (!requestedBenefitIds.contains(
                    existingBenefitId
            )) {

                existing.setStatus(false);

                membershipPlanBenefitRepository.save(
                        existing
                );
            }
        }


        // -------------------------------------------------
        // ADD NEW BENEFITS
        // -------------------------------------------------

        Set<Integer> existingBenefitIds =
                existingBenefits
                        .stream()
                        .map(existing ->
                                existing.getBenefit()
                                        .getBenefitId()
                        )
                        .collect(Collectors.toSet());


        if (benefitRequests == null) {

            return;
        }


        for (
                MembershipPlanBenefitRequest
                        request
                : benefitRequests
        ) {

            Integer benefitId =
                    request.getBenefitId();


            // -------------------------------------------------
            // ALREADY ACTIVE
            // -------------------------------------------------

            if (existingBenefitIds.contains(
                    benefitId
            )) {

                continue;
            }


            // -------------------------------------------------
            // LOAD BENEFIT
            // -------------------------------------------------

            Benefit benefit =
                    benefitRepository
                            .findById(
                                    benefitId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Benefit not found: "
                                                    + benefitId
                                    )
                            );


            // -------------------------------------------------
            // TENANT VALIDATION
            // -------------------------------------------------

            if (benefit.getTenant() == null
                    || benefit.getTenant()
                    .getTenantId()
                    != tenantId) {

                throw new IllegalArgumentException(
                        "Benefit does not belong to this tenant."
                );
            }


            // -------------------------------------------------
            // ACTIVE VALIDATION
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    benefit.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "Benefit is inactive: "
                                + benefit.getBenefitName()
                );
            }


            // -------------------------------------------------
            // CHECK EXISTING INACTIVE RECORD
            // -------------------------------------------------

            Optional<MembershipPlanBenefit>
                    existingRecord =
                    membershipPlanBenefitRepository
                            .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBenefit_BenefitId(
                                    tenantId,
                                    plan.getMembershipPlanId(),
                                    benefitId
                            );


            if (existingRecord.isPresent()) {

                MembershipPlanBenefit
                        planBenefit =
                        existingRecord.get();


                planBenefit.setBenefitType(
                        MembershipPlanBenefit.BenefitType.INCLUDED
                );

                planBenefit.setFee(
                        BigDecimal.ZERO
                );

                planBenefit.setStatus(
                        true
                );


                membershipPlanBenefitRepository.save(
                        planBenefit
                );


                continue;
            }


            // -------------------------------------------------
            // CREATE NEW RECORD
            // -------------------------------------------------

            MembershipPlanBenefit
                    planBenefit =
                    new MembershipPlanBenefit();


            planBenefit.setTenant(
                    tenant
            );

            planBenefit.setMembershipPlan(
                    plan
            );

            planBenefit.setBenefit(
                    benefit
            );

            planBenefit.setBenefitType(
                    MembershipPlanBenefit.BenefitType.INCLUDED
            );

            planBenefit.setFee(
                    BigDecimal.ZERO
            );

            planBenefit.setStatus(
                    true
            );


            membershipPlanBenefitRepository.save(
                    planBenefit
            );
        }
    }


    // =====================================================
    // BUILD BRANCH ASSIGNMENT
    // =====================================================

    private MembershipPlanBranch buildAssignment(
            MembershipPlan plan,
            Branch branch,
            BigDecimal price,
            BigDecimal joiningFee,
            BigDecimal renewalFee) {

        validateBranchPricing(
                price,
                joiningFee,
                renewalFee
        );


        MembershipPlanBranch assignment =
                new MembershipPlanBranch();


        assignment.setTenant(
                plan.getTenant()
        );

        assignment.setMembershipPlan(
                plan
        );

        assignment.setBranch(
                branch
        );

        assignment.setPrice(
                price
        );

        assignment.setJoiningFee(
                joiningFee
        );

        assignment.setRenewalFee(
                renewalFee
        );

        assignment.setStatus(true);


        return assignment;
    }


    // =====================================================
    // FIND TENANT BRANCH
    // =====================================================

    private Branch findTenantBranch(
            Integer tenantId,
            Integer branchId) {

        Branch branch =
                branchRepository.findById(
                        branchId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Branch not found: "
                                        + branchId
                        )
                );


        if (branch.getTenant() == null
                || tenantId.intValue()
                != branch.getTenant().getTenantId()) {

            throw new IllegalArgumentException(
                    "Selected branch does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                branch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Selected branch is inactive."
            );
        }


        return branch;
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MembershipPlan> findById(
            Integer membershipPlanId) {

        return membershipPlanRepository.findById(
                membershipPlanId
        );
    }


    // =====================================================
    // FIND BY TENANT
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlan> findByTenant(
            Integer tenantId) {

        return membershipPlanRepository
                .findByTenant_TenantIdOrderByPlanNameAsc(
                        tenantId
                );
    }


    // =====================================================
    // FIND BRANCH ASSIGNMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlanBranch>
    findBranchAssignments(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanBranchRepository
                .findByTenant_TenantIdAndBranch_BranchId(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND ACTIVE BRANCH ASSIGNMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlanBranch>
    findActiveBranchAssignments(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanBranchRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndMembershipPlan_StatusTrueAndStatusTrue(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND PLAN BRANCH ASSIGNMENT
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MembershipPlanBranch>
    findBranchAssignment(
            Integer tenantId,
            Integer membershipPlanId,
            Integer branchId) {

        if (tenantId == null
                || membershipPlanId == null
                || branchId == null) {

            return Optional.empty();
        }


        return membershipPlanBranchRepository
                .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
                        tenantId,
                        membershipPlanId,
                        branchId
                );
    }


    // =====================================================
    // FIND BY PLAN CODE
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MembershipPlan>
    findByPlanCode(
            Integer tenantId,
            String planCode) {

        if (planCode == null
                || planCode.trim().isEmpty()) {

            return Optional.empty();
        }


        return membershipPlanRepository
                .findByTenant_TenantIdAndPlanCode(
                        tenantId,
                        planCode.trim().toUpperCase()
                );
    }


    // =====================================================
    // FIND BRANCH ASSIGNMENT BY CODE
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MembershipPlanBranch>
    findBranchAssignmentByPlanCode(
            Integer tenantId,
            Integer branchId,
            String planCode) {

        Optional<MembershipPlan> plan =
                findByPlanCode(
                        tenantId,
                        planCode
                );


        if (plan.isEmpty()) {

            return Optional.empty();
        }


        return findBranchAssignment(
                tenantId,
                plan.get().getMembershipPlanId(),
                branchId
        );
    }


    // =====================================================
    // CHECK PLAN AVAILABLE FOR BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public boolean isPlanAvailableForBranch(
            Integer membershipPlanId,
            int tenantId,
            Integer branchId) {

        Optional<MembershipPlanBranch>
                assignment =
                findBranchAssignment(
                        tenantId,
                        membershipPlanId,
                        branchId
                );


        return assignment.isPresent()
                && Boolean.TRUE.equals(
                assignment.get().getStatus()
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    public MembershipPlan activate(
            Integer membershipPlanId) {

        MembershipPlan plan =
                membershipPlanRepository.findById(
                        membershipPlanId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership plan not found."
                        )
                );


        plan.setStatus(true);


        return membershipPlanRepository.save(
                plan
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    public MembershipPlan deactivate(
            Integer membershipPlanId) {

        MembershipPlan plan =
                membershipPlanRepository.findById(
                        membershipPlanId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership plan not found."
                        )
                );


        plan.setStatus(false);


        return membershipPlanRepository.save(
                plan
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Integer membershipPlanId) {

        if (membershipPlanId == null
                || !membershipPlanRepository
                .existsById(membershipPlanId)) {

            throw new IllegalArgumentException(
                    "Membership plan not found."
            );
        }


        membershipPlanRepository.deleteById(
                membershipPlanId
        );
    }


    // =====================================================
    // VALIDATE TENANT
    // =====================================================

    private void validateTenant(
            Tenant tenant) {

        if (tenant == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }
    }


    // =====================================================
    // VALIDATE REQUEST
    // =====================================================

    private void validateRequest(
            MembershipPlanRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Membership plan request cannot be null."
            );
        }


        if (request.getPlanCode() == null
                || request.getPlanCode()
                .trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Plan code is required."
            );
        }


        if (request.getPlanName() == null
                || request.getPlanName()
                .trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Plan name is required."
            );
        }


        if (request.getDurationType() == null) {

            throw new IllegalArgumentException(
                    "Duration type is required."
            );
        }


        if (request.getBillingType() == null) {

            throw new IllegalArgumentException(
                    "Billing type is required."
            );
        }


        if (request.getAccessBillingMode() == null) {

            throw new IllegalArgumentException(
                    "Access billing mode is required."
            );
        }


        if (request.getAccessType() == null) {

            throw new IllegalArgumentException(
                    "Access type is required."
            );


        }


        if (request.getFreezeFee() == null
                || request.getFreezeFee().signum() < 0) {

            throw new IllegalArgumentException(
                    "Freeze fee cannot be negative."
            );
        }


        String branchMode =
                normalizeBranchMode(
                        request.getBranchMode()
                );


        if (!"ALL_BRANCHES".equals(branchMode)
                && !"SELECTED_BRANCHES"
                .equals(branchMode)) {

            throw new IllegalArgumentException(
                    "Invalid branch mode. Use ALL_BRANCHES or SELECTED_BRANCHES."
            );
        }


        // -------------------------------------------------
        // ALL BRANCHES PRICING
        // -------------------------------------------------

        if ("ALL_BRANCHES".equals(branchMode)) {

            validateBranchPricing(
                    request.getDefaultPrice(),
                    request.getDefaultJoiningFee(),
                    request.getDefaultRenewalFee()
            );
        }


        // -------------------------------------------------
        // SELECTED BRANCHES
        // -------------------------------------------------

        if ("SELECTED_BRANCHES".equals(branchMode)) {

            validateSelectedBranches(
                    request.getBranches()
            );
        }
    }


    // =====================================================
    // VALIDATE SELECTED BRANCHES
    // =====================================================

    private void validateSelectedBranches(
            List<MembershipPlanBranchRequest>
                    branches) {

        if (branches == null
                || branches.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one branch must be selected."
            );
        }
    }


    // =====================================================
    // VALIDATE BRANCH PRICING
    // =====================================================

    private void validateBranchPricing(
            BigDecimal price,
            BigDecimal joiningFee,
            BigDecimal renewalFee) {

        if (price == null
                || price.signum() < 0) {

            throw new IllegalArgumentException(
                    "Branch plan price cannot be negative."
            );
        }


        if (joiningFee == null
                || joiningFee.signum() < 0) {

            throw new IllegalArgumentException(
                    "Branch joining fee cannot be negative."
            );
        }


        if (renewalFee == null
                || renewalFee.signum() < 0) {

            throw new IllegalArgumentException(
                    "Branch renewal fee cannot be negative."
            );
        }
    }


    // =====================================================
    // DURATION VALIDATION
    // =====================================================

    private void validateDuration(
            MembershipPlan plan) {

        if (plan.getDurationType()
                == MembershipPlan.DurationType.UNLIMITED) {

            plan.setDurationValue(null);

            return;
        }


        if (plan.getDurationValue() == null
                || plan.getDurationValue() <= 0) {

            throw new IllegalArgumentException(
                    "Duration value must be greater than zero."
            );
        }
    }


    // =====================================================
    // BILLING VALIDATION
    // =====================================================

    private void validateBilling(
            MembershipPlan plan) {

        if (plan.getBillingType()
                == MembershipPlan.BillingType.ONE_TIME) {

            plan.setBillingFrequency(null);

            plan.setBillingFrequencyValue(null);

            return;
        }


        if (plan.getBillingFrequency() == null) {

            throw new IllegalArgumentException(
                    "Billing frequency is required."
            );
        }


        if (plan.getBillingFrequencyValue() == null
                || plan.getBillingFrequencyValue() <= 0) {

            throw new IllegalArgumentException(
                    "Billing frequency value must be greater than zero."
            );
        }
    }


    // =====================================================
    // FREEZE VALIDATION
    // =====================================================

    private void validateFreeze(
            MembershipPlan plan) {

        if (!Boolean.TRUE.equals(
                plan.getFreezeAllowed()
        )) {

            plan.setMaxFreezeDays(0);

            plan.setFreezeFee(
                    BigDecimal.ZERO
            );

            return;
        }


        if (plan.getMaxFreezeDays() == null
                || plan.getMaxFreezeDays() < 0) {

            throw new IllegalArgumentException(
                    "Maximum freeze days cannot be negative."
            );
        }


        if (plan.getFreezeFee() == null
                || plan.getFreezeFee().signum() < 0) {

            throw new IllegalArgumentException(
                    "Freeze fee cannot be negative."
            );
        }
    }


    // =====================================================
    // NORMALIZE PLAN CODE
    // =====================================================

    private String normalizeCode(
            String code) {

        if (code == null) {
            return null;
        }

        return code
                .trim()
                .toUpperCase();
    }


    // =====================================================
    // NORMALIZE BRANCH MODE
    // =====================================================

    private String normalizeBranchMode(
            String branchMode) {

        if (branchMode == null) {
            return null;
        }

        return branchMode
                .trim()
                .toUpperCase();
    }


    // =====================================================
    // FIND PLANS BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlan> findByBranch(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanBranchRepository
                .findByTenant_TenantIdAndBranch_BranchId(
                        tenantId,
                        branchId
                )
                .stream()
                .filter(MembershipPlanBranch::getStatus)
                .map(MembershipPlanBranch::getMembershipPlan)
                .filter(plan ->
                        Boolean.TRUE.equals(
                                plan.getStatus()
                        )
                )
                .distinct()
                .collect(Collectors.toList());
    }


    // =====================================================
    // FIND ACTIVE PLANS BY TENANT
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlan> findActiveByTenant(
            Integer tenantId) {

        return membershipPlanRepository
                .findByTenant_TenantIdAndStatusTrueOrderByPlanNameAsc(
                        tenantId
                );
    }


    // =====================================================
    // FIND ACTIVE PLANS BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipPlan> findActiveByBranch(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanBranchRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndStatusTrue(
                        tenantId,
                        branchId
                )
                .stream()
                .map(MembershipPlanBranch::getMembershipPlan)
                .filter(plan ->
                        Boolean.TRUE.equals(
                                plan.getStatus()
                        )
                )
                .distinct()
                .collect(Collectors.toList());
    }
}