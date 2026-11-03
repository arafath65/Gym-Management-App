package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRenewalRepository
        extends JpaRepository<MembershipRenewal, Integer> {


    // =====================================================
    // FIND ALL TENANT RENEWALS
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdOrderByRenewalDateDesc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndBranch_BranchIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY RENEWAL NUMBER
    // =====================================================

    Optional<MembershipRenewal>
    findByTenant_TenantIdAndRenewalNumber(
            Integer tenantId,
            String renewalNumber
    );


    // =====================================================
    // EXISTENCE CHECK
    // =====================================================

    boolean
    existsByTenant_TenantIdAndRenewalNumber(
            Integer tenantId,
            String renewalNumber
    );


    // =====================================================
    // ACTIVE RENEWALS
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndStatusOrderByRenewalDateDesc(
            Integer tenantId,
            Boolean status
    );


    // =====================================================
    // MEMBERSHIP + STATUS
    // =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusOrderByRenewalDateDesc(
            Integer tenantId,
            Integer memberMembershipId,
            Boolean status
    );

    // =====================================================
// FIND BY RENEWAL DATE RANGE
// =====================================================

    List<MembershipRenewal>
    findByTenant_TenantIdAndRenewalDateBetweenOrderByRenewalDateDesc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );
}