package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MemberMembershipBenefit;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBenefit;
import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import com.arafath65.gymManagement.repository.MemberMembershipBenefitRepository;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBenefitRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBranchRepository;
import com.arafath65.gymManagement.repository.MembershipPlanRepository;
import com.arafath65.gymManagement.dto.request.MemberMembershipPlanRequest;
import com.arafath65.gymManagement.entity.MembershipChangeSettlement;
import com.arafath65.gymManagement.repository.MembershipChangeSettlementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberMembershipService {

    private final MemberMembershipRepository
            memberMembershipRepository;

    private final MembershipPlanRepository
            membershipPlanRepository;

    private final MembershipPlanBranchRepository
            membershipPlanBranchRepository;

    private final MembershipPlanBenefitRepository
            membershipPlanBenefitRepository;

    private final MemberMembershipBenefitRepository
            memberMembershipBenefitRepository;

    private final MembershipChangeSettlementRepository
            membershipChangeSettlementRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipService(
            MemberMembershipRepository
                    memberMembershipRepository,

            MembershipPlanRepository
                    membershipPlanRepository,

            MembershipPlanBranchRepository
                    membershipPlanBranchRepository,

            MembershipPlanBenefitRepository
                    membershipPlanBenefitRepository,

            MemberMembershipBenefitRepository
                    memberMembershipBenefitRepository,

            MembershipChangeSettlementRepository
                    membershipChangeSettlementRepository) {

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.membershipPlanRepository =
                membershipPlanRepository;

        this.membershipPlanBranchRepository =
                membershipPlanBranchRepository;

        this.membershipPlanBenefitRepository =
                membershipPlanBenefitRepository;

        this.memberMembershipBenefitRepository =
                memberMembershipBenefitRepository;

        this.membershipChangeSettlementRepository =
                membershipChangeSettlementRepository;
    }


    // =====================================================
    // CREATE MEMBERSHIP
    // =====================================================

    public MemberMembership create(
            MemberMembership membership) {

        if (membership == null) {

            throw new IllegalArgumentException(
                    "Membership cannot be null."
            );
        }


        // -------------------------------------------------
        // BASIC VALIDATION
        // -------------------------------------------------

        if (membership.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }


        if (membership.getBranch() == null) {

            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }


        if (membership.getMember() == null) {

            throw new IllegalArgumentException(
                    "Member is required."
            );
        }


        if (membership.getMembershipPlan() == null) {

            throw new IllegalArgumentException(
                    "Membership plan is required."
            );
        }


        // -------------------------------------------------
        // TENANT / BRANCH
        // -------------------------------------------------

        int tenantId =
                membership.getTenant()
                        .getTenantId();

        int branchId =
                membership.getBranch()
                        .getBranchId();


        // -------------------------------------------------
        // MEMBERSHIP NUMBER
        // -------------------------------------------------

        String membershipNumber =
                membership.getMembershipNumber();


        if (membershipNumber == null
                || membershipNumber.trim().isEmpty()) {

            membershipNumber =
                    "TEMP-" + System.nanoTime();
        }


        membershipNumber =
                membershipNumber
                        .trim()
                        .toUpperCase();


        membership.setMembershipNumber(
                membershipNumber
        );


        // -------------------------------------------------
        // DUPLICATE MEMBERSHIP NUMBER
        // -------------------------------------------------

        if (!membershipNumber.startsWith("TEMP-")
                && memberMembershipRepository
                .existsByTenant_TenantIdAndBranch_BranchIdAndMembershipNumber(
                        tenantId,
                        branchId,
                        membershipNumber
                )) {

            throw new IllegalArgumentException(
                    "Membership number already exists."
            );
        }


        // -------------------------------------------------
        // LOAD PLAN
        // -------------------------------------------------

        Integer planId =
                membership.getMembershipPlan()
                        .getMembershipPlanId();


        if (planId == null) {

            throw new IllegalArgumentException(
                    "Membership plan ID is required."
            );
        }


        MembershipPlan plan =
                membershipPlanRepository
                        .findById(
                                planId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership plan not found."
                                )
                        );


        // -------------------------------------------------
        // VERIFY PLAN TENANT
        // -------------------------------------------------

        if (plan.getTenant() == null
                || plan.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Membership plan does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN ACTIVE
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                plan.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Membership plan is inactive."
            );
        }


        // -------------------------------------------------
        // FIND PLAN BRANCH ASSIGNMENT
        // -------------------------------------------------

        MembershipPlanBranch planBranch =
                membershipPlanBranchRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
                                tenantId,
                                plan.getMembershipPlanId(),
                                branchId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership plan is not available for this branch."
                                )
                        );


        // -------------------------------------------------
        // VERIFY PLAN BRANCH TENANT
        // -------------------------------------------------

        if (planBranch.getTenant() == null
                || planBranch.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Membership plan branch assignment does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN BRANCH
        // -------------------------------------------------

        if (planBranch.getBranch() == null
                || planBranch.getBranch()
                .getBranchId()
                != branchId) {

            throw new IllegalArgumentException(
                    "Membership plan is not available for this branch."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN BRANCH STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                planBranch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Membership plan is inactive for this branch."
            );
        }


        // -------------------------------------------------
        // SET PLAN
        // -------------------------------------------------

        membership.setMembershipPlan(
                plan
        );


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        LocalDate startDate =
                membership.getStartDate();


        if (startDate == null) {

            startDate =
                    LocalDate.now();
        }


        membership.setStartDate(
                startDate
        );


        // -------------------------------------------------
        // END DATE
        // -------------------------------------------------

        LocalDate endDate =
                calculateEndDate(
                        startDate,
                        plan
                );


        membership.setEndDate(
                endDate
        );


        // -------------------------------------------------
        // PRICE SNAPSHOT
        // -------------------------------------------------

        BigDecimal planPrice =
                safe(
                        planBranch.getPrice()
                );


        BigDecimal joiningFee =
                safe(
                        planBranch.getJoiningFee()
                );


        BigDecimal renewalFee =
                safe(
                        planBranch.getRenewalFee()
                );


        membership.setPlanPrice(
                planPrice
        );


        membership.setJoiningFee(
                joiningFee
        );


        membership.setRenewalFee(
                renewalFee
        );


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        BigDecimal discount =
                safe(
                        membership.getDiscountAmount()
                );


        if (discount.compareTo(
                planPrice.add(joiningFee)
        ) > 0) {

            throw new IllegalArgumentException(
                    "Discount cannot be greater than the total membership charges."
            );
        }


        membership.setDiscountAmount(
                discount
        );


        // -------------------------------------------------
        // INITIAL TOTAL
        // -------------------------------------------------

        /*
         * At membership creation time there are
         * no member-level purchased benefits yet.
         *
         * Plan Price
         * + Joining Fee
         * - Discount
         * = Initial Total
         */

        BigDecimal totalAmount =
                planPrice
                        .add(joiningFee)
                        .subtract(discount);


        if (totalAmount.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            totalAmount =
                    BigDecimal.ZERO;
        }


        membership.setTotalAmount(
                totalAmount
        );


        // -------------------------------------------------
        // INITIAL PAYMENT TOTALS
        // -------------------------------------------------

        membership.setPaidAmount(
                BigDecimal.ZERO
        );


        membership.setBalanceAmount(
                totalAmount
        );


        // -------------------------------------------------
        // INITIAL STATUS
        // -------------------------------------------------

        membership.setStatus(
                true
        );


        membership.setMembershipState(
                MemberMembership.MembershipState.ACTIVE
        );


        // -------------------------------------------------
        // FREEZE / RENEWAL DEFAULTS
        // -------------------------------------------------

        membership.setTotalFreezeDays(
                0
        );


        membership.setRenewalCount(
                0
        );


        // -------------------------------------------------
        // FIRST SAVE
        // -------------------------------------------------

        MemberMembership savedMembership =
                memberMembershipRepository.save(
                        membership
                );


        // -------------------------------------------------
        // GENERATE FINAL NUMBER
        // -------------------------------------------------

        if (savedMembership.getMembershipNumber()
                .startsWith("TEMP-")) {

            String finalMembershipNumber =
                    String.format(
                            "MEM-%06d",
                            savedMembership
                                    .getMemberMembershipId()
                    );


            savedMembership.setMembershipNumber(
                    finalMembershipNumber
            );
        }


        // -------------------------------------------------
        // FINAL SAVE
        // -------------------------------------------------

        savedMembership =
                memberMembershipRepository.save(
                        savedMembership
                );


        // -------------------------------------------------
        // AUTOMATIC PLAN BENEFITS
        // -------------------------------------------------

        assignIncludedPlanBenefits(
                tenantId,
                savedMembership,
                plan
        );


        // -------------------------------------------------
        // RETURN
        // -------------------------------------------------

        return savedMembership;
    }


    // =====================================================
    // ASSIGN INCLUDED PLAN BENEFITS
    // =====================================================

    private void assignIncludedPlanBenefits(
            Integer tenantId,
            MemberMembership membership,
            MembershipPlan plan) {

        if (membership == null
                || membership.getMemberMembershipId() == null) {

            throw new IllegalArgumentException(
                    "Saved membership is required before assigning benefits."
            );
        }


        if (plan == null
                || plan.getMembershipPlanId() == null) {

            return;
        }


        // -------------------------------------------------
        // LOAD ACTIVE PLAN BENEFITS
        // -------------------------------------------------

        List<MembershipPlanBenefit> planBenefits =
                membershipPlanBenefitRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndStatusTrue(
                                tenantId,
                                plan.getMembershipPlanId()
                        );


        if (planBenefits == null
                || planBenefits.isEmpty()) {

            return;
        }


        // -------------------------------------------------
        // ASSIGN INCLUDED BENEFITS
        // -------------------------------------------------

        for (
                MembershipPlanBenefit planBenefit
                : planBenefits) {

            if (planBenefit == null) {

                continue;
            }


            // -------------------------------------------------
            // ONLY INCLUDED BENEFITS
            // -------------------------------------------------

            if (planBenefit.getBenefitType()
                    != MembershipPlanBenefit.BenefitType.INCLUDED) {

                continue;
            }


            // -------------------------------------------------
            // BENEFIT VALIDATION
            // -------------------------------------------------

            if (planBenefit.getBenefit() == null
                    || planBenefit.getBenefit()
                    .getBenefitId() == null) {

                continue;
            }


            Integer benefitId =
                    planBenefit.getBenefit()
                            .getBenefitId();


            // -------------------------------------------------
            // CHECK DUPLICATE ACTIVE BENEFIT
            // -------------------------------------------------

            boolean alreadyExists =
                    memberMembershipBenefitRepository
                            .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBenefit_BenefitIdAndStatusTrue(
                                    tenantId,
                                    membership.getMemberMembershipId(),
                                    benefitId
                            );


            if (alreadyExists) {

                continue;
            }


            // -------------------------------------------------
            // CREATE MEMBER BENEFIT
            // -------------------------------------------------

            MemberMembershipBenefit
                    memberMembershipBenefit =
                    new MemberMembershipBenefit();


            // -------------------------------------------------
            // TENANT
            // -------------------------------------------------

            memberMembershipBenefit.setTenant(
                    membership.getTenant()
            );


            // -------------------------------------------------
            // BRANCH
            // -------------------------------------------------

            memberMembershipBenefit.setBranch(
                    membership.getBranch()
            );


            // -------------------------------------------------
            // MEMBERSHIP
            // -------------------------------------------------

            memberMembershipBenefit.setMemberMembership(
                    membership
            );


            // -------------------------------------------------
            // BENEFIT
            // -------------------------------------------------

            memberMembershipBenefit.setBenefit(
                    planBenefit.getBenefit()
            );


            // -------------------------------------------------
            // SOURCE
            // -------------------------------------------------

            memberMembershipBenefit.setSourceType(
                    MemberMembershipBenefit.SourceType.INCLUDED
            );


            // -------------------------------------------------
            // INCLUDED BENEFIT FEE
            // -------------------------------------------------

            /*
             * INCLUDED benefits are already part of
             * the membership plan price.
             *
             * Therefore they must not add another
             * charge to the membership.
             */

            memberMembershipBenefit.setFee(
                    BigDecimal.ZERO
            );


            // -------------------------------------------------
            // VALIDITY
            // -------------------------------------------------

            /*
             * Membership-plan included benefits follow
             * the membership validity period.
             */

            memberMembershipBenefit.setStartDate(
                    membership.getStartDate()
            );


            memberMembershipBenefit.setEndDate(
                    membership.getEndDate()
            );


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            memberMembershipBenefit.setStatus(
                    true
            );


            // -------------------------------------------------
            // NOTES
            // -------------------------------------------------

            memberMembershipBenefit.setNotes(
                    "Included with membership plan."
            );


            // -------------------------------------------------
            // SAVE
            // -------------------------------------------------

            memberMembershipBenefitRepository.save(
                    memberMembershipBenefit
            );
        }
    }


    // =====================================================
    // ASSIGN / CHANGE MEMBERSHIP PLAN
    // =====================================================


    public MemberMembership assignMembershipPlan(
            Integer tenantId,
            Integer memberMembershipId,
            Integer membershipPlanId,
            LocalDate startDate,
            MemberMembershipPlanRequest.SettlementType settlementType,
            BigDecimal settlementAmount,
            String settlementReason,
            String notes) {

        // 1. Validate settlement details.
        if (settlementType == null) {
            throw new IllegalArgumentException(
                    "Settlement type is required."
            );
        }

        BigDecimal amount = safe(settlementAmount);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Settlement amount cannot be negative."
            );
        }

        if (settlementType
                == MemberMembershipPlanRequest.SettlementType.NONE
                && amount.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException(
                    "Settlement amount must be zero when settlement type is NONE."
            );
        }

        if (settlementReason != null
                && settlementReason.length() > 1000) {
            throw new IllegalArgumentException(
                    "Settlement reason cannot exceed 1000 characters."
            );
        }

        // 2. Load the existing membership.
        MemberMembership oldMembership =
                memberMembershipRepository.findById(memberMembershipId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Membership not found."
                        ));

        if (oldMembership.getTenant() == null
                || !tenantId.equals(
                oldMembership.getTenant().getTenantId())) {
            throw new IllegalArgumentException(
                    "Membership does not belong to this tenant."
            );
        }

        if (oldMembership.getBranch() == null
                || oldMembership.getMember() == null) {
            throw new IllegalArgumentException(
                    "Membership branch or member is missing."
            );
        }

        if (Boolean.FALSE.equals(oldMembership.getStatus())
                || oldMembership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {
            throw new IllegalArgumentException(
                    "An inactive or cancelled membership cannot be changed."
            );
        }

        Integer branchId =
                oldMembership.getBranch().getBranchId();

        // 3. Load and validate the new plan.
        MembershipPlan plan =
                membershipPlanRepository.findById(membershipPlanId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Membership plan not found."
                        ));

        if (plan.getTenant() == null
                || !tenantId.equals(plan.getTenant().getTenantId())) {
            throw new IllegalArgumentException(
                    "Membership plan does not belong to this tenant."
            );
        }

        if (!Boolean.TRUE.equals(plan.getStatus())) {
            throw new IllegalArgumentException(
                    "Membership plan is inactive."
            );
        }

        MembershipPlanBranch planBranch =
                membershipPlanBranchRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
                                tenantId,
                                membershipPlanId,
                                branchId
                        )
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Membership plan is not available for this branch."
                        ));

        if (!Boolean.TRUE.equals(planBranch.getStatus())) {
            throw new IllegalArgumentException(
                    "Membership plan is inactive for this branch."
            );
        }

        // 4. Create a NEW membership, preserving the old record.
        MemberMembership newMembership = new MemberMembership();

        newMembership.setTenant(oldMembership.getTenant());
        newMembership.setBranch(oldMembership.getBranch());
        newMembership.setMember(oldMembership.getMember());
        newMembership.setMembershipPlan(plan);

        newMembership.setStartDate(
                startDate != null ? startDate : LocalDate.now()
        );

        // The existing create() method calculates the end date,
        // snapshots branch pricing and assigns included benefits.
        newMembership.setDiscountAmount(BigDecimal.ZERO);

        String newNotes = notes == null ? "" : notes.trim();
        newMembership.setNotes(
                newNotes.isEmpty()
                        ? "Created by changing membership plan from "
                          + oldMembership.getMembershipNumber()
                        : newNotes
        );

        newMembership = create(newMembership);

        // 5. Record the settlement separately.
        MembershipChangeSettlement settlement =
                new MembershipChangeSettlement();

        settlement.setTenant(oldMembership.getTenant());
        settlement.setBranch(oldMembership.getBranch());
        settlement.setOldMembership(oldMembership);
        settlement.setNewMembership(newMembership);
        settlement.setSettlementAmount(amount);
        settlement.setSettlementReason(settlementReason);

        switch (settlementType) {
            case NONE:
                settlement.setSettlementType(
                        MembershipChangeSettlement.SettlementType.NONE
                );
                break;

            case CARRY_FORWARD:
                settlement.setSettlementType(
                        MembershipChangeSettlement.SettlementType.CARRY_FORWARD
                );
                break;

            case REFUND:
                settlement.setSettlementType(
                        MembershipChangeSettlement.SettlementType.REFUND
                );
                break;

            case WAIVE:
                settlement.setSettlementType(
                        MembershipChangeSettlement.SettlementType.WAIVE_BALANCE
                );
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported settlement type."
                );
        }

        membershipChangeSettlementRepository.save(settlement);

        // 6. Mark the old membership as cancelled only after the
        // new membership and settlement have been saved.
        oldMembership.setMembershipState(
                MemberMembership.MembershipState.CANCELLED
        );

        String oldNotes = oldMembership.getNotes() == null
                ? ""
                : oldMembership.getNotes().trim();

        String changeNote =
                "Membership plan changed to "
                        + newMembership.getMembershipNumber()
                        + " on "
                        + LocalDate.now() + ".";

        oldMembership.setNotes(
                oldNotes.isEmpty()
                        ? changeNote
                        : oldNotes + "\n" + changeNote
        );

        memberMembershipRepository.save(oldMembership);

        return newMembership;
    }



    // =====================================================
    // RECALCULATE MEMBERSHIP FINANCIALS
    // =====================================================

    public MemberMembership recalculateFinancials(
            Integer membershipId) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                membershipId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        if (membership.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Membership tenant is missing."
            );
        }


        Integer tenantId =
                membership.getTenant()
                        .getTenantId();


        // -------------------------------------------------
        // BASE MEMBERSHIP AMOUNT
        // -------------------------------------------------

        BigDecimal planPrice =
                safe(
                        membership.getPlanPrice()
                );


        BigDecimal joiningFee =
                safe(
                        membership.getJoiningFee()
                );


        BigDecimal discount =
                safe(
                        membership.getDiscountAmount()
                );


        // -------------------------------------------------
        // ACTIVE MEMBER BENEFITS
        // -------------------------------------------------

        List<MemberMembershipBenefit> benefits =
                memberMembershipBenefitRepository
                        .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusTrue(
                                tenantId,
                                membershipId
                        );


        // -------------------------------------------------
        // PURCHASED BENEFIT TOTAL
        // -------------------------------------------------

        BigDecimal purchasedBenefitTotal =
                BigDecimal.ZERO;


        for (
                MemberMembershipBenefit benefit
                : benefits) {

            if (benefit == null) {
                continue;
            }


            if (benefit.getSourceType()
                    == MemberMembershipBenefit.SourceType.PURCHASED) {

                purchasedBenefitTotal =
                        purchasedBenefitTotal.add(
                                safe(
                                        benefit.getFee()
                                )
                        );
            }
        }


        // -------------------------------------------------
        // FINAL TOTAL
        // -------------------------------------------------

        BigDecimal totalAmount =
                planPrice
                        .add(joiningFee)
                        .add(purchasedBenefitTotal)
                        .subtract(discount);


        if (totalAmount.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            totalAmount =
                    BigDecimal.ZERO;
        }


        membership.setTotalAmount(
                totalAmount
        );


        // -------------------------------------------------
        // EXISTING PAID AMOUNT
        // -------------------------------------------------

        BigDecimal paidAmount =
                safe(
                        membership.getPaidAmount()
                );


        if (paidAmount.compareTo(
                totalAmount
        ) > 0) {

            throw new IllegalArgumentException(
                    "Existing paid amount cannot be greater than the recalculated total amount."
            );
        }


        // -------------------------------------------------
        // BALANCE
        // -------------------------------------------------

        BigDecimal balanceAmount =
                totalAmount.subtract(
                        paidAmount
                );


        membership.setBalanceAmount(
                balanceAmount
        );


        return memberMembershipRepository.save(
                membership
        );
    }


    // =====================================================
    // CALCULATE END DATE
    // =====================================================

    private LocalDate calculateEndDate(
            LocalDate startDate,
            MembershipPlan plan) {

        if (plan.getDurationType()
                == MembershipPlan.DurationType.UNLIMITED) {

            return LocalDate.of(
                    9999,
                    12,
                    31
            );
        }


        Integer value =
                plan.getDurationValue();


        if (value == null
                || value <= 0) {

            throw new IllegalArgumentException(
                    "Invalid membership duration."
            );
        }


        switch (plan.getDurationType()) {

            case DAYS:

                return startDate.plusDays(
                        value
                );


            case MONTHS:

                return startDate.plusMonths(
                        value
                );


            case YEARS:

                return startDate.plusYears(
                        value
                );


            default:

                throw new IllegalArgumentException(
                        "Unsupported membership duration type."
                );
        }
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MemberMembership> findById(
            Integer membershipId) {

        return memberMembershipRepository
                .findById(
                        membershipId
                );
    }


    // =====================================================
    // FIND ALL TENANT MEMBERSHIPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByTenant(
            int tenantId) {

        return memberMembershipRepository
                .findByTenant_TenantIdOrderByStartDateDesc(
                        tenantId
                );
    }


    // =====================================================
    // FIND MEMBER MEMBERSHIPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByMember(
            Integer tenantId,
            Integer memberId) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByStartDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND MEMBER MEMBERSHIPS BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByMemberAndBranch(
            Integer tenantId,
            Integer branchId,
            Integer memberId) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndMember_MemberIdOrderByStartDateDesc(
                        tenantId,
                        branchId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY MEMBERSHIP NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MemberMembership>
    findByMembershipNumber(
            Integer tenantId,
            String membershipNumber) {

        if (membershipNumber == null
                || membershipNumber.trim().isEmpty()) {

            return Optional.empty();
        }


        return memberMembershipRepository
                .findByTenant_TenantIdAndMembershipNumber(
                        tenantId,
                        membershipNumber
                                .trim()
                                .toUpperCase()
                );
    }


    // =====================================================
    // FIND BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByStatus(
            int tenantId,
            Boolean status) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndStatusOrderByEndDateAsc(
                        tenantId,
                        status
                );
    }


    // =====================================================
    // FIND BY BRANCH AND STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByBranchAndStatus(
            int tenantId,
            int branchId,
            Boolean status) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndStatusOrderByEndDateAsc(
                        tenantId,
                        branchId,
                        status
                );
    }


    // =====================================================
    // FIND BY MEMBERSHIP STATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findByMembershipState(
            int tenantId,
            MemberMembership.MembershipState membershipState) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndMembershipStateOrderByEndDateAsc(
                        tenantId,
                        membershipState
                );
    }


    // =====================================================
    // FIND BY BRANCH AND MEMBERSHIP STATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership>
    findByBranchAndMembershipState(
            int tenantId,
            int branchId,
            MemberMembership.MembershipState membershipState) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndMembershipStateOrderByEndDateAsc(
                        tenantId,
                        branchId,
                        membershipState
                );
    }


    // =====================================================
    // EXPIRING MEMBERSHIPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findExpiringBetween(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndEndDateBetweenOrderByEndDateAsc(
                        tenantId,
                        startDate,
                        endDate
                );
    }


    // =====================================================
    // EXPIRING MEMBERSHIPS BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberMembership> findExpiringBetween(
            Integer tenantId,
            Integer branchId,
            LocalDate startDate,
            LocalDate endDate) {

        return memberMembershipRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndEndDateBetweenOrderByEndDateAsc(
                        tenantId,
                        branchId,
                        startDate,
                        endDate
                );
    }


    // =====================================================
    // UPDATE PAYMENT TOTALS
    // =====================================================

    public MemberMembership updatePaymentTotals(
            Integer membershipId,
            BigDecimal paidAmount) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                membershipId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        BigDecimal totalAmount =
                safe(
                        membership.getTotalAmount()
                );


        BigDecimal paid =
                safe(
                        paidAmount
                );


        if (paid.compareTo(
                totalAmount
        ) > 0) {

            throw new IllegalArgumentException(
                    "Paid amount cannot be greater than total amount."
            );
        }


        BigDecimal balance =
                totalAmount.subtract(
                        paid
                );


        membership.setPaidAmount(
                paid
        );


        membership.setBalanceAmount(
                balance
        );


        return memberMembershipRepository.save(
                membership
        );
    }


    // =====================================================
    // UPDATE STATUS
    // =====================================================

    public MemberMembership updateStatus(
            Integer membershipId,
            Boolean status) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Status is required."
            );
        }


        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                membershipId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        membership.setStatus(
                status
        );


        return memberMembershipRepository.save(
                membership
        );
    }


    // =====================================================
    // DEACTIVATE MEMBERSHIP RECORD
    // =====================================================

    public MemberMembership deactivate(
            Integer membershipId) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                membershipId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        membership.setStatus(
                false
        );


        return memberMembershipRepository.save(
                membership
        );
    }


    // =====================================================
    // UPDATE EXPIRED MEMBERSHIPS
    // =====================================================

    @Transactional
    public int updateExpiredMemberships(
            int tenantId) {

        LocalDate today =
                LocalDate.now();


        List<MemberMembership> memberships =
                memberMembershipRepository
                        .findByTenant_TenantIdAndStatusTrueAndMembershipStateAndEndDateBefore(
                                tenantId,
                                MemberMembership.MembershipState.ACTIVE,
                                today
                        );


        int updatedCount = 0;


        for (
                MemberMembership membership
                : memberships) {

            membership.setMembershipState(
                    MemberMembership.MembershipState.EXPIRED
            );


            memberMembershipRepository.save(
                    membership
            );


            updatedCount++;
        }


        return updatedCount;
    }


    // =====================================================
    // CANCEL MEMBERSHIP
    // =====================================================

    public MemberMembership cancel(
            Integer membershipId) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                membershipId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Membership is already cancelled."
            );
        }


        membership.setMembershipState(
                MemberMembership.MembershipState.CANCELLED
        );

        membership.setStatus(false);

        return memberMembershipRepository.save(
                membership
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Integer membershipId) {

        if (!memberMembershipRepository
                .existsById(
                        membershipId
                )) {

            throw new IllegalArgumentException(
                    "Membership not found."
            );
        }


        memberMembershipRepository.deleteById(
                membershipId
        );
    }


    // =====================================================
    // SAFE BIG DECIMAL
    // =====================================================

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}