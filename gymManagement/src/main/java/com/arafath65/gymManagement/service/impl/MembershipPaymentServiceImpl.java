package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MembershipPaymentRequest;
import com.arafath65.gymManagement.dto.response.MembershipPaymentSummaryResponse;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import com.arafath65.gymManagement.entity.MembershipPayment;
import com.arafath65.gymManagement.entity.MembershipPaymentDetail;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipBillingCycleRepository;
import com.arafath65.gymManagement.repository.MembershipPaymentRepository;
import com.arafath65.gymManagement.repository.MembershipRenewalRepository;
import com.arafath65.gymManagement.service.MembershipPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipPaymentServiceImpl
        implements MembershipPaymentService {

    private final MembershipPaymentRepository
            paymentRepository;

    private final MemberMembershipRepository
            membershipRepository;

    private final MembershipBillingCycleRepository
            billingCycleRepository;

    private final MembershipRenewalRepository
            renewalRepository;


    public MembershipPaymentServiceImpl(
            MembershipPaymentRepository paymentRepository,
            MemberMembershipRepository membershipRepository,
            MembershipBillingCycleRepository billingCycleRepository,
            MembershipRenewalRepository renewalRepository) {

        this.paymentRepository =
                paymentRepository;

        this.membershipRepository =
                membershipRepository;

        this.billingCycleRepository =
                billingCycleRepository;

        this.renewalRepository =
                renewalRepository;
    }


    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    @Override
    public MembershipPayment createPayment(
            Integer tenantId,
            MembershipPaymentRequest request) {

        validateTenantId(tenantId);

        if (request == null) {
            throw new BadRequestException(
                    "Payment request is required."
            );
        }

        if (request.getMemberMembershipId() == null) {
            throw new BadRequestException(
                    "Membership is required."
            );
        }

        // =====================================================
        // VALIDATE PAYMENT DETAILS
        // =====================================================

        if (request.getPaymentDetails() == null
                || request.getPaymentDetails().isEmpty()) {

            throw new BadRequestException(
                    "At least one payment method is required."
            );
        }


        BigDecimal paymentAmount =
                normalizeAmount(
                        request.getAmount()
                );


        if (paymentAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "Payment amount must be greater than zero."
            );
        }


        // =====================================================
        // CALCULATE PAYMENT DETAIL TOTAL
        // =====================================================

        BigDecimal detailTotal =
                BigDecimal.ZERO;


        for (var detailRequest :
                request.getPaymentDetails()) {

            if (detailRequest == null) {

                throw new BadRequestException(
                        "Payment detail cannot be null."
                );
            }


            if (detailRequest.getPaymentMethod()
                    == null) {

                throw new BadRequestException(
                        "Payment method is required."
                );
            }


            BigDecimal detailAmount =
                    normalizeAmount(
                            detailRequest.getAmount()
                    );


            if (detailAmount.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new BadRequestException(
                        "Payment detail amount must be greater than zero."
                );
            }


            detailTotal =
                    detailTotal.add(
                            detailAmount
                    );
        }


        detailTotal =
                detailTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // =====================================================
        // PAYMENT TOTAL MUST MATCH DETAILS
        // =====================================================

        if (detailTotal.compareTo(
                paymentAmount
        ) != 0) {

            throw new BadRequestException(
                    "Payment amount must equal the total of payment details."
            );
        }


        // =====================================================
        // LOAD MEMBERSHIP
        // =====================================================

        MemberMembership membership =
                membershipRepository
                        .findById(
                                request.getMemberMembershipId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        validateMembershipTenant(
                tenantId,
                membership
        );


        // =====================================================
        // TARGET VALIDATION
        // =====================================================

        Integer renewalId =
                request.getMembershipRenewalId();

        Integer billingCycleId =
                request.getMembershipBillingCycleId();


        if (renewalId != null
                && billingCycleId != null) {

            throw new BadRequestException(
                    "A payment cannot belong to both a renewal and a billing cycle."
            );
        }


        MembershipRenewal renewal = null;

        MembershipBillingCycle billingCycle = null;


        // =====================================================
        // RENEWAL PAYMENT
        // =====================================================

        if (renewalId != null) {

            renewal =
                    renewalRepository
                            .findById(
                                    renewalId
                            )
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Membership renewal not found."
                                    )
                            );


            validateRenewal(
                    tenantId,
                    membership,
                    renewal
            );


            validatePaymentAgainstBalance(
                    paymentAmount,
                    renewal.getBalanceAmount(),
                    "renewal"
            );
        }


        // =====================================================
        // BILLING CYCLE PAYMENT
        // =====================================================

        if (billingCycleId != null) {

            billingCycle =
                    billingCycleRepository
                            .findById(
                                    billingCycleId
                            )
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Membership billing cycle not found."
                                    )
                            );


            validateBillingCycle(
                    tenantId,
                    membership,
                    billingCycle
            );


            validatePaymentAgainstBalance(
                    paymentAmount,
                    billingCycle.getBalanceAmount(),
                    "billing cycle"
            );
        }


        // =====================================================
        // CREATE PAYMENT MASTER
        // =====================================================

        MembershipPayment payment =
                new MembershipPayment();


        payment.setTenant(
                membership.getTenant()
        );


        payment.setBranch(
                membership.getBranch()
        );


        payment.setMemberMembership(
                membership
        );


        payment.setMembershipRenewal(
                renewal
        );


        payment.setMembershipBillingCycle(
                billingCycle
        );


        payment.setPaymentNumber(
                generatePaymentNumber()
        );


        payment.setPaymentDate(
                request.getPaymentDate() != null
                        ? request.getPaymentDate()
                        .toLocalDate()
                        : LocalDate.now()
        );


        payment.setTotalAmount(
                paymentAmount
        );


        payment.setReferenceNumber(
                null
        );


        payment.setNotes(
                request.getNotes()
        );


        payment.setStatus(true);


        // =====================================================
        // CREATE PAYMENT DETAILS
        // =====================================================

        for (var detailRequest :
                request.getPaymentDetails()) {

            MembershipPaymentDetail detail =
                    new MembershipPaymentDetail();


            detail.setPaymentMethod(
                    detailRequest.getPaymentMethod()
            );


            detail.setAmount(
                    normalizeAmount(
                            detailRequest.getAmount()
                    )
            );


            detail.setReferenceNumber(
                    detailRequest.getReferenceNumber()
            );


            payment.addPaymentDetail(
                    detail
            );
        }


        // =====================================================
        // SAVE PAYMENT
        // =====================================================

        MembershipPayment savedPayment =
                paymentRepository.save(
                        payment
                );


        // =====================================================
        // UPDATE BILLING CYCLE
        // =====================================================

        if (billingCycle != null) {

            applyPaymentToBillingCycle(
                    billingCycle,
                    paymentAmount
            );


            billingCycleRepository.save(
                    billingCycle
            );
        }


        // =====================================================
        // UPDATE RENEWAL
        // =====================================================

        if (renewal != null) {

            applyPaymentToRenewal(
                    renewal,
                    paymentAmount
            );


            renewalRepository.save(
                    renewal
            );
        }


        return savedPayment;
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipPayment> findById(
            Integer tenantId,
            Integer paymentId) {

        validateTenantId(tenantId);

        if (paymentId == null) {
            throw new BadRequestException(
                    "Payment ID is required."
            );
        }

        return paymentRepository
                .findById(paymentId)
                .filter(payment ->
                        payment.getTenant() != null
                                && payment.getTenant()
                                .getTenantId()
                                == tenantId
                );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPayment> findByMembership(
            Integer tenantId,
            Integer memberMembershipId) {

        validateTenantId(tenantId);

        return paymentRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
                        tenantId,
                        memberMembershipId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPayment> findByMember(
            Integer tenantId,
            Integer memberId) {

        validateTenantId(tenantId);

        return paymentRepository
                .findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByPaymentDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPayment> findByBranch(
            Integer tenantId,
            Integer branchId) {

        validateTenantId(tenantId);

        return paymentRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByPaymentDateDesc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND BY BILLING CYCLE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPayment> findByBillingCycle(
            Integer tenantId,
            Integer membershipBillingCycleId) {

        validateTenantId(tenantId);

        if (membershipBillingCycleId == null) {
            throw new BadRequestException(
                    "Billing cycle ID is required."
            );
        }

        return paymentRepository
                .findByTenant_TenantIdAndMembershipBillingCycle_MembershipBillingCycleIdOrderByPaymentDateDesc(
                        tenantId,
                        membershipBillingCycleId
                );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void delete(
            Integer tenantId,
            Integer paymentId) {

        validateTenantId(tenantId);

        MembershipPayment payment =
                getPayment(
                        tenantId,
                        paymentId
                );

        if (Boolean.TRUE.equals(
                payment.getStatus()
        )) {

            throw new BadRequestException(
                    "Active payment cannot be deleted. Cancel it instead."
            );
        }

        paymentRepository.delete(
                payment
        );
    }


    // =====================================================
    // CANCEL PAYMENT
    // =====================================================

    @Override
    public void cancelPayment(
            Integer tenantId,
            Integer paymentId) {

        validateTenantId(tenantId);

        // =====================================================
        // LOAD PAYMENT
        // =====================================================

        MembershipPayment payment =
                getPayment(
                        tenantId,
                        paymentId
                );


        // =====================================================
        // ALREADY CANCELLED
        // =====================================================

        if (!Boolean.TRUE.equals(
                payment.getStatus()
        )) {

            throw new BadRequestException(
                    "Payment is already cancelled."
            );
        }


        // =====================================================
        // PAYMENT AMOUNT
        // =====================================================

        BigDecimal amount =
                normalizeAmount(
                        payment.getTotalAmount()
                );


        if (amount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "Payment has an invalid amount."
            );
        }


        // =====================================================
        // BILLING CYCLE
        // =====================================================

        MembershipBillingCycle billingCycle =
                payment.getMembershipBillingCycle();


        if (billingCycle != null) {

            reverseBillingCyclePayment(
                    billingCycle,
                    amount
            );

            billingCycleRepository.save(
                    billingCycle
            );
        }


        // =====================================================
        // MEMBERSHIP RENEWAL
        // =====================================================

        MembershipRenewal renewal =
                payment.getMembershipRenewal();


        if (renewal != null) {

            reverseRenewalPayment(
                    renewal,
                    amount
            );

            renewalRepository.save(
                    renewal
            );
        }


        // =====================================================
        // ORIGINAL MEMBERSHIP
        // =====================================================

        /*
         * If this is an original membership payment,
         * restore the membership financial totals.
         *
         * This is necessary because fixed-term memberships
         * also use MembershipPayment.
         */

        if (billingCycle == null
                && renewal == null) {

            MemberMembership membership =
                    payment.getMemberMembership();

            if (membership != null) {

                reverseOriginalMembershipPayment(
                        membership,
                        amount
                );

                membershipRepository.save(
                        membership
                );
            }
        }


        // =====================================================
        // CANCEL PAYMENT
        // =====================================================

        payment.setStatus(false);

        paymentRepository.save(
                payment
        );
    }


    // =====================================================
// SUMMARY
// =====================================================

    @Override
    @Transactional(readOnly = true)
    public MembershipPaymentSummaryResponse
    getMembershipPaymentSummary(
            Integer tenantId,
            Integer memberMembershipId) {

        validateTenantId(tenantId);

        if (memberMembershipId == null) {
            throw new BadRequestException(
                    "Membership ID is required."
            );
        }

        // =====================================================
        // LOAD MEMBERSHIP
        // =====================================================

        MemberMembership membership =
                membershipRepository
                        .findById(memberMembershipId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );

        validateMembershipTenant(
                tenantId,
                membership
        );


        // =====================================================
        // LOAD ALL PAYMENTS FOR MEMBERSHIP
        // =====================================================

        List<MembershipPayment> payments =
                paymentRepository
                        .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
                                tenantId,
                                memberMembershipId
                        );


        // =====================================================
        // CALCULATE PAYMENT SUMMARY
        // =====================================================

        BigDecimal activePaymentAmount =
                BigDecimal.ZERO;

        BigDecimal cancelledPaymentAmount =
                BigDecimal.ZERO;

        int activePaymentCount = 0;

        int cancelledPaymentCount = 0;


        for (MembershipPayment payment : payments) {

            if (payment == null) {
                continue;
            }

            BigDecimal amount =
                    normalizeAmount(
                            payment.getTotalAmount()
                    );


            if (Boolean.TRUE.equals(
                    payment.getStatus()
            )) {

                activePaymentAmount =
                        activePaymentAmount
                                .add(amount);

                activePaymentCount++;

            } else {

                cancelledPaymentAmount =
                        cancelledPaymentAmount
                                .add(amount);

                cancelledPaymentCount++;
            }
        }


        // =====================================================
        // MEMBERSHIP TOTAL
        // =====================================================

        BigDecimal totalAmount =
                normalizeAmount(
                        membership.getTotalAmount()
                );


        // =====================================================
        // PAID AMOUNT
        // =====================================================

        /*
         * Only ACTIVE payments are considered paid.
         *
         * Cancelled payments are excluded.
         */

        BigDecimal paidAmount =
                activePaymentAmount
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // =====================================================
        // BALANCE
        // =====================================================

        BigDecimal balanceAmount =
                totalAmount
                        .subtract(paidAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // Prevent negative balance
        if (balanceAmount.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            balanceAmount =
                    BigDecimal.ZERO.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }


        // =====================================================
        // CREATE RESPONSE
        // =====================================================

        MembershipPaymentSummaryResponse response =
                new MembershipPaymentSummaryResponse();


        response.setMemberMembershipId(
                membership.getMemberMembershipId()
        );


        response.setMembershipNumber(
                membership.getMembershipNumber()
        );


        response.setTotalAmount(
                totalAmount
        );


        response.setPaidAmount(
                paidAmount
        );


        response.setBalanceAmount(
                balanceAmount
        );


        response.setActivePaymentAmount(
                activePaymentAmount
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );


        response.setCancelledPaymentAmount(
                cancelledPaymentAmount
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );


        response.setActivePaymentCount(
                activePaymentCount
        );


        response.setCancelledPaymentCount(
                cancelledPaymentCount
        );


        return response;
    }


    // =====================================================
    // APPLY PAYMENT TO BILLING CYCLE
    // =====================================================

    private void applyPaymentToBillingCycle(
            MembershipBillingCycle cycle,
            BigDecimal amount) {

        BigDecimal currentPaid =
                normalizeAmount(
                        cycle.getPaidAmount()
                );

        BigDecimal total =
                normalizeAmount(
                        cycle.getTotalAmount()
                );

        BigDecimal newPaid =
                currentPaid
                        .add(amount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        if (newPaid.compareTo(total) > 0) {

            throw new BadRequestException(
                    "Payment exceeds billing cycle balance."
            );
        }

        BigDecimal newBalance =
                total
                        .subtract(newPaid)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        cycle.setPaidAmount(
                newPaid
        );

        cycle.setBalanceAmount(
                newBalance
        );


        if (newBalance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PAID
            );

        } else {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PARTIALLY_PAID
            );
        }
    }


    // =====================================================
    // REVERSE BILLING CYCLE PAYMENT
    // =====================================================

    private void reverseBillingCyclePayment(
            MembershipBillingCycle cycle,
            BigDecimal amount) {

        BigDecimal currentPaid =
                normalizeAmount(
                        cycle.getPaidAmount()
                );

        BigDecimal total =
                normalizeAmount(
                        cycle.getTotalAmount()
                );

        BigDecimal newPaid =
                currentPaid
                        .subtract(amount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        if (newPaid.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            newPaid =
                    BigDecimal.ZERO;
        }

        BigDecimal newBalance =
                total
                        .subtract(newPaid)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        cycle.setPaidAmount(
                newPaid
        );

        cycle.setBalanceAmount(
                newBalance
        );


        if (newPaid.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.ACTIVE
            );

        } else if (newBalance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PAID
            );

        } else {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PARTIALLY_PAID
            );
        }
    }


    // =====================================================
    // APPLY PAYMENT TO RENEWAL
    // =====================================================

    private void applyPaymentToRenewal(
            MembershipRenewal renewal,
            BigDecimal amount) {

        BigDecimal paid =
                normalizeAmount(
                        renewal.getPaidAmount()
                );

        BigDecimal total =
                normalizeAmount(
                        renewal.getTotalAmount()
                );

        BigDecimal newPaid =
                paid.add(amount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        if (newPaid.compareTo(total) > 0) {

            throw new BadRequestException(
                    "Payment exceeds renewal balance."
            );
        }

        renewal.setPaidAmount(
                newPaid
        );

        renewal.setBalanceAmount(
                total.subtract(newPaid)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );
    }


    // =====================================================
    // REVERSE RENEWAL PAYMENT
    // =====================================================

    private void reverseRenewalPayment(
            MembershipRenewal renewal,
            BigDecimal amount) {

        BigDecimal paid =
                normalizeAmount(
                        renewal.getPaidAmount()
                );

        BigDecimal newPaid =
                paid.subtract(amount);

        if (newPaid.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            newPaid =
                    BigDecimal.ZERO;
        }

        renewal.setPaidAmount(
                newPaid
        );

        renewal.setBalanceAmount(
                normalizeAmount(
                        renewal.getTotalAmount()
                )
                        .subtract(newPaid)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );
    }

    // =====================================================
// REVERSE ORIGINAL MEMBERSHIP PAYMENT
// =====================================================

    private void reverseOriginalMembershipPayment(
            MemberMembership membership,
            BigDecimal amount) {

        BigDecimal currentPaid =
                normalizeAmount(
                        membership.getPaidAmount()
                );

        BigDecimal total =
                normalizeAmount(
                        membership.getTotalAmount()
                );


        BigDecimal newPaid =
                currentPaid
                        .subtract(amount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        if (newPaid.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            newPaid =
                    BigDecimal.ZERO;
        }


        BigDecimal newBalance =
                total
                        .subtract(newPaid)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        membership.setPaidAmount(
                newPaid
        );

        membership.setBalanceAmount(
                newBalance
        );


        /*
         * Do not change membershipState here.
         *
         * Payment cancellation affects financial status,
         * not the membership's ACTIVE/FROZEN/EXPIRED state.
         */
    }


    // =====================================================
    // VALIDATE BILLING CYCLE
    // =====================================================

    private void validateBillingCycle(
            Integer tenantId,
            MemberMembership membership,
            MembershipBillingCycle cycle) {

        if (cycle.getTenant() == null
                || cycle.getTenant().getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Billing cycle does not belong to this tenant."
            );
        }

        if (cycle.getMemberMembership() == null
                || cycle.getMemberMembership()
                .getMemberMembershipId()
                != membership.getMemberMembershipId()) {

            throw new BadRequestException(
                    "Billing cycle does not belong to this membership."
            );
        }

        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled billing cycle cannot receive payments."
            );
        }

        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.EXPIRED) {

            throw new BadRequestException(
                    "Expired billing cycle cannot receive payments."
            );
        }

        if (cycle.getBalanceAmount() == null
                || cycle.getBalanceAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Billing cycle has no outstanding balance."
            );
        }
    }


    // =====================================================
    // VALIDATE RENEWAL
    // =====================================================

    private void validateRenewal(
            Integer tenantId,
            MemberMembership membership,
            MembershipRenewal renewal) {

        if (renewal.getTenant() == null
                || renewal.getTenant().getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Membership renewal does not belong to this tenant."
            );
        }

        if (renewal.getMemberMembership() == null
                || renewal.getMemberMembership()
                .getMemberMembershipId()
                != membership.getMemberMembershipId()) {

            throw new BadRequestException(
                    "Membership renewal does not belong to this membership."
            );
        }

        if (!Boolean.TRUE.equals(
                renewal.getStatus()
        )) {

            throw new BadRequestException(
                    "Inactive membership renewal cannot receive payments."
            );
        }

        if (renewal.getBalanceAmount() == null
                || renewal.getBalanceAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Membership renewal has no outstanding balance."
            );
        }
    }


    // =====================================================
    // VALIDATE PAYMENT AMOUNT
    // =====================================================

    private void validatePaymentAgainstBalance(
            BigDecimal paymentAmount,
            BigDecimal balance,
            String target) {

        BigDecimal currentBalance =
                normalizeAmount(balance);

        if (currentBalance.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "There is no outstanding balance for this "
                            + target
                            + "."
            );
        }

        if (paymentAmount.compareTo(
                currentBalance
        ) > 0) {

            throw new BadRequestException(
                    "Payment amount exceeds the "
                            + target
                            + " outstanding balance."
            );
        }
    }


    // =====================================================
    // LOAD PAYMENT
    // =====================================================

    private MembershipPayment getPayment(
            Integer tenantId,
            Integer paymentId) {

        MembershipPayment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Payment not found."
                                )
                        );

        if (payment.getTenant() == null
                || payment.getTenant()
                .getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Payment does not belong to this tenant."
            );
        }

        return payment;
    }


    // =====================================================
    // TENANT VALIDATION
    // =====================================================

    private void validateMembershipTenant(
            Integer tenantId,
            MemberMembership membership) {

        if (membership.getTenant() == null) {

            throw new BadRequestException(
                    "Membership tenant information is missing."
            );
        }

        if (membership.getTenant()
                .getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // PAYMENT NUMBER
    // =====================================================

    private String generatePaymentNumber() {

        return "PAY-"
                + System.currentTimeMillis();
    }


    // =====================================================
    // AMOUNT
    // =====================================================

    private BigDecimal normalizeAmount(
            BigDecimal amount) {

        if (amount == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // TENANT ID
    // =====================================================

    private void validateTenantId(
            Integer tenantId) {

        if (tenantId == null
                || tenantId <= 0) {

            throw new BadRequestException(
                    "Valid tenant ID is required."
            );
        }
    }
}