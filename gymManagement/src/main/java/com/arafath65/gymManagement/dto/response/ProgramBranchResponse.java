package com.arafath65.gymManagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProgramBranchResponse {

    private Integer programBranchId;

    private Integer branchId;

    private String branchNo;

    private String branchName;

    private BigDecimal fee;

    private Boolean status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


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


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}