package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MembershipFreezeRequest {

    // MEMBERSHIP

    @NotNull(message = "Membership is required.")
    private Integer memberMembershipId;


    // DATES

    @NotNull(message = "Freeze start date is required.")
    private LocalDate freezeStartDate;

    @NotNull(message = "Freeze end date is required.")
    private LocalDate freezeEndDate;


    // REASON

    private String reason;

    private String notes;


    // GETTERS / SETTERS

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}