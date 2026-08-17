package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_memberships",
        indexes = {
                @Index(
                        name = "idx_member_membership_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_member_membership_plan",
                        columnList = "membership_plan_id"
                ),
                @Index(
                        name = "idx_member_membership_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_member_membership_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_member_membership_end_date",
                        columnList = "end_date"
                )
        }
)
public class MemberMembership {

    // ID

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "member_membership_id"
    )
    private Integer memberMembershipId;


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


    // MEMBERSHIP PLAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_plan_id",
            nullable = false
    )
    private MembershipPlan membershipPlan;


    // MEMBERSHIP NUMBER

    @Column(
            name = "membership_number",
            nullable = false,
            length = 50
    )
    private String membershipNumber;


    // DATES

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;


    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;


    // PRICE SNAPSHOT

    @Column(
            name = "plan_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal planPrice = BigDecimal.ZERO;


    @Column(
            name = "joining_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal joiningFee = BigDecimal.ZERO;


    @Column(
            name = "renewal_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal renewalFee = BigDecimal.ZERO;


    // DISCOUNT

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discountAmount = BigDecimal.ZERO;


    // TOTAL

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;


    // PAYMENT TOTALS

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


    // STATUS

    @Column(
            name = "status",
            nullable = false
    )
    private Boolean status = true;


    // FREEZE

    @Column(
            name = "total_freeze_days",
            nullable = false
    )
    private Integer totalFreezeDays = 0;


    // RENEWAL

    @Column(
            name = "renewal_count",
            nullable = false
    )
    private Integer renewalCount = 0;


    // NOTES

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "membership_state",
            nullable = false,
            length = 20
    )
    private MembershipState membershipState =
            MembershipState.ACTIVE;


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

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // GETTERS / SETTERS

    public Integer getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(
            Integer memberMembershipId) {
        this.memberMembershipId =
                memberMembershipId;
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

    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(
            MembershipPlan membershipPlan) {
        this.membershipPlan =
                membershipPlan;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(
            String membershipNumber) {
        this.membershipNumber =
                membershipNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(
            BigDecimal planPrice) {
        this.planPrice = planPrice;
    }

    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(
            BigDecimal joiningFee) {
        this.joiningFee = joiningFee;
    }

    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {
        this.renewalFee = renewalFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(
            BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(
            BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {
        this.status = status;
    }

    public Integer getTotalFreezeDays() {
        return totalFreezeDays;
    }

    public void setTotalFreezeDays(
            Integer totalFreezeDays) {
        this.totalFreezeDays =
                totalFreezeDays;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(
            Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MembershipState getMembershipState() {
        return membershipState;
    }

    public void setMembershipState(
            MembershipState membershipState) {
        this.membershipState = membershipState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum MembershipState {

        ACTIVE,
        FROZEN,
        EXPIRED,
        CANCELLED
    }
}