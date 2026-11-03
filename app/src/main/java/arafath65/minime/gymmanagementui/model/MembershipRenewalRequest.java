package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MembershipRenewalRequest {

    private Integer memberMembershipId;

    private String renewalDate;

    private String durationType;

    private Integer durationValue;

    private BigDecimal discountAmount;

    private String notes;


    public MembershipRenewalRequest() {
    }


    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {

        this.memberMembershipId =
                memberMembershipId;
    }


    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            String renewalDate) {

        this.renewalDate =
                renewalDate;
    }


    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(
            String durationType) {

        this.durationType =
                durationType;
    }


    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(
            Integer durationValue) {

        this.durationValue =
                durationValue;
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