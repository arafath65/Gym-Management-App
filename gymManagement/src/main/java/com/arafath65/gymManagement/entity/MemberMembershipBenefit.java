package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_membership_benefits",
        indexes = {
                @Index(
                        name = "idx_member_membership_benefit_membership",
                        columnList = "member_membership_id"
                ),
                @Index(
                        name = "idx_member_membership_benefit_benefit",
                        columnList = "benefit_id"
                ),
                @Index(
                        name = "idx_member_membership_benefit_status",
                        columnList = "status"
                )
        }
)
public class MemberMembershipBenefit {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "member_membership_benefit_id"
    )
    private Integer memberMembershipBenefitId;


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
    // BENEFIT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "benefit_id",
            nullable = false
    )
    private Benefit benefit;


    // =====================================================
    // SOURCE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "source_type",
            nullable = false,
            length = 20
    )
    private SourceType sourceType =
            SourceType.PURCHASED;


    // =====================================================
    // FEE SNAPSHOT
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
    // DATES
    // =====================================================

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;


    @Column(
            name = "end_date"
    )
    private LocalDate endDate;


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

    public Integer getMemberMembershipBenefitId() {
        return memberMembershipBenefitId;
    }

    public void setMemberMembershipBenefitId(
            Integer memberMembershipBenefitId) {

        this.memberMembershipBenefitId =
                memberMembershipBenefitId;
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


    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(
            Benefit benefit) {

        this.benefit =
                benefit;
    }


    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(
            SourceType sourceType) {

        this.sourceType =
                sourceType;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate =
                startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {

        this.endDate =
                endDate;
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


    // =====================================================
    // ENUM
    // =====================================================

    public enum SourceType {

        INCLUDED,

        PURCHASED,

        PROMOTIONAL
    }
}