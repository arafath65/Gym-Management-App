package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.ProgramPayment;
import com.arafath65.gymManagement.entity.ProgramPaymentDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProgramPaymentResponse {

    private Integer programPaymentId;

    private Integer tenantId;

    private Integer branchId;
    private String branchName;

    private Integer memberId;
    private String memberNo;
    private String memberName;

    private Integer programEnrollmentId;
    private String enrollmentNumber;

    private Integer programId;
    private String programCode;
    private String programName;

    private String paymentNumber;

    private LocalDate paymentDate;

    private BigDecimal totalAmount;

    private String referenceNumber;

    private String notes;

    private List<ProgramPaymentDetailResponse>
            paymentDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramPaymentId() {
        return programPaymentId;
    }

    public void setProgramPaymentId(
            Integer programPaymentId) {

        this.programPaymentId =
                programPaymentId;
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


    public Integer getProgramEnrollmentId() {
        return programEnrollmentId;
    }

    public void setProgramEnrollmentId(
            Integer programEnrollmentId) {

        this.programEnrollmentId =
                programEnrollmentId;
    }


    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(
            String enrollmentNumber) {

        this.enrollmentNumber =
                enrollmentNumber;
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


    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(
            String paymentNumber) {

        this.paymentNumber =
                paymentNumber;
    }


    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDate paymentDate) {

        this.paymentDate =
                paymentDate;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }


    public List<ProgramPaymentDetailResponse>
    getPaymentDetails() {

        return paymentDetails;
    }

    public void setPaymentDetails(
            List<ProgramPaymentDetailResponse>
                    paymentDetails) {

        this.paymentDetails =
                paymentDetails;
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