package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByPermissionCode(String permissionCode);
    List<Permission> findAllByStatusTrueOrderByModuleAscScreenAscActionAsc();
}
