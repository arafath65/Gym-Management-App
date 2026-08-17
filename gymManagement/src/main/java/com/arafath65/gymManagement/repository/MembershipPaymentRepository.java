package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPaymentRepository
        extends JpaRepository<MembershipPayment, Integer> {

    List<MembershipPayment>
    findByTenant_TenantIdOrderByPaymentDateDesc(
            Integer tenantId
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndMember_MemberIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer memberId
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer memberMembershipId
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId,
            Integer memberMembershipId
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Integer tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndPaymentMethodOrderByPaymentDateDesc(
            Integer tenantId,
            MembershipPayment.PaymentMethod paymentMethod
    );

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdAndPaymentMethodOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId,
            MembershipPayment.PaymentMethod paymentMethod
    );

    boolean existsByTenant_TenantIdAndPaymentNumber(
            Integer tenantId,
            String paymentNumber
    );

    boolean existsByTenant_TenantIdAndBranch_BranchIdAndPaymentNumber(
            Integer tenantId,
            Integer branchId,
            String paymentNumber
    );

    Optional<MembershipPayment>
    findByTenant_TenantIdAndPaymentNumber(
            int tenantId,
            String paymentNumber
    );
}