package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MembershipPlanBranchResponse {

    private Integer membershipPlanBranchId;

    private Integer branchId;

    private String branchNo;

    private String branchName;

    private BigDecimal price;

    private BigDecimal joiningFee;

    private BigDecimal renewalFee;

    private Boolean status;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanBranchResponse() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMembershipPlanBranchId() {
        return membershipPlanBranchId;
    }

    public void setMembershipPlanBranchId(
            Integer membershipPlanBranchId) {

        this.membershipPlanBranchId =
                membershipPlanBranchId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }


    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }


    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(BigDecimal joiningFee) {
        this.joiningFee = joiningFee;
    }


    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(BigDecimal renewalFee) {
        this.renewalFee = renewalFee;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}