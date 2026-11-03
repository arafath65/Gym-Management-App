package com.arafath65.gymManagement.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class MembershipPlanRequest {

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


    // =====================================================
    // BRANCH ASSIGNMENT
    // =====================================================

    /*
     * ALL_BRANCHES
     * SELECTED_BRANCHES
     */
    private String branchMode;


    /*
     * Used when branchMode = ALL_BRANCHES
     */
    private BigDecimal defaultPrice;
    private BigDecimal defaultJoiningFee;
    private BigDecimal defaultRenewalFee;


    /*
     * Used when branchMode = SELECTED_BRANCHES
     */
    private List<MembershipPlanBranchRequest> branches;


    // =====================================================
    // BENEFITS
    // =====================================================

    /*
     * Benefits included in this membership plan.
     *
     * The benefit records are selected from the
     * Benefit Master. No benefit names or IDs are
     * hard-coded in the Membership Plan.
     */
    private List<MembershipPlanBenefitRequest> benefits;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

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

    public void setBillingFrequency(String billingFrequency) {
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

    public void setFreezeFee(
            BigDecimal freezeFee) {

        this.freezeFee =
                freezeFee;
    }


    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }


    // =====================================================
    // BRANCH MODE
    // =====================================================

    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(String branchMode) {
        this.branchMode = branchMode;
    }


    // =====================================================
    // DEFAULT PRICING
    // =====================================================

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(
            BigDecimal defaultPrice) {

        this.defaultPrice = defaultPrice;
    }


    public BigDecimal getDefaultJoiningFee() {
        return defaultJoiningFee;
    }

    public void setDefaultJoiningFee(
            BigDecimal defaultJoiningFee) {

        this.defaultJoiningFee =
                defaultJoiningFee;
    }


    public BigDecimal getDefaultRenewalFee() {
        return defaultRenewalFee;
    }

    public void setDefaultRenewalFee(
            BigDecimal defaultRenewalFee) {

        this.defaultRenewalFee =
                defaultRenewalFee;
    }


    // =====================================================
    // BRANCHES
    // =====================================================

    public List<MembershipPlanBranchRequest> getBranches() {
        return branches;
    }

    public void setBranches(
            List<MembershipPlanBranchRequest> branches) {

        this.branches = branches;
    }


    // =====================================================
    // BENEFITS
    // =====================================================

    public List<MembershipPlanBenefitRequest> getBenefits() {
        return benefits;
    }

    public void setBenefits(
            List<MembershipPlanBenefitRequest> benefits) {

        this.benefits = benefits;
    }
}