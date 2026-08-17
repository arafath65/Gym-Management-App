package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.DeviceActivationRequest;
import com.arafath65.gymManagement.dto.response.DeviceActivationResponse;

public interface DeviceActivationService {

    DeviceActivationResponse activate(
            DeviceActivationRequest request
    );

}