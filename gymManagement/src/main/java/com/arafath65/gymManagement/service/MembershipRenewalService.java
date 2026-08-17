package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipRenewalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipRenewalService {

    private final MembershipRenewalRepository membershipRenewalRepository;

    private final MemberMembershipRepository memberMembershipRepository;


    public MembershipRenewalService(
            MembershipRenewalRepository membershipRenewalRepository,
            MemberMembershipRepository memberMembershipRepository) {

        this.membershipRenewalRepository =
                membershipRenewalRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;
    }


    // =====================================================
    // RENEW MEMBERSHIP
    // =====================================================

    public MembershipRenewal renew(
            int membershipId,
            BigDecimal discountAmount,
            LocalDate renewalStartDate,
            String notes) {

        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership not found."
                        )
                );


        // -------------------------------------------------
        // VALIDATE MEMBERSHIP STATE
        // -------------------------------------------------

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled membership cannot be renewed."
            );
        }


        // -------------------------------------------------
        // VALIDATE MEMBERSHIP STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Inactive membership cannot be renewed."
            );
        }


        /*
         * EXPIRED memberships are allowed to renew.
         *
         * FROZEN memberships are not renewed while the
         * membership is still frozen.
         *
         * A frozen membership should first return to ACTIVE
         * through freeze completion.
         */

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.FROZEN) {

            throw new IllegalArgumentException(
                    "Frozen membership cannot be renewed. Complete the freeze first."
            );
        }


        // -------------------------------------------------
        // LOAD PLAN
        // -------------------------------------------------

        MembershipPlan plan =
                membership.getMembershipPlan();


        if (plan == null) {

            throw new IllegalArgumentException(
                    "Membership plan not found."
            );
        }


        // -------------------------------------------------
        // VERIFY PLAN IS ACTIVE
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                plan.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Membership plan is inactive."
            );
        }


        // -------------------------------------------------
        // PREVIOUS DATES
        // -------------------------------------------------

        LocalDate previousStartDate =
                membership.getStartDate();


        LocalDate previousEndDate =
                membership.getEndDate();


        // -------------------------------------------------
        // RENEWAL START DATE
        // -------------------------------------------------

        LocalDate newStartDate;


        if (renewalStartDate != null) {

            newStartDate =
                    renewalStartDate;

        } else {

            /*
             * If current membership has not yet expired,
             * continue after its current end date.
             *
             * If it has already expired,
             * renewal starts today.
             */

            if (previousEndDate != null
                    && !previousEndDate.isBefore(
                    LocalDate.now()
            )) {

                newStartDate =
                        previousEndDate.plusDays(1);

            } else {

                newStartDate =
                        LocalDate.now();
            }
        }


        // -------------------------------------------------
        // NEW END DATE
        // -------------------------------------------------

        LocalDate newEndDate =
                calculateEndDate(
                        newStartDate,
                        plan
                );


        // -------------------------------------------------
        // RENEWAL FEE
        // -------------------------------------------------

        BigDecimal renewalFee =
                safe(
                        plan.getRenewalFee()
                );


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        BigDecimal discount =
                safe(
                        discountAmount
                );


        if (discount.compareTo(
                renewalFee
        ) > 0) {

            throw new IllegalArgumentException(
                    "Discount cannot be greater than renewal fee."
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
        // CREATE RENEWAL
        // -------------------------------------------------

        MembershipRenewal renewal =
                new MembershipRenewal();


        renewal.setTenant(
                membership.getTenant()
        );


        renewal.setBranch(
                membership.getBranch()
        );


        renewal.setMember(
                membership.getMember()
        );


        renewal.setMemberMembership(
                membership
        );


        // -------------------------------------------------
        // RENEWAL NUMBER
        // -------------------------------------------------

        int renewalNumber =
                membership.getRenewalCount() == null
                        ? 1
                        : membership.getRenewalCount() + 1;


        renewal.setRenewalNumber(
                renewalNumber
        );


        // -------------------------------------------------
        // PREVIOUS DATES
        // -------------------------------------------------

        renewal.setPreviousStartDate(
                previousStartDate
        );


        renewal.setPreviousEndDate(
                previousEndDate
        );


        // -------------------------------------------------
        // NEW DATES
        // -------------------------------------------------

        renewal.setNewStartDate(
                newStartDate
        );


        renewal.setNewEndDate(
                newEndDate
        );


        // -------------------------------------------------
        // AMOUNTS
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


        /*
         * Renewal starts unpaid.
         *
         * Payment will be recorded through
         * MembershipPayment.
         */

        renewal.setPaidAmount(
                BigDecimal.ZERO
        );


        renewal.setBalanceAmount(
                totalAmount
        );


        // -------------------------------------------------
        // DATE / NOTES
        // -------------------------------------------------

        renewal.setRenewalDate(
                LocalDateTime.now()
        );


        renewal.setNotes(
                notes
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
                renewalNumber
        );


        // Membership record remains active.

        membership.setStatus(
                true
        );


        // Successful renewal returns membership to ACTIVE.

        membership.setMembershipState(
                MemberMembership.MembershipState.ACTIVE
        );


        /*
         * IMPORTANT:
         *
         * Original membership payment totals are NOT changed.
         *
         * Renewal has its own:
         *
         * total_amount
         * paid_amount
         * balance_amount
         *
         * MembershipPayment updates the renewal amounts
         * when renewal payments are recorded.
         */

        memberMembershipRepository.save(
                membership
        );


        return savedRenewal;
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
    public Optional<MembershipRenewal> findById(
            int renewalId) {

        return membershipRenewalRepository.findById(
                renewalId
        );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipRenewal> findByMembership(
            int tenantId,
            int membershipId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalDateDesc(
                        tenantId,
                        membershipId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipRenewal> findByMember(
            int tenantId,
            int memberId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByRenewalDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipRenewal> findByBranch(
            int tenantId,
            int branchId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByRenewalDateDesc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipRenewal> findByDateRange(
            int tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndRenewalDateBetweenOrderByRenewalDateDesc(
                        tenantId,
                        startDate,
                        endDate
                );
    }


    // =====================================================
    // RENEWAL HISTORY BY MEMBERSHIP
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipRenewal> findHistory(
            int tenantId,
            int membershipId) {

        return membershipRenewalRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalNumberAsc(
                        tenantId,
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