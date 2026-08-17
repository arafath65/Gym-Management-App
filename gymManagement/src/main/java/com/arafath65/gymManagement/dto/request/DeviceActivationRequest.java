package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DeviceActivationRequest {

    @NotBlank(message = "Activation code is required")
    private String activationCode;

    @NotBlank(message = "Device UUID is required")
    private String deviceUuid;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotBlank(message = "Device model is required")
    private String model;

    @NotBlank(message = "Android version is required")
    private String androidVersion;

    @NotBlank(message = "App version is required")
    private String appVersion;

    private String firebaseToken;

    public DeviceActivationRequest() {
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}