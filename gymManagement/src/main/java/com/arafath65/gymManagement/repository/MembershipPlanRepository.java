package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository
        extends JpaRepository<MembershipPlan, Integer> {

    // =====================================================
    // FIND ALL TENANT PLANS
    // =====================================================

    List<MembershipPlan>
    findByTenant_TenantIdOrderByPlanNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND ACTIVE TENANT PLANS
    // =====================================================

    List<MembershipPlan>
    findByTenant_TenantIdAndStatusTrueOrderByPlanNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND INACTIVE TENANT PLANS
    // =====================================================

    List<MembershipPlan>
    findByTenant_TenantIdAndStatusFalseOrderByPlanNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY PLAN ID + TENANT
    // =====================================================

    Optional<MembershipPlan>
    findByMembershipPlanIdAndTenant_TenantId(
            Integer membershipPlanId,
            Integer tenantId
    );


    // =====================================================
    // FIND BY PLAN CODE
    // =====================================================

    Optional<MembershipPlan>
    findByTenant_TenantIdAndPlanCode(
            Integer tenantId,
            String planCode
    );


    // =====================================================
    // CHECK DUPLICATE PLAN CODE
    // =====================================================

    boolean
    existsByTenant_TenantIdAndPlanCode(
            Integer tenantId,
            String planCode
    );


    // =====================================================
    // CHECK ACTIVE PLAN
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMembershipPlanIdAndStatusTrue(
            Integer tenantId,
            Integer membershipPlanId
    );
}