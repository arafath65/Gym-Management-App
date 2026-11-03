package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_membership_programs",
        indexes = {
                @Index(
                        name = "idx_mmp_membership",
                        columnList = "member_membership_id"
                ),
                @Index(
                        name = "idx_mmp_program",
                        columnList = "program_id"
                ),
                @Index(
                        name = "idx_mmp_branch",
                        columnList = "branch_id"
                ),
                @Index(
                        name = "idx_mmp_status",
                        columnList = "status"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mmp_membership_program",
                        columnNames = {
                                "member_membership_id",
                                "program_id"
                        }
                )
        }
)
public class MemberMembershipProgram {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_membership_program_id")
    private Integer memberMembershipProgramId;


    // =====================================================
    // TENANT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // =====================================================
    // BRANCH
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // =====================================================
    // MEMBER MEMBERSHIP
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_membership_id",
            nullable = false
    )
    private MemberMembership memberMembership;


    // =====================================================
    // PROGRAM
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "program_id",
            nullable = false
    )
    private Program program;


    // =====================================================
    // FEE SNAPSHOT
    // =====================================================

    @Column(
            name = "fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal fee = BigDecimal.ZERO;


    // =====================================================
    // DATES
    // =====================================================

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;


    @Column(name = "end_date")
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
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipProgram() {
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

    public Integer getMemberMembershipProgramId() {
        return memberMembershipProgramId;
    }

    public void setMemberMembershipProgramId(
            Integer memberMembershipProgramId) {

        this.memberMembershipProgramId =
                memberMembershipProgramId;
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


    public MemberMembership getMemberMembership() {
        return memberMembership;
    }

    public void setMemberMembership(
            MemberMembership memberMembership) {

        this.memberMembership =
                memberMembership;
    }


    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
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
}