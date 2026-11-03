package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_plan_branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mpb_plan_branch",
                        columnNames = {
                                "membership_plan_id",
                                "branch_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_mpb_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_mpb_plan",
                        columnList = "membership_plan_id"
                ),
                @Index(
                        name = "idx_mpb_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_mpb_status",
                        columnList = "status"
                )
        }
)
public class MembershipPlanBranch {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_plan_branch_id"
    )
    private Integer membershipPlanBranchId;


    // =====================================================
    // TENANT
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // =====================================================
    // MEMBERSHIP PLAN
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "membership_plan_id",
            nullable = false
    )
    private MembershipPlan membershipPlan;


    // =====================================================
    // BRANCH
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // =====================================================
    // PRICE
    // =====================================================

    @Column(
            name = "price",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal price =
            BigDecimal.ZERO;


    // =====================================================
    // JOINING FEE
    // =====================================================

    @Column(
            name = "joining_fee",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal joiningFee =
            BigDecimal.ZERO;


    // =====================================================
    // RENEWAL FEE
    // =====================================================

    @Column(
            name = "renewal_fee",
            precision = 15,
            scale = 2,
            nullable = false
    )
    private BigDecimal renewalFee =
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
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanBranch() {
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

        if (price == null) {
            price = BigDecimal.ZERO;
        }

        if (joiningFee == null) {
            joiningFee = BigDecimal.ZERO;
        }

        if (renewalFee == null) {
            renewalFee = BigDecimal.ZERO;
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

    public Integer getMembershipPlanBranchId() {
        return membershipPlanBranchId;
    }

    public void setMembershipPlanBranchId(
            Integer membershipPlanBranchId) {

        this.membershipPlanBranchId =
                membershipPlanBranchId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(
            MembershipPlan membershipPlan) {

        this.membershipPlan =
                membershipPlan;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(
            Branch branch) {

        this.branch =
                branch;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price) {

        this.price =
                price;
    }


    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(
            BigDecimal joiningFee) {

        this.joiningFee =
                joiningFee;
    }


    public BigDecimal getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(
            BigDecimal renewalFee) {

        this.renewalFee =
                renewalFee;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(
            Boolean status) {

        this.status =
                status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}