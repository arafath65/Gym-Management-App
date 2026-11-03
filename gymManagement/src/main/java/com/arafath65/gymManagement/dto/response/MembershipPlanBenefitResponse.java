package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipPlanBenefit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPlanBenefitResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer membershipPlanBenefitId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // PLAN
    // =====================================================

    private Integer membershipPlanId;

    private String planCode;

    private String planName;


    // =====================================================
    // BENEFIT
    // =====================================================

    private Integer benefitId;

    private String benefitCode;

    private String benefitName;

    private MembershipPlanBenefit.BenefitType
            benefitType;


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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipPlanBenefitId() {
        return membershipPlanBenefitId;
    }

    public void setMembershipPlanBenefitId(
            Integer membershipPlanBenefitId) {

        this.membershipPlanBenefitId =
                membershipPlanBenefitId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
    }


    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {

        this.membershipPlanId =
                membershipPlanId;
    }


    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(
            String planCode) {

        this.planCode =
                planCode;
    }


    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName) {

        this.planName =
                planName;
    }


    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(
            Integer benefitId) {

        this.benefitId =
                benefitId;
    }


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


    public MembershipPlanBenefit.BenefitType
    getBenefitType() {

        return benefitType;
    }

    public void setBenefitType(
            MembershipPlanBenefit.BenefitType
                    benefitType) {

        this.benefitType =
                benefitType;
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