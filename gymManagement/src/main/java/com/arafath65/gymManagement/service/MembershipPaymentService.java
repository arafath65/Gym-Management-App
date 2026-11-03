package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MembershipPaymentRequest;
import com.arafath65.gymManagement.dto.response.MembershipPaymentSummaryResponse;
import com.arafath65.gymManagement.entity.MembershipPayment;

import java.util.List;
import java.util.Optional;

public interface MembershipPaymentService {

    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    MembershipPayment createPayment(
            Integer tenantId,
            MembershipPaymentRequest request
    );


    // =====================================================
    // FIND BY ID
    // =====================================================

    Optional<MembershipPayment> findById(
            Integer tenantId,
            Integer paymentId
    );


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    List<MembershipPayment> findByMembership(
            Integer tenantId,
            Integer memberMembershipId
    );


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    List<MembershipPayment> findByMember(
            Integer tenantId,
            Integer memberId
    );


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    List<MembershipPayment> findByBranch(
            Integer tenantId,
            Integer branchId
    );


    // =====================================================
    // FIND BY BILLING CYCLE
    // =====================================================

    List<MembershipPayment> findByBillingCycle(
            Integer tenantId,
            Integer membershipBillingCycleId
    );


    // =====================================================
    // DELETE
    // =====================================================

    void delete(
            Integer tenantId,
            Integer paymentId
    );


    // =====================================================
    // CANCEL PAYMENT
    // =====================================================

    void cancelPayment(
            Integer tenantId,
            Integer paymentId
    );


    // =====================================================
    // PAYMENT SUMMARY
    // =====================================================

    MembershipPaymentSummaryResponse
    getMembershipPaymentSummary(
            Integer tenantId,
            Integer memberMembershipId
    );
}