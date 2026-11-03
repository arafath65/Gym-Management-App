package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProgramBranchRequest {

    // =====================================================
    // BRANCH
    // =====================================================

    @NotNull(
            message = "Branch ID is required."
    )
    private Integer branchId;


    // =====================================================
    // FEE
    // =====================================================

    @NotNull(
            message = "Program fee is required."
    )
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Program fee cannot be negative."
    )
    private BigDecimal fee;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramBranchRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }
}