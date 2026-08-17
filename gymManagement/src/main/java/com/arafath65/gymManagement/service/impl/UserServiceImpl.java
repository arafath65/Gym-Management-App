package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.UserCreateRequest;
import com.arafath65.gymManagement.dto.response.UserResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Role;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.entity.User;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RoleRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.repository.UserRepository;
import com.arafath65.gymManagement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            BranchRepository branchRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.branchRepository = branchRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    @Override
    public UserResponse save(UserCreateRequest request) {

        // -----------------------------------------------------
        // 1. Find Tenant
        // -----------------------------------------------------

        Tenant tenant = tenantRepository
                .findById(request.getTenantId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Tenant not found."
                        ));


        // -----------------------------------------------------
        // 2. Check Tenant Status
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // -----------------------------------------------------
        // 3. Check Username
        // -----------------------------------------------------

        if (userRepository
                .existsByTenantTenantIdAndUsername(
                        request.getTenantId(),
                        request.getUsername())) {

            throw new BadRequestException(
                    "Username already exists for this tenant."
            );
        }


        // -----------------------------------------------------
        // 4. Find Role
        // -----------------------------------------------------

        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Role not found."
                        ));


        // -----------------------------------------------------
        // 5. Check Role Status
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                role.getStatus())) {

            throw new BadRequestException(
                    "Selected role is inactive."
            );
        }


        // -----------------------------------------------------
        // 6. Find Branch
        // -----------------------------------------------------

        Branch branch = null;

        if (request.getBranchId() != null) {

            branch = branchRepository
                    .findById(request.getBranchId())
                    .orElseThrow(() ->
                            new BadRequestException(
                                    "Branch not found."
                            ));


            // -------------------------------------------------
            // 7. Check Branch belongs to Tenant
            // -------------------------------------------------

            if (branch.getTenant()
                    .getTenantId() != tenant.getTenantId()) {

                throw new BadRequestException(
                        "Branch does not belong to the selected tenant."
                );
            }


            // -------------------------------------------------
            // 8. Check Branch Status
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    branch.getStatus())) {

                throw new BadRequestException(
                        "Branch is inactive."
                );
            }
        }


        // -----------------------------------------------------
        // 9. Create User
        // -----------------------------------------------------

        User user = new User();

        user.setTenant(tenant);
        user.setBranch(branch);
        user.setRole(role);

        user.setUsername(
                request.getUsername()
        );

        // IMPORTANT:
        // Never store plain password.

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFullName(
                request.getFullName()
        );

        user.setProfileImage(
                request.getProfileImage()
        );

        user.setMobile(
                request.getMobile()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setStatus(true);


        // -----------------------------------------------------
        // 10. Save
        // -----------------------------------------------------

        User savedUser =
                userRepository.save(user);


        // -----------------------------------------------------
        // 11. Build Response
        // -----------------------------------------------------

        return mapToResponse(savedUser);
    }


    // =========================================================
    // MAP ENTITY → RESPONSE
    // =========================================================

    private UserResponse mapToResponse(
            User user) {

        UserResponse response =
                new UserResponse();

        response.setUserId(
                user.getUserId()
        );


        // Tenant

        response.setTenantId(
                user.getTenant()
                        .getTenantId()
        );

        response.setTenantNo(
                user.getTenant()
                        .getTenantNo()
        );


        // Branch

        if (user.getBranch() != null) {

            response.setBranchId(
                    user.getBranch()
                            .getBranchId()
            );

            response.setBranchNo(
                    user.getBranch()
                            .getBranchNo()
            );

            response.setBranchName(
                    user.getBranch()
                            .getBranchName()
            );
        }


        // Role

        response.setRoleId(
                user.getRole()
                        .getRoleId()
        );

        response.setRoleName(
                user.getRole()
                        .getRoleName()
        );


        // User information

        response.setUsername(
                user.getUsername()
        );

        response.setFullName(
                user.getFullName()
        );

        response.setProfileImage(
                user.getProfileImage()
        );

        response.setMobile(
                user.getMobile()
        );

        response.setEmail(
                user.getEmail()
        );

        response.setStatus(
                user.getStatus()
        );

        return response;
    }
}