package arafath65.minime.gymmanagementui.model;

public class BranchResponse {

    private Integer branchId;
    private Integer tenantId;

    private String tenantNo;
    private String gymName;

    private String branchNo;
    private String branchName;

    private String address;
    private String contactNo;

    private Boolean status;
    private String createdAt;


    public Integer getBranchId() {
        return branchId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public String getTenantNo() {
        return tenantNo;
    }

    public String getGymName() {
        return gymName;
    }

    public String getBranchNo() {
        return branchNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}