package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.DeviceVerifyRequest;
import com.arafath65.gymManagement.dto.response.DeviceVerifyResponse;

public interface DeviceVerifyService {

    DeviceVerifyResponse verify(
            DeviceVerifyRequest request
    );
}