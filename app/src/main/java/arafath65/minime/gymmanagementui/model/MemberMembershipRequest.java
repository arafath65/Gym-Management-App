package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MemberMembershipRequest {

    private Integer memberId;

    private Integer branchId;

    private Integer membershipPlanId;

    private String startDate;

    private BigDecimal discountAmount;

    private String notes;


    public MemberMembershipRequest() {
    }


    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Integer memberId) {

        this.memberId =
                memberId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


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


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
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