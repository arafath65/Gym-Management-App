package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.ProgramEnrollment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProgramEnrollmentResponse {

    private Integer programEnrollmentId;

    private Integer tenantId;

    private Integer branchId;
    private String branchName;

    private Integer memberId;
    private String memberNo;
    private String memberName;

    private Integer programId;
    private String programCode;
    private String programName;

    private String enrollmentNumber;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal programFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    private ProgramEnrollment.PaymentType paymentType;

    private ProgramEnrollment.EnrollmentState enrollmentState;

    private Boolean status;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramEnrollmentId() {
        return programEnrollmentId;
    }

    public void setProgramEnrollmentId(
            Integer programEnrollmentId) {

        this.programEnrollmentId =
                programEnrollmentId;
    }


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


    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(
            Integer programId) {

        this.programId =
                programId;
    }


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


    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(
            String enrollmentNumber) {

        this.enrollmentNumber =
                enrollmentNumber;
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


    public BigDecimal getProgramFee() {
        return programFee;
    }

    public void setProgramFee(
            BigDecimal programFee) {

        this.programFee =
                programFee;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {

        this.paidAmount =
                paidAmount;
    }


    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {

        this.balanceAmount =
                balanceAmount;
    }


    public ProgramEnrollment.PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(
            ProgramEnrollment.PaymentType paymentType) {

        this.paymentType =
                paymentType;
    }


    public ProgramEnrollment.EnrollmentState getEnrollmentState() {
        return enrollmentState;
    }

    public void setEnrollmentState(
            ProgramEnrollment.EnrollmentState enrollmentState) {

        this.enrollmentState =
                enrollmentState;
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