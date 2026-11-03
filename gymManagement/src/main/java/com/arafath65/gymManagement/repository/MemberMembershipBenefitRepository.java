package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MemberMembershipBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberMembershipBenefitRepository
        extends JpaRepository<MemberMembershipBenefit, Integer> {

    // =====================================================
    // FIND BENEFITS OF A MEMBER MEMBERSHIP
    // =====================================================

    List<MemberMembershipBenefit>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusTrue(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND ALL BENEFIT HISTORY OF A MEMBER
    // =====================================================

    List<MemberMembershipBenefit>
    findByTenant_TenantIdAndMemberMembership_Member_MemberId(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND SPECIFIC MEMBER MEMBERSHIP BENEFIT
    // =====================================================

    Optional<MemberMembershipBenefit>
    findByMemberMembershipBenefitIdAndTenant_TenantId(
            Integer memberMembershipBenefitId,
            Integer tenantId
    );


    // =====================================================
    // CHECK ACTIVE BENEFIT
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBenefit_BenefitIdAndStatusTrue(
            Integer tenantId,
            Integer memberMembershipId,
            Integer benefitId
    );
}