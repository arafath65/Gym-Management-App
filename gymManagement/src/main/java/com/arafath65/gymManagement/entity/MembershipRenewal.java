package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "membership_renewals",
        indexes = {

                @Index(
                        name = "idx_membership_renewal_tenant",
                        columnList = "tenant_id"
                ),

                @Index(
                        name = "idx_membership_renewal_branch",
                        columnList = "branch_id"
                ),

                @Index(
                        name = "idx_membership_renewal_membership",
                        columnList = "member_membership_id"
                ),

                @Index(
                        name = "idx_membership_renewal_date",
                        columnList = "renewal_date"
                ),

                @Index(
                        name = "idx_membership_renewal_status",
                        columnList = "status"
                )
        }
)
public class MembershipRenewal {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_renewal_id"
    )
    private Integer membershipRenewalId;


    // =====================================================
    // TENANT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // =====================================================
    // BRANCH
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // =====================================================
    // MEMBER MEMBERSHIP
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_membership_id",
            nullable = false
    )
    private MemberMembership memberMembership;

    // =====================================================
// MEMBER
// =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private Member member;


    // =====================================================
    // RENEWAL NUMBER
    // =====================================================

    @Column(
            name = "renewal_number",
            nullable = false,
            length = 50
    )
    private String renewalNumber;

    // =====================================================
// PAYMENTS
// =====================================================

    @OneToMany(
            mappedBy = "membershipRenewal",
            fetch = FetchType.LAZY
    )
    private List<MembershipPayment> payments =
            new ArrayList<>();


    // =====================================================
    // RENEWAL DATE
    // =====================================================

    @Column(
            name = "renewal_date",
            nullable = false
    )
    private LocalDate renewalDate;


    // =====================================================
// PREVIOUS MEMBERSHIP PERIOD
// =====================================================

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


// =====================================================
// NEW MEMBERSHIP PERIOD
// =====================================================

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


    // =====================================================
    // RENEWAL PERIOD
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "duration_type",
            nullable = false,
            length = 20
    )
    private MembershipPlan.DurationType durationType;


    @Column(
            name = "duration_value",
            nullable = false
    )
    private Integer durationValue;


    // =====================================================
    // PRICE
    // =====================================================

    @Column(
            name = "renewal_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal renewalFee =
            BigDecimal.ZERO;


    // =====================================================
    // DISCOUNT
    // =====================================================

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;


    // =====================================================
    // TOTAL
    // =====================================================

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount =
            BigDecimal.ZERO;


    // =====================================================
    // PAYMENT
    // =====================================================

    @Column(
            name = "paid_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal paidAmount =
            BigDecimal.ZERO;


    @Column(
            name = "balance_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal balanceAmount =
            BigDecimal.ZERO;


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
    // AUDIT
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
    // LIFECYCLE
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

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


    public MemberMembership getMemberMembership() {
        return memberMembership;
    }

    public void setMemberMembership(
            MemberMembership memberMembership) {

        this.memberMembership =
                memberMembership;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(
            Member member) {

        this.member = member;
    }

    public List<MembershipPayment> getPayments() {
        return payments;
    }

    public void setPayments(
            List<MembershipPayment> payments) {

        this.payments = payments;
    }


    public String getRenewalNumber() {
        return renewalNumber;
    }

    public void setRenewalNumber(
            String renewalNumber) {

        this.renewalNumber =
                renewalNumber;
    }


    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDate renewalDate) {

        this.renewalDate =
                renewalDate;
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


    public MembershipPlan.DurationType
    getDurationType() {

        return durationType;
    }

    public void setDurationType(
            MembershipPlan.DurationType durationType) {

        this.durationType =
                durationType;
    }


    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(
            Integer durationValue) {

        this.durationValue =
                durationValue;
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