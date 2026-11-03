package arafath65.minime.gymmanagementui.model;

public class MemberMembershipPlanRequest {

    private Integer membershipPlanId;

    private String startDate;

    private String notes;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipPlanRequest() {
    }


    public MemberMembershipPlanRequest(
            Integer membershipPlanId,
            String startDate,
            String notes) {

        this.membershipPlanId =
                membershipPlanId;

        this.startDate =
                startDate;

        this.notes =
                notes;
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {

        this.membershipPlanId =
                membershipPlanId;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(
            String startDate) {

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