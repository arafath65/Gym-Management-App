package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class ProgramPaymentRequest {

    // =====================================================
    // ENROLLMENT
    // =====================================================

    private Integer programEnrollmentId;

    // =====================================================
    // PAYMENT DATE
    // =====================================================

    private String paymentDate;

    // =====================================================
    // TOTAL PAYMENT
    // =====================================================

    private BigDecimal totalAmount;

    // =====================================================
    // PAYMENT DETAILS
    // =====================================================

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
    // CONSTRUCTOR
    // =====================================================

    public ProgramPaymentRequest() {
    }


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


    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }
}