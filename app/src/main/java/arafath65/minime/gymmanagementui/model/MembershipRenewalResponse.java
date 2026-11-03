package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MembershipRenewalResponse {

    private Integer membershipRenewalId;

    private Integer tenantId;

    private Integer branchId;

    private String branchName;

    private Integer memberId;

    private Integer memberMembershipId;

    private String membershipNumber;

    private String renewalNumber;

    private String renewalDate;

    private String previousStartDate;

    private String previousEndDate;

    private String newStartDate;

    private String newEndDate;

    private String durationType;

    private Integer durationValue;

    private BigDecimal renewalFee;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;

    private Boolean status;

    private String notes;

    private String createdAt;

    private String updatedAt;


    public MembershipRenewalResponse() {
    }


    public Integer getMembershipRenewalId() {
        return membershipRenewalId;
    }

    public void setMembershipRenewalId(
            Integer membershipRenewalId) {
        this.membershipRenewalId =
                membershipRenewalId;
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


    public String getRenewalNumber() {
        return renewalNumber;
    }

    public void setRenewalNumber(
            String renewalNumber) {
        this.renewalNumber =
                renewalNumber;
    }


    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            String renewalDate) {
        this.renewalDate =
                renewalDate;
    }


    public String getPreviousStartDate() {
        return previousStartDate;
    }

    public void setPreviousStartDate(
            String previousStartDate) {
        this.previousStartDate =
                previousStartDate;
    }


    public String getPreviousEndDate() {
        return previousEndDate;
    }

    public void setPreviousEndDate(
            String previousEndDate) {
        this.previousEndDate =
                previousEndDate;
    }


    public String getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(
            String newStartDate) {
        this.newStartDate =
                newStartDate;
    }


    public String getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(
            String newEndDate) {
        this.newEndDate =
                newEndDate;
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


    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {
        this.renewalFee =
                renewalFee;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {
        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {
        this.totalAmount =
                totalAmount;
    }


    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {
        this.paidAmount =
                paidAmount;
    }


    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {
        this.balanceAmount =
                balanceAmount;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}