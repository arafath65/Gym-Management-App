package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "programs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_tenant_code",
                        columnNames = {
                                "tenant_id",
                                "program_code"
                        }
                )
        }
)
public class Program {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "program_id")
    private Integer programId;


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
    // PROGRAM CODE
    // =====================================================

    @Column(
            name = "program_code",
            nullable = false,
            length = 50
    )
    private String programCode;


    // =====================================================
    // PROGRAM NAME
    // =====================================================

    @Column(
            name = "program_name",
            nullable = false,
            length = 150
    )
    private String programName;


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

    @Column(
            name = "duration_days",
            nullable = false
    )
    private Integer durationDays;


    // =====================================================
    // STATUS
    // =====================================================

    @Column(
            name = "status",
            nullable = false
    )
    private Boolean status = true;


    // =====================================================
    // TIMESTAMPS
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
            mappedBy = "program",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProgramBranch>
            branchAssignments =
            new ArrayList<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Program() {
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

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(
            Integer programId) {

        this.programId =
                programId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {

        this.tenant =
                tenant;
    }


    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(
            String programCode) {

        this.programCode =
                programCode;
    }


    public String getProgramName() {
        return programName;
    }

    public void setProgramName(
            String programName) {

        this.programName =
                programName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(
            Integer durationDays) {

        this.durationDays =
                durationDays;
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


    public List<ProgramBranch>
    getBranchAssignments() {

        return branchAssignments;
    }


    public void setBranchAssignments(
            List<ProgramBranch>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    public void addBranchAssignment(
            ProgramBranch assignment) {

        branchAssignments.add(
                assignment
        );

        assignment.setProgram(
                this
        );
    }


    public void removeBranchAssignment(
            ProgramBranch assignment) {

        branchAssignments.remove(
                assignment
        );

        assignment.setProgram(
                null
        );
    }
}