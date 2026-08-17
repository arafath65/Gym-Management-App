package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MemberMembership;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberMembershipResponse {

    // ID

    private Integer memberMembershipId;


    // TENANT

    private Integer tenantId;


    // BRANCH

    private Integer branchId;
    private String branchName;


    // MEMBER

    private Integer memberId;


    // MEMBERSHIP PLAN

    private Integer membershipPlanId;
    private String planCode;
    private String planName;


    // MEMBERSHIP

    private String membershipNumber;

    private LocalDate startDate;
    private LocalDate endDate;


    // PRICE

    private BigDecimal planPrice;
    private BigDecimal joiningFee;
    private BigDecimal renewalFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;


    // PAYMENT

    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;


    // STATUS

    private Boolean status;
    // MEMBERSHIP STATE

    private MemberMembership.MembershipState membershipState;


    // FREEZE

    private Integer totalFreezeDays;


    // RENEWAL

    private Integer renewalCount;


    // NOTES

    private String notes;


    // AUDIT

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // GETTERS / SETTERS

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
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

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {
        this.membershipPlanId =
                membershipPlanId;
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

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {
        this.membershipNumber =
                membershipNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(BigDecimal planPrice) {
        this.planPrice = planPrice;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {
        this.status = status;
    }

    public MemberMembership.MembershipState getMembershipState() {
        return membershipState;
    }

    public void setMembershipState(
            MemberMembership.MembershipState membershipState) {

        this.membershipState =
                membershipState;
    }

    public Integer getTotalFreezeDays() {
        return totalFreezeDays;
    }

    public void setTotalFreezeDays(
            Integer totalFreezeDays) {
        this.totalFreezeDays = totalFreezeDays;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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