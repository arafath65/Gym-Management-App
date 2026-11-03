package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ProgramBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramBranchRepository
        extends JpaRepository<ProgramBranch, Integer> {

    // =====================================================
    // TENANT
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantId(
            Integer tenantId
    );


    // =====================================================
    // PROGRAM
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantIdAndProgram_ProgramId(
            Integer tenantId,
            Integer programId
    );


    // =====================================================
    // BRANCH
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantIdAndBranch_BranchId(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY BRANCH
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusTrue(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY PROGRAM
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantIdAndProgram_ProgramIdAndStatusTrue(
            Integer tenantId,
            Integer programId
    );


    // =====================================================
    // SPECIFIC ASSIGNMENT
    // =====================================================

    Optional<ProgramBranch>
    findByTenant_TenantIdAndProgram_ProgramIdAndBranch_BranchId(
            Integer tenantId,
            Integer programId,
            Integer branchId
    );


    // =====================================================
    // EXISTS
    // =====================================================

    boolean
    existsByTenant_TenantIdAndProgram_ProgramIdAndBranch_BranchId(
            Integer tenantId,
            Integer programId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE PROGRAMS FOR BRANCH
    // =====================================================

    List<ProgramBranch>
    findByTenant_TenantIdAndBranch_BranchIdAndProgram_StatusTrueAndStatusTrue(
            Integer tenantId,
            Integer branchId
    );
}