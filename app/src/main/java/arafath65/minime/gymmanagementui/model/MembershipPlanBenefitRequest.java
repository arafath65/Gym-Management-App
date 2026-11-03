package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MembershipPlanBenefitRequest {

    private Integer benefitId;

    private String benefitType;

    private BigDecimal fee;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanBenefitRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(Integer benefitId) {
        this.benefitId = benefitId;
    }


    public String getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(String benefitType) {
        this.benefitType = benefitType;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}