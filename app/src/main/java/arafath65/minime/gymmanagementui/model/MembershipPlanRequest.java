package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class MembershipPlanRequest {

    // =====================================================
    // BASIC INFORMATION
    // =====================================================

    private String planCode;
    private String planName;
    private String description;


    // =====================================================
    // DURATION
    // =====================================================

    private String durationType;
    private Integer durationValue;


    // =====================================================
    // BILLING
    // =====================================================

    private String billingType;
    private String billingFrequency;
    private Integer billingFrequencyValue;


    // =====================================================
    // ACCESS BILLING
    // =====================================================

    private String accessBillingMode;

    private Boolean monthlyActivationRequired;
    private Boolean dailyChargeEnabled;


    // =====================================================
    // FREEZE
    // =====================================================

    private Boolean freezeAllowed;
    private Integer maxFreezeDays;
    private BigDecimal freezeFee;


    // =====================================================
    // ACCESS TYPE
    // =====================================================

    private String accessType;


    // =====================================================
    // BRANCH ASSIGNMENT
    // =====================================================

    /*
     * ALL_BRANCHES
     * SELECTED_BRANCHES
     */
    private String branchMode;


    // =====================================================
    // DEFAULT PRICING
    // =====================================================

    /*
     * Used when:
     *
     * branchMode = ALL_BRANCHES
     */

    private BigDecimal defaultPrice;

    private BigDecimal defaultJoiningFee;

    private BigDecimal defaultRenewalFee;


    // =====================================================
    // SELECTED BRANCHES
    // =====================================================

    /*
     * Used when:
     *
     * branchMode = SELECTED_BRANCHES
     *
     * Each branch can have its own pricing.
     */

    private List<MembershipPlanBranchRequest> branches;
    private List<MembershipPlanBenefitRequest> benefits;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanRequest() {
    }


    // =====================================================
    // BASIC INFORMATION
    // =====================================================

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


    // =====================================================
    // DURATION
    // =====================================================

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(
            String durationType) {

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


    // =====================================================
    // BILLING
    // =====================================================

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(
            String billingType) {

        this.billingType =
                billingType;
    }


    public String getBillingFrequency() {
        return billingFrequency;
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


    // =====================================================
    // ACCESS BILLING
    // =====================================================

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


    // =====================================================
    // FREEZE
    // =====================================================

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


    // =====================================================
    // ACCESS TYPE
    // =====================================================

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(
            String accessType) {

        this.accessType =
                accessType;
    }


    // =====================================================
    // BRANCH MODE
    // =====================================================

    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(
            String branchMode) {

        this.branchMode =
                branchMode;
    }


    // =====================================================
    // DEFAULT PRICING
    // =====================================================

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(
            BigDecimal defaultPrice) {

        this.defaultPrice =
                defaultPrice;
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
    // SELECTED BRANCHES
    // =====================================================

    public List<MembershipPlanBranchRequest> getBranches() {
        return branches;
    }

    public void setBranches(
            List<MembershipPlanBranchRequest> branches) {

        this.branches =
                branches;
    }

    // =====================================================
// BENEFITS
// =====================================================

    public List<MembershipPlanBenefitRequest> getBenefits() {
        return benefits;
    }

    public void setBenefits(
            List<MembershipPlanBenefitRequest> benefits) {

        this.benefits =
                benefits;
    }
}