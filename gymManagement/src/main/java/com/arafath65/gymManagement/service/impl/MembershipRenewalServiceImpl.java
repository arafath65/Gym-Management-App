package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MembershipRenewalRequest;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBranchRepository;
import com.arafath65.gymManagement.repository.MembershipRenewalRepository;
import com.arafath65.gymManagement.service.MembershipRenewalService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipRenewalServiceImpl
        implements MembershipRenewalService {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipRenewalRepository
            membershipRenewalRepository;

    private final MemberMembershipRepository
            memberMembershipRepository;

    private final MembershipPlanBranchRepository
            membershipPlanBranchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipRenewalServiceImpl(

            MembershipRenewalRepository
                    membershipRenewalRepository,

            MemberMembershipRepository
                    memberMembershipRepository,

            MembershipPlanBranchRepository
                    membershipPlanBranchRepository) {

        this.membershipRenewalRepository =
                membershipRenewalRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.membershipPlanBranchRepository =
                membershipPlanBranchRepository;
    }


    // =====================================================
    // CREATE RENEWAL
    // =====================================================

    @Override
    public MembershipRenewal createRenewal(

            Integer tenantId,

            MembershipRenewalRequest request) {

        // -------------------------------------------------
        // VALIDATE REQUEST
        // -------------------------------------------------

        if (request == null) {

            throw new BadRequestException(
                    "Renewal request is required."
            );
        }


        if (request.getMemberMembershipId() == null) {

            throw new BadRequestException(
                    "Membership is required."
            );
        }


        // -------------------------------------------------
        // FIND MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                request.getMemberMembershipId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        // -------------------------------------------------
        // TENANT VALIDATION
        // -------------------------------------------------

        if (membership.getTenant() == null ||
                membership.getTenant().getTenantId()
                        != tenantId) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // STATUS VALIDATION
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            throw new BadRequestException(
                    "Membership is inactive."
            );
        }


        // -------------------------------------------------
        // MEMBER VALIDATION
        // -------------------------------------------------

        if (membership.getMember() == null) {

            throw new BadRequestException(
                    "Member linked to this membership was not found."
            );
        }


        // -------------------------------------------------
        // BRANCH VALIDATION
        // -------------------------------------------------

        if (membership.getBranch() == null) {

            throw new BadRequestException(
                    "Branch linked to this membership was not found."
            );
        }


        Branch branch =
                membership.getBranch();


        // -------------------------------------------------
        // VERIFY BRANCH TENANT
        // -------------------------------------------------

        if (branch.getTenant() == null ||
                branch.getTenant().getTenantId()
                        != tenantId) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                membership.getMembershipPlan();


        if (plan == null) {

            throw new BadRequestException(
                    "Membership plan not found."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN TENANT
        // -------------------------------------------------

        if (plan.getTenant() == null ||
                plan.getTenant().getTenantId()
                        != tenantId) {

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
        // FIND PLAN → BRANCH ASSIGNMENT
        // -------------------------------------------------

        MembershipPlanBranch planBranch =
                membershipPlanBranchRepository
                        .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
                                tenantId,
                                plan.getMembershipPlanId(),
                                branch.getBranchId()
                        )
                        .orElseThrow(
                                () -> new BadRequestException(
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
        // VERIFY PLAN BRANCH TENANT
        // -------------------------------------------------

        if (planBranch.getTenant() == null ||
                planBranch.getTenant().getTenantId()
                        != tenantId) {

            throw new BadRequestException(
                    "Membership plan branch assignment does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN BRANCH
        // -------------------------------------------------

        if (planBranch.getMembershipPlan() == null ||
                planBranch.getMembershipPlan()
                        .getMembershipPlanId()
                        != plan.getMembershipPlanId()) {

            throw new BadRequestException(
                    "Invalid membership plan branch assignment."
            );
        }


        // -------------------------------------------------
        // VERIFY ASSIGNED BRANCH
        // -------------------------------------------------

        if (planBranch.getBranch() == null ||
                planBranch.getBranch()
                        .getBranchId()
                        != branch.getBranchId()) {

            throw new BadRequestException(
                    "Invalid membership plan branch assignment."
            );
        }


        // -------------------------------------------------
        // RENEWAL FEE
        // -------------------------------------------------

        /*
         * IMPORTANT:
         *
         * MembershipPlan no longer contains renewalFee.
         *
         * Renewal fee is branch-specific and comes from
         * MembershipPlanBranch.
         */

        BigDecimal renewalFee =
                planBranch.getRenewalFee() == null
                        ? BigDecimal.ZERO
                        : planBranch.getRenewalFee();


        if (renewalFee.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "Renewal fee is not configured for this membership plan and branch."
            );
        }


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        BigDecimal discount =
                request.getDiscountAmount() == null
                        ? BigDecimal.ZERO
                        : request.getDiscountAmount();


        if (discount.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new BadRequestException(
                    "Discount cannot be negative."
            );
        }


        if (discount.compareTo(
                renewalFee
        ) > 0) {

            throw new BadRequestException(
                    "Discount cannot be greater than the renewal fee."
            );
        }


        // -------------------------------------------------
        // TOTAL
        // -------------------------------------------------

        BigDecimal totalAmount =
                renewalFee.subtract(
                        discount
                );


        // -------------------------------------------------
        // RENEWAL DATE
        // -------------------------------------------------

        LocalDate renewalDate =
                request.getRenewalDate() != null
                        ? request.getRenewalDate()
                        : LocalDate.now();


        // -------------------------------------------------
        // PREVIOUS PERIOD
        // -------------------------------------------------

        LocalDate previousStartDate =
                membership.getStartDate();

        LocalDate previousEndDate =
                membership.getEndDate();


        if (previousStartDate == null) {

            throw new BadRequestException(
                    "Membership start date is not configured."
            );
        }


        if (previousEndDate == null) {

            throw new BadRequestException(
                    "Membership end date is not configured."
            );
        }


        // -------------------------------------------------
        // NEW PERIOD START
        // -------------------------------------------------

        LocalDate newStartDate;


        if (!renewalDate.isAfter(
                previousEndDate
        )) {

            /*
             * Membership is still active.
             *
             * Continue from the existing period.
             */

            newStartDate =
                    previousEndDate;

        } else {

            /*
             * Membership has expired.
             *
             * Start from the renewal date.
             */

            newStartDate =
                    renewalDate;
        }


        // -------------------------------------------------
        // NEW END DATE
        // -------------------------------------------------

        LocalDate newEndDate =
                calculateEndDate(
                        newStartDate,
                        request.getDurationType(),
                        request.getDurationValue()
                );


        // -------------------------------------------------
        // RENEWAL NUMBER
        // -------------------------------------------------

        String renewalNumber =
                generateRenewalNumber(
                        tenantId
                );


        // -------------------------------------------------
        // BUILD RENEWAL
        // -------------------------------------------------

        MembershipRenewal renewal =
                new MembershipRenewal();


        Tenant tenant =
                membership.getTenant();


        renewal.setTenant(
                tenant
        );

        renewal.setBranch(
                branch
        );

        renewal.setMember(
                membership.getMember()
        );

        renewal.setMemberMembership(
                membership
        );


        // -------------------------------------------------
        // RENEWAL INFORMATION
        // -------------------------------------------------

        renewal.setRenewalNumber(
                renewalNumber
        );

        renewal.setRenewalDate(
                renewalDate
        );


        // -------------------------------------------------
        // PREVIOUS PERIOD SNAPSHOT
        // -------------------------------------------------

        renewal.setPreviousStartDate(
                previousStartDate
        );

        renewal.setPreviousEndDate(
                previousEndDate
        );


        // -------------------------------------------------
        // NEW PERIOD
        // -------------------------------------------------

        renewal.setNewStartDate(
                newStartDate
        );

        renewal.setNewEndDate(
                newEndDate
        );


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        renewal.setDurationType(
                request.getDurationType()
        );

        renewal.setDurationValue(
                request.getDurationValue()
        );


        // -------------------------------------------------
        // FINANCIAL
        // -------------------------------------------------

        renewal.setRenewalFee(
                renewalFee
        );

        renewal.setDiscountAmount(
                discount
        );

        renewal.setTotalAmount(
                totalAmount
        );

        renewal.setPaidAmount(
                BigDecimal.ZERO
        );

        renewal.setBalanceAmount(
                totalAmount
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        renewal.setStatus(
                true
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        renewal.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // SAVE RENEWAL
        // -------------------------------------------------

        MembershipRenewal savedRenewal =
                membershipRenewalRepository.save(
                        renewal
                );


        // -------------------------------------------------
        // UPDATE MEMBERSHIP
        // -------------------------------------------------

        membership.setStartDate(
                newStartDate
        );

        membership.setEndDate(
                newEndDate
        );


        membership.setRenewalCount(
                membership.getRenewalCount() == null
                        ? 1
                        : membership.getRenewalCount() + 1
        );


        // -------------------------------------------------
        // REACTIVATE EXPIRED/CANCELLED MEMBERSHIP
        // -------------------------------------------------

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.EXPIRED
                ||
                membership.getMembershipState()
                        == MemberMembership.MembershipState.CANCELLED) {

            membership.setMembershipState(
                    MemberMembership.MembershipState.ACTIVE
            );
        }


        membership.setStatus(
                true
        );


        memberMembershipRepository.save(
                membership
        );


        return savedRenewal;
    }


    // =====================================================
    // CALCULATE END DATE
    // =====================================================

    private LocalDate calculateEndDate(

            LocalDate baseDate,

            MembershipPlan.DurationType durationType,

            Integer durationValue) {

        if (baseDate == null) {

            throw new BadRequestException(
                    "Base date is required."
            );
        }


        if (durationType == null) {

            throw new BadRequestException(
                    "Duration type is required."
            );
        }


        if (durationType ==
                MembershipPlan.DurationType.UNLIMITED) {

            throw new BadRequestException(
                    "Unlimited renewal is not supported yet."
            );
        }


        if (durationValue == null ||
                durationValue <= 0) {

            throw new BadRequestException(
                    "Duration value must be greater than zero."
            );
        }


        switch (durationType) {

            case DAYS:

                return baseDate.plusDays(
                        durationValue
                );


            case MONTHS:

                return baseDate.plusMonths(
                        durationValue
                );


            case YEARS:

                return baseDate.plusYears(
                        durationValue
                );


            default:

                throw new BadRequestException(
                        "Invalid renewal duration type."
                );
        }
    }


    // =====================================================
    // GENERATE RENEWAL NUMBER
    // =====================================================

    private String generateRenewalNumber(
            Integer tenantId) {

        long count =
                membershipRenewalRepository
                        .count();


        return String.format(
                "REN-%06d",
                count + 1
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<MembershipRenewal> findById(
            Integer renewalId) {

        return membershipRenewalRepository
                .findById(
                        renewalId
                );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MembershipRenewal> findByMembership(

            Integer tenantId,

            Integer memberMembershipId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalDateDesc(
                        tenantId,
                        memberMembershipId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MembershipRenewal> findByMember(

            Integer tenantId,

            Integer memberId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByRenewalDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MembershipRenewal> findByBranch(

            Integer tenantId,

            Integer branchId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByRenewalDateDesc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MembershipRenewal> findByDateRange(

            Integer tenantId,

            LocalDate startDate,

            LocalDate endDate) {

        if (startDate == null ||
                endDate == null) {

            throw new BadRequestException(
                    "Start date and end date are required."
            );
        }


        if (endDate.isBefore(
                startDate
        )) {

            throw new BadRequestException(
                    "End date cannot be before start date."
            );
        }


        return membershipRenewalRepository
                .findByTenant_TenantIdAndRenewalDateBetweenOrderByRenewalDateDesc(
                        tenantId,
                        startDate,
                        endDate
                );
    }
}