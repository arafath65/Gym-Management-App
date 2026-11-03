package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MembershipPaymentResponse {

    // ID

    private Integer membershipPaymentId;


    // TENANT

    private Integer tenantId;


    // BRANCH

    private Integer branchId;
    private String branchName;


    // MEMBER

    private Integer memberId;


    // MEMBERSHIP

    private Integer memberMembershipId;
    private String membershipNumber;


    // RENEWAL

    private Integer membershipRenewalId;

    // =====================================================
// BILLING CYCLE
// =====================================================

    private Integer membershipBillingCycleId;

    private String cycleNumber;


    // PAYMENT

    private String paymentNumber;
    private BigDecimal amount;

    private MembershipPayment.PaymentMethod paymentMethod;

    private LocalDateTime paymentDate;

    private String referenceNumber;
    private String notes;


    // AUDIT

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MembershipPaymentDetailResponse>
            paymentDetails;


    // GETTERS / SETTERS

    public Integer getMembershipPaymentId() {
        return membershipPaymentId;
    }

    public void setMembershipPaymentId(
            Integer membershipPaymentId) {
        this.membershipPaymentId =
                membershipPaymentId;
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

    public Integer getMembershipRenewalId() {
        return membershipRenewalId;
    }

    public void setMembershipRenewalId(
            Integer membershipRenewalId) {
        this.membershipRenewalId =
                membershipRenewalId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public Integer getMembershipBillingCycleId() {
        return membershipBillingCycleId;
    }

    public void setMembershipBillingCycleId(
            Integer membershipBillingCycleId) {

        this.membershipBillingCycleId =
                membershipBillingCycleId;
    }


    public String getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(
            String cycleNumber) {

        this.cycleNumber =
                cycleNumber;
    }

    public void setPaymentNumber(
            String paymentNumber) {
        this.paymentNumber =
                paymentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public MembershipPayment.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            MembershipPayment.PaymentMethod paymentMethod) {
        this.paymentMethod =
                paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate) {
        this.paymentDate =
                paymentDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {
        this.referenceNumber =
                referenceNumber;
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

    public List<MembershipPaymentDetailResponse>
    getPaymentDetails() {

        return paymentDetails;
    }

    public void setPaymentDetails(
            List<MembershipPaymentDetailResponse>
                    paymentDetails) {

        this.paymentDetails =
                paymentDetails;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}