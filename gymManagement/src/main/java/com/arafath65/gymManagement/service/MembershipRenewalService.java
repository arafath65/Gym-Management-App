package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MembershipRenewalRequest;
import com.arafath65.gymManagement.entity.MembershipRenewal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipRenewalService {

    // =====================================================
    // CREATE RENEWAL
    // =====================================================

    MembershipRenewal createRenewal(
            Integer tenantId,
            MembershipRenewalRequest request
    );


    // =====================================================
    // FIND BY ID
    // =====================================================

    Optional<MembershipRenewal> findById(
            Integer renewalId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipRenewal> findByMembership(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipRenewal> findByMember(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipRenewal> findByBranch(
            Integer tenantId,
            Integer branchId
    );

    // =====================================================
// FIND BY DATE RANGE
// =====================================================

    List<MembershipRenewal> findByDateRange(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );
}