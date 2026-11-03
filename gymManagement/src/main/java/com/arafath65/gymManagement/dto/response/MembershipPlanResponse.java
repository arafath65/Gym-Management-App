package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MembershipPlanResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer membershipPlanId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // PLAN
    // =====================================================

    private String planCode;

    private String planName;

    private String description;


    // =====================================================
    // DURATION
    // =====================================================

    private MembershipPlan.DurationType durationType;

    private Integer durationValue;


    // =====================================================
    // BILLING
    // =====================================================

    private MembershipPlan.BillingType billingType;

    private MembershipPlan.BillingFrequency billingFrequency;

    private Integer billingFrequencyValue;


    // =====================================================
    // ACCESS BILLING MODE
    // =====================================================

    private MembershipPlan.AccessBillingMode accessBillingMode;


    // =====================================================
    // MONTHLY FLEX
    // =====================================================

    private Boolean monthlyActivationRequired;


    // =====================================================
    // DAILY SETTINGS
    // =====================================================

    private Boolean dailyChargeEnabled;


    // =====================================================
    // FREEZE
    // =====================================================

    private Boolean freezeAllowed;

    private Integer maxFreezeDays;

    private BigDecimal freezeFee;


    // =====================================================
    // ACCESS
    // =====================================================

    private MembershipPlan.AccessType accessType;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // BRANCH ASSIGNMENTS
    // =====================================================

    private List<MembershipPlanBranchResponse>
            branchAssignments;


    // =====================================================
    // BENEFITS
    // =====================================================

    /*
     * Benefits assigned to this membership plan.
     *
     * These are loaded dynamically from the
     * membership_plan_benefits relationship.
     */
    private List<MembershipPlanBenefitResponse>
            benefits;


    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


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

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
    }


    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(
            String planCode) {

        this.planCode =
                planCode;
    }


    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName) {

        this.planName =
                planName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public MembershipPlan.DurationType
    getDurationType() {

        return durationType;
    }

    public void setDurationType(
            MembershipPlan.DurationType durationType) {

        this.durationType =
                durationType;
    }


    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(
            Integer durationValue) {

        this.durationValue =
                durationValue;
    }


    public MembershipPlan.BillingType
    getBillingType() {

        return billingType;
    }

    public void setBillingType(
            MembershipPlan.BillingType billingType) {

        this.billingType =
                billingType;
    }


    public MembershipPlan.BillingFrequency
    getBillingFrequency() {

        return billingFrequency;
    }

    public void setBillingFrequency(
            MembershipPlan.BillingFrequency
                    billingFrequency) {

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


    public MembershipPlan.AccessBillingMode
    getAccessBillingMode() {

        return accessBillingMode;
    }

    public void setAccessBillingMode(
            MembershipPlan.AccessBillingMode
                    accessBillingMode) {

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

    public void setFreezeFee(
            BigDecimal freezeFee) {

        this.freezeFee =
                freezeFee;
    }


    public MembershipPlan.AccessType
    getAccessType() {

        return accessType;
    }

    public void setAccessType(
            MembershipPlan.AccessType accessType) {

        this.accessType =
                accessType;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    // =====================================================
    // BRANCH ASSIGNMENTS
    // =====================================================

    public List<MembershipPlanBranchResponse>
    getBranchAssignments() {

        return branchAssignments;
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

    public List<MembershipPlanBenefitResponse>
    getBenefits() {

        return benefits;
    }

    public void setBenefits(
            List<MembershipPlanBenefitResponse>
                    benefits) {

        this.benefits =
                benefits;
    }


    // =====================================================
    // AUDIT
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}