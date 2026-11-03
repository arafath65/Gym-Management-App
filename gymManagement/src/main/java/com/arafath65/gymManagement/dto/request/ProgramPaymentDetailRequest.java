package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.ProgramPaymentDetail;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProgramPaymentDetailRequest {

    // =====================================================
    // PAYMENT METHOD
    // =====================================================

    @NotNull(
            message = "Payment method is required."
    )
    private ProgramPaymentDetail.PaymentMethod
            paymentMethod;


    // =====================================================
    // AMOUNT
    // =====================================================

    @NotNull(
            message = "Payment detail amount is required."
    )
    @DecimalMin(
            value = "0.01",
            message = "Payment detail amount must be greater than zero."
    )
    private BigDecimal amount;


    // =====================================================
    // REFERENCE
    // =====================================================

    private String referenceNumber;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public ProgramPaymentDetail.PaymentMethod
    getPaymentMethod() {

        return paymentMethod;
    }

    public void setPaymentMethod(
            ProgramPaymentDetail.PaymentMethod
                    paymentMethod) {

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