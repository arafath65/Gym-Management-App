package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MembershipPlanResponse {

    private Integer membershipPlanId;

    private Integer tenantId;

    private Integer branchId;

    private String planCode;

    private String planName;

    private String description;

    private String durationType;

    private Integer durationValue;

    private BigDecimal price;

    private BigDecimal joiningFee;

    private BigDecimal renewalFee;

    private String billingType;

    private String billingFrequency;

    private Integer billingFrequencyValue;

    private Boolean freezeAllowed;

    private Integer maxFreezeDays;

    private BigDecimal freezeFee;

    private String accessType;

    private Boolean status;


    // =====================================================
    // GETTERS
    // =====================================================

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public String getDescription() {
        return description;
    }

    public String getDurationType() {
        return durationType;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public String getBillingType() {
        return billingType;
    }

    public String getBillingFrequency() {
        return billingFrequency;
    }

    public Integer getBillingFrequencyValue() {
        return billingFrequencyValue;
    }

    public Boolean getFreezeAllowed() {
        return freezeAllowed;
    }

    public Integer getMaxFreezeDays() {
        return maxFreezeDays;
    }

    public BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public String getAccessType() {
        return accessType;
    }

    public Boolean getStatus() {
        return status;
    }
}