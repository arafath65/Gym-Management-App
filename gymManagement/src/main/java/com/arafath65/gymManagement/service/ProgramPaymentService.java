package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.ProgramPaymentDetailRequest;
import com.arafath65.gymManagement.dto.request.ProgramPaymentRequest;
import com.arafath65.gymManagement.entity.ProgramEnrollment;
import com.arafath65.gymManagement.entity.ProgramPayment;
import com.arafath65.gymManagement.entity.ProgramPaymentDetail;
import com.arafath65.gymManagement.repository.ProgramEnrollmentRepository;
import com.arafath65.gymManagement.repository.ProgramPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProgramPaymentService {

    private final ProgramPaymentRepository
            programPaymentRepository;

    private final ProgramEnrollmentRepository
            programEnrollmentRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramPaymentService(
            ProgramPaymentRepository programPaymentRepository,
            ProgramEnrollmentRepository programEnrollmentRepository) {

        this.programPaymentRepository =
                programPaymentRepository;

        this.programEnrollmentRepository =
                programEnrollmentRepository;
    }


    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    public ProgramPayment createPayment(
            Integer tenantId,
            ProgramPaymentRequest request) {

        // -------------------------------------------------
        // BASIC VALIDATION
        // -------------------------------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Payment request cannot be null."
            );
        }


        if (request.getProgramEnrollmentId() == null) {

            throw new IllegalArgumentException(
                    "Program enrollment is required."
            );
        }


        if (request.getTotalAmount() == null) {

            throw new IllegalArgumentException(
                    "Payment amount is required."
            );
        }


        BigDecimal paymentAmount =
                safe(
                        request.getTotalAmount()
                );


        if (paymentAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }


        // -------------------------------------------------
        // PAYMENT DETAILS
        // -------------------------------------------------

        List<ProgramPaymentDetailRequest>
                detailRequests =
                request.getPaymentDetails();


        if (detailRequests == null
                || detailRequests.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one payment method is required."
            );
        }


        // -------------------------------------------------
        // ENROLLMENT
        // -------------------------------------------------

        ProgramEnrollment enrollment =
                programEnrollmentRepository
                        .findById(
                                request.getProgramEnrollmentId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Program enrollment not found."
                                )
                        );


        // -------------------------------------------------
        // TENANT VALIDATION
        // -------------------------------------------------

        if (enrollment.getTenant() == null
                || enrollment.getTenant().getTenantId() != tenantId) {

            throw new IllegalArgumentException(
                    "Program enrollment does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // ENROLLMENT STATE
        // -------------------------------------------------

        if (enrollment.getEnrollmentState()
                == ProgramEnrollment.EnrollmentState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled program enrollment cannot receive payments."
            );
        }


        // -------------------------------------------------
        // CURRENT BALANCE
        // -------------------------------------------------

        BigDecimal currentBalance =
                safe(
                        enrollment.getBalanceAmount()
                );


        if (currentBalance.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "This program enrollment is already fully paid."
            );
        }


        // -------------------------------------------------
        // OVERPAYMENT VALIDATION
        // -------------------------------------------------

        if (paymentAmount.compareTo(
                currentBalance
        ) > 0) {

            throw new IllegalArgumentException(
                    "Payment amount cannot be greater than the current balance."
            );
        }


        // -------------------------------------------------
        // DETAIL TOTAL
        // -------------------------------------------------

        BigDecimal detailTotal =
                BigDecimal.ZERO;


        for (
                ProgramPaymentDetailRequest detailRequest
                : detailRequests
        ) {

            if (detailRequest == null) {

                throw new IllegalArgumentException(
                        "Invalid payment detail."
                );
            }


            if (detailRequest.getPaymentMethod()
                    == null) {

                throw new IllegalArgumentException(
                        "Payment method is required."
                );
            }


            BigDecimal detailAmount =
                    safe(
                            detailRequest.getAmount()
                    );


            if (detailAmount.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new IllegalArgumentException(
                        "Payment detail amount must be greater than zero."
                );
            }


            detailTotal =
                    detailTotal.add(
                            detailAmount
                    );
        }


        // -------------------------------------------------
        // DETAIL TOTAL MUST EQUAL PAYMENT TOTAL
        // -------------------------------------------------

        if (detailTotal.compareTo(
                paymentAmount
        ) != 0) {

            throw new IllegalArgumentException(
                    "Payment detail total must equal the payment amount."
            );
        }


        // -------------------------------------------------
        // PAYMENT DATE
        // -------------------------------------------------

        LocalDate paymentDate =
                request.getPaymentDate();


        if (paymentDate == null) {

            paymentDate =
                    LocalDate.now();
        }


        // -------------------------------------------------
        // CREATE PAYMENT MASTER
        // -------------------------------------------------

        ProgramPayment payment =
                new ProgramPayment();


        payment.setTenant(
                enrollment.getTenant()
        );


        payment.setBranch(
                enrollment.getBranch()
        );


        payment.setMember(
                enrollment.getMember()
        );


        payment.setProgramEnrollment(
                enrollment
        );


        payment.setPaymentDate(
                paymentDate
        );


        payment.setTotalAmount(
                paymentAmount
        );


        payment.setReferenceNumber(
                request.getReferenceNumber()
        );


        payment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // TEMP PAYMENT NUMBER
        // -------------------------------------------------

        payment.setPaymentNumber(
                "TEMP-" + System.nanoTime()
        );


        // -------------------------------------------------
        // SAVE MASTER
        // -------------------------------------------------

        ProgramPayment savedPayment =
                programPaymentRepository.save(
                        payment
                );


        // -------------------------------------------------
        // GENERATE PAYMENT NUMBER
        // -------------------------------------------------

        String paymentNumber =
                String.format(
                        "PAY-%06d",
                        savedPayment.getProgramPaymentId()
                );


        savedPayment.setPaymentNumber(
                paymentNumber
        );


        // -------------------------------------------------
        // PAYMENT DETAILS
        // -------------------------------------------------

        List<ProgramPaymentDetail>
                details =
                new ArrayList<>();


        for (
                ProgramPaymentDetailRequest detailRequest
                : detailRequests
        ) {

            ProgramPaymentDetail detail =
                    new ProgramPaymentDetail();


            detail.setProgramPayment(
                    savedPayment
            );


            detail.setPaymentMethod(
                    detailRequest.getPaymentMethod()
            );


            detail.setAmount(
                    detailRequest.getAmount()
            );


            detail.setReferenceNumber(
                    detailRequest.getReferenceNumber()
            );


            details.add(
                    detail
            );
        }


        savedPayment.setPaymentDetails(
                details
        );


        // -------------------------------------------------
        // UPDATE ENROLLMENT TOTALS
        // -------------------------------------------------

        BigDecimal oldPaid =
                safe(
                        enrollment.getPaidAmount()
                );


        BigDecimal totalAmount =
                safe(
                        enrollment.getTotalAmount()
                );


        BigDecimal newPaid =
                oldPaid.add(
                        paymentAmount
                );


        if (newPaid.compareTo(
                totalAmount
        ) > 0) {

            throw new IllegalArgumentException(
                    "Payment would cause enrollment overpayment."
            );
        }


        BigDecimal newBalance =
                totalAmount.subtract(
                        newPaid
                );


        enrollment.setPaidAmount(
                newPaid
        );


        enrollment.setBalanceAmount(
                newBalance
        );


        // -------------------------------------------------
        // SAVE ENROLLMENT
        // -------------------------------------------------

        programEnrollmentRepository.save(
                enrollment
        );


        // -------------------------------------------------
        // PAYMENT COMPLETES FINANCIAL BALANCE
        // -------------------------------------------------

        if (newBalance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            /*
             * IMPORTANT:
             *
             * We do NOT automatically change the
             * program enrollment to COMPLETED here.
             *
             * Payment completion and program completion
             * are different things.
             *
             * Example:
             *
             * 60-day program
             * Paid in full on day 20
             *
             * The program is still ACTIVE until
             * the 60-day program is actually completed.
             */

            enrollment.setPaidAmount(
                    newPaid
            );

            enrollment.setBalanceAmount(
                    BigDecimal.ZERO
            );

            programEnrollmentRepository.save(
                    enrollment
            );
        }


        // -------------------------------------------------
        // FINAL SAVE
        // -------------------------------------------------

        return programPaymentRepository.save(
                savedPayment
        );
    }


    // =====================================================
    // FIND PAYMENT
    // =====================================================

    @Transactional(readOnly = true)
    public ProgramPayment findById(
            Integer tenantId,
            Integer paymentId) {

        ProgramPayment payment =
                programPaymentRepository
                        .findById(
                                paymentId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Program payment not found."
                                )
                        );


        validateTenant(
                tenantId,
                payment
        );


        return payment;
    }


    // =====================================================
    // FIND BY ENROLLMENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramPayment> findByEnrollment(
            Integer tenantId,
            Integer enrollmentId) {

        return programPaymentRepository
                .findByTenant_TenantIdAndProgramEnrollment_ProgramEnrollmentIdOrderByPaymentDateDescProgramPaymentIdDesc(
                        tenantId,
                        enrollmentId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramPayment> findByMember(
            Integer tenantId,
            Integer memberId) {

        return programPaymentRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByPaymentDateDescProgramPaymentIdDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY PROGRAM
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramPayment> findByProgram(
            Integer tenantId,
            Integer programId) {

        return programPaymentRepository
                .findByTenant_TenantIdAndProgramEnrollment_Program_ProgramIdOrderByPaymentDateDescProgramPaymentIdDesc(
                        tenantId,
                        programId
                );
    }


    // =====================================================
    // FIND BY DATE RANGE
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramPayment> findByDateRange(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null
                || endDate == null) {

            throw new IllegalArgumentException(
                    "Start date and end date are required."
            );
        }


        if (endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }


        return programPaymentRepository
                .findByTenant_TenantIdAndPaymentDateBetweenOrderByPaymentDateDescProgramPaymentIdDesc(
                        tenantId,
                        startDate,
                        endDate
                );
    }


    // =====================================================
    // FIND BY PAYMENT NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public ProgramPayment findByPaymentNumber(
            Integer tenantId,
            String paymentNumber) {

        if (paymentNumber == null
                || paymentNumber.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Payment number is required."
            );
        }


        return programPaymentRepository
                .findByTenant_TenantIdAndPaymentNumber(
                        tenantId,
                        paymentNumber
                                .trim()
                                .toUpperCase()
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Program payment not found."
                        )
                );
    }


    // =====================================================
    // VALIDATE TENANT
    // =====================================================

    private void validateTenant(
            Integer tenantId,
            ProgramPayment payment) {

        if (payment.getTenant() == null
                || payment.getTenant().getTenantId() != tenantId) {

            throw new IllegalArgumentException(
                    "Program payment does not belong to this tenant."
            );

        }
    }


    // =====================================================
    // SAFE DECIMAL
    // =====================================================

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}