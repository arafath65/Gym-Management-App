package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "membership_plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_membership_plan_code",
                        columnNames = {
                                "tenant_id",
                                "plan_code"
                        }
                )
        }
)
public class MembershipPlan {

    // ID

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_plan_id"
    )
    private Integer membershipPlanId;


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
            name = "branch_id"
    )
    private Branch branch;


    // PLAN

    @Column(
            name = "plan_code",
            nullable = false,
            length = 50
    )
    private String planCode;


    @Column(
            name = "plan_name",
            nullable = false,
            length = 150
    )
    private String planName;


    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;


    // DURATION

    @Enumerated(EnumType.STRING)
    @Column(
            name = "duration_type",
            nullable = false,
            length = 20
    )
    private DurationType durationType =
            DurationType.MONTHS;


    @Column(
            name = "duration_value"
    )
    private Integer durationValue;


    // PRICE

    @Column(
            name = "price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price =
            BigDecimal.ZERO;


    @Column(
            name = "joining_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal joiningFee =
            BigDecimal.ZERO;


    @Column(
            name = "renewal_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal renewalFee =
            BigDecimal.ZERO;


    // BILLING

    @Enumerated(EnumType.STRING)
    @Column(
            name = "billing_type",
            nullable = false,
            length = 20
    )
    private BillingType billingType =
            BillingType.ONE_TIME;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "billing_frequency",
            length = 20
    )
    private BillingFrequency billingFrequency;


    @Column(
            name = "billing_frequency_value"
    )
    private Integer billingFrequencyValue;


    // FREEZE

    @Column(
            name = "freeze_allowed",
            nullable = false
    )
    private Boolean freezeAllowed = true;


    @Column(
            name = "max_freeze_days"
    )
    private Integer maxFreezeDays;


    @Column(
            name = "freeze_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal freezeFee =
            BigDecimal.ZERO;


    // ACCESS

    @Enumerated(EnumType.STRING)
    @Column(
            name = "access_type",
            nullable = false,
            length = 20
    )
    private AccessType accessType =
            AccessType.FULL;


    // STATUS

    @Column(
            name = "status",
            nullable = false
    )
    private Boolean status = true;


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

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(
            Integer membershipPlanId) {
        this.membershipPlanId =
                membershipPlanId;
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

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(
            String planCode) {
        this.planCode =
                planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName) {
        this.planName =
                planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description =
                description;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(
            DurationType durationType) {
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

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(
            BillingType billingType) {
        this.billingType =
                billingType;
    }

    public BillingFrequency getBillingFrequency() {
        return billingFrequency;
    }

    public void setBillingFrequency(
            BillingFrequency billingFrequency) {
        this.billingFrequency =
                billingFrequency;
    }

    public Integer getBillingFrequencyValue() {
        return billingFrequencyValue;
    }

    public void setBillingFrequencyValue(
            Integer billingFrequencyValue) {
        this.billingFrequencyValue =
                billingFrequencyValue;
    }

    public Boolean getFreezeAllowed() {
        return freezeAllowed;
    }

    public void setFreezeAllowed(
            Boolean freezeAllowed) {
        this.freezeAllowed =
                freezeAllowed;
    }

    public Integer getMaxFreezeDays() {
        return maxFreezeDays;
    }

    public void setMaxFreezeDays(
            Integer maxFreezeDays) {
        this.maxFreezeDays =
                maxFreezeDays;
    }

    public BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(
            BigDecimal freezeFee) {
        this.freezeFee =
                freezeFee;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(
            AccessType accessType) {
        this.accessType =
                accessType;
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


    // ENUMS

    public enum DurationType {
        DAYS,
        MONTHS,
        YEARS,
        UNLIMITED
    }

    public enum BillingType {
        ONE_TIME,
        RECURRING,
        INSTALLMENT
    }

    public enum BillingFrequency {
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        CUSTOM
    }

    public enum AccessType {
        FULL,
        OFF_PEAK,
        CUSTOM
    }
}