package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class MembershipPlanResponse {

    private Integer membershipPlanId;

    private Integer tenantId;

    private String planCode;
    private String planName;
    private String description;

    private String durationType;
    private Integer durationValue;

    private String billingType;
    private String billingFrequency;
    private Integer billingFrequencyValue;

    private String accessBillingMode;

    private Boolean monthlyActivationRequired;
    private Boolean dailyChargeEnabled;

    private Boolean freezeAllowed;
    private Integer maxFreezeDays;
    private BigDecimal freezeFee;

    private String accessType;

    private Boolean status;

    private String createdAt;
    private String updatedAt;

    private List<MembershipPlanBranchResponse> branchAssignments;
    private List<MembershipPlanBenefitResponse> benefits;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanResponse() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {

        this.membershipPlanId =
                membershipPlanId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }


    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }


    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }


    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }


    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }


    public String getBillingFrequency() {
        return billingFrequency;
    }

    public List<MembershipPlanBranchResponse>
    getBranchAssignments() {

        return branchAssignments;
    }

    public void setBillingFrequency(
            String billingFrequency) {

        this.billingFrequency =
                billingFrequency;
    }


    public Integer getBillingFrequencyValue() {
        return billingFrequencyValue;
    }

    public void setBillingFrequencyValue(
            Integer billingFrequencyValue) {

        this.billingFrequencyValue =
                billingFrequencyValue;
    }


    public String getAccessBillingMode() {
        return accessBillingMode;
    }

    public void setAccessBillingMode(
            String accessBillingMode) {

        this.accessBillingMode =
                accessBillingMode;
    }


    public Boolean getMonthlyActivationRequired() {
        return monthlyActivationRequired;
    }

    public void setMonthlyActivationRequired(
            Boolean monthlyActivationRequired) {

        this.monthlyActivationRequired =
                monthlyActivationRequired;
    }


    public Boolean getDailyChargeEnabled() {
        return dailyChargeEnabled;
    }

    public void setDailyChargeEnabled(
            Boolean dailyChargeEnabled) {

        this.dailyChargeEnabled =
                dailyChargeEnabled;
    }


    public Boolean getFreezeAllowed() {
        return freezeAllowed;
    }

    public void setFreezeAllowed(
            Boolean freezeAllowed) {

        this.freezeAllowed =
                freezeAllowed;
    }


    public Integer getMaxFreezeDays() {
        return maxFreezeDays;
    }

    public void setMaxFreezeDays(
            Integer maxFreezeDays) {

        this.maxFreezeDays =
                maxFreezeDays;
    }


    public BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(BigDecimal freezeFee) {
        this.freezeFee = freezeFee;
    }


    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public void setBranchAssignments(
            List<MembershipPlanBranchResponse>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
    }

    // =====================================================
// BENEFITS
// =====================================================

    public List<MembershipPlanBenefitResponse> getBenefits() {
        return benefits;
    }

    public void setBenefits(
            List<MembershipPlanBenefitResponse> benefits) {

        this.benefits =
                benefits;
    }
}