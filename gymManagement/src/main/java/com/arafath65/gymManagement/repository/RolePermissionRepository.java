package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findAllByRoleRoleId(Integer roleId);
    void deleteAllByRoleRoleId(Integer roleId);
    boolean existsByRoleRoleIdAndPermissionPermissionCode(Integer roleId, String permissionCode);

    boolean existsByRoleRoleIdAndPermissionPermissionCodeAndPermissionStatusTrue(
            Integer roleId,
            String permissionCode
    );
}
