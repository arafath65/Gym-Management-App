package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import com.arafath65.gymManagement.entity.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MembershipBillingCycleResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer membershipBillingCycleId;


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
    // CYCLE
    // =====================================================

    private String cycleNumber;

    private MembershipPlan.AccessBillingMode billingMode;


    // =====================================================
    // PERIOD
    // =====================================================

    private LocalDate periodStart;

    private LocalDate periodEnd;


    // =====================================================
    // FINANCIAL
    // =====================================================

    private BigDecimal baseAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;


    // =====================================================
    // ACTIVATION
    // =====================================================

    private LocalDate activatedDate;


    // =====================================================
    // STATUS
    // =====================================================

    private MembershipBillingCycle.CycleStatus status;


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

    public Integer getMembershipBillingCycleId() {
        return membershipBillingCycleId;
    }

    public void setMembershipBillingCycleId(
            Integer membershipBillingCycleId) {

        this.membershipBillingCycleId =
                membershipBillingCycleId;
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


    public String getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(
            String cycleNumber) {

        this.cycleNumber =
                cycleNumber;
    }


    public MembershipPlan.AccessBillingMode getBillingMode() {
        return billingMode;
    }

    public void setBillingMode(
            MembershipPlan.AccessBillingMode billingMode) {

        this.billingMode =
                billingMode;
    }


    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {

        this.periodStart =
                periodStart;
    }


    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {

        this.periodEnd =
                periodEnd;
    }


    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(
            BigDecimal baseAmount) {

        this.baseAmount =
                baseAmount;
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


    public LocalDate getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(
            LocalDate activatedDate) {

        this.activatedDate =
                activatedDate;
    }


    public MembershipBillingCycle.CycleStatus getStatus() {
        return status;
    }

    public void setStatus(
            MembershipBillingCycle.CycleStatus status) {

        this.status =
                status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }


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