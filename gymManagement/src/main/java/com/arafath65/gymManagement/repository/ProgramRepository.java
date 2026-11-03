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
    // TENANT
    // =====================================================

    List<Program>
    findByTenant_TenantIdOrderByProgramNameAsc(
            Integer tenantId
    );


    // =====================================================
    // ACTIVE TENANT
    // =====================================================

    List<Program>
    findByTenant_TenantIdAndStatusTrueOrderByProgramNameAsc(
            Integer tenantId
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