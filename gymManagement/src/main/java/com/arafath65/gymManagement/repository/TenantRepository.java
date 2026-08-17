package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {

    Optional<Tenant> findByTenantNo(String tenantNo);

}