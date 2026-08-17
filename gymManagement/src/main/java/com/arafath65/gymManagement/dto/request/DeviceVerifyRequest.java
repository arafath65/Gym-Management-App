package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DeviceVerifyRequest {

    @NotBlank(message = "Device UUID is required")
    private String deviceUuid;

    @NotBlank(message = "Device token is required")
    private String deviceToken;

    public DeviceVerifyRequest() {
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}