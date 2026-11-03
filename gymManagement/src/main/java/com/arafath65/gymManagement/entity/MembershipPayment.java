package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "membership_payments",
        indexes = {

                @Index(
                        name = "idx_membership_payment_tenant",
                        columnList = "tenant_id"
                ),

                @Index(
                        name = "idx_membership_payment_branch",
                        columnList = "branch_id"
                ),

                @Index(
                        name = "idx_membership_payment_membership",
                        columnList = "member_membership_id"
                ),

                @Index(
                        name = "idx_membership_payment_date",
                        columnList = "payment_date"
                ),

                @Index(
                        name = "idx_membership_payment_status",
                        columnList = "status"
                )
        },
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "uk_membership_payment_number",
                        columnNames = {
                                "tenant_id",
                                "payment_number"
                        }
                )
        }
)
public class MembershipPayment {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_payment_id"
    )
    private Integer membershipPaymentId;


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
// MEMBERSHIP RENEWAL
// =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_renewal_id"
    )
    private MembershipRenewal membershipRenewal;

    // =====================================================
// MEMBERSHIP BILLING CYCLE
// =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_billing_cycle_id"
    )
    private MembershipBillingCycle membershipBillingCycle;


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
    // TOTAL AMOUNT
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
    // REFERENCE
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
    // STATUS
    // =====================================================

    @Column(
            name = "status",
            nullable = false
    )
    private Boolean status = true;


    // =====================================================
    // PAYMENT DETAILS
    // =====================================================

    @OneToMany(
            mappedBy = "membershipPayment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MembershipPaymentDetail>
            paymentDetails =
            new ArrayList<>();


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

    public MembershipRenewal getMembershipRenewal() {
        return membershipRenewal;
    }

    public void setMembershipRenewal(
            MembershipRenewal membershipRenewal) {

        this.membershipRenewal =
                membershipRenewal;
    }

    public MembershipBillingCycle getMembershipBillingCycle() {
        return membershipBillingCycle;
    }

    public void setMembershipBillingCycle(
            MembershipBillingCycle membershipBillingCycle) {

        this.membershipBillingCycle =
                membershipBillingCycle;
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


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status = status;
    }


    public List<MembershipPaymentDetail>
    getPaymentDetails() {

        return paymentDetails;
    }

    public void setPaymentDetails(
            List<MembershipPaymentDetail>
                    paymentDetails) {

        this.paymentDetails =
                paymentDetails;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    // =====================================================
    // HELPER
    // =====================================================

    public void addPaymentDetail(
            MembershipPaymentDetail detail) {

        paymentDetails.add(detail);

        detail.setMembershipPayment(this);
    }


    public void removePaymentDetail(
            MembershipPaymentDetail detail) {

        paymentDetails.remove(detail);

        detail.setMembershipPayment(null);
    }


    // =====================================================
    // ENUM
    // =====================================================

    public enum PaymentMethod {

        CASH,

        CARD,

        BANK_TRANSFER,

        CHEQUE
    }
}