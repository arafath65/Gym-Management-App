package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MemberMembershipProgramRequest {

    // =====================================================
    // PROGRAM
    // =====================================================

    @NotNull(
            message = "Program is required."
    )
    private Integer programId;


    // =====================================================
    // FEE
    // =====================================================

    /*
     * Fee is the snapshot of the program fee at the
     * time it is assigned to the membership.
     */
    private BigDecimal fee;


    // =====================================================
    // START DATE
    // =====================================================

    private LocalDate startDate;


    // =====================================================
    // END DATE
    // =====================================================

    private LocalDate endDate;


    // =====================================================
    // NOTES
    // =====================================================

    private String notes;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipProgramRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(
            Integer programId) {

        this.programId =
                programId;
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


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }
}