package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPlanBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanBranchRepository
        extends JpaRepository<MembershipPlanBranch, Integer> {

    // =====================================================
    // TENANT
    // =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantId(Integer tenantId);


    // =====================================================
    // MEMBERSHIP PLAN
    // =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantIdAndMembershipPlan_MembershipPlanId(
            Integer tenantId,
            Integer membershipPlanId
    );


    // =====================================================
    // BRANCH
    // =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantIdAndBranch_BranchId(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY BRANCH
    // =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusTrue(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY PLAN
    // =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndStatusTrue(
            Integer tenantId,
            Integer membershipPlanId
    );


    // =====================================================
    // SPECIFIC ASSIGNMENT
    // =====================================================

    Optional<MembershipPlanBranch>
    findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
            Integer tenantId,
            Integer membershipPlanId,
            Integer branchId
    );


    // =====================================================
    // EXISTS
    // =====================================================

    boolean existsByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBranch_BranchId(
            Integer tenantId,
            Integer membershipPlanId,
            Integer branchId
    );

    // =====================================================
// ACTIVE PLANS FOR BRANCH
// =====================================================

    List<MembershipPlanBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndMembershipPlan_StatusTrueAndStatusTrue(
            Integer tenantId,
            Integer branchId
    );
}