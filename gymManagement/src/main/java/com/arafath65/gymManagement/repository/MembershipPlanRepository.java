package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository
        extends JpaRepository<MembershipPlan, Integer> {

    List<MembershipPlan> findByTenant_TenantIdOrderByPlanNameAsc(
            Integer tenantId
    );

    List<MembershipPlan> findByTenant_TenantIdAndBranch_BranchIdOrderByPlanNameAsc(
            Integer tenantId,
            Integer branchId
    );

    List<MembershipPlan> findByTenant_TenantIdAndStatusTrueOrderByPlanNameAsc(
            Integer tenantId
    );

    List<MembershipPlan> findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueOrderByPlanNameAsc(
            Integer tenantId,
            Integer branchId
    );

    Optional<MembershipPlan> findByTenant_TenantIdAndPlanCode(
            Integer tenantId,
            String planCode
    );

    Optional<MembershipPlan> findByTenant_TenantIdAndBranch_BranchIdAndPlanCode(
            Integer tenantId,
            Integer branchId,
            String planCode
    );

    boolean existsByTenant_TenantIdAndPlanCode(
            Integer tenantId,
            String planCode
    );

    boolean existsByTenant_TenantIdAndBranch_BranchIdAndPlanCode(
            Integer tenantId,
            Integer branchId,
            String planCode
    );
}