package arafath65.minime.gymmanagementui.model;

public class MemberMembershipProgramRequest {

    private Integer programId;

    private String startDate;

    private String endDate;

    private String notes;


    public MemberMembershipProgramRequest() {
    }


    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}