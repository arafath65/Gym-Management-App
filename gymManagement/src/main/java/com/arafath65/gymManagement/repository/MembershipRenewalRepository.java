package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MembershipRenewalRepository
        extends JpaRepository<MembershipRenewal, Integer> {

    List<MembershipRenewal>
    findByTenant_TenantIdOrderByRenewalDateDesc(
            Integer tenantId
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndBranch_BranchIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer branchId
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndMember_MemberIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer memberId
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer memberMembershipId
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndBranch_BranchIdAndMemberMembership_MemberMembershipIdOrderByRenewalDateDesc(
            Integer tenantId,
            Integer branchId,
            Integer memberMembershipId
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndRenewalDateBetweenOrderByRenewalDateDesc(
            Integer tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndBranch_BranchIdAndRenewalDateBetweenOrderByRenewalDateDesc(
            Integer tenantId,
            Integer branchId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<MembershipRenewal>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByRenewalNumberAsc(
            Integer tenantId,
            Integer memberMembershipId
    );
}