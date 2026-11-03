package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "benefit_branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bb_benefit_branch",
                        columnNames = {
                                "benefit_id",
                                "branch_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_bb_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_bb_benefit",
                        columnList = "benefit_id"
                ),
                @Index(
                        name = "idx_bb_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_bb_status",
                        columnList = "status"
                )
        }
)
public class BenefitBranch {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "benefit_branch_id"
    )
    private Integer benefitBranchId;


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
    // BENEFIT
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "benefit_id",
            nullable = false
    )
    private Benefit benefit;


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
    // BRANCH-SPECIFIC FEE
    // =====================================================

    /*
     * NULL = use Benefit.defaultFee
     *
     * Value = branch-specific fee override
     */

    @Column(
            name = "fee",
            precision = 12,
            scale = 2
    )
    private BigDecimal fee;


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

    public BenefitBranch() {
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

    public Integer getBenefitBranchId() {
        return benefitBranchId;
    }

    public void setBenefitBranchId(
            Integer benefitBranchId) {

        this.benefitBranchId =
                benefitBranchId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(
            Benefit benefit) {

        this.benefit =
                benefit;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(
            Branch branch) {

        this.branch =
                branch;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
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