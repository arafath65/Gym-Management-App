package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPlanResponse {

    // ID

    private Integer membershipPlanId;


    // TENANT

    private Integer tenantId;


    // BRANCH

    private Integer branchId;
    private String branchName;


    // PLAN

    private String planCode;
    private String planName;
    private String description;


    // DURATION

    private MembershipPlan.DurationType durationType;
    private Integer durationValue;


    // PRICE

    private BigDecimal price;
    private BigDecimal joiningFee;
    private BigDecimal renewalFee;


    // BILLING

    private MembershipPlan.BillingType billingType;
    private MembershipPlan.BillingFrequency billingFrequency;
    private Integer billingFrequencyValue;


    // FREEZE

    private Boolean freezeAllowed;
    private Integer maxFreezeDays;
    private BigDecimal freezeFee;


    // ACCESS

    private MembershipPlan.AccessType accessType;


    // STATUS

    private Boolean status;


    // AUDIT

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // GETTERS / SETTERS

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

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public MembershipPlan.DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(
            MembershipPlan.DurationType durationType) {
        this.durationType = durationType;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(
            Integer durationValue) {
        this.durationValue = durationValue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(
            BigDecimal joiningFee) {
        this.joiningFee = joiningFee;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {
        this.renewalFee = renewalFee;
    }

    public MembershipPlan.BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(
            MembershipPlan.BillingType billingType) {
        this.billingType = billingType;
    }

    public MembershipPlan.BillingFrequency getBillingFrequency() {
        return billingFrequency;
    }

    public void setBillingFrequency(
            MembershipPlan.BillingFrequency billingFrequency) {
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

    public Boolean getFreezeAllowed() {
        return freezeAllowed;
    }

    public void setFreezeAllowed(Boolean freezeAllowed) {
        this.freezeAllowed = freezeAllowed;
    }

    public Integer getMaxFreezeDays() {
        return maxFreezeDays;
    }

    public void setMaxFreezeDays(Integer maxFreezeDays) {
        this.maxFreezeDays = maxFreezeDays;
    }

    public BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(BigDecimal freezeFee) {
        this.freezeFee = freezeFee;
    }

    public MembershipPlan.AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(
            MembershipPlan.AccessType accessType) {
        this.accessType = accessType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}