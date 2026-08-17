package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.DeviceVerifyRequest;
import com.arafath65.gymManagement.dto.response.DeviceVerifyResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.service.DeviceVerifyService;
import org.springframework.stereotype.Service;

@Service
public class DeviceVerifyServiceImpl implements DeviceVerifyService {

    private final RegisteredDeviceRepository registeredDeviceRepository;

    public DeviceVerifyServiceImpl(
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }

    @Override
    public DeviceVerifyResponse verify(
            DeviceVerifyRequest request) {

        // 1. Find device using BOTH UUID and token
        RegisteredDevice device =
                registeredDeviceRepository
                        .findByDeviceUuidAndDeviceToken(
                                request.getDeviceUuid(),
                                request.getDeviceToken()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Device is not registered."
                                ));

        // 2. Check device status
        if (!Boolean.TRUE.equals(device.getStatus())) {

            throw new BadRequestException(
                    "This device has been deactivated."
            );
        }

        // 3. Get tenant
        Tenant tenant = device.getTenant();

        if (tenant == null) {

            throw new BadRequestException(
                    "Device has no tenant assigned."
            );
        }

        // 4. Check tenant status
        if (!Boolean.TRUE.equals(tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }

        // 5. Get branch
        Branch branch = device.getBranch();

        if (branch == null) {

            throw new BadRequestException(
                    "Device has no branch assigned."
            );
        }

        // 6. Check branch status
        if (!Boolean.TRUE.equals(
                branch.getStatus())) {

            throw new BadRequestException(
                    "Branch is inactive."
            );
        }

        // 7. Create response
        DeviceVerifyResponse response =
                new DeviceVerifyResponse();

        response.setRegistered(true);

        response.setMessage(
                "Device verified successfully."
        );

        response.setDeviceId(
                device.getDeviceId()
        );

        response.setTenantNo(
                tenant.getTenantNo()
        );

        response.setGymName(
                tenant.getGymName()
        );

        response.setBranchNo(
                branch.getBranchNo()
        );

        response.setBranchName(
                branch.getBranchName()
        );

        return response;
    }
}