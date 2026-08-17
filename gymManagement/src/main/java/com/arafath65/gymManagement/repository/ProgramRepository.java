package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository
        extends JpaRepository<Program, Integer> {


    // =====================================================
    // FIND ALL TENANT PROGRAMS
    // =====================================================

    List<Program>
    findByTenant_TenantIdOrderByProgramNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND ACTIVE TENANT PROGRAMS
    // =====================================================

    List<Program>
    findByTenant_TenantIdAndStatusTrueOrderByProgramNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<Program>
    findByTenant_TenantIdAndBranch_BranchIdOrderByProgramNameAsc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND ACTIVE BY BRANCH
    // =====================================================

    List<Program>
    findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueOrderByProgramNameAsc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY CODE
    // =====================================================

    Optional<Program>
    findByTenant_TenantIdAndProgramCode(
            Integer tenantId,
            String programCode
    );


    // =====================================================
    // EXISTS CODE
    // =====================================================

    boolean existsByTenant_TenantIdAndProgramCode(
            Integer tenantId,
            String programCode
    );
}