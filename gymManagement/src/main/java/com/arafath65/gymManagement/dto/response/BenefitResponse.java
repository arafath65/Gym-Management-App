package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.Benefit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BenefitResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer benefitId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // BENEFIT
    // =====================================================

    private String benefitCode;

    private String benefitName;

    private String description;


    // =====================================================
    // BILLING
    // =====================================================

    private Benefit.BillingType billingType;

    private BigDecimal defaultFee;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String branchMode;

    private List<BenefitBranchResponse> branchAssignments;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(
            Integer benefitId) {

        this.benefitId =
                benefitId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
    }


    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(
            String benefitCode) {

        this.benefitCode =
                benefitCode;
    }

    public String getBranchMode() {
        return branchMode;
    }

    public void setBranchMode(
            String branchMode) {

        this.branchMode =
                branchMode;
    }


    public List<BenefitBranchResponse>
    getBranchAssignments() {

        return branchAssignments;
    }

    public void setBranchAssignments(
            List<BenefitBranchResponse>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
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