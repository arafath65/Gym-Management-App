package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.ProgramPayment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProgramPaymentRequest {

    // =====================================================
    // ENROLLMENT
    // =====================================================

    @NotNull(
            message = "Program enrollment is required."
    )
    private Integer programEnrollmentId;


    // =====================================================
    // PAYMENT DATE
    // =====================================================

    private LocalDate paymentDate;


    // =====================================================
    // TOTAL PAYMENT AMOUNT
    // =====================================================

    @NotNull(
            message = "Payment amount is required."
    )
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero."
    )
    private BigDecimal totalAmount;


    // =====================================================
    // PAYMENT DETAILS
    // =====================================================

    @NotEmpty(
            message = "At least one payment method is required."
    )
    @Valid
    private List<ProgramPaymentDetailRequest>
            paymentDetails;


    // =====================================================
    // REFERENCE
    // =====================================================

    private String referenceNumber;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramEnrollmentId() {
        return programEnrollmentId;
    }

    public void setProgramEnrollmentId(
            Integer programEnrollmentId) {

        this.programEnrollmentId =
                programEnrollmentId;
    }


    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDate paymentDate) {

        this.paymentDate =
                paymentDate;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public List<ProgramPaymentDetailRequest>
    getPaymentDetails() {

        return paymentDetails;
    }

    public void setPaymentDetails(
            List<ProgramPaymentDetailRequest>
                    paymentDetails) {

        this.paymentDetails =
                paymentDetails;
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

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }
}