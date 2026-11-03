package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.MembershipPayment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPaymentRequest {

    // =====================================================
    // MEMBERSHIP
    // =====================================================

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // =====================================================
    // MEMBERSHIP RENEWAL
    // =====================================================

    /*
     * Optional.
     *
     * If provided:
     *     Payment belongs to a membership renewal.
     *
     * Must NOT be provided together with
     * membershipBillingCycleId.
     */

    private Integer membershipRenewalId;


    // =====================================================
    // BILLING CYCLE
    // =====================================================

    /*
     * Optional.
     *
     * Used for:
     *
     * MONTHLY_CONTRACT
     * MONTHLY_FLEX
     * DAILY
     *
     * Must NOT be provided together with
     * membershipRenewalId.
     */

    private Integer membershipBillingCycleId;


    /// =====================================================
// PAYMENT
// =====================================================

    @NotNull(message = "Payment amount is required.")
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero."
    )
    private BigDecimal amount;


// =====================================================
// PAYMENT DETAILS
// =====================================================

    @NotNull(message = "Payment details are required.")
    @jakarta.validation.Valid
    @jakarta.validation.constraints.NotEmpty(
            message = "At least one payment method is required."
    )
    private java.util.List<MembershipPaymentDetailRequest>
            paymentDetails;


    // =====================================================
    // PAYMENT DATE
    // =====================================================

    private LocalDateTime paymentDate;




    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

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


    public Integer getMembershipBillingCycleId() {
        return membershipBillingCycleId;
    }

    public void setMembershipBillingCycleId(
            Integer membershipBillingCycleId) {

        this.membershipBillingCycleId =
                membershipBillingCycleId;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount =
                amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate) {

        this.paymentDate =
                paymentDate;
    }


    public java.util.List<MembershipPaymentDetailRequest>
    getPaymentDetails() {

        return paymentDetails;
    }

    public void setPaymentDetails(
            java.util.List<MembershipPaymentDetailRequest>
                    paymentDetails) {

        this.paymentDetails =
                paymentDetails;
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