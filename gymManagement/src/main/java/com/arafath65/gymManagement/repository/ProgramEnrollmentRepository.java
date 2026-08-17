package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ProgramEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramEnrollmentRepository
        extends JpaRepository<
        ProgramEnrollment,
        Integer> {


    // =====================================================
    // ALL TENANT ENROLLMENTS
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdOrderByStartDateDesc(
            Integer tenantId
    );


    // =====================================================
    // BY MEMBER
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdAndMember_MemberIdOrderByStartDateDesc(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // BY PROGRAM
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdAndProgram_ProgramIdOrderByStartDateDesc(
            Integer tenantId,
            Integer programId
    );


    // =====================================================
    // BY BRANCH
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdAndBranch_BranchIdOrderByStartDateDesc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // ACTIVE BY MEMBER
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdAndMember_MemberIdAndEnrollmentStateOrderByEndDateAsc(
            Integer tenantId,
            Integer memberId,
            ProgramEnrollment.EnrollmentState enrollmentState
    );


    // =====================================================
    // ACTIVE BY PROGRAM
    // =====================================================

    List<ProgramEnrollment>
    findByTenant_TenantIdAndProgram_ProgramIdAndEnrollmentStateOrderByEndDateAsc(
            Integer tenantId,
            Integer programId,
            ProgramEnrollment.EnrollmentState enrollmentState
    );


    // =====================================================
    // ENROLLMENT NUMBER
    // =====================================================

    Optional<ProgramEnrollment>
    findByTenant_TenantIdAndEnrollmentNumber(
            Integer tenantId,
            String enrollmentNumber
    );


    // =====================================================
    // EXISTS ENROLLMENT NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndEnrollmentNumber(
            Integer tenantId,
            String enrollmentNumber
    );
}