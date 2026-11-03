
package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_change_settlements")
public class MembershipChangeSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer membershipChangeSettlementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "old_membership_id", nullable = false)
    private MemberMembership oldMembership;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_membership_id", nullable = false)
    private MemberMembership newMembership;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_type", nullable = false, length = 30)
    private SettlementType settlementType;

    @Column(name = "settlement_amount", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal settlementAmount = BigDecimal.ZERO;

    @Column(name = "settlement_reason", length = 1000)
    private String settlementReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum SettlementType {
        NONE,
        COLLECT_REMAINING,
        WAIVE_BALANCE,
        CARRY_FORWARD,
        REFUND,
        MANUAL
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (settlementAmount == null) {
            settlementAmount = BigDecimal.ZERO;
        }
    }

    public Integer getMembershipChangeSettlementId() {
        return membershipChangeSettlementId;
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

    public MemberMembership getOldMembership() {
        return oldMembership;
    }

    public void setOldMembership(MemberMembership oldMembership) {
        this.oldMembership = oldMembership;
    }

    public MemberMembership getNewMembership() {
        return newMembership;
    }

    public void setNewMembership(MemberMembership newMembership) {
        this.newMembership = newMembership;
    }

    public SettlementType getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getSettlementReason() {
        return settlementReason;
    }

    public void setSettlementReason(String settlementReason) {
        this.settlementReason = settlementReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
