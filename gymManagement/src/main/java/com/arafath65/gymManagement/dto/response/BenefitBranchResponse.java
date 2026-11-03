package com.arafath65.gymManagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BenefitBranchResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer benefitBranchId;


    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;

    private String branchNo;

    private String branchName;


    // =====================================================
    // FEE
    // =====================================================

    /*
     * NULL = use Benefit.defaultFee
     *
     * Value = branch-specific fee
     */
    private BigDecimal fee;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitBranchResponse() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBenefitBranchId() {
        return benefitBranchId;
    }

    public void setBenefitBranchId(
            Integer benefitBranchId) {

        this.benefitBranchId =
                benefitBranchId;
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