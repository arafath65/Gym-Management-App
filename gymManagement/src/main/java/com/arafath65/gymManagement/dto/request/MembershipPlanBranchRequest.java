package com.arafath65.gymManagement.dto.request;

import java.math.BigDecimal;

public class MembershipPlanBranchRequest {

    private Integer branchId;

    private BigDecimal price;
    private BigDecimal joiningFee;
    private BigDecimal renewalFee;


    public MembershipPlanBranchRequest() {
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(
            BigDecimal joiningFee) {

        this.joiningFee = joiningFee;
    }


    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {

        this.renewalFee = renewalFee;
    }
}