package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "program_branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pb_program_branch",
                        columnNames = {
                                "program_id",
                                "branch_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_pb_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_pb_program",
                        columnList = "program_id"
                ),
                @Index(
                        name = "idx_pb_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_pb_status",
                        columnList = "status"
                )
        }
)
public class ProgramBranch {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "program_branch_id"
    )
    private Integer programBranchId;


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
    // PROGRAM
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "program_id",
            nullable = false
    )
    private Program program;


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
    // PROGRAM FEE
    // =====================================================

    @Column(
            name = "fee",
            precision = 15,
            scale = 2,
            nullable = false
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
    // CONSTRUCTOR
    // =====================================================

    public ProgramBranch() {
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

        if (fee == null) {
            fee = BigDecimal.ZERO;
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

    public Integer getProgramBranchId() {
        return programBranchId;
    }

    public void setProgramBranchId(
            Integer programBranchId) {

        this.programBranchId =
                programBranchId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public Program getProgram() {
        return program;
    }

    public void setProgram(
            Program program) {

        this.program =
                program;
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