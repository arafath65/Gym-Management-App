package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.DeviceActivationRequest;
import com.arafath65.gymManagement.dto.response.DeviceActivationResponse;
import com.arafath65.gymManagement.entity.ActivationCode;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.exception.ResourceNotFoundException;
import com.arafath65.gymManagement.repository.ActivationCodeRepository;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.service.DeviceActivationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeviceActivationServiceImpl
        implements DeviceActivationService {

    private final ActivationCodeRepository activationCodeRepository;
    private final RegisteredDeviceRepository registeredDeviceRepository;
    private final BranchRepository branchRepository;

    public DeviceActivationServiceImpl(
            ActivationCodeRepository activationCodeRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository) {

        this.activationCodeRepository = activationCodeRepository;
        this.registeredDeviceRepository = registeredDeviceRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public DeviceActivationResponse activate(
            DeviceActivationRequest request) {

        // 1. Find activation code
        ActivationCode activationCode =
                activationCodeRepository
                        .findByActivationCode(
                                request.getActivationCode())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invalid activation code."));

        // 2. Check activation code status
        if (!Boolean.TRUE.equals(activationCode.getStatus())) {

            throw new BadRequestException(
                    "This activation code is inactive.");
        }

        // 3. Check whether already used
        if (Boolean.TRUE.equals(activationCode.getUsed())) {

            throw new BadRequestException(
                    "This activation code has already been used.");
        }

        // 4. Check expiry
        if (activationCode.getExpiresAt() == null ||
                activationCode.getExpiresAt()
                        .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "This activation code has expired.");
        }

        // 5. Get Tenant
        Tenant tenant = activationCode.getTenant();

        if (tenant == null) {

            throw new BadRequestException(
                    "Activation code has no tenant assigned.");
        }

        // 6. Check Tenant status
        if (!Boolean.TRUE.equals(tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }

        // 7. Get Branch
        Branch branch = activationCode.getBranch();

        if (branch == null) {

            throw new BadRequestException(
                    "Activation code has no branch assigned.");
        }

        // 8. Check Branch status
        if (!Boolean.TRUE.equals(
                branch.getStatus())) {

            throw new BadRequestException(
                    "Branch is inactive.");
        }

        // 9. Check whether this device UUID
        // is already registered
        if (registeredDeviceRepository
                .existsByDeviceUuid(
                        request.getDeviceUuid())) {

            throw new BadRequestException(
                    "This device is already registered.");
        }

        // 10. Generate unique device token
        String deviceToken;

        do {

            deviceToken = UUID.randomUUID().toString();

        } while (registeredDeviceRepository
                .existsByDeviceToken(deviceToken));

        // 11. Create Registered Device
        RegisteredDevice device =
                new RegisteredDevice();

        device.setTenant(tenant);
        device.setBranch(branch);
        device.setActivationCode(activationCode);

        device.setDeviceUuid(
                request.getDeviceUuid());

        device.setDeviceToken(
                deviceToken);

        device.setDeviceName(
                request.getDeviceName());

        device.setModel(
                request.getModel());

        device.setAndroidVersion(
                request.getAndroidVersion());

        device.setAppVersion(
                request.getAppVersion());

        device.setFirebaseToken(
                request.getFirebaseToken());

        device.setRegisteredAt(
                LocalDateTime.now());

        device.setStatus(true);

        // 12. Save device
        RegisteredDevice savedDevice =
                registeredDeviceRepository.save(device);

        // 13. Mark activation code as USED
        activationCode.setUsed(true);

        activationCode.setUsedAt(
                LocalDateTime.now());

        activationCodeRepository.save(
                activationCode);

        // 14. Create response
        DeviceActivationResponse response =
                new DeviceActivationResponse();

        response.setRegistered(true);

        response.setMessage(
                "Device activated successfully.");

        response.setDeviceToken(
                savedDevice.getDeviceToken());

        response.setDeviceId(
                savedDevice.getDeviceId());

        response.setTenantNo(
                tenant.getTenantNo());

        response.setGymName(
                tenant.getGymName());

        response.setBranchNo(
                branch.getBranchNo());

        response.setBranchName(
                branch.getBranchName());

        return response;
    }
}