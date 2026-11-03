package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPayment;

import java.math.BigDecimal;

public class MembershipPaymentDetailResponse {

    private Integer membershipPaymentDetailId;

    private MembershipPayment.PaymentMethod paymentMethod;

    private BigDecimal amount;

    private String referenceNumber;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipPaymentDetailId() {
        return membershipPaymentDetailId;
    }

    public void setMembershipPaymentDetailId(
            Integer membershipPaymentDetailId) {

        this.membershipPaymentDetailId =
                membershipPaymentDetailId;
    }


    public MembershipPayment.PaymentMethod
    getPaymentMethod() {

        return paymentMethod;
    }

    public void setPaymentMethod(
            MembershipPayment.PaymentMethod paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount =
                amount;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }
}