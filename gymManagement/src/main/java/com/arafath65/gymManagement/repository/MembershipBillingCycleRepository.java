package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import com.arafath65.gymManagement.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipBillingCycleRepository
        extends JpaRepository<
        MembershipBillingCycle,
        Integer> {


    // =====================================================
    // FIND BY TENANT
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdOrderByPeriodStartDesc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPeriodStartDesc(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByPeriodStartDesc(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndBranch_BranchIdOrderByPeriodStartDesc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY CYCLE NUMBER
    // =====================================================

    Optional<MembershipBillingCycle>
    findByTenant_TenantIdAndCycleNumber(
            Integer tenantId,
            String cycleNumber
    );


    // =====================================================
    // FIND BY MEMBERSHIP + CYCLE NUMBER
    // =====================================================

    Optional<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndCycleNumber(
            Integer tenantId,
            Integer memberMembershipId,
            String cycleNumber
    );


    // =====================================================
    // FIND CURRENT / SPECIFIC PERIOD
    // =====================================================

    Optional<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            Integer tenantId,
            Integer memberMembershipId,
            LocalDate periodStart,
            LocalDate periodEnd
    );


    // =====================================================
    // FIND BY STATUS
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndStatusOrderByPeriodStartDesc(
            Integer tenantId,
            MembershipBillingCycle.CycleStatus status
    );


    // =====================================================
    // FIND BY BILLING MODE
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndBillingModeOrderByPeriodStartDesc(
            Integer tenantId,
            MembershipPlan.AccessBillingMode billingMode
    );


    // =====================================================
    // FIND BY MEMBERSHIP + BILLING MODE
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBillingModeOrderByPeriodStartDesc(
            Integer tenantId,
            Integer memberMembershipId,
            MembershipPlan.AccessBillingMode billingMode
    );


    // =====================================================
    // FIND PERIOD RANGE
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndPeriodStartBetweenOrderByPeriodStartAsc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // FIND BRANCH + PERIOD RANGE
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndBranch_BranchIdAndPeriodStartBetweenOrderByPeriodStartAsc(
            Integer tenantId,
            Integer branchId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // CHECK EXISTING PERIOD
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndPeriodStartAndPeriodEnd(
            Integer tenantId,
            Integer memberMembershipId,
            LocalDate periodStart,
            LocalDate periodEnd
    );


    // =====================================================
    // CHECK EXISTING STATUS
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatus(
            Integer tenantId,
            Integer memberMembershipId,
            MembershipBillingCycle.CycleStatus status
    );


    // =====================================================
    // FIND COMPLETED PERIODS
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndPeriodEndBeforeAndStatusNot(
            Integer tenantId,
            LocalDate date,
            MembershipBillingCycle.CycleStatus status
    );


    // =====================================================
    // FIND OUTSTANDING CYCLES
    // =====================================================

    List<MembershipBillingCycle>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBalanceAmountGreaterThanAndStatusNotOrderByPeriodStartAsc(
            Integer tenantId,
            Integer memberMembershipId,
            BigDecimal balanceAmount,
            MembershipBillingCycle.CycleStatus status
    );
}