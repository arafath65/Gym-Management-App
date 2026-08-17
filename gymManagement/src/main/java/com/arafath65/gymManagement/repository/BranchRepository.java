package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository
        extends JpaRepository<Branch, Integer> {

    Optional<Branch> findByTenant_TenantIdAndBranchNo(
            Integer tenantId,
            String branchNo
    );

    List<Branch> findByTenant_TenantId(
            Integer tenantId
    );

    List<Branch> findByTenant_TenantIdAndStatusTrue(
            Integer tenantId
    );
}