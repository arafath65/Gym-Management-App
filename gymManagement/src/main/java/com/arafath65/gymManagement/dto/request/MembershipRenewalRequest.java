package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipRenewalRequest {

    // MEMBERSHIP

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // RENEWAL START DATE

    private LocalDate renewalStartDate;


    // DISCOUNT

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Discount cannot be negative."
    )
    private BigDecimal discountAmount = BigDecimal.ZERO;


    // NOTES

    private String notes;


    // GETTERS / SETTERS

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
    }

    public LocalDate getRenewalStartDate() {
        return renewalStartDate;
    }

    public void setRenewalStartDate(
            LocalDate renewalStartDate) {
        this.renewalStartDate =
                renewalStartDate;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }
}