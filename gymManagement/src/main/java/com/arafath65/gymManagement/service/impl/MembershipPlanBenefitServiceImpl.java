package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MembershipPlanBenefitRequest;
import com.arafath65.gymManagement.dto.response.MembershipPlanBenefitResponse;
import com.arafath65.gymManagement.entity.Benefit;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.entity.MembershipPlanBenefit;
import com.arafath65.gymManagement.repository.BenefitRepository;
import com.arafath65.gymManagement.repository.MembershipPlanBenefitRepository;
import com.arafath65.gymManagement.repository.MembershipPlanRepository;
import com.arafath65.gymManagement.service.MembershipPlanBenefitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MembershipPlanBenefitServiceImpl
        implements MembershipPlanBenefitService {

    private final MembershipPlanBenefitRepository
            membershipPlanBenefitRepository;

    private final MembershipPlanRepository
            membershipPlanRepository;

    private final BenefitRepository
            benefitRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanBenefitServiceImpl(
            MembershipPlanBenefitRepository
                    membershipPlanBenefitRepository,

            MembershipPlanRepository
                    membershipPlanRepository,

            BenefitRepository
                    benefitRepository) {

        this.membershipPlanBenefitRepository =
                membershipPlanBenefitRepository;

        this.membershipPlanRepository =
                membershipPlanRepository;

        this.benefitRepository =
                benefitRepository;
    }


    // =====================================================
    // ADD BENEFIT TO PLAN
    // =====================================================

    @Override
    public MembershipPlanBenefitResponse
    addBenefitToPlan(
            Integer tenantId,
            Integer membershipPlanId,
            MembershipPlanBenefitRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Membership plan benefit request is required."
            );
        }


        // -------------------------------------------------
        // LOAD PLAN
        // -------------------------------------------------

        MembershipPlan membershipPlan =
                membershipPlanRepository
                        .findById(membershipPlanId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan not found."
                                )
                        );


        // -------------------------------------------------
        // VALIDATE PLAN TENANT
        // -------------------------------------------------

        validatePlanTenant(
                membershipPlan,
                tenantId
        );


        // -------------------------------------------------
        // LOAD BENEFIT
        // -------------------------------------------------

        Benefit benefit =
                benefitRepository
                        .findById(
                                request.getBenefitId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Benefit not found."
                                )
                        );


        // -------------------------------------------------
        // VALIDATE BENEFIT TENANT
        // -------------------------------------------------

        validateBenefitTenant(
                benefit,
                tenantId
        );


        // -------------------------------------------------
        // BENEFIT MUST BE ACTIVE
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                benefit.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Benefit is inactive."
            );
        }


        // -------------------------------------------------
        // CHECK DUPLICATE
        // -------------------------------------------------

        if (membershipPlanBenefitRepository
                .existsByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBenefit_BenefitId(
                        tenantId,
                        membershipPlanId,
                        request.getBenefitId()
                )) {

            throw new IllegalArgumentException(
                    "This benefit is already assigned to this membership plan."
            );
        }


        // -------------------------------------------------
        // VALIDATE FEE
        // -------------------------------------------------

        BigDecimal fee =
                request.getFee();

        if (fee == null) {

            throw new IllegalArgumentException(
                    "Benefit fee is required."
            );
        }

        if (fee.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Benefit fee cannot be negative."
            );
        }


        // -------------------------------------------------
        // INCLUDED BENEFIT
        // -------------------------------------------------

        if (request.getBenefitType()
                == MembershipPlanBenefit.BenefitType.INCLUDED) {

            fee = BigDecimal.ZERO;
        }


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        MembershipPlanBenefit
                planBenefit =
                new MembershipPlanBenefit();

        planBenefit.setTenant(
                membershipPlan.getTenant()
        );

        planBenefit.setMembershipPlan(
                membershipPlan
        );

        planBenefit.setBenefit(
                benefit
        );

        planBenefit.setBenefitType(
                request.getBenefitType()
        );

        planBenefit.setFee(
                fee
        );

        planBenefit.setStatus(
                true
        );


        MembershipPlanBenefit saved =
                membershipPlanBenefitRepository.save(
                        planBenefit
                );


        return mapToResponse(
                saved
        );
    }


    // =====================================================
    // GET PLAN BENEFITS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanBenefitResponse>
    getPlanBenefits(
            Integer tenantId,
            Integer membershipPlanId) {

        MembershipPlan membershipPlan =
                membershipPlanRepository
                        .findById(membershipPlanId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan not found."
                                )
                        );


        validatePlanTenant(
                membershipPlan,
                tenantId
        );


        return membershipPlanBenefitRepository
                .findByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndStatusTrue(
                        tenantId,
                        membershipPlanId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =====================================================
    // GET ONE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public MembershipPlanBenefitResponse
    getPlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId) {

        MembershipPlanBenefit planBenefit =
                membershipPlanBenefitRepository
                        .findById(
                                membershipPlanBenefitId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan benefit not found."
                                )
                        );


        validateTenant(
                planBenefit,
                tenantId
        );


        return mapToResponse(
                planBenefit
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public MembershipPlanBenefitResponse
    updatePlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId,
            MembershipPlanBenefitRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Membership plan benefit request is required."
            );
        }


        MembershipPlanBenefit planBenefit =
                membershipPlanBenefitRepository
                        .findById(
                                membershipPlanBenefitId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan benefit not found."
                                )
                        );


        validateTenant(
                planBenefit,
                tenantId
        );


        // -------------------------------------------------
        // LOAD NEW BENEFIT
        // -------------------------------------------------

        Benefit benefit =
                benefitRepository
                        .findById(
                                request.getBenefitId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Benefit not found."
                                )
                        );


        validateBenefitTenant(
                benefit,
                tenantId
        );


        if (!Boolean.TRUE.equals(
                benefit.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Benefit is inactive."
            );
        }


        // -------------------------------------------------
        // CHECK DUPLICATE WHEN CHANGING BENEFIT
        // -------------------------------------------------

        Integer currentBenefitId =
                planBenefit
                        .getBenefit()
                        .getBenefitId();


        boolean benefitChanged =
                !currentBenefitId.equals(
                        request.getBenefitId()
                );


        if (benefitChanged
                && membershipPlanBenefitRepository
                .existsByTenant_TenantIdAndMembershipPlan_MembershipPlanIdAndBenefit_BenefitId(
                        tenantId,
                        planBenefit
                                .getMembershipPlan()
                                .getMembershipPlanId(),
                        request.getBenefitId()
                )) {

            throw new IllegalArgumentException(
                    "This benefit is already assigned to this membership plan."
            );
        }


        // -------------------------------------------------
        // VALIDATE FEE
        // -------------------------------------------------

        BigDecimal fee =
                request.getFee();

        if (fee == null) {

            throw new IllegalArgumentException(
                    "Benefit fee is required."
            );
        }

        if (fee.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Benefit fee cannot be negative."
            );
        }


        if (request.getBenefitType()
                == MembershipPlanBenefit.BenefitType.INCLUDED) {

            fee = BigDecimal.ZERO;
        }


        // -------------------------------------------------
        // UPDATE
        // -------------------------------------------------

        planBenefit.setBenefit(
                benefit
        );

        planBenefit.setBenefitType(
                request.getBenefitType()
        );

        planBenefit.setFee(
                fee
        );


        MembershipPlanBenefit updated =
                membershipPlanBenefitRepository.save(
                        planBenefit
                );


        return mapToResponse(
                updated
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @Override
    public void deactivatePlanBenefit(
            Integer tenantId,
            Integer membershipPlanBenefitId) {

        MembershipPlanBenefit planBenefit =
                membershipPlanBenefitRepository
                        .findById(
                                membershipPlanBenefitId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan benefit not found."
                                )
                        );


        validateTenant(
                planBenefit,
                tenantId
        );


        planBenefit.setStatus(
                false
        );


        membershipPlanBenefitRepository.save(
                planBenefit
        );
    }


    // =====================================================
    // VALIDATE PLAN TENANT
    // =====================================================

    private void validatePlanTenant(
            MembershipPlan membershipPlan,
            Integer tenantId) {

        if (membershipPlan.getTenant() == null
                || membershipPlan
                .getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Membership plan does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // VALIDATE BENEFIT TENANT
    // =====================================================

    private void validateBenefitTenant(
            Benefit benefit,
            Integer tenantId) {

        if (benefit.getTenant() == null
                || benefit
                .getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Benefit does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // VALIDATE PLAN BENEFIT TENANT
    // =====================================================

    private void validateTenant(
            MembershipPlanBenefit planBenefit,
            Integer tenantId) {

        if (planBenefit.getTenant() == null
                || planBenefit
                .getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Membership plan benefit does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private MembershipPlanBenefitResponse
    mapToResponse(
            MembershipPlanBenefit planBenefit) {

        MembershipPlanBenefitResponse response =
                new MembershipPlanBenefitResponse();


        response.setMembershipPlanBenefitId(
                planBenefit
                        .getMembershipPlanBenefitId()
        );


        response.setTenantId(
                planBenefit
                        .getTenant()
                        .getTenantId()
        );


        response.setMembershipPlanId(
                planBenefit
                        .getMembershipPlan()
                        .getMembershipPlanId()
        );


        response.setPlanCode(
                planBenefit
                        .getMembershipPlan()
                        .getPlanCode()
        );


        response.setPlanName(
                planBenefit
                        .getMembershipPlan()
                        .getPlanName()
        );


        response.setBenefitId(
                planBenefit
                        .getBenefit()
                        .getBenefitId()
        );


        response.setBenefitCode(
                planBenefit
                        .getBenefit()
                        .getBenefitCode()
        );


        response.setBenefitName(
                planBenefit
                        .getBenefit()
                        .getBenefitName()
        );


        response.setBenefitType(
                planBenefit
                        .getBenefitType()
        );


        response.setFee(
                planBenefit
                        .getFee()
        );


        response.setStatus(
                planBenefit
                        .getStatus()
        );


        response.setCreatedAt(
                planBenefit
                        .getCreatedAt()
        );


        response.setUpdatedAt(
                planBenefit
                        .getUpdatedAt()
        );


        return response;
    }
}