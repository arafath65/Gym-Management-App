package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MemberMembershipBenefitRequest {

    // =====================================================
    // BENEFIT
    // =====================================================

    @NotNull(
            message = "Benefit is required."
    )
    private Integer benefitId;


    // =====================================================
    // START DATE
    // =====================================================

    private LocalDate startDate;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


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


    public LocalDate getStartDate() {
        return startDate;
    }


    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
    }


    public String getNotes() {
        return notes;
    }


    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }
}