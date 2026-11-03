package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.BenefitRequest;
import com.arafath65.gymManagement.dto.response.BenefitResponse;

import java.util.List;

public interface BenefitService {

    // =====================================================
    // CREATE
    // =====================================================

    BenefitResponse createBenefit(
            Integer tenantId,
            BenefitRequest request
    );


    // =====================================================
    // GET ALL
    // =====================================================

    List<BenefitResponse> getAllBenefits(
            Integer tenantId
    );


    // =====================================================
    // GET ACTIVE
    // =====================================================

    List<BenefitResponse> getActiveBenefits(
            Integer tenantId
    );


    // =====================================================
    // GET ACTIVE BENEFITS BY BRANCH
    // =====================================================

    List<BenefitResponse> getActiveBenefitsByBranch(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // GET ONE
    // =====================================================

    BenefitResponse getBenefit(
            Integer tenantId,
            Integer benefitId
    );


    // =====================================================
    // UPDATE
    // =====================================================

    BenefitResponse updateBenefit(
            Integer tenantId,
            Integer benefitId,
            BenefitRequest request
    );


    // =====================================================
    // ACTIVATE
    // =====================================================

    void activateBenefit(
            Integer tenantId,
            Integer benefitId
    );


    // =====================================================
    // DEACTIVATE
    // =====================================================

    void deactivateBenefit(
            Integer tenantId,
            Integer benefitId
    );
}