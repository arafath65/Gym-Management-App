package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.MembershipPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MembershipPlanRequest {

    // PLAN

    @NotBlank(message = "Plan code is required.")
    private String planCode;

    @NotBlank(message = "Plan name is required.")
    private String planName;

    private String description;


    // BRANCH

    private Integer branchId;


    // DURATION

    @NotNull(message = "Duration type is required.")
    private MembershipPlan.DurationType durationType;

    @Min(
            value = 1,
            message = "Duration value must be greater than zero."
    )
    private Integer durationValue;


    // PRICE

    @NotNull(message = "Price is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Price cannot be negative."
    )
    private BigDecimal price = BigDecimal.ZERO;


    @NotNull(message = "Joining fee is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Joining fee cannot be negative."
    )
    private BigDecimal joiningFee = BigDecimal.ZERO;


    @NotNull(message = "Renewal fee is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Renewal fee cannot be negative."
    )
    private BigDecimal renewalFee = BigDecimal.ZERO;


    // BILLING

    @NotNull(message = "Billing type is required.")
    private MembershipPlan.BillingType billingType =
            MembershipPlan.BillingType.ONE_TIME;

    private MembershipPlan.BillingFrequency billingFrequency;

    @Min(
            value = 1,
            message = "Billing frequency value must be greater than zero."
    )
    private Integer billingFrequencyValue;


    // FREEZE

    private Boolean freezeAllowed = true;

    @Min(
            value = 0,
            message = "Maximum freeze days cannot be negative."
    )
    private Integer maxFreezeDays;

    @NotNull(message = "Freeze fee is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Freeze fee cannot be negative."
    )
    private BigDecimal freezeFee = BigDecimal.ZERO;


    // ACCESS

    @NotNull(message = "Access type is required.")
    private MembershipPlan.AccessType accessType =
            MembershipPlan.AccessType.FULL;


    // GETTERS / SETTERS

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

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
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

    public void setDurationValue(Integer durationValue) {
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

    public void setJoiningFee(BigDecimal joiningFee) {
        this.joiningFee = joiningFee;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(BigDecimal renewalFee) {
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
        this.billingFrequency = billingFrequency;
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
}