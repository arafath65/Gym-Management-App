package com.arafath65.gymManagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ProgramRequest {

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
    // BRANCH MODE
    // =====================================================

    /*
     * ALL_BRANCHES
     * SELECTED_BRANCHES
     */
    @NotBlank(
            message = "Branch mode is required."
    )
    private String branchMode;


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    /*
     * Used when branchMode = ALL_BRANCHES
     */
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Default fee cannot be negative."
    )
    private BigDecimal defaultFee;


    // =====================================================
    // SELECTED BRANCHES
    // =====================================================

    /*
     * Used when branchMode = SELECTED_BRANCHES
     */
    @Valid
    private List<ProgramBranchRequest> branches;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

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


    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(
            String branchMode) {

        this.branchMode =
                branchMode;
    }


    public BigDecimal getDefaultFee() {
        return defaultFee;
    }

    public void setDefaultFee(
            BigDecimal defaultFee) {

        this.defaultFee =
                defaultFee;
    }


    public List<ProgramBranchRequest> getBranches() {
        return branches;
    }

    public void setBranches(
            List<ProgramBranchRequest> branches) {

        this.branches =
                branches;
    }
}