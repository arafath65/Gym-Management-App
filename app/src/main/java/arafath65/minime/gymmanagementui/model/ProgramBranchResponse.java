package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class ProgramBranchResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer programBranchId;


    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;

    private String branchNo;

    private String branchName;


    // =====================================================
    // FEE
    // =====================================================

    private BigDecimal fee;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // AUDIT
    // =====================================================

    private String createdAt;

    private String updatedAt;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramBranchResponse() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramBranchId() {
        return programBranchId;
    }

    public void setProgramBranchId(
            Integer programBranchId) {

        this.programBranchId =
                programBranchId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(
            String branchNo) {

        this.branchNo =
                branchNo;
    }


    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(
            String branchName) {

        this.branchName =
                branchName;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            String createdAt) {

        this.createdAt =
                createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            String updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}