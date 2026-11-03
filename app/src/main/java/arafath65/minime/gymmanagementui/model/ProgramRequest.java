package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;
import java.util.List;

public class ProgramRequest {

    // =====================================================
    // BASIC INFORMATION
    // =====================================================

    private String programCode;

    private String programName;

    private String description;

    private Integer durationDays;


    // =====================================================
    // BRANCH MODE
    // =====================================================

    /*
     * ALL_BRANCHES
     * SELECTED_BRANCHES
     */
    private String branchMode;


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    /*
     * Used when branchMode = ALL_BRANCHES
     */
    private BigDecimal defaultFee;


    // =====================================================
    // SELECTED BRANCHES
    // =====================================================

    /*
     * Used when branchMode = SELECTED_BRANCHES
     */
    private List<ProgramBranchRequest> branches;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramRequest() {
    }


    // =====================================================
    // PROGRAM CODE
    // =====================================================

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(
            String programCode) {

        this.programCode =
                programCode;
    }


    // =====================================================
    // PROGRAM NAME
    // =====================================================

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(
            String programName) {

        this.programName =
                programName;
    }


    // =====================================================
    // DESCRIPTION
    // =====================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    // =====================================================
    // DURATION
    // =====================================================

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(
            Integer durationDays) {

        this.durationDays =
                durationDays;
    }


    // =====================================================
    // BRANCH MODE
    // =====================================================

    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(
            String branchMode) {

        this.branchMode =
                branchMode;
    }


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    public BigDecimal getDefaultFee() {
        return defaultFee;
    }

    public void setDefaultFee(
            BigDecimal defaultFee) {

        this.defaultFee =
                defaultFee;
    }


    // =====================================================
    // BRANCHES
    // =====================================================

    public List<ProgramBranchRequest>
    getBranches() {

        return branches;
    }

    public void setBranches(
            List<ProgramBranchRequest>
                    branches) {

        this.branches =
                branches;
    }
}