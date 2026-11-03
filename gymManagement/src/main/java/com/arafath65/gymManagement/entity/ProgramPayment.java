package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "program_payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_payment_number",
                        columnNames = {
                                "tenant_id",
                                "payment_number"
                        }
                )
        }
)
public class ProgramPayment {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_payment_id")
    private Integer programPaymentId;


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
    // PROGRAM ENROLLMENT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "program_enrollment_id",
            nullable = false
    )
    private ProgramEnrollment programEnrollment;


    // =====================================================
    // PAYMENT NUMBER
    // =====================================================

    @Column(
            name = "payment_number",
            nullable = false,
            length = 50
    )
    private String paymentNumber;


    // =====================================================
    // PAYMENT DATE
    // =====================================================

    @Column(
            name = "payment_date",
            nullable = false
    )
    private LocalDate paymentDate;


    // =====================================================
    // TOTAL PAYMENT AMOUNT
    // =====================================================

    @Column(
            name = "total_amount",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal totalAmount;


    // =====================================================
    // REFERENCE NUMBER
    // =====================================================

    @Column(
            name = "reference_number",
            length = 100
    )
    private String referenceNumber;


    // =====================================================
    // NOTES
    // =====================================================

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;


    // =====================================================
    // PAYMENT DETAILS
    // =====================================================

    @OneToMany(
            mappedBy = "programPayment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProgramPaymentDetail> paymentDetails =
            new ArrayList<>();


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
    // PRE PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (paymentDate == null) {
            paymentDate = LocalDate.now();
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

    public Integer getProgramPaymentId() {
        return programPaymentId;
    }

    public void setProgramPaymentId(
            Integer programPaymentId) {

        this.programPaymentId =
                programPaymentId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant = tenant;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(
            Branch branch) {

        this.branch = branch;
    }


    public Member getMember() {
        return member;
    }

    public void setMember(
            Member member) {

        this.member = member;
    }


    public ProgramEnrollment getProgramEnrollment() {
        return programEnrollment;
    }

    public void setProgramEnrollment(
            ProgramEnrollment programEnrollment) {

        this.programEnrollment =
                programEnrollment;
    }


    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(
            String paymentNumber) {

        this.paymentNumber =
                paymentNumber;
    }


    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDate paymentDate) {

        this.paymentDate =
                paymentDate;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }


    public List<ProgramPaymentDetail> getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(
            List<ProgramPaymentDetail> paymentDetails) {

        this.paymentDetails =
                paymentDetails;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}