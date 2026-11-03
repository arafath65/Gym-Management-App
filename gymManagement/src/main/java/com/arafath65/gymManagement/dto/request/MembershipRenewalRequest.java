package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipRenewalRequest {

    // =====================================================
    // MEMBERSHIP
    // =====================================================

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // =====================================================
    // RENEWAL DATE
    // =====================================================

    private LocalDate renewalDate;


    // =====================================================
    // DURATION
    // =====================================================

    @NotNull(message = "Duration type is required.")
    private com.arafath65.gymManagement.entity.MembershipPlan.DurationType durationType;


    @NotNull(message = "Duration value is required.")
    @Positive(message = "Duration value must be greater than zero.")
    private Integer durationValue;


    // =====================================================
    // DISCOUNT
    // =====================================================

    @NotNull(message = "Discount amount is required.")
    @DecimalMin(
            value = "0.00",
            message = "Discount amount cannot be negative."
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;


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


    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDate renewalDate) {

        this.renewalDate =
                renewalDate;
    }


    public com.arafath65.gymManagement.entity.MembershipPlan.DurationType
    getDurationType() {

        return durationType;
    }

    public void setDurationType(
            com.arafath65.gymManagement.entity.MembershipPlan.DurationType
                    durationType) {

        this.durationType =
                durationType;
    }


    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(
            Integer durationValue) {

        this.durationValue =
                durationValue;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
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