package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MembershipPlanBenefitRequest;
import com.arafath65.gymManagement.dto.response.MembershipPlanBenefitResponse;

import java.util.List;

public interface MembershipPlanBenefitService {

    // =====================================================
    // ADD BENEFIT TO PLAN
    // =====================================================

    MembershipPlanBenefitResponse addBenefitToPlan(
            Integer tenantId,
            Integer membershipPlanId,
            MembershipPlanBenefitRequest request
    );


    // =====================================================
    // GET PLAN BENEFITS
    // =====================================================

    List<MembershipPlanBenefitResponse> getPlanBenefits(
            Integer tenantId,
            Integer membershipPlanId
    );


    // =====================================================
    // GET ONE
    // =====================================================

    MembershipPlanBenefitResponse getPlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId
    );


    // =====================================================
    // UPDATE
    // =====================================================

    MembershipPlanBenefitResponse updatePlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId,
            MembershipPlanBenefitRequest request
    );


    // =====================================================
    // DEACTIVATE
    // =====================================================

    void deactivatePlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId
    );
}