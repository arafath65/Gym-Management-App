package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MemberMembershipProgramRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipProgramResponse;

import java.util.List;

public interface MemberMembershipProgramService {

    // =====================================================
    // ASSIGN PROGRAM
    // =====================================================

    MemberMembershipProgramResponse assignProgram(
            Integer tenantId,
            Integer memberMembershipId,
            MemberMembershipProgramRequest request
    );


    // =====================================================
    // GET PROGRAMS OF MEMBERSHIP
    // =====================================================

    List<MemberMembershipProgramResponse> getPrograms(
            Integer tenantId,
            Integer memberMembershipId
    );

    // =====================================================
// GET ALL PROGRAM HISTORY OF MEMBER
// =====================================================

    List<MemberMembershipProgramResponse> getProgramHistory(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // GET ONE
    // =====================================================

    MemberMembershipProgramResponse getProgram(
            Integer tenantId,
            Integer memberMembershipProgramId
    );


    // =====================================================
    // UPDATE
    // =====================================================

    MemberMembershipProgramResponse updateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId,
            MemberMembershipProgramRequest request
    );


    // =====================================================
    // DEACTIVATE
    // =====================================================

    void deactivateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId
    );


    // =====================================================
    // ACTIVATE
    // =====================================================

    void activateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId
    );
}