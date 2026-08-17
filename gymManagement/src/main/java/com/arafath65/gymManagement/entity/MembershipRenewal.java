package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_renewals",
        indexes = {
                @Index(
                        name = "idx_membership_renewal_membership",
                        columnList = "member_membership_id"
                ),
                @Index(
                        name = "idx_membership_renewal_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_membership_renewal_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_membership_renewal_date",
                        columnList = "renewal_date"
                )
        }
)
public class MembershipRenewal {

    // ID

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_renewal_id"
    )
    private Integer membershipRenewalId;


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


    // RENEWAL NUMBER

    @Column(
            name = "renewal_number",
            nullable = false
    )
    private Integer renewalNumber;


    // PREVIOUS DATES

    @Column(
            name = "previous_start_date",
            nullable = false
    )
    private LocalDate previousStartDate;


    @Column(
            name = "previous_end_date",
            nullable = false
    )
    private LocalDate previousEndDate;


    // NEW DATES

    @Column(
            name = "new_start_date",
            nullable = false
    )
    private LocalDate newStartDate;


    @Column(
            name = "new_end_date",
            nullable = false
    )
    private LocalDate newEndDate;


    // PRICE

    @Column(
            name = "renewal_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal renewalFee = BigDecimal.ZERO;


    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discountAmount = BigDecimal.ZERO;


    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;


    // PAYMENT

    @Column(
            name = "paid_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal paidAmount = BigDecimal.ZERO;


    @Column(
            name = "balance_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal balanceAmount = BigDecimal.ZERO;


    // RENEWAL DATE

    @Column(
            name = "renewal_date",
            nullable = false
    )
    private LocalDateTime renewalDate;


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


    // LIFECYCLE

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if (renewalDate == null) {
            renewalDate = now;
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

    public Integer getMembershipRenewalId() {
        return membershipRenewalId;
    }

    public void setMembershipRenewalId(
            Integer membershipRenewalId) {
        this.membershipRenewalId =
                membershipRenewalId;
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

    public Integer getRenewalNumber() {
        return renewalNumber;
    }

    public void setRenewalNumber(
            Integer renewalNumber) {
        this.renewalNumber =
                renewalNumber;
    }

    public LocalDate getPreviousStartDate() {
        return previousStartDate;
    }

    public void setPreviousStartDate(
            LocalDate previousStartDate) {
        this.previousStartDate =
                previousStartDate;
    }

    public LocalDate getPreviousEndDate() {
        return previousEndDate;
    }

    public void setPreviousEndDate(
            LocalDate previousEndDate) {
        this.previousEndDate =
                previousEndDate;
    }

    public LocalDate getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(
            LocalDate newStartDate) {
        this.newStartDate =
                newStartDate;
    }

    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(
            LocalDate newEndDate) {
        this.newEndDate =
                newEndDate;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {
        this.renewalFee =
                renewalFee;
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

    public LocalDateTime getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDateTime renewalDate) {
        this.renewalDate =
                renewalDate;
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
}