package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class BenefitRequest {

    // =====================================================
    // BENEFIT CODE
    // =====================================================

    private String benefitCode;


    // =====================================================
    // BENEFIT NAME
    // =====================================================

    private String benefitName;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    private String description;


    // =====================================================
    // BILLING TYPE
    // =====================================================

    private String billingType;


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    private BigDecimal defaultFee;


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


    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(
            String billingType) {

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