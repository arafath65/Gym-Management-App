package arafath65.minime.gymmanagementui.model;

import java.time.LocalDate;

public class MemberMembershipBenefitRequest {

    private Integer benefitId;

    private LocalDate startDate;

    private String notes;


    public MemberMembershipBenefitRequest() {
    }


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