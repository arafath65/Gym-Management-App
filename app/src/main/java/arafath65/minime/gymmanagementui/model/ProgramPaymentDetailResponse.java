package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class ProgramPaymentDetailResponse {

    private Integer programPaymentDetailId;

    private String paymentMethod;

    private BigDecimal amount;

    private String referenceNumber;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramPaymentDetailResponse() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramPaymentDetailId() {
        return programPaymentDetailId;
    }

    public void setProgramPaymentDetailId(
            Integer programPaymentDetailId) {

        this.programPaymentDetailId =
                programPaymentDetailId;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            String paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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