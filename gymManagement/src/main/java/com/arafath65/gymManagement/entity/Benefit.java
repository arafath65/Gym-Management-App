package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "benefits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_benefit_code",
                        columnNames = {
                                "tenant_id",
                                "benefit_code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_benefit_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_benefit_status",
                        columnList = "status"
                )
        }
)
public class Benefit {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "benefit_id"
    )
    private Integer benefitId;


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
    // BENEFIT CODE
    // =====================================================

    @Column(
            name = "benefit_code",
            nullable = false,
            length = 50
    )
    private String benefitCode;


    // =====================================================
    // BENEFIT NAME
    // =====================================================

    @Column(
            name = "benefit_name",
            nullable = false,
            length = 150
    )
    private String benefitName;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;


    // =====================================================
    // BILLING TYPE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "billing_type",
            nullable = false,
            length = 20
    )
    private BillingType billingType =
            BillingType.ONE_TIME;


    // =====================================================
    // DEFAULT FEE
    // =====================================================

    /*
     * Default fee used when a branch does not have
     * a specific fee configured.
     */

    @Column(
            name = "default_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal defaultFee =
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
    // BRANCH ASSIGNMENTS
    // =====================================================

    @OneToMany(
            mappedBy = "benefit",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BenefitBranch>
            branchAssignments =
            new ArrayList<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Benefit() {
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

        if (billingType == null) {
            billingType =
                    BillingType.ONE_TIME;
        }

        if (defaultFee == null) {
            defaultFee =
                    BigDecimal.ZERO;
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

    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(
            Integer benefitId) {

        this.benefitId =
                benefitId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(
            String benefitCode) {

        this.benefitCode =
                benefitCode;
    }


    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(
            String benefitName) {

        this.benefitName =
                benefitName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(
            BillingType billingType) {

        this.billingType =
                billingType;
    }


    public BigDecimal getDefaultFee() {
        return defaultFee;
    }

    public void setDefaultFee(
            BigDecimal defaultFee) {

        this.defaultFee =
                defaultFee;
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
    // BRANCH ASSIGNMENTS
    // =====================================================

    public List<BenefitBranch>
    getBranchAssignments() {

        return branchAssignments;
    }


    public void setBranchAssignments(
            List<BenefitBranch>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    public void addBranchAssignment(
            BenefitBranch assignment) {

        branchAssignments.add(
                assignment
        );

        assignment.setBenefit(
                this
        );
    }


    public void removeBranchAssignment(
            BenefitBranch assignment) {

        branchAssignments.remove(
                assignment
        );

        assignment.setBenefit(
                null
        );
    }


    // =====================================================
    // ENUM
    // =====================================================

    public enum BillingType {

        ONE_TIME,

        MONTHLY,

        MEMBERSHIP_PERIOD
    }
}