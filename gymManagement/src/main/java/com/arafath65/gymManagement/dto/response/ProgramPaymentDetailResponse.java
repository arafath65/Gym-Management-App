package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.ProgramPaymentDetail;

import java.math.BigDecimal;

public class ProgramPaymentDetailResponse {

    private Integer programPaymentDetailId;

    private ProgramPaymentDetail.PaymentMethod
            paymentMethod;

    private BigDecimal amount;

    private String referenceNumber;


    public Integer getProgramPaymentDetailId() {
        return programPaymentDetailId;
    }

    public void setProgramPaymentDetailId(
            Integer programPaymentDetailId) {

        this.programPaymentDetailId =
                programPaymentDetailId;
    }


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