package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "program_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_enrollment_number",
                        columnNames = {
                                "tenant_id",
                                "enrollment_number"
                        }
                )
        }
)
public class ProgramEnrollment {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_enrollment_id")
    private Integer programEnrollmentId;


    // =====================================================
    // TENANT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // =====================================================
    // BRANCH
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // =====================================================
    // MEMBER
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private Member member;


    // =====================================================
    // PROGRAM
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "program_id",
            nullable = false
    )
    private Program program;


    // =====================================================
    // ENROLLMENT NUMBER
    // =====================================================

    @Column(
            name = "enrollment_number",
            nullable = false,
            length = 50
    )
    private String enrollmentNumber;


    // =====================================================
    // START DATE
    // =====================================================

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;


    // =====================================================
    // END DATE
    // =====================================================

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;


    // =====================================================
    // PROGRAM FEE SNAPSHOT
    // =====================================================

    @Column(
            name = "program_fee",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal programFee;


    // =====================================================
    // DISCOUNT
    // =====================================================

    @Column(
            name = "discount_amount",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;


    // =====================================================
    // TOTAL AMOUNT
    // =====================================================

    @Column(
            name = "total_amount",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal totalAmount;


    // =====================================================
    // PAID AMOUNT
    // =====================================================

    @Column(
            name = "paid_amount",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal paidAmount =
            BigDecimal.ZERO;


    // =====================================================
    // BALANCE AMOUNT
    // =====================================================

    @Column(
            name = "balance_amount",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal balanceAmount;


    // =====================================================
    // PAYMENT TYPE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_type",
            nullable = false,
            length = 20
    )
    private PaymentType paymentType;


    // =====================================================
    // STATE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "enrollment_state",
            nullable = false,
            length = 20
    )
    private EnrollmentState enrollmentState;


    // =====================================================
    // STATUS
    // =====================================================

    @Column(
            name = "status",
            nullable = false
    )
    private Boolean status = true;


    // =====================================================
    // NOTES
    // =====================================================

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;


    // =====================================================
    // TIMESTAMPS
    // =====================================================

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;


    // =====================================================
    // PAYMENT TYPE ENUM
    // =====================================================

    public enum PaymentType {

        FULL,
        INSTALLMENT
    }


    // =====================================================
    // ENROLLMENT STATE ENUM
    // =====================================================

    public enum EnrollmentState {

        ACTIVE,
        COMPLETED,
        CANCELLED
    }


    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = true;
        }

        if (paymentType == null) {
            paymentType =
                    PaymentType.FULL;
        }

        if (enrollmentState == null) {
            enrollmentState =
                    EnrollmentState.ACTIVE;
        }

        if (discountAmount == null) {
            discountAmount =
                    BigDecimal.ZERO;
        }

        if (paidAmount == null) {
            paidAmount =
                    BigDecimal.ZERO;
        }
    }


    // =====================================================
    // PRE UPDATE
    // =====================================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramEnrollmentId() {
        return programEnrollmentId;
    }

    public void setProgramEnrollmentId(
            Integer programEnrollmentId) {

        this.programEnrollmentId =
                programEnrollmentId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(
            Branch branch) {

        this.branch =
                branch;
    }


    public Member getMember() {
        return member;
    }

    public void setMember(
            Member member) {

        this.member =
                member;
    }


    public Program getProgram() {
        return program;
    }

    public void setProgram(
            Program program) {

        this.program =
                program;
    }


    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(
            String enrollmentNumber) {

        this.enrollmentNumber =
                enrollmentNumber;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {

        this.endDate =
                endDate;
    }


    public BigDecimal getProgramFee() {
        return programFee;
    }

    public void setProgramFee(
            BigDecimal programFee) {

        this.programFee =
                programFee;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {

        this.paidAmount =
                paidAmount;
    }


    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {

        this.balanceAmount =
                balanceAmount;
    }


    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(
            PaymentType paymentType) {

        this.paymentType =
                paymentType;
    }


    public EnrollmentState getEnrollmentState() {
        return enrollmentState;
    }

    public void setEnrollmentState(
            EnrollmentState enrollmentState) {

        this.enrollmentState =
                enrollmentState;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}