package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BenefitBranchRequest {

    // =====================================================
    // BRANCH
    // =====================================================

    @NotNull(message = "Branch is required.")
    private Integer branchId;


    // =====================================================
    // BRANCH-SPECIFIC FEE
    // =====================================================

    /*
     * NULL = use Benefit.defaultFee
     *
     * Value = override the default fee for this branch
     */

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Benefit fee cannot be negative."
    )
    private BigDecimal fee;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitBranchRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}