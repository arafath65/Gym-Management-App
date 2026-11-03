package arafath65.minime.gymmanagementui.model;

import java.util.List;

public class ProgramResponse {

    // =====================================================
    // ID
    // =====================================================

    private Integer programId;


    // =====================================================
    // TENANT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // PROGRAM
    // =====================================================

    private String programCode;

    private String programName;

    private String description;


    // =====================================================
    // DURATION
    // =====================================================

    private Integer durationDays;


    // =====================================================
    // STATUS
    // =====================================================

    private Boolean status;


    // =====================================================
    // BRANCH ASSIGNMENTS
    // =====================================================

    private List<ProgramBranchResponse>
            branchAssignments;


    // =====================================================
    // AUDIT
    // =====================================================

    private String createdAt;

    private String updatedAt;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramResponse() {
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


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Integer tenantId) {

        this.tenantId =
                tenantId;
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


    public List<ProgramBranchResponse>
    getBranchAssignments() {

        return branchAssignments;
    }

    public void setBranchAssignments(
            List<ProgramBranchResponse>
                    branchAssignments) {

        this.branchAssignments =
                branchAssignments;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            String createdAt) {

        this.createdAt =
                createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            String updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}