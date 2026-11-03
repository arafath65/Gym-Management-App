package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPlanBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanBenefitRepository
        extends JpaRepository<MembershipPlanBenefit, Integer> {

    // =====================================================
    // FIND ALL BENEFITS OF A PLAN
    // =====================================================

    List<MembershipPlanBenefit>
    findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndStatusTrue(
            Integer tenantId,
            Integer membershipPlanId
    );


    // =====================================================
    // FIND SPECIFIC PLAN + BENEFIT
    // =====================================================

    Optional<MembershipPlanBenefit>
    findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBenefit_BenefitId(
            Integer tenantId,
            Integer membershipPlanId,
            Integer benefitId
    );


    // =====================================================
    // CHECK EXISTING PLAN BENEFIT
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBenefit_BenefitId(
            Integer tenantId,
            Integer membershipPlanId,
            Integer benefitId
    );


    // =====================================================
    // DELETE / DEACTIVATE SUPPORT
    // =====================================================

    Optional<MembershipPlanBenefit>
    findByMembershipPlanBenefitIdAndTenant_TenantId(
            Integer membershipPlanBenefitId,
            Integer tenantId
    );
}