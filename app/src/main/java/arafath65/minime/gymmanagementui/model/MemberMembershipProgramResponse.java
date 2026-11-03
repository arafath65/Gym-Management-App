package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MemberMembershipProgramResponse {

    private Integer memberMembershipProgramId;

    private Integer tenantId;

    private Integer memberMembershipId;

    private Integer programId;

    private String programCode;

    private String programName;

    private String description;

    private Integer durationDays;

    private Integer branchId;

    private String branchName;

    private BigDecimal fee;

    private String startDate;

    private String endDate;

    private Boolean status;

    private String notes;

    private String createdAt;

    private String updatedAt;


    public Integer getMemberMembershipProgramId() {
        return memberMembershipProgramId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public String getProgramCode() {
        return programCode;
    }

    public String getProgramName() {
        return programName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}