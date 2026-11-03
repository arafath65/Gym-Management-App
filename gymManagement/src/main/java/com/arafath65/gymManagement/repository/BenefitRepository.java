package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitRepository
        extends JpaRepository<Benefit, Integer> {

    // =====================================================
    // FIND BY TENANT
    // =====================================================

    List<Benefit> findByTenant_TenantIdOrderByBenefitNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND ACTIVE BY TENANT
    // =====================================================

    List<Benefit> findByTenant_TenantIdAndStatusTrueOrderByBenefitNameAsc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY TENANT + CODE
    // =====================================================

    Optional<Benefit> findByTenant_TenantIdAndBenefitCode(
            Integer tenantId,
            String benefitCode
    );


    // =====================================================
    // CHECK DUPLICATE CODE
    // =====================================================

    boolean existsByTenant_TenantIdAndBenefitCode(
            Integer tenantId,
            String benefitCode
    );
}