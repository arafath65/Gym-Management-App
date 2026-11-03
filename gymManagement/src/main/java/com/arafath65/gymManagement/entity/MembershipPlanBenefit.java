package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_plan_benefits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plan_benefit",
                        columnNames = {
                                "membership_plan_id",
                                "benefit_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_plan_benefit_plan",
                        columnList = "membership_plan_id"
                ),
                @Index(
                        name = "idx_plan_benefit_benefit",
                        columnList = "benefit_id"
                ),
                @Index(
                        name = "idx_plan_benefit_status",
                        columnList = "status"
                )
        }
)
public class MembershipPlanBenefit {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_plan_benefit_id"
    )
    private Integer membershipPlanBenefitId;


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
    // MEMBERSHIP PLAN
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_plan_id",
            nullable = false
    )
    private MembershipPlan membershipPlan;


    // =====================================================
    // BENEFIT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "benefit_id",
            nullable = false
    )
    private Benefit benefit;


    // =====================================================
    // BENEFIT TYPE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "benefit_type",
            nullable = false,
            length = 20
    )
    private BenefitType benefitType =
            BenefitType.OPTIONAL;


    // =====================================================
    // PLAN-SPECIFIC FEE
    // =====================================================

    @Column(
            name = "fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal fee =
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

    public Integer getMembershipPlanBenefitId() {
        return membershipPlanBenefitId;
    }

    public void setMembershipPlanBenefitId(
            Integer membershipPlanBenefitId) {

        this.membershipPlanBenefitId =
                membershipPlanBenefitId;
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


    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(
            Benefit benefit) {

        this.benefit =
                benefit;
    }


    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(
            BenefitType benefitType) {

        this.benefitType =
                benefitType;
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


    // =====================================================
    // ENUM
    // =====================================================

    public enum BenefitType {

        INCLUDED,

        OPTIONAL
    }
}