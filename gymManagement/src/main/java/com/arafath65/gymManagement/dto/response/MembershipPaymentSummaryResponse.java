package com.arafath65.gymManagement.dto.response;

import java.math.BigDecimal;

public class MembershipPaymentSummaryResponse {

    private Integer memberMembershipId;

    private String membershipNumber;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;

    private BigDecimal activePaymentAmount;

    private BigDecimal cancelledPaymentAmount;

    private Integer activePaymentCount;

    private Integer cancelledPaymentCount;


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


    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {

        this.membershipNumber =
                membershipNumber;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {

        this.paidAmount =
                paidAmount;
    }


    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {

        this.balanceAmount =
                balanceAmount;
    }


    public BigDecimal getActivePaymentAmount() {
        return activePaymentAmount;
    }

    public void setActivePaymentAmount(
            BigDecimal activePaymentAmount) {

        this.activePaymentAmount =
                activePaymentAmount;
    }


    public BigDecimal getCancelledPaymentAmount() {
        return cancelledPaymentAmount;
    }

    public void setCancelledPaymentAmount(
            BigDecimal cancelledPaymentAmount) {

        this.cancelledPaymentAmount =
                cancelledPaymentAmount;
    }


    public Integer getActivePaymentCount() {
        return activePaymentCount;
    }

    public void setActivePaymentCount(
            Integer activePaymentCount) {

        this.activePaymentCount =
                activePaymentCount;
    }


    public Integer getCancelledPaymentCount() {
        return cancelledPaymentCount;
    }

    public void setCancelledPaymentCount(
            Integer cancelledPaymentCount) {

        this.cancelledPaymentCount =
                cancelledPaymentCount;
    }
}