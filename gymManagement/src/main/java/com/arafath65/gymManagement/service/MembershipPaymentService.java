package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPayment;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipPaymentRepository;
import com.arafath65.gymManagement.repository.MembershipRenewalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipPaymentService {

    private final MembershipPaymentRepository membershipPaymentRepository;
    private final MemberMembershipRepository memberMembershipRepository;
    private final MembershipRenewalRepository membershipRenewalRepository;

    public MembershipPaymentService(
            MembershipPaymentRepository membershipPaymentRepository,
            MemberMembershipRepository memberMembershipRepository,
            MembershipRenewalRepository membershipRenewalRepository) {

        this.membershipPaymentRepository =
                membershipPaymentRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.membershipRenewalRepository =
                membershipRenewalRepository;
    }


    // SAVE PAYMENT

    public MembershipPayment save(
            MembershipPayment payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null."
            );
        }

        if (payment.getTenant() == null) {
            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }

        if (payment.getBranch() == null) {
            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }

        if (payment.getMemberMembership() == null) {
            throw new IllegalArgumentException(
                    "Membership is required."
            );
        }

        if (payment.getPaymentMethod() == null) {
            throw new IllegalArgumentException(
                    "Payment method is required."
            );
        }

        if (payment.getAmount() == null
                || payment.getAmount().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        // CHECK DUPLICATE PAYMENT NUMBER

        // TENANT / BRANCH

        int tenantId =
                payment.getTenant()
                        .getTenantId();

        int branchId =
                payment.getBranch()
                        .getBranchId();


        // LOAD MEMBERSHIP

        int membershipId =
                payment.getMemberMembership()
                        .getMemberMembershipId();

        MemberMembership membership =
                memberMembershipRepository.findById(
                        membershipId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership not found."
                        )
                );


        // VERIFY TENANT

        if (membership.getTenant()
                .getTenantId() != tenantId) {

            throw new IllegalArgumentException(
                    "Membership does not belong to this tenant."
            );
        }


        // VERIFY BRANCH

        if (membership.getBranch()
                .getBranchId() != branchId) {

            throw new IllegalArgumentException(
                    "Membership does not belong to this branch."
            );
        }


        // SET ACTUAL MEMBER

        payment.setMember(
                membership.getMember()
        );

        payment.setMemberMembership(
                membership
        );


        // PAYMENT DATE

        if (payment.getPaymentDate() == null) {

            payment.setPaymentDate(
                    LocalDateTime.now()
            );
        }


        // PAYMENT AMOUNT

        BigDecimal paymentAmount =
                safe(payment.getAmount());


        /*
         * PAYMENT FOR RENEWAL
         *
         * If membershipRenewal is present,
         * update only that renewal's paid/balance.
         */

        if (payment.getMembershipRenewal() != null) {

            MembershipRenewal renewal =
                    membershipRenewalRepository.findById(
                            payment.getMembershipRenewal()
                                    .getMembershipRenewalId()
                    ).orElseThrow(
                            () -> new IllegalArgumentException(
                                    "Membership renewal not found."
                            )
                    );


            // VERIFY RENEWAL BELONGS TO MEMBERSHIP

            if (renewal.getMemberMembership()
                    .getMemberMembershipId()
                    != membershipId) {

                throw new IllegalArgumentException(
                        "Renewal does not belong to this membership."
                );
            }


            // VERIFY TENANT

            if (renewal.getTenant()
                    .getTenantId() != tenantId) {

                throw new IllegalArgumentException(
                        "Renewal does not belong to this tenant."
                );
            }


            // VERIFY BRANCH

            if (renewal.getBranch()
                    .getBranchId() != branchId) {

                throw new IllegalArgumentException(
                        "Renewal does not belong to this branch."
                );
            }


            // CURRENT RENEWAL TOTALS

            BigDecimal renewalTotal =
                    safe(renewal.getTotalAmount());

            BigDecimal renewalPaid =
                    safe(renewal.getPaidAmount());

            BigDecimal renewalBalance =
                    safe(renewal.getBalanceAmount());


            /*
             * Balance should normally be:
             *
             * Total - Paid
             *
             * Recalculate it to keep data consistent.
             */

            renewalBalance =
                    renewalTotal.subtract(
                            renewalPaid
                    );

            if (renewalBalance.signum() < 0) {
                renewalBalance =
                        BigDecimal.ZERO;
            }


            // PREVENT OVERPAYMENT

            if (paymentAmount.compareTo(
                    renewalBalance
            ) > 0) {

                throw new IllegalArgumentException(
                        "Payment amount cannot be greater than renewal balance."
                );
            }


            // UPDATE RENEWAL

            BigDecimal newPaid =
                    renewalPaid.add(
                            paymentAmount
                    );

            BigDecimal newBalance =
                    renewalTotal.subtract(
                            newPaid
                    );

            if (newBalance.signum() < 0) {
                newBalance =
                        BigDecimal.ZERO;
            }

            renewal.setPaidAmount(
                    newPaid
            );

            renewal.setBalanceAmount(
                    newBalance
            );

            membershipRenewalRepository.save(
                    renewal
            );
        }


        /*
         * ORIGINAL MEMBERSHIP PAYMENT
         *
         * If membershipRenewal is null,
         * update the main membership totals.
         */

        else {

            BigDecimal totalAmount =
                    safe(membership.getTotalAmount());

            BigDecimal currentPaid =
                    safe(membership.getPaidAmount());

            BigDecimal currentBalance =
                    totalAmount.subtract(
                            currentPaid
                    );

            if (currentBalance.signum() < 0) {
                currentBalance =
                        BigDecimal.ZERO;
            }


            // PREVENT OVERPAYMENT

            if (paymentAmount.compareTo(
                    currentBalance
            ) > 0) {

                throw new IllegalArgumentException(
                        "Payment amount cannot be greater than membership balance."
                );
            }


            // UPDATE MEMBERSHIP

            BigDecimal newPaid =
                    currentPaid.add(
                            paymentAmount
                    );

            BigDecimal newBalance =
                    totalAmount.subtract(
                            newPaid
                    );

            if (newBalance.signum() < 0) {
                newBalance =
                        BigDecimal.ZERO;
            }

            membership.setPaidAmount(
                    newPaid
            );

            membership.setBalanceAmount(
                    newBalance
            );

            memberMembershipRepository.save(
                    membership
            );
        }


        // -------------------------------------------------
// TEMPORARY PAYMENT NUMBER
// -------------------------------------------------

        payment.setPaymentNumber(
                "TEMP-" + System.nanoTime()
        );


// -------------------------------------------------
// SAVE PAYMENT
// -------------------------------------------------

        MembershipPayment savedPayment =
                membershipPaymentRepository.save(
                        payment
                );


// -------------------------------------------------
// GENERATE FINAL PAYMENT NUMBER
// -------------------------------------------------

        String paymentNumber =
                String.format(
                        "PAY-%06d",
                        savedPayment.getMembershipPaymentId()
                );

        savedPayment.setPaymentNumber(
                paymentNumber
        );


// -------------------------------------------------
// SAVE FINAL PAYMENT NUMBER
// -------------------------------------------------

        return membershipPaymentRepository.save(
                savedPayment
        );

    }


    // FIND BY ID

    @Transactional(readOnly = true)
    public Optional<MembershipPayment> findById(
            int paymentId) {

        return membershipPaymentRepository.findById(
                paymentId
        );
    }


    // FIND BY MEMBERSHIP

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByMembership(
            int tenantId,
            int membershipId) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPaymentDateDesc(
                        tenantId,
                        membershipId
                );
    }


    // FIND BY MEMBER

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByMember(
            int tenantId,
            int memberId) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByPaymentDateDesc(
                        tenantId,
                        memberId
                );
    }


    // FIND BY BRANCH

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByBranch(
            int tenantId,
            int branchId) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByPaymentDateDesc(
                        tenantId,
                        branchId
                );
    }


    // FIND BY PAYMENT METHOD

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByPaymentMethod(
            int tenantId,
            MembershipPayment.PaymentMethod paymentMethod) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndPaymentMethodOrderByPaymentDateDesc(
                        tenantId,
                        paymentMethod
                );
    }


    // FIND BY DATE RANGE

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByDateRange(
            int tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndPaymentDateBetweenOrderByPaymentDateDesc(
                        tenantId,
                        startDate,
                        endDate
                );
    }


    // FIND BY BRANCH AND DATE RANGE

    @Transactional(readOnly = true)
    public List<MembershipPayment> findByBranchAndDateRange(
            int tenantId,
            int branchId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return membershipPaymentRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndPaymentDateBetweenOrderByPaymentDateDesc(
                        tenantId,
                        branchId,
                        startDate,
                        endDate
                );
    }


    // FIND BY PAYMENT NUMBER

    @Transactional(readOnly = true)
    public Optional<MembershipPayment> findByPaymentNumber(
            int tenantId,
            String paymentNumber) {

        if (paymentNumber == null
                || paymentNumber.trim().isEmpty()) {

            return Optional.empty();
        }

        return membershipPaymentRepository
                .findByTenant_TenantIdAndPaymentNumber(
                        tenantId,
                        paymentNumber
                                .trim()
                                .toUpperCase()
                );
    }


    // DELETE PAYMENT

    public void delete(
            int paymentId) {

        MembershipPayment payment =
                membershipPaymentRepository.findById(
                        paymentId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Payment not found."
                        )
                );


        MemberMembership membership =
                memberMembershipRepository.findById(
                        payment.getMemberMembership()
                                .getMemberMembershipId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership not found."
                        )
                );


        BigDecimal paymentAmount =
                safe(payment.getAmount());


        /*
         * RENEWAL PAYMENT
         */

        if (payment.getMembershipRenewal() != null) {

            MembershipRenewal renewal =
                    membershipRenewalRepository.findById(
                            payment.getMembershipRenewal()
                                    .getMembershipRenewalId()
                    ).orElseThrow(
                            () -> new IllegalArgumentException(
                                    "Membership renewal not found."
                            )
                    );

            BigDecimal currentPaid =
                    safe(renewal.getPaidAmount());

            BigDecimal newPaid =
                    currentPaid.subtract(
                            paymentAmount
                    );

            if (newPaid.signum() < 0) {
                newPaid =
                        BigDecimal.ZERO;
            }

            BigDecimal totalAmount =
                    safe(renewal.getTotalAmount());

            BigDecimal newBalance =
                    totalAmount.subtract(
                            newPaid
                    );

            if (newBalance.signum() < 0) {
                newBalance =
                        BigDecimal.ZERO;
            }

            renewal.setPaidAmount(
                    newPaid
            );

            renewal.setBalanceAmount(
                    newBalance
            );

            membershipRenewalRepository.save(
                    renewal
            );
        }


        /*
         * ORIGINAL MEMBERSHIP PAYMENT
         */

        else {

            BigDecimal currentPaid =
                    safe(membership.getPaidAmount());

            BigDecimal newPaid =
                    currentPaid.subtract(
                            paymentAmount
                    );

            if (newPaid.signum() < 0) {
                newPaid =
                        BigDecimal.ZERO;
            }

            BigDecimal totalAmount =
                    safe(membership.getTotalAmount());

            BigDecimal newBalance =
                    totalAmount.subtract(
                            newPaid
                    );

            if (newBalance.signum() < 0) {
                newBalance =
                        BigDecimal.ZERO;
            }

            membership.setPaidAmount(
                    newPaid
            );

            membership.setBalanceAmount(
                    newBalance
            );

            memberMembershipRepository.save(
                    membership
            );
        }


        membershipPaymentRepository.delete(
                payment
        );
    }


    // SAFE BIG DECIMAL

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}