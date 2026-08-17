package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberMembershipService {

    private final MemberMembershipRepository memberMembershipRepository;

    private final MembershipPlanRepository membershipPlanRepository;


    public MemberMembershipService(
            MemberMembershipRepository memberMembershipRepository,
            MembershipPlanRepository membershipPlanRepository) {

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.membershipPlanRepository =
                membershipPlanRepository;
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
        // LOAD PLAN
        // -------------------------------------------------

        Integer planId =
                membership.getMembershipPlan()
                        .getMembershipPlanId();


        MembershipPlan plan =
                membershipPlanRepository
                        .findById(planId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership plan not found."
                                )
                        );


        // -------------------------------------------------
        // VERIFY PLAN TENANT
        // -------------------------------------------------

        if (plan.getTenant()
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
        // VERIFY PLAN BRANCH
        // -------------------------------------------------

        if (plan.getBranch() != null
                && plan.getBranch()
                .getBranchId()
                != branchId) {

            throw new IllegalArgumentException(
                    "Membership plan is not available for this branch."
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
                        plan.getPrice()
                );


        BigDecimal joiningFee =
                safe(
                        plan.getJoiningFee()
                );


        BigDecimal renewalFee =
                safe(
                        plan.getRenewalFee()
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
        // TOTAL
        // -------------------------------------------------

        BigDecimal totalAmount =
                planPrice
                        .add(joiningFee)
                        .subtract(discount);


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
        // TEMPORARY MEMBERSHIP NUMBER
        // -------------------------------------------------

        /*
         * MySQL generates the membership ID.
         *
         * membership_number is NOT NULL,
         * therefore a temporary value is required
         * for the first INSERT.
         */

        membership.setMembershipNumber(
                "TEMP-" + System.nanoTime()
        );


        // -------------------------------------------------
        // FIRST SAVE
        // -------------------------------------------------

        MemberMembership savedMembership =
                memberMembershipRepository.save(
                        membership
                );


        // -------------------------------------------------
        // GENERATE FINAL MEMBERSHIP NUMBER
        // -------------------------------------------------

        String membershipNumber =
                String.format(
                        "MEM-%06d",
                        savedMembership
                                .getMemberMembershipId()
                );


        savedMembership.setMembershipNumber(
                membershipNumber
        );


        // -------------------------------------------------
        // FINAL SAVE
        // -------------------------------------------------

        return memberMembershipRepository.save(
                savedMembership
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

        return memberMembershipRepository.findById(
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
    public Optional<MemberMembership> findByMembershipNumber(
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
    public List<MemberMembership> findByBranchAndMembershipState(
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
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
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
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
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
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
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


        for (MemberMembership membership
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
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
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


        /*
         * Cancellation changes only the lifecycle state.
         *
         * status remains unchanged.
         */

        membership.setMembershipState(
                MemberMembership.MembershipState.CANCELLED
        );


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