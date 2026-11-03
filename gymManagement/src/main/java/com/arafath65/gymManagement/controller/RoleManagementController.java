package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.RoleCreateRequest;
import com.arafath65.gymManagement.dto.request.RolePermissionsRequest;
import com.arafath65.gymManagement.dto.response.PermissionResponse;
import com.arafath65.gymManagement.dto.response.RoleResponse;
import com.arafath65.gymManagement.service.RoleManagementService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleManagementController {
    private final RoleManagementService service;
    public RoleManagementController(RoleManagementService service) { this.service = service; }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> permissions() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Permissions retrieved.", service.getPermissions()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> roles() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Roles retrieved.", service.getRoles()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Role created.", service.createRole(request)));
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<RoleResponse>> replacePermissions(@PathVariable Integer roleId, @Valid @RequestBody RolePermissionsRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Role permissions updated.", service.replacePermissions(roleId, request)));
    }
}
