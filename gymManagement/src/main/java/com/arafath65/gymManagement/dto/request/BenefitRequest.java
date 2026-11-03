package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.Benefit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class BenefitRequest {

    // =====================================================
    // CODE
    // =====================================================

    @NotBlank(
            message = "Benefit code is required."
    )
    private String benefitCode;


    // =====================================================
    // NAME
    // =====================================================

    @NotBlank(
            message = "Benefit name is required."
    )
    private String benefitName;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    private String description;


    // =====================================================
    // BILLING TYPE
    // =====================================================

    @NotNull(
            message = "Billing type is required."
    )
    private Benefit.BillingType billingType =
            Benefit.BillingType.ONE_TIME;


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    @NotNull(
            message = "Default fee is required."
    )
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Default fee cannot be negative."
    )
    private BigDecimal defaultFee =
            BigDecimal.ZERO;


    // =====================================================
    // BRANCH MODE
    // =====================================================

    /*
     * ALL_BRANCHES
     * SELECTED_BRANCHES
     */
    private String branchMode;


    // =====================================================
    // BRANCH ASSIGNMENTS
    // =====================================================

    /*
     * Used when branchMode = SELECTED_BRANCHES
     */
    @Valid
    private List<BenefitBranchRequest> branches;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(
            String benefitCode) {

        this.benefitCode =
                benefitCode;
    }


    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(
            String benefitName) {

        this.benefitName =
                benefitName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public Benefit.BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(
            Benefit.BillingType billingType) {

        this.billingType =
                billingType;
    }


    public BigDecimal getDefaultFee() {
        return defaultFee;
    }

    public void setDefaultFee(
            BigDecimal defaultFee) {

        this.defaultFee =
                defaultFee;
    }


    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(
            String branchMode) {

        this.branchMode =
                branchMode;
    }


    public List<BenefitBranchRequest> getBranches() {
        return branches;
    }

    public void setBranches(
            List<BenefitBranchRequest> branches) {

        this.branches =
                branches;
    }
}