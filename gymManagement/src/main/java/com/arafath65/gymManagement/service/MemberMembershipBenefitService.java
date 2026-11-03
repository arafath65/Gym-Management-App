package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MemberMembershipBenefitRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipBenefitResponse;

import java.util.List;

public interface MemberMembershipBenefitService {

    // =====================================================
    // ADD BENEFIT TO MEMBER MEMBERSHIP
    // =====================================================

    MemberMembershipBenefitResponse
    addBenefitToMembership(
            Integer tenantId,
            Integer memberMembershipId,
            MemberMembershipBenefitRequest request
    );


    // =====================================================
    // GET MEMBERSHIP BENEFITS
    // =====================================================

    List<MemberMembershipBenefitResponse>
    getMembershipBenefits(
            Integer tenantId,
            Integer memberMembershipId
    );

    // =====================================================
// GET ALL BENEFIT HISTORY OF MEMBER
// =====================================================

    List<MemberMembershipBenefitResponse>
    getBenefitHistory(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // GET ONE
    // =====================================================

    MemberMembershipBenefitResponse
    getMembershipBenefit(
            Integer tenantId,
            Integer memberMembershipBenefitId
    );


    // =====================================================
    // DEACTIVATE
    // =====================================================

    void deactivateMembershipBenefit(
            Integer tenantId,
            Integer memberMembershipBenefitId
    );
}