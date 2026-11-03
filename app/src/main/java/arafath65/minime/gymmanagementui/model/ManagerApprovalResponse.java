package arafath65.minime.gymmanagementui.model;

public class ManagerApprovalResponse {

    private String authorizationToken;
    private String operationCode;
    private Integer memberId;
    private String approvedBy;
    private String expiresAt;

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}