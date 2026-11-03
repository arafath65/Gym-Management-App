package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MemberMembershipBenefitResponse {

    private Integer memberMembershipBenefitId;

    private Integer tenantId;

    private Integer branchId;
    private String branchName;

    private Integer memberMembershipId;
    private String membershipNumber;

    private Integer memberId;
    private String memberNo;
    private String memberName;

    private Integer benefitId;
    private String benefitCode;
    private String benefitName;

    private String sourceType;

    private BigDecimal fee;

    private String startDate;
    private String endDate;

    private Boolean status;

    private String notes;

    private String createdAt;
    private String updatedAt;


    public MemberMembershipBenefitResponse() {
    }


    public Integer getMemberMembershipBenefitId() {
        return memberMembershipBenefitId;
    }

    public void setMemberMembershipBenefitId(
            Integer memberMembershipBenefitId) {

        this.memberMembershipBenefitId =
                memberMembershipBenefitId;
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


    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }


    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }


    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(Integer benefitId) {
        this.benefitId = benefitId;
    }


    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
    }


    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(String benefitName) {
        this.benefitName = benefitName;
    }


    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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