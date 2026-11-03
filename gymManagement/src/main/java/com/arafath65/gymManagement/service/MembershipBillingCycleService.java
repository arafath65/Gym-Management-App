package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MembershipBillingCycleRequest;
import com.arafath65.gymManagement.entity.MembershipBillingCycle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipBillingCycleService {

    // =====================================================
    // CREATE
    // =====================================================

    MembershipBillingCycle createCycle(
            Integer tenantId,
            MembershipBillingCycleRequest request
    );


    // =====================================================
    // FIND BY ID
    // =====================================================

    Optional<MembershipBillingCycle> findById(
            Integer tenantId,
            Integer billingCycleId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipBillingCycle> findByMembership(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipBillingCycle> findByMember(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipBillingCycle> findByBranch(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND CURRENT CYCLE
    // =====================================================

    Optional<MembershipBillingCycle> findCurrentCycle(
            Integer tenantId,
            Integer memberMembershipId,
            LocalDate date
    );


    // =====================================================
    // ACTIVATE CYCLE
    // =====================================================

    MembershipBillingCycle activateCycle(
            Integer tenantId,
            Integer billingCycleId
    );


    // =====================================================
    // CANCEL CYCLE
    // =====================================================

    void cancelCycle(
            Integer tenantId,
            Integer billingCycleId
    );


    // =====================================================
    // DELETE
    // =====================================================

    void delete(
            Integer tenantId,
            Integer billingCycleId
    );

    // =====================================================
// STATUS MANAGEMENT
// =====================================================

    MembershipBillingCycle refreshStatus(
            Integer tenantId,
            Integer billingCycleId
    );


// =====================================================
// EXPIRE COMPLETED PERIODS
// =====================================================

    int expireCompletedCycles(
            Integer tenantId,
            LocalDate date
    );


// =====================================================
// FIND OUTSTANDING CYCLES
// =====================================================

    List<MembershipBillingCycle> findOutstandingCycles(
            Integer tenantId,
            Integer memberMembershipId
    );
}