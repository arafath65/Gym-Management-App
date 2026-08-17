package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.ProgramEnrollment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProgramEnrollmentRequest {

    // =====================================================
    // MEMBER
    // =====================================================

    @NotNull(
            message = "Member is required."
    )
    private Integer memberId;


    // =====================================================
    // BRANCH
    // =====================================================

    @NotNull(
            message = "Branch is required."
    )
    private Integer branchId;


    // =====================================================
    // PROGRAM
    // =====================================================

    @NotNull(
            message = "Program is required."
    )
    private Integer programId;


    // =====================================================
    // START DATE
    // =====================================================

    private LocalDate startDate;


    // =====================================================
    // PAYMENT TYPE
    // =====================================================

    @NotNull(
            message = "Payment type is required."
    )
    private ProgramEnrollment.PaymentType paymentType;


    // =====================================================
    // DISCOUNT
    // =====================================================

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Discount cannot be negative."
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;


    // =====================================================
    // INITIAL PAYMENT
    // =====================================================

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Initial payment cannot be negative."
    )
    private BigDecimal initialPayment =
            BigDecimal.ZERO;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Integer memberId) {

        this.memberId =
                memberId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(
            Integer programId) {

        this.programId =
                programId;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
    }


    public ProgramEnrollment.PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(
            ProgramEnrollment.PaymentType paymentType) {

        this.paymentType =
                paymentType;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getInitialPayment() {
        return initialPayment;
    }

    public void setInitialPayment(
            BigDecimal initialPayment) {

        this.initialPayment =
                initialPayment;
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