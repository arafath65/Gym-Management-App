package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.MembershipPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipBillingCycleRequest {

    // =====================================================
    // MEMBERSHIP
    // =====================================================

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // =====================================================
    // BILLING MODE
    // =====================================================

    @NotNull(message = "Billing mode is required.")
    private MembershipPlan.AccessBillingMode billingMode;


    // =====================================================
    // PERIOD
    // =====================================================

    @NotNull(message = "Period start date is required.")
    private LocalDate periodStart;

    @NotNull(message = "Period end date is required.")
    private LocalDate periodEnd;


    // =====================================================
    // AMOUNT
    // =====================================================

    @NotNull(message = "Base amount is required.")
    @DecimalMin(
            value = "0.00",
            message = "Base amount cannot be negative."
    )
    private BigDecimal baseAmount;


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


    public MembershipPlan.AccessBillingMode getBillingMode() {
        return billingMode;
    }

    public void setBillingMode(
            MembershipPlan.AccessBillingMode billingMode) {

        this.billingMode =
                billingMode;
    }


    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {

        this.periodStart =
                periodStart;
    }


    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {

        this.periodEnd =
                periodEnd;
    }


    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(
            BigDecimal baseAmount) {

        this.baseAmount =
                baseAmount;
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