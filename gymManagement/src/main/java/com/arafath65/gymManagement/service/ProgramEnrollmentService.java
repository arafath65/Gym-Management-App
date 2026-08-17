package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.ProgramEnrollment;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MemberRepository;
import com.arafath65.gymManagement.repository.ProgramEnrollmentRepository;
import com.arafath65.gymManagement.repository.ProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgramEnrollmentService {

    private final ProgramEnrollmentRepository
            programEnrollmentRepository;

    private final ProgramRepository
            programRepository;

    private final MemberRepository
            memberRepository;

    private final BranchRepository
            branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramEnrollmentService(
            ProgramEnrollmentRepository programEnrollmentRepository,
            ProgramRepository programRepository,
            MemberRepository memberRepository,
            BranchRepository branchRepository) {

        this.programEnrollmentRepository =
                programEnrollmentRepository;

        this.programRepository =
                programRepository;

        this.memberRepository =
                memberRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE ENROLLMENT
    // =====================================================

    public ProgramEnrollment create(
            ProgramEnrollment enrollment) {

        if (enrollment == null) {

            throw new IllegalArgumentException(
                    "Program enrollment cannot be null."
            );
        }


        // -------------------------------------------------
        // REQUIRED RELATIONSHIPS
        // -------------------------------------------------

        if (enrollment.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }


        if (enrollment.getBranch() == null) {

            throw new IllegalArgumentException(
                    "Branch is required."
            );
        }


        if (enrollment.getMember() == null) {

            throw new IllegalArgumentException(
                    "Member is required."
            );
        }


        if (enrollment.getProgram() == null) {

            throw new IllegalArgumentException(
                    "Program is required."
            );
        }


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        Integer tenantId =
                enrollment.getTenant()
                        .getTenantId();


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch =
                branchRepository.findById(
                        enrollment.getBranch()
                                .getBranchId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Branch not found."
                        )
                );


        if (branch.getTenant() == null
                || branch.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Branch does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                branch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Branch is inactive."
            );
        }


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        Member member =
                memberRepository.findById(
                        enrollment.getMember()
                                .getMemberId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Member not found."
                        )
                );


        if (member.getTenant() == null
                || member.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Member does not belong to this tenant."
            );
        }


        if (member.getBranch() == null
                || member.getBranch()
                .getBranchId()
                != branch.getBranchId()) {

            throw new IllegalArgumentException(
                    "Member does not belong to the selected branch."
            );
        }


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        Program program =
                programRepository.findById(
                        enrollment.getProgram()
                                .getProgramId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Program not found."
                        )
                );


        if (program.getTenant() == null
                || program.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Program does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                program.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Program is inactive."
            );
        }


        // -------------------------------------------------
        // PROGRAM BRANCH RULE
        // -------------------------------------------------

        if (program.getBranch() != null
                && program.getBranch()
                .getBranchId()
                != branch.getBranchId()) {

            throw new IllegalArgumentException(
                    "Program is not available for this branch."
            );
        }


        // -------------------------------------------------
        // SET VERIFIED RELATIONSHIPS
        // -------------------------------------------------

        enrollment.setBranch(
                branch
        );

        enrollment.setMember(
                member
        );

        enrollment.setProgram(
                program
        );


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        LocalDate startDate =
                enrollment.getStartDate();


        if (startDate == null) {

            startDate =
                    LocalDate.now();
        }


        enrollment.setStartDate(
                startDate
        );


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        Integer durationDays =
                program.getDurationDays();


        if (durationDays == null
                || durationDays <= 0) {

            throw new IllegalArgumentException(
                    "Program has an invalid duration."
            );
        }


        /*
         * Example:
         *
         * Start = 16 Aug
         * Duration = 60 days
         *
         * Day 1 = 16 Aug
         * Day 60 = 14 Oct
         *
         * Therefore end date is:
         *
         * startDate + durationDays - 1
         */

        LocalDate endDate =
                startDate.plusDays(
                        durationDays - 1L
                );


        enrollment.setEndDate(
                endDate
        );


        // -------------------------------------------------
        // PROGRAM FEE SNAPSHOT
        // -------------------------------------------------

        BigDecimal programFee =
                safe(
                        program.getFee()
                );


        enrollment.setProgramFee(
                programFee
        );


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        BigDecimal discount =
                safe(
                        enrollment.getDiscountAmount()
                );


        if (discount.compareTo(
                programFee
        ) > 0) {

            throw new IllegalArgumentException(
                    "Discount cannot be greater than program fee."
            );
        }


        enrollment.setDiscountAmount(
                discount
        );


        // -------------------------------------------------
        // TOTAL
        // -------------------------------------------------

        BigDecimal totalAmount =
                programFee.subtract(
                        discount
                );


        enrollment.setTotalAmount(
                totalAmount
        );


        // -------------------------------------------------
        // PAYMENT TYPE
        // -------------------------------------------------

        if (enrollment.getPaymentType() == null) {

            throw new IllegalArgumentException(
                    "Payment type is required."
            );
        }


        // -------------------------------------------------
        // INITIAL PAYMENT
        // -------------------------------------------------

        BigDecimal initialPayment =
                safe(
                        enrollment.getPaidAmount()
                );


        if (initialPayment.compareTo(
                totalAmount
        ) > 0) {

            throw new IllegalArgumentException(
                    "Initial payment cannot be greater than total amount."
            );
        }


        // -------------------------------------------------
        // FULL PAYMENT RULE
        // -------------------------------------------------

        if (enrollment.getPaymentType()
                == ProgramEnrollment.PaymentType.FULL) {

            if (initialPayment.compareTo(
                    totalAmount
            ) != 0) {

                throw new IllegalArgumentException(
                        "Full payment must equal the total program amount."
                );
            }
        }


        // -------------------------------------------------
        // INSTALLMENT RULE
        // -------------------------------------------------

        if (enrollment.getPaymentType()
                == ProgramEnrollment.PaymentType.INSTALLMENT) {

            if (initialPayment.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new IllegalArgumentException(
                        "Initial installment payment must be greater than zero."
                );
            }
        }


        enrollment.setPaidAmount(
                initialPayment
        );


        enrollment.setBalanceAmount(
                totalAmount.subtract(
                        initialPayment
                )
        );


        // -------------------------------------------------
        // STATE
        // -------------------------------------------------

        enrollment.setEnrollmentState(
                ProgramEnrollment.EnrollmentState.ACTIVE
        );


        enrollment.setStatus(
                true
        );


        // -------------------------------------------------
        // TEMPORARY ENROLLMENT NUMBER
        // -------------------------------------------------

        enrollment.setEnrollmentNumber(
                "TEMP-" + System.nanoTime()
        );


        // -------------------------------------------------
        // FIRST SAVE
        // -------------------------------------------------

        ProgramEnrollment saved =
                programEnrollmentRepository.save(
                        enrollment
                );


        // -------------------------------------------------
        // FINAL ENROLLMENT NUMBER
        // -------------------------------------------------

        String enrollmentNumber =
                String.format(
                        "ENR-%06d",
                        saved.getProgramEnrollmentId()
                );


        saved.setEnrollmentNumber(
                enrollmentNumber
        );


        // -------------------------------------------------
        // FINAL SAVE
        // -------------------------------------------------

        return programEnrollmentRepository.save(
                saved
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<ProgramEnrollment> findById(
            Integer enrollmentId) {

        return programEnrollmentRepository.findById(
                enrollmentId
        );
    }


    // =====================================================
    // FIND ALL TENANT ENROLLMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findByTenant(
            Integer tenantId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdOrderByStartDateDesc(
                        tenantId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findByMember(
            Integer tenantId,
            Integer memberId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdAndMember_MemberIdOrderByStartDateDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY PROGRAM
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findByProgram(
            Integer tenantId,
            Integer programId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdAndProgram_ProgramIdOrderByStartDateDesc(
                        tenantId,
                        programId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findByBranch(
            Integer tenantId,
            Integer branchId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByStartDateDesc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND MEMBER ACTIVE PROGRAMS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findActiveByMember(
            Integer tenantId,
            Integer memberId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdAndMember_MemberIdAndEnrollmentStateOrderByEndDateAsc(
                        tenantId,
                        memberId,
                        ProgramEnrollment.EnrollmentState.ACTIVE
                );
    }


    // =====================================================
    // FIND ACTIVE PROGRAM ENROLLMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findActiveByProgram(
            Integer tenantId,
            Integer programId) {

        return programEnrollmentRepository
                .findByTenant_TenantIdAndProgram_ProgramIdAndEnrollmentStateOrderByEndDateAsc(
                        tenantId,
                        programId,
                        ProgramEnrollment.EnrollmentState.ACTIVE
                );
    }


    // =====================================================
    // FIND BY ENROLLMENT NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<ProgramEnrollment>
    findByEnrollmentNumber(
            Integer tenantId,
            String enrollmentNumber) {

        if (enrollmentNumber == null
                || enrollmentNumber.trim().isEmpty()) {

            return Optional.empty();
        }


        return programEnrollmentRepository
                .findByTenant_TenantIdAndEnrollmentNumber(
                        tenantId,
                        enrollmentNumber
                                .trim()
                                .toUpperCase()
                );
    }


    // =====================================================
    // UPDATE PAYMENT TOTALS
    // =====================================================

    public ProgramEnrollment updatePaymentTotals(
            Integer enrollmentId,
            BigDecimal newPaidAmount) {

        ProgramEnrollment enrollment =
                programEnrollmentRepository
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Program enrollment not found."
                                )
                        );


        BigDecimal total =
                safe(
                        enrollment.getTotalAmount()
                );


        BigDecimal paid =
                safe(
                        newPaidAmount
                );


        if (paid.signum() < 0) {

            throw new IllegalArgumentException(
                    "Paid amount cannot be negative."
            );
        }


        if (paid.compareTo(
                total
        ) > 0) {

            throw new IllegalArgumentException(
                    "Paid amount cannot be greater than total amount."
            );
        }


        enrollment.setPaidAmount(
                paid
        );


        enrollment.setBalanceAmount(
                total.subtract(
                        paid
                )
        );


        if (enrollment.getBalanceAmount()
                .compareTo(BigDecimal.ZERO) == 0) {

            enrollment.setEnrollmentState(
                    ProgramEnrollment.EnrollmentState.COMPLETED
            );
        }


        return programEnrollmentRepository.save(
                enrollment
        );
    }


    // =====================================================
    // COMPLETE ENROLLMENT
    // =====================================================

    public ProgramEnrollment complete(
            Integer enrollmentId) {

        ProgramEnrollment enrollment =
                programEnrollmentRepository
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Program enrollment not found."
                                )
                        );


        enrollment.setEnrollmentState(
                ProgramEnrollment.EnrollmentState.COMPLETED
        );


        return programEnrollmentRepository.save(
                enrollment
        );
    }


    // =====================================================
    // CANCEL ENROLLMENT
    // =====================================================

    public ProgramEnrollment cancel(
            Integer enrollmentId) {

        ProgramEnrollment enrollment =
                programEnrollmentRepository
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Program enrollment not found."
                                )
                        );


        if (enrollment.getEnrollmentState()
                == ProgramEnrollment.EnrollmentState.CANCELLED) {

            throw new IllegalArgumentException(
                    "Program enrollment is already cancelled."
            );
        }


        enrollment.setEnrollmentState(
                ProgramEnrollment.EnrollmentState.CANCELLED
        );


        return programEnrollmentRepository.save(
                enrollment
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Integer enrollmentId) {

        if (!programEnrollmentRepository
                .existsById(
                        enrollmentId
                )) {

            throw new IllegalArgumentException(
                    "Program enrollment not found."
            );
        }


        programEnrollmentRepository.deleteById(
                enrollmentId
        );
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