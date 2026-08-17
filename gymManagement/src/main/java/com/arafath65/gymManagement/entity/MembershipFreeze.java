package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_freezes",
        indexes = {
                @Index(
                        name = "idx_membership_freeze_membership",
                        columnList = "member_membership_id"
                ),
                @Index(
                        name = "idx_membership_freeze_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_membership_freeze_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_membership_freeze_start_date",
                        columnList = "freeze_start_date"
                )
        }
)
public class MembershipFreeze {

    // ID

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_freeze_id"
    )
    private Integer membershipFreezeId;


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


    // FREEZE DATES

    @Column(
            name = "freeze_start_date",
            nullable = false
    )
    private LocalDate freezeStartDate;


    @Column(
            name = "freeze_end_date",
            nullable = false
    )
    private LocalDate freezeEndDate;


    // FREEZE DAYS

    @Column(
            name = "freeze_days",
            nullable = false
    )
    private Integer freezeDays;


    // FEE

    @Column(
            name = "freeze_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private java.math.BigDecimal freezeFee =
            java.math.BigDecimal.ZERO;


    // REASON

    @Column(
            name = "reason",
            length = 255
    )
    private String reason;


    // STATUS

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private FreezeStatus status =
            FreezeStatus.ACTIVE;


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

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // GETTERS / SETTERS

    public Integer getMembershipFreezeId() {
        return membershipFreezeId;
    }

    public void setMembershipFreezeId(
            Integer membershipFreezeId) {
        this.membershipFreezeId =
                membershipFreezeId;
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

    public LocalDate getFreezeStartDate() {
        return freezeStartDate;
    }

    public void setFreezeStartDate(
            LocalDate freezeStartDate) {
        this.freezeStartDate =
                freezeStartDate;
    }

    public LocalDate getFreezeEndDate() {
        return freezeEndDate;
    }

    public void setFreezeEndDate(
            LocalDate freezeEndDate) {
        this.freezeEndDate =
                freezeEndDate;
    }

    public Integer getFreezeDays() {
        return freezeDays;
    }

    public void setFreezeDays(
            Integer freezeDays) {
        this.freezeDays =
                freezeDays;
    }

    public java.math.BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(
            java.math.BigDecimal freezeFee) {
        this.freezeFee =
                freezeFee;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FreezeStatus getStatus() {
        return status;
    }

    public void setStatus(FreezeStatus status) {
        this.status = status;
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


    // ENUM

    public enum FreezeStatus {

        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}