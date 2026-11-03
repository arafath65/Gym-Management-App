package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_plan_id"
    )
    private Integer membershipPlanId;


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
    // PLAN CODE
    // =====================================================

    @Column(
            name = "plan_code",
            nullable = false,
            length = 50
    )
    private String planCode;


    // =====================================================
    // PLAN NAME
    // =====================================================

    @Column(
            name = "plan_name",
            nullable = false,
            length = 150
    )
    private String planName;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;


    // =====================================================
    // DURATION
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "duration_type",
            nullable = false,
            length = 20
    )
    private DurationType durationType;


    @Column(
            name = "duration_value"
    )
    private Integer durationValue;


    // =====================================================
    // BILLING
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "billing_type",
            nullable = false,
            length = 20
    )
    private BillingType billingType;


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


    // =====================================================
    // ACCESS BILLING
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "access_billing_mode",
            nullable = false,
            length = 30
    )
    private AccessBillingMode accessBillingMode;


    // =====================================================
    // MONTHLY ACTIVATION
    // =====================================================

    @Column(
            name = "monthly_activation_required",
            nullable = false
    )
    private Boolean monthlyActivationRequired =
            false;


    // =====================================================
    // DAILY CHARGE
    // =====================================================

    @Column(
            name = "daily_charge_enabled",
            nullable = false
    )
    private Boolean dailyChargeEnabled =
            false;


    // =====================================================
    // FREEZE
    // =====================================================

    @Column(
            name = "freeze_allowed",
            nullable = false
    )
    private Boolean freezeAllowed =
            false;


    @Column(
            name = "max_freeze_days"
    )
    private Integer maxFreezeDays;


    @Column(
            name = "freeze_fee"
    )
    private java.math.BigDecimal freezeFee;


    // =====================================================
    // ACCESS TYPE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "access_type",
            nullable = false,
            length = 20
    )
    private AccessType accessType;


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
    // BRANCH ASSIGNMENTS
    // =====================================================

    @OneToMany(
            mappedBy = "membershipPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MembershipPlanBranch>
            branchAssignments =
            new ArrayList<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlan() {
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

        if (monthlyActivationRequired == null) {
            monthlyActivationRequired = false;
        }

        if (dailyChargeEnabled == null) {
            dailyChargeEnabled = false;
        }

        if (freezeAllowed == null) {
            freezeAllowed = false;
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


    public AccessBillingMode getAccessBillingMode() {
        return accessBillingMode;
    }

    public void setAccessBillingMode(
            AccessBillingMode accessBillingMode) {

        this.accessBillingMode =
                accessBillingMode;
    }


    public Boolean getMonthlyActivationRequired() {
        return monthlyActivationRequired;
    }

    public void setMonthlyActivationRequired(
            Boolean monthlyActivationRequired) {

        this.monthlyActivationRequired =
                monthlyActivationRequired;
    }


    public Boolean getDailyChargeEnabled() {
        return dailyChargeEnabled;
    }

    public void setDailyChargeEnabled(
            Boolean dailyChargeEnabled) {

        this.dailyChargeEnabled =
                dailyChargeEnabled;
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


    public java.math.BigDecimal getFreezeFee() {
        return freezeFee;
    }

    public void setFreezeFee(
            java.math.BigDecimal freezeFee) {

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


    public List<MembershipPlanBranch>
    getBranchAssignments() {

        return branchAssignments;
    }


    public void setBranchAssignments(
            List<MembershipPlanBranch>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    public void addBranchAssignment(
            MembershipPlanBranch assignment) {

        branchAssignments.add(
                assignment
        );

        assignment.setMembershipPlan(
                this
        );
    }


    public void removeBranchAssignment(
            MembershipPlanBranch assignment) {

        branchAssignments.remove(
                assignment
        );

        assignment.setMembershipPlan(
                null
        );
    }


    // =====================================================
    // ENUMS
    // =====================================================

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


    public enum AccessBillingMode {

        FIXED_TERM,

        MONTHLY_CONTRACT,

        MONTHLY_FLEX,

        DAILY
    }


    public enum AccessType {

        FULL,

        OFF_PEAK,

        CUSTOM
    }
}