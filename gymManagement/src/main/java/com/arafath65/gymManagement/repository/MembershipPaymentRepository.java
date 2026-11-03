package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPaymentRepository
        extends JpaRepository<MembershipPayment, Integer> {

    // =====================================================
    // FIND BY TENANT
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdOrderByPaymentDateDesc(
            Integer tenantId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY PAYMENT NUMBER
    // =====================================================

    Optional<MembershipPayment>
    findByTenant_TenantIdAndPaymentNumber(
            Integer tenantId,
            String paymentNumber
    );


    // =====================================================
    // FIND BY BRANCH + PAYMENT NUMBER
    // =====================================================

    Optional<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdAndPaymentNumber(
            Integer tenantId,
            Integer branchId,
            String paymentNumber
    );


    // =====================================================
    // FIND BY PAYMENT DATE
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndPaymentDateOrderByPaymentDateDesc(
            Integer tenantId,
            LocalDate paymentDate
    );


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // FIND BY BRANCH + DATE RANGE
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndBranch_BranchIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Integer tenantId,
            Integer branchId,
            LocalDate startDate,
            LocalDate endDate
    );


    // =====================================================
    // FIND ACTIVE PAYMENTS
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndStatusOrderByPaymentDateDesc(
            Integer tenantId,
            Boolean status
    );


    // =====================================================
    // FIND ACTIVE PAYMENTS BY MEMBERSHIP
    // =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusOrderByPaymentDateDesc(
            Integer tenantId,
            Integer memberMembershipId,
            Boolean status
    );


    // =====================================================
    // CHECK PAYMENT NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndPaymentNumber(
            Integer tenantId,
            String paymentNumber
    );


    // =====================================================
    // CHECK BRANCH + PAYMENT NUMBER
    // =====================================================

    boolean existsByTenant_TenantIdAndBranch_BranchIdAndPaymentNumber(
            Integer tenantId,
            Integer branchId,
            String paymentNumber
    );

    // =====================================================
// FIND BY BILLING CYCLE
// =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndMembershipBillingCycle_MembershipBillingCycleIdOrderByPaymentDateDesc(
            Integer tenantId,
            Integer membershipBillingCycleId
    );


// =====================================================
// FIND ACTIVE PAYMENTS BY BILLING CYCLE
// =====================================================

    List<MembershipPayment>
    findByTenant_TenantIdAndMembershipBillingCycle_MembershipBillingCycleIdAndStatusOrderByPaymentDateDesc(
            Integer tenantId,
            Integer membershipBillingCycleId,
            Boolean status
    );


// =====================================================
// CHECK PAYMENT EXISTS FOR BILLING CYCLE
// =====================================================

    boolean existsByTenant_TenantIdAndMembershipBillingCycle_MembershipBillingCycleId(
            Integer tenantId,
            Integer membershipBillingCycleId
    );
}