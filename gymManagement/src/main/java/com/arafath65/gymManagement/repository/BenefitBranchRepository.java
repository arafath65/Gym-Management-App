package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.BenefitBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitBranchRepository
        extends JpaRepository<BenefitBranch, Integer> {

    // =====================================================
    // TENANT
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantId(
            Integer tenantId
    );


    // =====================================================
    // BENEFIT
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantIdAndBenefit_BenefitId(
            Integer tenantId,
            Integer benefitId
    );


    // =====================================================
    // BRANCH
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantIdAndBranch_BranchId(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY BRANCH
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusTrue(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BENEFITS BY BRANCH
    //
    // Returns only:
    // 1. Active branch assignment
    // 2. Active benefit
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueAndBenefit_StatusTrueOrderByBenefit_BenefitNameAsc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY BENEFIT
    // =====================================================

    List<BenefitBranch>
    findByTenant_TenantIdAndBenefit_BenefitIdAndStatusTrue(
            Integer tenantId,
            Integer benefitId
    );


    // =====================================================
    // SPECIFIC ASSIGNMENT
    // =====================================================

    Optional<BenefitBranch>
    findByTenant_TenantIdAndBenefit_BenefitIdAndBranch_BranchId(
            Integer tenantId,
            Integer benefitId,
            Integer branchId
    );

    Optional<BenefitBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndBenefit_BenefitIdAndStatusTrue(
            Integer tenantId,
            Integer branchId,
            Integer benefitId
    );


    // =====================================================
    // EXISTS
    // =====================================================

    boolean existsByTenant_TenantIdAndBenefit_BenefitIdAndBranch_BranchId(
            Integer tenantId,
            Integer benefitId,
            Integer branchId
    );
}