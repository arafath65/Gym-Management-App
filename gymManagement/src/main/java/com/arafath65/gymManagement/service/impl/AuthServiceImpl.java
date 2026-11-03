package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.LoginRequest;
import com.arafath65.gymManagement.dto.response.LoginResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Role;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.entity.User;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.repository.UserRepository;
import com.arafath65.gymManagement.service.AuthService;
import com.arafath65.gymManagement.service.UserSessionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RegisteredDeviceRepository registeredDeviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionService userSessionService;

    public AuthServiceImpl(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            PasswordEncoder passwordEncoder,
            UserSessionService userSessionService) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.registeredDeviceRepository = registeredDeviceRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSessionService = userSessionService;
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public LoginResponse login(LoginRequest request) {

        // -----------------------------------------------------
        // 1. Find Tenant
        // -----------------------------------------------------

        Tenant tenant = tenantRepository
                .findByTenantNo(request.getTenantNo())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid tenant number."
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
        // 3. Find Registered Device
        // -----------------------------------------------------

        RegisteredDevice device =
                registeredDeviceRepository
                        .findByDeviceToken(
                                request.getDeviceToken()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Device is not registered."
                                ));


        // -----------------------------------------------------
        // 4. Check Device Status
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                device.getStatus())) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


        // -----------------------------------------------------
        // 5. Check Device belongs to Tenant
        // -----------------------------------------------------

        if (device.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        // -----------------------------------------------------
        // 6. Find User
        // -----------------------------------------------------

        User user = userRepository
                .findByTenantTenantIdAndUsername(
                        tenant.getTenantId(),
                        request.getUsername()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid username or password."
                        ));


        // -----------------------------------------------------
        // 7. Check User Status
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(
                user.getStatus())) {

            throw new BadRequestException(
                    "User account is inactive."
            );
        }


        // -----------------------------------------------------
        // 8. Check Password
        // -----------------------------------------------------

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Invalid username or password."
            );
        }


        // -----------------------------------------------------
        // 9. Update Last Login
        // -----------------------------------------------------

        user.setLastLogin(
                LocalDateTime.now()
        );

        userRepository.save(user);


        // -----------------------------------------------------
        // 10. Update Device Last Login
        // -----------------------------------------------------

        device.setLastLogin(
                LocalDateTime.now()
        );

        registeredDeviceRepository.save(device);


        // -----------------------------------------------------
        // 11. Build Response
        // -----------------------------------------------------

        LoginResponse response = mapToResponse(user, tenant);
        UserSessionService.IssuedSession issued = userSessionService.create(user);
        response.setSessionToken(issued.token());
        response.setSessionExpiresAt(issued.expiresAt().toString());
        return response;
    }


    // =========================================================
    // MAP RESPONSE
    // =========================================================

    private LoginResponse mapToResponse(
            User user,
            Tenant tenant) {

        LoginResponse response =
                new LoginResponse();


        // -----------------------------------------------------
        // User
        // -----------------------------------------------------

        response.setUserId(
                user.getUserId()
        );

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


        // -----------------------------------------------------
        // Tenant
        // -----------------------------------------------------

        response.setTenantId(
                tenant.getTenantId()
        );

        response.setTenantNo(
                tenant.getTenantNo()
        );

        response.setGymName(
                tenant.getGymName()
        );


        // -----------------------------------------------------
        // Branch
        // -----------------------------------------------------

        Branch branch =
                user.getBranch();

        if (branch != null) {

            response.setBranchId(
                    branch.getBranchId()
            );

            response.setBranchNo(
                    branch.getBranchNo()
            );

            response.setBranchName(
                    branch.getBranchName()
            );
        }


        // -----------------------------------------------------
        // Role
        // -----------------------------------------------------

        Role role =
                user.getRole();

        if (role != null) {

            response.setRoleId(
                    role.getRoleId()
            );

            response.setRoleName(
                    role.getRoleName()
            );
        }

        return response;
    }
}