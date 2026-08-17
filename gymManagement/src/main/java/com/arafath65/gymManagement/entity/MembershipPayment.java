package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_payments",
        indexes = {
                @Index(
                        name = "idx_membership_payment_membership",
                        columnList = "member_membership_id"
                ),
                @Index(
                        name = "idx_membership_payment_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_membership_payment_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_membership_payment_date",
                        columnList = "payment_date"
                )
        }
)
public class MembershipPayment {

    // ID

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_payment_id"
    )
    private Integer membershipPaymentId;


    // TENANT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // BRANCH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // MEMBER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private Member member;


    // MEMBERSHIP

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_membership_id",
            nullable = false
    )
    private MemberMembership memberMembership;

    // RENEWAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_renewal_id"
    )
    private MembershipRenewal membershipRenewal;


    // PAYMENT NUMBER

    @Column(
            name = "payment_number",
            nullable = false,
            length = 50
    )
    private String paymentNumber;


    // PAYMENT

    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 20
    )
    private PaymentMethod paymentMethod;


    @Column(
            name = "payment_date",
            nullable = false
    )
    private LocalDateTime paymentDate;


    // REFERENCE

    @Column(
            name = "reference_number",
            length = 100
    )
    private String referenceNumber;


    // NOTES

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;


    // AUDIT

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


    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if (paymentDate == null) {
            paymentDate = now;
        }

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // GETTERS / SETTERS

    public Integer getMembershipPaymentId() {
        return membershipPaymentId;
    }

    public void setMembershipPaymentId(
            Integer membershipPaymentId) {
        this.membershipPaymentId =
                membershipPaymentId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public MemberMembership getMemberMembership() {
        return memberMembership;
    }

    public void setMemberMembership(
            MemberMembership memberMembership) {
        this.memberMembership =
                memberMembership;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(
            String paymentNumber) {
        this.paymentNumber =
                paymentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {
        this.paymentMethod =
                paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate) {
        this.paymentDate =
                paymentDate;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public MembershipRenewal getMembershipRenewal() {
        return membershipRenewal;
    }

    public void setMembershipRenewal(
            MembershipRenewal membershipRenewal) {
        this.membershipRenewal =
                membershipRenewal;
    }


    // ENUM

    public enum PaymentMethod {

        CASH,
        CARD,
        CHEQUE,
        ONLINE_TRANSFER
    }
}