package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "membership_billing_cycles",
        indexes = {

                @Index(
                        name = "idx_billing_cycle_tenant",
                        columnList = "tenant_id"
                ),

                @Index(
                        name = "idx_billing_cycle_branch",
                        columnList = "branch_id"
                ),

                @Index(
                        name = "idx_billing_cycle_membership",
                        columnList = "member_membership_id"
                ),

                @Index(
                        name = "idx_billing_cycle_period",
                        columnList = "period_start,period_end"
                ),

                @Index(
                        name = "idx_billing_cycle_status",
                        columnList = "status"
                )
        }
)
public class MembershipBillingCycle {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_billing_cycle_id"
    )
    private Integer membershipBillingCycleId;


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
    // CYCLE NUMBER
    // =====================================================

    @Column(
            name = "cycle_number",
            nullable = false,
            length = 50
    )
    private String cycleNumber;

    // =====================================================
// MEMBERSHIP PAYMENTS
// =====================================================

    @OneToMany(
            mappedBy = "membershipBillingCycle",
            fetch = FetchType.LAZY
    )
    private List<MembershipPayment> membershipPayments =
            new ArrayList<>();


    // =====================================================
    // BILLING MODE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "billing_mode",
            nullable = false,
            length = 30
    )
    private MembershipPlan.AccessBillingMode billingMode;


    // =====================================================
    // PERIOD
    // =====================================================

    @Column(
            name = "period_start",
            nullable = false
    )
    private LocalDate periodStart;


    @Column(
            name = "period_end",
            nullable = false
    )
    private LocalDate periodEnd;


    // =====================================================
    // PRICE SNAPSHOT
    // =====================================================

    @Column(
            name = "base_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal baseAmount =
            BigDecimal.ZERO;


    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;


    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount =
            BigDecimal.ZERO;


    // =====================================================
    // PAYMENT TOTALS
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
    // ACTIVATION
    // =====================================================

    @Column(
            name = "activated_date"
    )
    private LocalDate activatedDate;


    // =====================================================
    // STATUS
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private CycleStatus status =
            CycleStatus.NOT_ACTIVATED;


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

    public Integer getMembershipBillingCycleId() {
        return membershipBillingCycleId;
    }

    public void setMembershipBillingCycleId(
            Integer membershipBillingCycleId) {

        this.membershipBillingCycleId =
                membershipBillingCycleId;
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


    public MemberMembership getMemberMembership() {
        return memberMembership;
    }

    public void setMemberMembership(
            MemberMembership memberMembership) {

        this.memberMembership =
                memberMembership;
    }


    public String getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(
            String cycleNumber) {

        this.cycleNumber =
                cycleNumber;
    }

    public List<MembershipPayment> getMembershipPayments() {
        return membershipPayments;
    }

    public void setMembershipPayments(
            List<MembershipPayment> membershipPayments) {

        this.membershipPayments =
                membershipPayments;
    }


    public MembershipPlan.AccessBillingMode
    getBillingMode() {

        return billingMode;
    }

    public void setBillingMode(
            MembershipPlan.AccessBillingMode billingMode) {

        this.billingMode =
                billingMode;
    }


    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {

        this.periodStart =
                periodStart;
    }


    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {

        this.periodEnd =
                periodEnd;
    }


    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(
            BigDecimal baseAmount) {

        this.baseAmount =
                baseAmount;
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


    public LocalDate getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(
            LocalDate activatedDate) {

        this.activatedDate =
                activatedDate;
    }


    public CycleStatus getStatus() {
        return status;
    }

    public void setStatus(
            CycleStatus status) {

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

    // =====================================================
// PAYMENT HELPERS
// =====================================================

    public void addMembershipPayment(
            MembershipPayment payment) {

        membershipPayments.add(payment);

        payment.setMembershipBillingCycle(this);
    }

    public void removeMembershipPayment(
            MembershipPayment payment) {

        membershipPayments.remove(payment);

        payment.setMembershipBillingCycle(null);
    }


    // =====================================================
    // ENUM
    // =====================================================

    public enum CycleStatus {

        NOT_ACTIVATED,

        ACTIVE,

        PARTIALLY_PAID,

        PAID,

        EXPIRED,

        CANCELLED
    }
}