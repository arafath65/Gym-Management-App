package arafath65.minime.gymmanagementui.model;

public class DeviceVerifyRequest {

    private String deviceUuid;
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