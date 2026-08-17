package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipFreeze;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipFreezeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipFreezeService {

    private final MembershipFreezeRepository membershipFreezeRepository;

    private final MemberMembershipRepository memberMembershipRepository;


    public MembershipFreezeService(
            MembershipFreezeRepository membershipFreezeRepository,
            MemberMembershipRepository memberMembershipRepository) {

        this.membershipFreezeRepository =
                membershipFreezeRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;
    }


    // =====================================================
    // CREATE FREEZE
    // =====================================================

    public MembershipFreeze createFreeze(
            MembershipFreeze freeze) {

        if (freeze == null) {

            throw new IllegalArgumentException(
                    "Freeze cannot be null."
            );
        }


        // -------------------------------------------------
        // BASIC VALIDATION
        // -------------------------------------------------

        if (freeze.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }


        if (freeze.getBranch() == null) {

            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }


        if (freeze.getMember() == null) {

            throw new IllegalArgumentException(
                    "Member is required."
            );
        }


        if (freeze.getMemberMembership() == null) {

            throw new IllegalArgumentException(
                    "Membership is required."
            );
        }


        if (freeze.getFreezeStartDate() == null) {

            throw new IllegalArgumentException(
                    "Freeze start date is required."
            );
        }


        if (freeze.getFreezeEndDate() == null) {

            throw new IllegalArgumentException(
                    "Freeze end date is required."
            );
        }


        if (freeze.getFreezeEndDate()
                .isBefore(
                        freeze.getFreezeStartDate()
                )) {

            throw new IllegalArgumentException(
                    "Freeze end date cannot be before freeze start date."
            );
        }


        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        int membershipId =
                freeze.getMemberMembership()
                        .getMemberMembershipId();


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


        // -------------------------------------------------
        // TENANT CHECK
        // -------------------------------------------------

        int tenantId =
                freeze.getTenant()
                        .getTenantId();


        if (membership.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Membership does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // BRANCH CHECK
        // -------------------------------------------------

        int branchId =
                freeze.getBranch()
                        .getBranchId();


        if (membership.getBranch()
                .getBranchId()
                != branchId) {

            throw new IllegalArgumentException(
                    "Membership does not belong to this branch."
            );
        }


        // -------------------------------------------------
        // MEMBER CHECK
        // -------------------------------------------------

        if (membership.getMember()
                .getMemberId()
                != freeze.getMember()
                .getMemberId()) {

            throw new IllegalArgumentException(
                    "Membership does not belong to this member."
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Inactive membership cannot be frozen."
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP STATE
        // -------------------------------------------------

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled membership cannot be frozen."
            );
        }


        if (membership.getMembershipState()
                == MemberMembership.MembershipState.EXPIRED) {

            throw new IllegalArgumentException(
                    "Expired membership cannot be frozen."
            );
        }


        if (membership.getMembershipState()
                == MemberMembership.MembershipState.FROZEN) {

            throw new IllegalArgumentException(
                    "Membership is already frozen."
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
        // CHECK FREEZE ALLOWED
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                plan.getFreezeAllowed()
        )) {

            throw new IllegalArgumentException(
                    "Freeze is not allowed for this membership plan."
            );
        }


        // -------------------------------------------------
        // CALCULATE FREEZE DAYS
        // -------------------------------------------------

        int freezeDays =
                (int) (
                        freeze.getFreezeStartDate()
                                .until(
                                        freeze.getFreezeEndDate()
                                )
                                .getDays()
                                + 1
                );


        if (freezeDays <= 0) {

            throw new IllegalArgumentException(
                    "Freeze days must be greater than zero."
            );
        }


        freeze.setFreezeDays(
                freezeDays
        );


        // -------------------------------------------------
        // MAX FREEZE DAYS
        // -------------------------------------------------

        int maxFreezeDays =
                plan.getMaxFreezeDays() == null
                        ? 0
                        : plan.getMaxFreezeDays();


        int alreadyUsed =
                membership.getTotalFreezeDays() == null
                        ? 0
                        : membership.getTotalFreezeDays();


        if (alreadyUsed + freezeDays
                > maxFreezeDays) {

            throw new IllegalArgumentException(
                    "Maximum freeze days exceeded. Remaining freeze days: "
                            + Math.max(
                            0,
                            maxFreezeDays - alreadyUsed
                    )
            );
        }


        // -------------------------------------------------
        // FREEZE FEE
        // -------------------------------------------------

        BigDecimal freezeFee =
                safe(
                        plan.getFreezeFee()
                );


        freeze.setFreezeFee(
                freezeFee
        );


        // -------------------------------------------------
        // SAVE FREEZE
        // -------------------------------------------------

        freeze.setStatus(
                MembershipFreeze.FreezeStatus.ACTIVE
        );


        MembershipFreeze savedFreeze =
                membershipFreezeRepository.save(
                        freeze
                );


        // -------------------------------------------------
        // UPDATE MEMBERSHIP
        // -------------------------------------------------

        int newTotalFreezeDays =
                alreadyUsed + freezeDays;


        membership.setTotalFreezeDays(
                newTotalFreezeDays
        );

        membership.setStatus(
                true
        );

        membership.setMembershipState(
                MemberMembership.MembershipState.FROZEN
        );


        membership.setMembershipState(
                MemberMembership.MembershipState.FROZEN
        );


        // Extend membership expiry by freeze days.

        LocalDate newEndDate =
                membership.getEndDate()
                        .plusDays(
                                freezeDays
                        );


        membership.setEndDate(
                newEndDate
        );


        memberMembershipRepository.save(
                membership
        );


        return savedFreeze;
    }


    // =====================================================
// COMPLETE FREEZE
// =====================================================

    @Transactional
    public MembershipFreeze completeFreeze(
            int freezeId) {

        MembershipFreeze freeze =
                membershipFreezeRepository.findById(
                        freezeId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Freeze not found."
                        )
                );


        // -------------------------------------------------
        // CHECK FREEZE STATUS
        // -------------------------------------------------

        if (freeze.getStatus()
                != MembershipFreeze.FreezeStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Only an active freeze can be completed."
            );
        }


        // -------------------------------------------------
        // COMPLETE FREEZE
        // -------------------------------------------------

        freeze.setStatus(
                MembershipFreeze.FreezeStatus.COMPLETED
        );


        MembershipFreeze savedFreeze =
                membershipFreezeRepository.save(
                        freeze
                );


        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipRepository.findById(
                        freeze.getMemberMembership()
                                .getMemberMembershipId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership not found."
                        )
                );


        // -------------------------------------------------
        // DO NOT AUTO-RESUME CANCELLED MEMBERSHIP
        // -------------------------------------------------

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            return savedFreeze;
        }


        // -------------------------------------------------
        // RESTORE MEMBERSHIP
        // -------------------------------------------------

        if (Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            membership.setMembershipState(
                    MemberMembership.MembershipState.ACTIVE
            );


            memberMembershipRepository.save(
                    membership
            );
        }


        return savedFreeze;
    }

    // =====================================================
// PROCESS EXPIRED FREEZES
// =====================================================

    @Transactional
    public int processExpiredFreezes() {

        LocalDate today =
                LocalDate.now();


        // -------------------------------------------------
        // FIND ACTIVE FREEZES WHOSE END DATE HAS PASSED
        // -------------------------------------------------

        List<MembershipFreeze> freezes =
                membershipFreezeRepository
                        .findByStatusAndFreezeEndDateBefore(
                                MembershipFreeze.FreezeStatus.ACTIVE,
                                today
                        );


        int completedCount = 0;


        // -------------------------------------------------
        // COMPLETE EACH FREEZE
        // -------------------------------------------------

        for (MembershipFreeze freeze
                : freezes) {

            completeFreeze(
                    freeze.getMembershipFreezeId()
            );


            completedCount++;
        }


        return completedCount;
    }

    // =====================================================
    // CANCEL FREEZE MANUALLY
    // =====================================================

    public MembershipFreeze cancelFreeze(
            int freezeId) {

        MembershipFreeze freeze =
                membershipFreezeRepository
                        .findById(
                                freezeId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Freeze not found."
                                )
                        );


        if (freeze.getStatus()
                != MembershipFreeze.FreezeStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Only an active freeze can be cancelled."
            );
        }


        freeze.setStatus(
                MembershipFreeze.FreezeStatus.CANCELLED
        );


        MembershipFreeze savedFreeze =
                membershipFreezeRepository.save(
                        freeze
                );


        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                freeze.getMemberMembership()
                                        .getMemberMembershipId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Membership not found."
                                )
                        );


        boolean anotherActiveFreeze =
                hasAnotherActiveFreeze(
                        membership.getTenant().getTenantId(),
                        membership.getMemberMembershipId(),
                        freeze.getMembershipFreezeId()
                );


        if (!anotherActiveFreeze
                && Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            membership.setMembershipState(
                    MemberMembership.MembershipState.ACTIVE
            );

            membership.setStatus(
                    true
            );

            memberMembershipRepository.save(
                    membership
            );
        }


        return savedFreeze;
    }


    // =====================================================
    // AUTOMATICALLY COMPLETE EXPIRED FREEZES
    // =====================================================

    @Transactional
    public int completeExpiredFreezes(
            int tenantId) {

        LocalDate today =
                LocalDate.now();


        List<MembershipFreeze> freezes =
                membershipFreezeRepository
                        .findByTenant_TenantIdAndStatusAndFreezeEndDateBeforeOrderByFreezeEndDateAsc(
                                tenantId,
                                MembershipFreeze.FreezeStatus.ACTIVE,
                                today
                        );


        int completedCount = 0;


        for (MembershipFreeze freeze
                : freezes) {

            MemberMembership membership =
                    memberMembershipRepository
                            .findById(
                                    freeze.getMemberMembership()
                                            .getMemberMembershipId()
                            )
                            .orElse(null);


            if (membership == null) {
                continue;
            }


            // Complete this freeze.

            freeze.setStatus(
                    MembershipFreeze.FreezeStatus.COMPLETED
            );


            membershipFreezeRepository.save(
                    freeze
            );


            // Check for another active freeze.

            boolean anotherActiveFreeze =
                    hasAnotherActiveFreeze(
                            tenantId,
                            membership.getMemberMembershipId(),
                            freeze.getMembershipFreezeId()
                    );


            /*
             * Return membership to ACTIVE only when:
             *
             * 1. No other active freeze exists.
             * 2. Membership record is active.
             * 3. Membership is currently FROZEN.
             *
             * We do not touch EXPIRED or CANCELLED states.
             */

            if (!anotherActiveFreeze
                    && Boolean.TRUE.equals(
                    membership.getStatus()
            )
                    && membership.getMembershipState()
                    == MemberMembership.MembershipState.FROZEN) {

                membership.setMembershipState(
                        MemberMembership.MembershipState.ACTIVE
                );

                membership.setStatus(
                        true
                );

                memberMembershipRepository.save(
                        membership
                );
            }


            completedCount++;
        }


        return completedCount;
    }


    // =====================================================
    // CHECK ANOTHER ACTIVE FREEZE
    // =====================================================

    private boolean hasAnotherActiveFreeze(
            int tenantId,
            int membershipId,
            int currentFreezeId) {

        List<MembershipFreeze> freezes =
                membershipFreezeRepository
                        .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByFreezeStartDateDesc(
                                tenantId,
                                membershipId
                        );


        for (MembershipFreeze freeze
                : freezes) {

            if (freeze.getMembershipFreezeId()
                    != currentFreezeId
                    && freeze.getStatus()
                    == MembershipFreeze.FreezeStatus.ACTIVE) {

                return true;
            }
        }


        return false;
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<MembershipFreeze> findById(
            int freezeId) {

        return membershipFreezeRepository.findById(
                freezeId
        );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipFreeze> findByMembership(
            int tenantId,
            int membershipId) {

        return membershipFreezeRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByFreezeStartDateDesc(
                        tenantId,
                        membershipId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipFreeze> findByMember(
            int tenantId,
            int memberId) {

        return membershipFreezeRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByFreezeStartDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipFreeze> findByStatus(
            int tenantId,
            MembershipFreeze.FreezeStatus status) {

        return membershipFreezeRepository
                .findByTenant_TenantIdAndStatusOrderByFreezeStartDateDesc(
                        tenantId,
                        status
                );
    }


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    @Transactional(readOnly = true)
    public List<MembershipFreeze> findByDateRange(
            int tenantId,
            LocalDate startDate,
            LocalDate endDate) {

        return membershipFreezeRepository
                .findByTenant_TenantIdAndFreezeStartDateBetweenOrderByFreezeStartDateAsc(
                        tenantId,
                        startDate,
                        endDate
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