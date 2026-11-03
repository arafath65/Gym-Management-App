package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class ProgramEnrollmentRequest {

    // =====================================================
    // MEMBER
    // =====================================================

    private Integer memberId;


    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;


    // =====================================================
    // PROGRAM
    // =====================================================

    private Integer programId;


    // =====================================================
    // START DATE
    // =====================================================

    private String startDate;


    // =====================================================
    // PAYMENT TYPE
    // =====================================================

    private String paymentType;


    // =====================================================
    // DISCOUNT
    // =====================================================

    private BigDecimal discountAmount;


    // =====================================================
    // INITIAL PAYMENT
    // =====================================================

    private BigDecimal initialPayment;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramEnrollmentRequest() {

        discountAmount = BigDecimal.ZERO;
        initialPayment = BigDecimal.ZERO;
    }


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


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(
            String startDate) {

        this.startDate =
                startDate;
    }


    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(
            String paymentType) {

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