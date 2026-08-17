package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.MembershipPayment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPaymentRequest {

    // MEMBERSHIP

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // RENEWAL
    // Optional.
    // Null = original membership payment.
    // Value = renewal payment.

    private Integer membershipRenewalId;


    // PAYMENT

    @NotNull(message = "Payment amount is required.")
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero."
    )
    private BigDecimal amount;


    @NotNull(message = "Payment method is required.")
    private MembershipPayment.PaymentMethod paymentMethod;


    private LocalDateTime paymentDate;

    private String referenceNumber;

    private String notes;


    // GETTERS / SETTERS

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
    }

    public Integer getMembershipRenewalId() {
        return membershipRenewalId;
    }

    public void setMembershipRenewalId(
            Integer membershipRenewalId) {
        this.membershipRenewalId =
                membershipRenewalId;
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
}