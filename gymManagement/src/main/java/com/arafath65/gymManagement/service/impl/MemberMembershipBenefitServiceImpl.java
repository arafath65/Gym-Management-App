package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MemberMembershipBenefitRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipBenefitResponse;
import com.arafath65.gymManagement.entity.Benefit;
import com.arafath65.gymManagement.entity.BenefitBranch;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MemberMembershipBenefit;
import com.arafath65.gymManagement.repository.BenefitBranchRepository;
import com.arafath65.gymManagement.repository.BenefitRepository;
import com.arafath65.gymManagement.repository.MemberMembershipBenefitRepository;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.service.MemberMembershipBenefitService;
import com.arafath65.gymManagement.service.MemberMembershipService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MemberMembershipBenefitServiceImpl
        implements MemberMembershipBenefitService {

    private final MemberMembershipBenefitRepository
            memberMembershipBenefitRepository;

    private final MemberMembershipRepository
            memberMembershipRepository;

    private final BenefitBranchRepository
            benefitBranchRepository;

    private final BenefitRepository
            benefitRepository;

    private final MemberMembershipService
            memberMembershipService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipBenefitServiceImpl(

            MemberMembershipBenefitRepository
                    memberMembershipBenefitRepository,

            MemberMembershipRepository
                    memberMembershipRepository,

            BenefitBranchRepository
                    benefitBranchRepository,

            BenefitRepository
                    benefitRepository,

            MemberMembershipService
                    memberMembershipService) {

        this.memberMembershipBenefitRepository =
                memberMembershipBenefitRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.benefitBranchRepository =
                benefitBranchRepository;

        this.benefitRepository =
                benefitRepository;

        this.memberMembershipService =
                memberMembershipService;
    }


    // =====================================================
    // ADD BENEFIT TO MEMBER MEMBERSHIP
    // =====================================================

    @Override
    public MemberMembershipBenefitResponse
    addBenefitToMembership(
            Integer tenantId,
            Integer memberMembershipId,
            MemberMembershipBenefitRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Member membership benefit request is required."
            );
        }


        if (request.getBenefitId() == null) {

            throw new IllegalArgumentException(
                    "Benefit is required."
            );
        }


        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                memberMembershipId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership not found."
                                )
                        );


        // -------------------------------------------------
        // VALIDATE MEMBERSHIP TENANT
        // -------------------------------------------------

        validateMembershipTenant(
                membership,
                tenantId
        );


        // -------------------------------------------------
        // MEMBERSHIP MUST BE ACTIVE
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                membership.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Membership is inactive."
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP STATE
        // -------------------------------------------------

        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cannot add a benefit to a cancelled membership."
            );
        }


        if (membership.getMembershipState()
                == MemberMembership.MembershipState.EXPIRED) {

            throw new IllegalArgumentException(
                    "Cannot add a benefit to an expired membership."
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP BRANCH
        // -------------------------------------------------

        Branch membershipBranch =
                membership.getBranch();


        if (membershipBranch == null
                || membershipBranch.getBranchId() == null) {

            throw new IllegalArgumentException(
                    "Membership branch is missing."
            );
        }


        Integer branchId =
                membershipBranch.getBranchId();


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
        // VERIFY BENEFIT AVAILABLE
        // FOR MEMBERSHIP BRANCH
        // -------------------------------------------------

        BenefitBranch benefitBranch =
                benefitBranchRepository
                        .findByTenant_TenantIdAndBranch_BranchIdAndBenefit_BenefitIdAndStatusTrue(
                                tenantId,
                                branchId,
                                request.getBenefitId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "This benefit is not available for the member's branch."
                                )
                        );


        // -------------------------------------------------
        // BENEFIT BRANCH MUST BE ACTIVE
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                benefitBranch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "This benefit is inactive for the member's branch."
            );
        }


        // -------------------------------------------------
        // CHECK DUPLICATE ACTIVE BENEFIT
        // -------------------------------------------------

        if (memberMembershipBenefitRepository
                .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBenefit_BenefitIdAndStatusTrue(
                        tenantId,
                        memberMembershipId,
                        request.getBenefitId()
                )) {

            throw new IllegalArgumentException(
                    "This benefit is already active for this membership."
            );
        }


        // -------------------------------------------------
        // SOURCE
        // -------------------------------------------------

        /*
         * Benefits selected from the branch Benefit catalogue
         * are purchased/add-on benefits.
         *
         * They are no longer dependent on the membership plan.
         */

        MemberMembershipBenefit.SourceType sourceType =
                MemberMembershipBenefit.SourceType.PURCHASED;


        // -------------------------------------------------
        // DETERMINE BRANCH FEE
        // -------------------------------------------------

        BigDecimal fee =
                benefitBranch.getFee();


        /*
         * If the branch assignment does not have a specific
         * fee, use the Benefit master default fee.
         */

        if (fee == null) {

            fee =
                    benefit.getDefaultFee();
        }


        if (fee == null) {

            fee =
                    BigDecimal.ZERO;
        }


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        LocalDate startDate =
                request.getStartDate();


        if (startDate == null) {

            startDate =
                    LocalDate.now();
        }


        // -------------------------------------------------
        // START DATE CANNOT BE BEFORE
        // MEMBERSHIP START DATE
        // -------------------------------------------------

        if (membership.getStartDate() != null
                && startDate.isBefore(
                membership.getStartDate()
        )) {

            throw new IllegalArgumentException(
                    "Benefit start date cannot be before membership start date."
            );
        }


        // -------------------------------------------------
        // END DATE
        // -------------------------------------------------

        LocalDate endDate =
                membership.getEndDate();


        // -------------------------------------------------
        // START DATE CANNOT BE AFTER
        // MEMBERSHIP END DATE
        // -------------------------------------------------

        if (membership.getEndDate() != null
                && startDate.isAfter(
                membership.getEndDate()
        )) {

            throw new IllegalArgumentException(
                    "Benefit start date cannot be after membership end date."
            );
        }


        // -------------------------------------------------
        // CREATE MEMBER BENEFIT
        // -------------------------------------------------

        MemberMembershipBenefit
                memberBenefit =
                new MemberMembershipBenefit();


        memberBenefit.setTenant(
                membership.getTenant()
        );


        memberBenefit.setBranch(
                membership.getBranch()
        );


        memberBenefit.setMemberMembership(
                membership
        );


        memberBenefit.setBenefit(
                benefit
        );


        memberBenefit.setSourceType(
                sourceType
        );


        memberBenefit.setFee(
                fee
        );


        memberBenefit.setStartDate(
                startDate
        );


        memberBenefit.setEndDate(
                endDate
        );


        memberBenefit.setStatus(
                true
        );


        memberBenefit.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // SAVE BENEFIT
        // -------------------------------------------------

        MemberMembershipBenefit saved =
                memberMembershipBenefitRepository.save(
                        memberBenefit
                );


        // -------------------------------------------------
        // RECALCULATE MEMBERSHIP FINANCIALS
        // -------------------------------------------------

        /*
         * Purchased branch benefits increase the
         * membership financial total.
         */

        memberMembershipService
                .recalculateFinancials(
                        memberMembershipId
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return mapToResponse(
                saved
        );
    }


    // =====================================================
    // GET MEMBERSHIP BENEFITS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MemberMembershipBenefitResponse>
    getMembershipBenefits(
            Integer tenantId,
            Integer memberMembershipId) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                memberMembershipId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership not found."
                                )
                        );


        validateMembershipTenant(
                membership,
                tenantId
        );


        return memberMembershipBenefitRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusTrue(
                        tenantId,
                        memberMembershipId
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
    public MemberMembershipBenefitResponse
    getMembershipBenefit(
            Integer tenantId,
            Integer memberMembershipBenefitId) {

        MemberMembershipBenefit memberBenefit =
                memberMembershipBenefitRepository
                        .findById(
                                memberMembershipBenefitId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership benefit not found."
                                )
                        );


        validateMemberBenefitTenant(
                memberBenefit,
                tenantId
        );


        return mapToResponse(
                memberBenefit
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @Override
    public void deactivateMembershipBenefit(
            Integer tenantId,
            Integer memberMembershipBenefitId) {

        MemberMembershipBenefit memberBenefit =
                memberMembershipBenefitRepository
                        .findById(
                                memberMembershipBenefitId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership benefit not found."
                                )
                        );


        validateMemberBenefitTenant(
                memberBenefit,
                tenantId
        );


        // -------------------------------------------------
        // GET MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberBenefit.getMemberMembership();


        if (membership == null) {

            throw new IllegalArgumentException(
                    "Member membership is missing for this benefit."
            );
        }


        // -------------------------------------------------
        // DEACTIVATE
        // -------------------------------------------------

        memberBenefit.setStatus(
                false
        );


        memberMembershipBenefitRepository.save(
                memberBenefit
        );


        // -------------------------------------------------
        // RECALCULATE
        // -------------------------------------------------

        /*
         * Purchased branch benefits are included in
         * membership financial calculations.
         */

        memberMembershipService
                .recalculateFinancials(
                        membership
                                .getMemberMembershipId()
                );
    }


    // =====================================================
    // VALIDATE MEMBERSHIP TENANT
    // =====================================================

    private void validateMembershipTenant(
            MemberMembership membership,
            Integer tenantId) {

        if (membership.getTenant() == null
                || membership
                .getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Member membership does not belong to this tenant."
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
    // VALIDATE MEMBER BENEFIT TENANT
    // =====================================================

    private void validateMemberBenefitTenant(
            MemberMembershipBenefit memberBenefit,
            Integer tenantId) {

        if (memberBenefit.getTenant() == null
                || memberBenefit
                .getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Member membership benefit does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private MemberMembershipBenefitResponse
    mapToResponse(
            MemberMembershipBenefit memberBenefit) {

        MemberMembershipBenefitResponse response =
                new MemberMembershipBenefitResponse();


        response.setMemberMembershipBenefitId(
                memberBenefit
                        .getMemberMembershipBenefitId()
        );


        response.setTenantId(
                memberBenefit
                        .getTenant()
                        .getTenantId()
        );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch =
                memberBenefit.getBranch();


        if (branch != null) {

            response.setBranchId(
                    branch.getBranchId()
            );


            response.setBranchName(
                    branch.getBranchName()
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberBenefit
                        .getMemberMembership();


        response.setMemberMembershipId(
                membership
                        .getMemberMembershipId()
        );


        response.setMembershipNumber(
                membership
                        .getMembershipNumber()
        );


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        Member member =
                membership.getMember();


        if (member != null) {

            response.setMemberId(
                    member.getMemberId()
            );


            response.setMemberNo(
                    member.getMemberNo()
            );


            response.setMemberName(
                    buildMemberName(
                            member
                    )
            );
        }


        // -------------------------------------------------
        // BENEFIT
        // -------------------------------------------------

        Benefit benefit =
                memberBenefit.getBenefit();


        response.setBenefitId(
                benefit.getBenefitId()
        );


        response.setBenefitCode(
                benefit.getBenefitCode()
        );


        response.setBenefitName(
                benefit.getBenefitName()
        );


        // -------------------------------------------------
        // SOURCE
        // -------------------------------------------------

        response.setSourceType(
                memberBenefit.getSourceType()
        );


        // -------------------------------------------------
        // FEE
        // -------------------------------------------------

        response.setFee(
                memberBenefit.getFee()
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        response.setStartDate(
                memberBenefit.getStartDate()
        );


        response.setEndDate(
                memberBenefit.getEndDate()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        response.setStatus(
                memberBenefit.getStatus()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                memberBenefit.getNotes()
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                memberBenefit.getCreatedAt()
        );


        response.setUpdatedAt(
                memberBenefit.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // MEMBER NAME
    // =====================================================

    private String buildMemberName(
            Member member) {

        String firstName =
                member.getFirstName();


        String lastName =
                member.getLastName();


        if (firstName == null) {

            firstName = "";
        }


        if (lastName == null) {

            lastName = "";
        }


        return (
                firstName
                        + " "
                        + lastName
        ).trim();
    }

    // =====================================================
// GET ALL BENEFIT HISTORY OF MEMBER
// =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MemberMembershipBenefitResponse>
    getBenefitHistory(
            Integer tenantId,
            Integer memberId) {

        return memberMembershipBenefitRepository
                .findByTenant_TenantIdAndMemberMembership_Member_MemberId(
                        tenantId,
                        memberId
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }
}