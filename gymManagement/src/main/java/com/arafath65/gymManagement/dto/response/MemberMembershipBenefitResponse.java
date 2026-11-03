package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MemberMembershipBenefit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberMembershipBenefitResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer memberMembershipBenefitId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;
    private String branchName;


    // =====================================================
    // MEMBER MEMBERSHIP
    // =====================================================

    private Integer memberMembershipId;
    private String membershipNumber;


    // =====================================================
    // MEMBER
    // =====================================================

    private Integer memberId;
    private String memberNo;
    private String memberName;


    // =====================================================
    // BENEFIT
    // =====================================================

    private Integer benefitId;
    private String benefitCode;
    private String benefitName;


    // =====================================================
    // SOURCE
    // =====================================================

    private MemberMembershipBenefit.SourceType
            sourceType;


    // =====================================================
    // FEE
    // =====================================================

    private BigDecimal fee;


    // =====================================================
    // DATES
    // =====================================================

    private LocalDate startDate;
    private LocalDate endDate;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(
            String branchName) {

        this.branchName =
                branchName;
    }


    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {

        this.memberMembershipId =
                memberMembershipId;
    }

    public Integer getMemberMembershipBenefitId() {
        return memberMembershipBenefitId;
    }

    public void setMemberMembershipBenefitId(
            Integer memberMembershipBenefitId) {

        this.memberMembershipBenefitId =
                memberMembershipBenefitId;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {

        this.membershipNumber =
                membershipNumber;
    }


    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Integer memberId) {

        this.memberId =
                memberId;
    }


    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(
            String memberNo) {

        this.memberNo =
                memberNo;
    }


    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(
            String memberName) {

        this.memberName =
                memberName;
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


    public MemberMembershipBenefit.SourceType
    getSourceType() {

        return sourceType;
    }

    public void setSourceType(
            MemberMembershipBenefit.SourceType
                    sourceType) {

        this.sourceType =
                sourceType;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {

        this.endDate =
                endDate;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
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