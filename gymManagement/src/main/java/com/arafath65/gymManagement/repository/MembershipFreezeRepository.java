package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipFreeze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembershipFreezeRepository
        extends JpaRepository<MembershipFreeze, Integer> {

    List<MembershipFreeze>
    findByTenant_TenantIdOrderByFreezeStartDateDesc(
            Integer tenantId
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndBranch_BranchIdOrderByFreezeStartDateDesc(
            Integer tenantId,
            Integer branchId
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndMember_MemberIdOrderByFreezeStartDateDesc(
            Integer tenantId,
            Integer memberId
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByFreezeStartDateDesc(
            Integer tenantId,
            Integer memberMembershipId
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndBranch_BranchIdAndMemberMembership_MemberMembershipIdOrderByFreezeStartDateDesc(
            Integer tenantId,
            Integer branchId,
            Integer memberMembershipId
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndStatusOrderByFreezeStartDateDesc(
            Integer tenantId,
            MembershipFreeze.FreezeStatus status
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusOrderByFreezeStartDateDesc(
            Integer tenantId,
            Integer branchId,
            MembershipFreeze.FreezeStatus status
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndFreezeStartDateBetweenOrderByFreezeStartDateAsc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<MembershipFreeze>
    findByTenant_TenantIdAndBranch_BranchIdAndFreezeStartDateBetweenOrderByFreezeStartDateAsc(
            Integer tenantId,
            Integer branchId,
            LocalDate startDate,
            LocalDate endDate
    );

    // =====================================================
// FIND ACTIVE FREEZES WHOSE END DATE HAS PASSED
// =====================================================

    List<MembershipFreeze>
    findByStatusAndFreezeEndDateBefore(
            MembershipFreeze.FreezeStatus status,
            LocalDate date
    );


    // =====================================================
    // ACTIVE FREEZES WHOSE END DATE HAS PASSED
    // =====================================================

    List<MembershipFreeze>
    findByTenant_TenantIdAndStatusAndFreezeEndDateBeforeOrderByFreezeEndDateAsc(
            Integer tenantId,
            MembershipFreeze.FreezeStatus status,
            LocalDate date
    );
}