package arafath65.minime.gymmanagementui.model;

public class LoginResponse {

    private int userId;

    private int tenantId;
    private String tenantNo;
    private String gymName;

    private Integer branchId;
    private String branchNo;
    private String branchName;

    private Integer roleId;
    private String roleName;

    private String username;
    private String fullName;
    private String profileImage;

    private String mobile;
    private String email;


    public int getUserId() {
        return userId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public String getTenantNo() {
        return tenantNo;
    }

    public String getGymName() {
        return gymName;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public String getBranchNo() {
        return branchNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }
}