package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MemberMembershipRequest {

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
    // MEMBERSHIP PLAN
    // =====================================================

    @NotNull(
            message = "Membership plan is required."
    )
    private Integer membershipPlanId;


    // =====================================================
    // START DATE
    // =====================================================

    private LocalDate startDate;


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


    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {

        this.membershipPlanId =
                membershipPlanId;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
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