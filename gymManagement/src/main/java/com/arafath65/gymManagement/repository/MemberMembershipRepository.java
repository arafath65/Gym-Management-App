package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MemberMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberMembershipRepository
        extends JpaRepository<MemberMembership, Integer> {


    // =====================================================
    // FIND ALL TENANT MEMBERSHIPS
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdOrderByStartDateDesc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdOrderByStartDateDesc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndMember_MemberIdOrderByStartDateDesc(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY MEMBER AND BRANCH
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdAndMember_MemberIdOrderByStartDateDesc(
            Integer tenantId,
            Integer branchId,
            Integer memberId
    );


    // =====================================================
    // FIND BY MEMBERSHIP NUMBER
    // =====================================================

    Optional<MemberMembership>
    findByTenant_TenantIdAndMembershipNumber(
            Integer tenantId,
            String membershipNumber
    );


    // =====================================================
    // FIND BY BRANCH AND MEMBERSHIP NUMBER
    // =====================================================

    Optional<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdAndMembershipNumber(
            Integer tenantId,
            Integer branchId,
            String membershipNumber
    );


    // =====================================================
    // FIND BY STATUS
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndStatusOrderByEndDateAsc(
            Integer tenantId,
            Boolean status
    );


    // =====================================================
    // FIND BY BRANCH AND STATUS
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusOrderByEndDateAsc(
            Integer tenantId,
            Integer branchId,
            Boolean status
    );


    // =====================================================
    // FIND BY END DATE RANGE
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndEndDateBetweenOrderByEndDateAsc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // FIND BY BRANCH AND END DATE RANGE
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdAndEndDateBetweenOrderByEndDateAsc(
            Integer tenantId,
            Integer branchId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // FIND BY MEMBER AND STATUS
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndMember_MemberIdAndStatus(
            Integer tenantId,
            Integer memberId,
            Boolean status
    );


    // =====================================================
    // FIND ACTIVE MEMBERSHIPS THAT HAVE EXPIRED
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndStatusTrueAndMembershipStateAndEndDateBefore(
            Integer tenantId,
            MemberMembership.MembershipState membershipState,
            LocalDate date
    );


    // =====================================================
    // FIND BY MEMBERSHIP STATE
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndMembershipStateOrderByEndDateAsc(
            Integer tenantId,
            MemberMembership.MembershipState membershipState
    );


    // =====================================================
    // FIND BY BRANCH AND MEMBERSHIP STATE
    // =====================================================

    List<MemberMembership>
    findByTenant_TenantIdAndBranch_BranchIdAndMembershipStateOrderByEndDateAsc(
            Integer tenantId,
            Integer branchId,
            MemberMembership.MembershipState membershipState
    );


    // =====================================================
    // EXISTENCE CHECK - MEMBERSHIP NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndMembershipNumber(
            Integer tenantId,
            String membershipNumber
    );


    // =====================================================
    // EXISTENCE CHECK - BRANCH + MEMBERSHIP NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndBranch_BranchIdAndMembershipNumber(
            Integer tenantId,
            Integer branchId,
            String membershipNumber
    );
}