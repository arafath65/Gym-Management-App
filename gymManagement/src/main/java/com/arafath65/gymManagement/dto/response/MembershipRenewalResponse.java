package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MembershipRenewalResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer membershipRenewalId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;
    private String branchName;


    // =====================================================
    // MEMBER
    // =====================================================

    private Integer memberId;


    // =====================================================
    // MEMBERSHIP
    // =====================================================

    private Integer memberMembershipId;
    private String membershipNumber;


    // =====================================================
    // RENEWAL
    // =====================================================

    private String renewalNumber;

    private LocalDate renewalDate;


    // =====================================================
    // PREVIOUS MEMBERSHIP PERIOD
    // =====================================================

    private LocalDate previousStartDate;

    private LocalDate previousEndDate;


    // =====================================================
    // NEW MEMBERSHIP PERIOD
    // =====================================================

    private LocalDate newStartDate;

    private LocalDate newEndDate;


    // =====================================================
    // DURATION
    // =====================================================

    private MembershipPlan.DurationType durationType;

    private Integer durationValue;


    // =====================================================
    // FINANCIAL
    // =====================================================

    private BigDecimal renewalFee;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipRenewalId() {
        return membershipRenewalId;
    }

    public void setMembershipRenewalId(
            Integer membershipRenewalId) {

        this.membershipRenewalId =
                membershipRenewalId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(
            String branchName) {

        this.branchName =
                branchName;
    }


    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Integer memberId) {

        this.memberId =
                memberId;
    }


    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {

        this.memberMembershipId =
                memberMembershipId;
    }


    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {

        this.membershipNumber =
                membershipNumber;
    }


    public String getRenewalNumber() {
        return renewalNumber;
    }

    public void setRenewalNumber(
            String renewalNumber) {

        this.renewalNumber =
                renewalNumber;
    }


    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDate renewalDate) {

        this.renewalDate =
                renewalDate;
    }


    // =====================================================
    // PREVIOUS PERIOD
    // =====================================================

    public LocalDate getPreviousStartDate() {
        return previousStartDate;
    }

    public void setPreviousStartDate(
            LocalDate previousStartDate) {

        this.previousStartDate =
                previousStartDate;
    }


    public LocalDate getPreviousEndDate() {
        return previousEndDate;
    }

    public void setPreviousEndDate(
            LocalDate previousEndDate) {

        this.previousEndDate =
                previousEndDate;
    }


    // =====================================================
    // NEW PERIOD
    // =====================================================

    public LocalDate getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(
            LocalDate newStartDate) {

        this.newStartDate =
                newStartDate;
    }


    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(
            LocalDate newEndDate) {

        this.newEndDate =
                newEndDate;
    }


    // =====================================================
    // DURATION
    // =====================================================

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


    // =====================================================
    // FINANCIAL
    // =====================================================

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {

        this.renewalFee =
                renewalFee;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {

        this.paidAmount =
                paidAmount;
    }


    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {

        this.balanceAmount =
                balanceAmount;
    }


    // =====================================================
    // STATUS
    // =====================================================

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    // =====================================================
    // NOTES
    // =====================================================

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
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