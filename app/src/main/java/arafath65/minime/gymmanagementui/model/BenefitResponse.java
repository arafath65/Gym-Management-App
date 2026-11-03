package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class BenefitResponse {

    private Integer benefitId;

    private Integer tenantId;

    private String benefitCode;

    private String benefitName;

    private String description;

    private String billingType;

    private BigDecimal defaultFee;

    private Boolean status;

    private String branchMode;

    private List<BenefitBranchResponse> branchAssignments;

    private String createdAt;

    private String updatedAt;


    public BenefitResponse() {
    }


    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(Integer benefitId) {
        this.benefitId = benefitId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }


    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
    }


    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(String benefitName) {
        this.benefitName = benefitName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }


    public BigDecimal getDefaultFee() {
        return defaultFee;
    }

    public void setDefaultFee(BigDecimal defaultFee) {
        this.defaultFee = defaultFee;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(String branchMode) {
        this.branchMode = branchMode;
    }


    public List<BenefitBranchResponse> getBranchAssignments() {
        return branchAssignments;
    }

    public void setBranchAssignments(
            List<BenefitBranchResponse> branchAssignments) {

        this.branchAssignments = branchAssignments;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}