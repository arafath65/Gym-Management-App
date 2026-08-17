package arafath65.minime.gymmanagementui.model;

public class LoginRequest {

    private String tenantNo;
    private String username;
    private String password;
    private String deviceToken;

    public LoginRequest(
            String tenantNo,
            String username,
            String password,
            String deviceToken) {

        this.tenantNo = tenantNo;
        this.username = username;
        this.password = password;
        this.deviceToken = deviceToken;
    }

    public String getTenantNo() {
        return tenantNo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}