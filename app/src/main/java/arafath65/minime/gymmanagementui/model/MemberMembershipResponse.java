package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MemberMembershipResponse {

    private Integer memberMembershipId;

    private Integer tenantId;

    private Integer branchId;
    private String branchName;

    private Integer memberId;

    private Integer membershipPlanId;
    private String planCode;
    private String planName;

    private String membershipNumber;

    private String startDate;
    private String endDate;

    private BigDecimal planPrice;
    private BigDecimal joiningFee;
    private BigDecimal renewalFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    private Boolean status;

    private String membershipState;

    private Integer totalFreezeDays;
    private Integer renewalCount;

    private String notes;

    private String createdAt;
    private String updatedAt;


    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getMembershipState() {
        return membershipState;
    }

    public Integer getTotalFreezeDays() {
        return totalFreezeDays;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}