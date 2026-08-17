package com.arafath65.gymManagement.dto.response;

import com.arafath65.gymManagement.entity.MembershipFreeze;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MembershipFreezeResponse {

    // ID

    private Integer membershipFreezeId;


    // TENANT

    private Integer tenantId;


    // BRANCH

    private Integer branchId;
    private String branchName;


    // MEMBER

    private Integer memberId;


    // MEMBERSHIP

    private Integer memberMembershipId;
    private String membershipNumber;


    // FREEZE

    private LocalDate freezeStartDate;
    private LocalDate freezeEndDate;
    private Integer freezeDays;

    private BigDecimal freezeFee;

    private String reason;

    private MembershipFreeze.FreezeStatus status;

    private String notes;


    // AUDIT

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // GETTERS / SETTERS

    public Integer getMembershipFreezeId() {
        return membershipFreezeId;
    }

    public void setMembershipFreezeId(
            Integer membershipFreezeId) {
        this.membershipFreezeId =
                membershipFreezeId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {
        this.membershipNumber =
                membershipNumber;
    }

    public LocalDate getFreezeStartDate() {
        return freezeStartDate;
    }

    public void setFreezeStartDate(
            LocalDate freezeStartDate) {
        this.freezeStartDate =
                freezeStartDate;
    }

    public LocalDate getFreezeEndDate() {
        return freezeEndDate;
    }

    public void setFreezeEndDate(
            LocalDate freezeEndDate) {
        this.freezeEndDate =
                freezeEndDate;
    }

    public Integer getFreezeDays() {
        return freezeDays;
    }

    public void setFreezeDays(Integer freezeDays) {
        this.freezeDays = freezeDays;
    }

    public BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(BigDecimal freezeFee) {
        this.freezeFee = freezeFee;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public MembershipFreeze.FreezeStatus getStatus() {
        return status;
    }

    public void setStatus(
            MembershipFreeze.FreezeStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}