package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProgramRequest {

    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;


    // =====================================================
    // CODE
    // =====================================================

    @NotBlank(
            message = "Program code is required."
    )
    private String programCode;


    // =====================================================
    // NAME
    // =====================================================

    @NotBlank(
            message = "Program name is required."
    )
    private String programName;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    private String description;


    // =====================================================
    // DURATION
    // =====================================================

    @NotNull(
            message = "Duration is required."
    )
    @Min(
            value = 1,
            message = "Duration must be at least 1 day."
    )
    private Integer durationDays;


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


    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(
            String programCode) {

        this.programCode =
                programCode;
    }


    public String getProgramName() {
        return programName;
    }

    public void setProgramName(
            String programName) {

        this.programName =
                programName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(
            Integer durationDays) {

        this.durationDays =
                durationDays;
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