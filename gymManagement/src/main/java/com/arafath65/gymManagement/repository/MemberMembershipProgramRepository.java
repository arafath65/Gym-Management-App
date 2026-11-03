package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MemberMembershipProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberMembershipProgramRepository
        extends JpaRepository<MemberMembershipProgram, Integer> {

    // =====================================================
    // FIND PROGRAMS OF A MEMBERSHIP
    // =====================================================

    List<MemberMembershipProgram>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusTrue(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND ALL PROGRAM HISTORY OF A MEMBER
    // =====================================================

    List<MemberMembershipProgram>
    findByTenant_TenantIdAndMemberMembership_Member_MemberId(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND SPECIFIC ASSIGNMENT
    // =====================================================

    Optional<MemberMembershipProgram>
    findByMemberMembershipProgramIdAndTenant_TenantId(
            Integer memberMembershipProgramId,
            Integer tenantId
    );


    // =====================================================
    // CHECK ACTIVE PROGRAM
    // =====================================================

    boolean
    existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndProgram_ProgramIdAndStatusTrue(
            Integer tenantId,
            Integer memberMembershipId,
            Integer programId
    );


    // =====================================================
    // FIND BY MEMBER MEMBERSHIP + PROGRAM
    // =====================================================

    Optional<MemberMembershipProgram>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndProgram_ProgramId(
            Integer tenantId,
            Integer memberMembershipId,
            Integer programId
    );
}