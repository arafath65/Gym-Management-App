package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class ProgramPaymentDetailRequest {

    // =====================================================
    // PAYMENT METHOD
    // =====================================================

    private String paymentMethod;

    // =====================================================
    // AMOUNT
    // =====================================================

    private BigDecimal amount;

    // =====================================================
    // REFERENCE
    // =====================================================

    private String referenceNumber;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramPaymentDetailRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}