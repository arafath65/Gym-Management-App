package com.arafath65.gymManagement.dto.request;

import com.arafath65.gymManagement.entity.MembershipPlanBenefit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MembershipPlanBenefitRequest {

    // =====================================================
    // BENEFIT
    // =====================================================

    @NotNull(
            message = "Benefit is required."
    )
    private Integer benefitId;


    // =====================================================
    // BENEFIT TYPE
    // =====================================================

    @NotNull(
            message = "Benefit type is required."
    )
    private MembershipPlanBenefit.BenefitType benefitType;


    // =====================================================
    // FEE
    // =====================================================

    @NotNull(
            message = "Benefit fee is required."
    )
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Benefit fee cannot be negative."
    )
    private BigDecimal fee;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(
            Integer benefitId) {

        this.benefitId =
                benefitId;
    }


    public MembershipPlanBenefit.BenefitType
    getBenefitType() {

        return benefitType;
    }

    public void setBenefitType(
            MembershipPlanBenefit.BenefitType
                    benefitType) {

        this.benefitType =
                benefitType;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }
}