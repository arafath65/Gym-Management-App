package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository
        extends JpaRepository<Member, Integer> {


    // =====================================================
    // FIND MEMBER BY TENANT + MEMBER NUMBER
    // =====================================================

    Optional<Member> findByTenant_TenantIdAndMemberNo(
            int tenantId,
            String memberNo
    );


    // =====================================================
    // FIND MEMBERS BY TENANT + BRANCH + STATUS
    // =====================================================

    List<Member> findByTenant_TenantIdAndBranch_BranchIdAndStatus(
            int tenantId,
            int branchId,
            boolean status
    );


    // =====================================================
    // FIND MEMBERS BY TENANT + STATUS
    // =====================================================

    List<Member> findByTenant_TenantIdAndStatus(
            int tenantId,
            boolean status
    );


    // =====================================================
    // CHECK MEMBER NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndMemberNo(
            int tenantId,
            String memberNo
    );
}