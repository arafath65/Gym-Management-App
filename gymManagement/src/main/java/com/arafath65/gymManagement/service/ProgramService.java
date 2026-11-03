package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.ProgramBranch;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.ProgramBranchRepository;
import com.arafath65.gymManagement.repository.ProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgramService {

    private final ProgramRepository programRepository;

    private final ProgramBranchRepository
            programBranchRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramService(
            ProgramRepository programRepository,
            ProgramBranchRepository programBranchRepository,
            BranchRepository branchRepository) {

        this.programRepository =
                programRepository;

        this.programBranchRepository =
                programBranchRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE PROGRAM
    // =====================================================

    public Program create(
            Program program,
            String branchMode,
            BigDecimal defaultFee,
            List<Integer> branchIds,
            List<BigDecimal> branchFees) {

        if (program == null) {

            throw new IllegalArgumentException(
                    "Program cannot be null."
            );
        }


        if (program.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }


        Integer tenantId =
                program.getTenant()
                        .getTenantId();


        // -------------------------------------------------
        // CODE
        // -------------------------------------------------

        String programCode =
                program.getProgramCode();


        if (programCode == null
                || programCode.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Program code is required."
            );
        }


        programCode =
                programCode.trim()
                        .toUpperCase();


        program.setProgramCode(
                programCode
        );


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (program.getProgramName() == null
                || program.getProgramName()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Program name is required."
            );
        }


        program.setProgramName(
                program.getProgramName()
                        .trim()
        );


        // -------------------------------------------------
        // DUPLICATE CODE
        // -------------------------------------------------

        if (programRepository
                .existsByTenant_TenantIdAndProgramCode(
                        tenantId,
                        programCode
                )) {

            throw new IllegalArgumentException(
                    "Program code already exists."
            );
        }


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        if (program.getDurationDays() == null
                || program.getDurationDays() <= 0) {

            throw new IllegalArgumentException(
                    "Program duration must be greater than zero."
            );
        }


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        if (program.getStatus() == null) {

            program.setStatus(true);
        }


        // -------------------------------------------------
        // BRANCH MODE
        // -------------------------------------------------

        String normalizedBranchMode =
                normalizeBranchMode(
                        branchMode
                );


        validateBranchConfiguration(
                normalizedBranchMode,
                defaultFee,
                branchIds,
                branchFees
        );


        // -------------------------------------------------
        // SAVE PROGRAM
        // -------------------------------------------------

        Program savedProgram =
                programRepository.save(
                        program
                );


        // -------------------------------------------------
        // CREATE BRANCH ASSIGNMENTS
        // -------------------------------------------------

        createBranchAssignments(
                savedProgram,
                normalizedBranchMode,
                defaultFee,
                branchIds,
                branchFees
        );


        return savedProgram;
    }


    // =====================================================
// UPDATE PROGRAM
// =====================================================

    public Program update(
            Integer programId,
            Program updatedProgram,
            String branchMode,
            BigDecimal defaultFee,
            List<Integer> branchIds,
            List<BigDecimal> branchFees) {

        // -------------------------------------------------
        // FIND EXISTING PROGRAM
        // -------------------------------------------------

        Program existing =
                programRepository.findById(
                        programId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Program not found."
                        )
                );


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (existing.getTenant() == null
                || updatedProgram == null
                || updatedProgram.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }


        Integer tenantId =
                existing.getTenant()
                        .getTenantId();


        if (updatedProgram.getTenant()
                .getTenantId()
                != tenantId) {

            throw new IllegalArgumentException(
                    "Program does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // PROGRAM CODE
        // -------------------------------------------------

        String programCode =
                updatedProgram.getProgramCode();


        if (programCode == null
                || programCode.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Program code is required."
            );
        }


        programCode =
                programCode.trim()
                        .toUpperCase();


        // -------------------------------------------------
        // DUPLICATE CODE
        // -------------------------------------------------

        if (!programCode.equals(
                existing.getProgramCode()
        )) {

            if (programRepository
                    .existsByTenant_TenantIdAndProgramCode(
                            tenantId,
                            programCode
                    )) {

                throw new IllegalArgumentException(
                        "Program code already exists."
                );
            }
        }


        // -------------------------------------------------
        // PROGRAM NAME
        // -------------------------------------------------

        if (updatedProgram.getProgramName() == null
                || updatedProgram.getProgramName()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Program name is required."
            );
        }


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        if (updatedProgram.getDurationDays() == null
                || updatedProgram.getDurationDays() <= 0) {

            throw new IllegalArgumentException(
                    "Program duration must be greater than zero."
            );
        }


        // -------------------------------------------------
        // BRANCH MODE
        // -------------------------------------------------

        String normalizedBranchMode =
                normalizeBranchMode(
                        branchMode
                );


        validateBranchConfiguration(
                normalizedBranchMode,
                defaultFee,
                branchIds,
                branchFees
        );


        // =================================================
        // UPDATE PROGRAM FIELDS
        // =================================================

        existing.setProgramCode(
                programCode
        );


        existing.setProgramName(
                updatedProgram.getProgramName()
                        .trim()
        );


        existing.setDescription(
                updatedProgram.getDescription()
        );


        existing.setDurationDays(
                updatedProgram.getDurationDays()
        );


        if (updatedProgram.getStatus() != null) {

            existing.setStatus(
                    updatedProgram.getStatus()
            );
        }


        // =================================================
        // EXISTING BRANCH ASSIGNMENTS
        // =================================================

        /*
         * IMPORTANT
         *
         * We DO NOT:
         *
         * programBranchRepository.deleteAll(...)
         *
         * and we DO NOT:
         *
         * existing.getBranchAssignments().clear()
         *
         * and recreate everything.
         *
         * Instead we update the existing managed entities.
         */

        List<ProgramBranch> existingAssignments =
                existing.getBranchAssignments();


        // -------------------------------------------------
        // SAFETY
        // -------------------------------------------------

        if (existingAssignments == null) {

            existingAssignments =
                    new ArrayList<>();

            existing.setBranchAssignments(
                    existingAssignments
            );
        }


        // =================================================
        // ALL BRANCHES
        // =================================================

        if ("ALL_BRANCHES".equals(
                normalizedBranchMode
        )) {

            List<Branch> activeBranches =
                    branchRepository
                            .findByTenant_TenantIdAndStatusTrue(
                                    tenantId
                            );


            if (activeBranches.isEmpty()) {

                throw new IllegalArgumentException(
                        "No active branches are available."
                );
            }


            BigDecimal fee =
                    safe(defaultFee);


            // -------------------------------------------------
            // REMOVE BRANCHES NO LONGER ACTIVE
            // -------------------------------------------------

            existingAssignments.removeIf(
                    assignment -> {

                        if (assignment == null
                                || assignment.getBranch() == null) {

                            return true;
                        }


                        Integer existingBranchId =
                                assignment.getBranch()
                                        .getBranchId();


                        boolean stillActive =
                                activeBranches.stream()
                                        .anyMatch(
                                                branch ->
                                                        branch.getBranchId()
                                                                .equals(
                                                                        existingBranchId
                                                                )
                                        );


                        return !stillActive;
                    }
            );


            // -------------------------------------------------
            // UPDATE EXISTING + ADD MISSING
            // -------------------------------------------------

            for (Branch branch :
                    activeBranches) {

                ProgramBranch assignment =
                        existingAssignments
                                .stream()
                                .filter(
                                        item ->
                                                item != null
                                                        && item.getBranch() != null
                                                        && item.getBranch()
                                                        .getBranchId()
                                                        .equals(
                                                                branch.getBranchId()
                                                        )
                                )
                                .findFirst()
                                .orElse(null);


                if (assignment != null) {

                    // Existing assignment
                    assignment.setFee(
                            fee
                    );

                    assignment.setStatus(
                            true
                    );

                } else {

                    // New assignment
                    ProgramBranch newAssignment =
                            new ProgramBranch();


                    newAssignment.setTenant(
                            existing.getTenant()
                    );


                    newAssignment.setProgram(
                            existing
                    );


                    newAssignment.setBranch(
                            branch
                    );


                    newAssignment.setFee(
                            fee
                    );


                    newAssignment.setStatus(
                            true
                    );


                    existingAssignments.add(
                            newAssignment
                    );
                }
            }
        }


        // =================================================
        // SELECTED BRANCHES
        // =================================================

        else {

            if (branchIds == null
                    || branchIds.isEmpty()) {

                throw new IllegalArgumentException(
                        "At least one branch must be selected."
                );
            }


            // -------------------------------------------------
            // CHECK DUPLICATE INPUT BRANCH IDS
            // -------------------------------------------------

            java.util.Set<Integer>
                    uniqueBranchIds =
                    new java.util.HashSet<>(
                            branchIds
                    );


            if (uniqueBranchIds.size()
                    != branchIds.size()) {

                throw new IllegalArgumentException(
                        "A branch cannot be selected more than once."
                );
            }


            // -------------------------------------------------
            // REMOVE UNSELECTED BRANCHES
            // -------------------------------------------------

            existingAssignments.removeIf(
                    assignment -> {

                        if (assignment == null
                                || assignment.getBranch() == null) {

                            return true;
                        }


                        Integer existingBranchId =
                                assignment.getBranch()
                                        .getBranchId();


                        return !uniqueBranchIds.contains(
                                existingBranchId
                        );
                    }
            );


            // -------------------------------------------------
            // UPDATE EXISTING / ADD NEW
            // -------------------------------------------------

            for (int i = 0;
                 i < branchIds.size();
                 i++) {

                Integer branchId =
                        branchIds.get(i);


                if (branchId == null) {

                    throw new IllegalArgumentException(
                            "Branch ID cannot be null."
                    );
                }


                // -------------------------------------------------
                // FIND BRANCH
                // -------------------------------------------------

                Branch branch =
                        branchRepository
                                .findById(
                                        branchId
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "Branch not found: "
                                                                + branchId
                                                )
                                );


                // -------------------------------------------------
                // TENANT
                // -------------------------------------------------

                if (branch.getTenant() == null
                        || branch.getTenant()
                        .getTenantId()
                        != tenantId) {

                    throw new IllegalArgumentException(
                            "Branch does not belong to this tenant."
                    );
                }


                // -------------------------------------------------
                // STATUS
                // -------------------------------------------------

                if (!Boolean.TRUE.equals(
                        branch.getStatus()
                )) {

                    throw new IllegalArgumentException(
                            "Branch is inactive: "
                                    + branch.getBranchName()
                    );
                }


                // -------------------------------------------------
                // FEE
                // -------------------------------------------------

                BigDecimal fee =
                        defaultFee;


                if (branchFees != null
                        && i < branchFees.size()
                        && branchFees.get(i) != null) {

                    fee =
                            branchFees.get(i);
                }


                fee =
                        safe(fee);


                // -------------------------------------------------
                // FIND EXISTING ASSIGNMENT
                // -------------------------------------------------

                ProgramBranch assignment =
                        existingAssignments
                                .stream()
                                .filter(
                                        item ->
                                                item != null
                                                        && item.getBranch() != null
                                                        && item.getBranch()
                                                        .getBranchId()
                                                        .equals(
                                                                branchId
                                                        )
                                )
                                .findFirst()
                                .orElse(null);


                // -------------------------------------------------
                // UPDATE EXISTING
                // -------------------------------------------------

                if (assignment != null) {

                    assignment.setFee(
                            fee
                    );


                    assignment.setStatus(
                            true
                    );


                    assignment.setTenant(
                            existing.getTenant()
                    );


                    assignment.setProgram(
                            existing
                    );


                    assignment.setBranch(
                            branch
                    );


                }

                // -------------------------------------------------
                // ADD NEW
                // -------------------------------------------------

                else {

                    ProgramBranch newAssignment =
                            new ProgramBranch();


                    newAssignment.setTenant(
                            existing.getTenant()
                    );


                    newAssignment.setProgram(
                            existing
                    );


                    newAssignment.setBranch(
                            branch
                    );


                    newAssignment.setFee(
                            fee
                    );


                    newAssignment.setStatus(
                            true
                    );


                    existingAssignments.add(
                            newAssignment
                    );
                }
            }
        }


        // =================================================
        // SAVE
        // =================================================

        return programRepository.save(
                existing
        );
    }


    // =====================================================
    // CREATE BRANCH ASSIGNMENTS
    // =====================================================

    private void createBranchAssignments(
            Program program,
            String branchMode,
            BigDecimal defaultFee,
            List<Integer> branchIds,
            List<BigDecimal> branchFees) {

        Integer tenantId =
                program.getTenant()
                        .getTenantId();


        // =================================================
        // ALL BRANCHES
        // =================================================

        if ("ALL_BRANCHES".equals(
                branchMode
        )) {

            List<Branch> branches =
                    branchRepository
                            .findByTenant_TenantIdAndStatusTrue(
                                    tenantId
                            );


            if (branches.isEmpty()) {

                throw new IllegalArgumentException(
                        "No active branches are available."
                );
            }


            BigDecimal fee =
                    safe(defaultFee);


            for (Branch branch : branches) {

                ProgramBranch assignment =
                        new ProgramBranch();


                assignment.setTenant(
                        program.getTenant()
                );


                assignment.setProgram(
                        program
                );


                assignment.setBranch(
                        branch
                );


                assignment.setFee(
                        fee
                );


                assignment.setStatus(
                        true
                );


                program.getBranchAssignments()
                        .add(
                                assignment
                        );
            }


            return;
        }


        // =================================================
        // SELECTED BRANCHES
        // =================================================

        if (branchIds == null
                || branchIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one branch must be selected."
            );
        }


        for (int i = 0;
             i < branchIds.size();
             i++) {

            Integer branchId =
                    branchIds.get(i);


            if (branchId == null) {

                throw new IllegalArgumentException(
                        "Branch ID cannot be null."
                );
            }


            Branch branch =
                    branchRepository
                            .findById(
                                    branchId
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Branch not found: "
                                                            + branchId
                                            )
                            );


            // -------------------------------------------------
            // TENANT SECURITY
            // -------------------------------------------------

            if (branch.getTenant() == null
                    || branch.getTenant()
                    .getTenantId()
                    != tenantId) {

                throw new IllegalArgumentException(
                        "Branch does not belong to this tenant."
                );
            }


            // -------------------------------------------------
            // ACTIVE
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    branch.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "Branch is inactive: "
                                + branch.getBranchName()
                );
            }


            // -------------------------------------------------
            // FEE
            // -------------------------------------------------

            BigDecimal fee =
                    defaultFee;


            if (branchFees != null
                    && i < branchFees.size()
                    && branchFees.get(i) != null) {

                fee =
                        branchFees.get(i);
            }


            fee =
                    safe(fee);


            // -------------------------------------------------
            // DUPLICATE BRANCH
            // -------------------------------------------------

            boolean alreadyAdded =
                    program.getBranchAssignments()
                            .stream()
                            .anyMatch(
                                    assignment ->
                                            assignment
                                                    .getBranch()
                                                    .getBranchId()
                                                    .equals(
                                                            branchId
                                                    )
                            );


            if (alreadyAdded) {

                throw new IllegalArgumentException(
                        "Branch selected more than once: "
                                + branch.getBranchName()
                );
            }


            // -------------------------------------------------
            // CREATE ASSIGNMENT
            // -------------------------------------------------

            ProgramBranch assignment =
                    new ProgramBranch();


            assignment.setTenant(
                    program.getTenant()
            );


            assignment.setProgram(
                    program
            );


            assignment.setBranch(
                    branch
            );


            assignment.setFee(
                    fee
            );


            assignment.setStatus(
                    true
            );


            program.getBranchAssignments()
                    .add(
                            assignment
                    );
        }
    }


    // =====================================================
    // VALIDATE BRANCH CONFIGURATION
    // =====================================================

    private void validateBranchConfiguration(
            String branchMode,
            BigDecimal defaultFee,
            List<Integer> branchIds,
            List<BigDecimal> branchFees) {

        if (!"ALL_BRANCHES".equals(
                branchMode
        )
                && !"SELECTED_BRANCHES".equals(
                branchMode
        )) {

            throw new IllegalArgumentException(
                    "Invalid branch mode. Use ALL_BRANCHES or SELECTED_BRANCHES."
            );
        }


        // -------------------------------------------------
        // ALL BRANCHES
        // -------------------------------------------------

        if ("ALL_BRANCHES".equals(
                branchMode
        )) {

            if (defaultFee == null) {

                throw new IllegalArgumentException(
                        "Default fee is required for all branches."
                );
            }


            if (defaultFee.signum() < 0) {

                throw new IllegalArgumentException(
                        "Default fee cannot be negative."
                );
            }


            return;
        }


        // -------------------------------------------------
        // SELECTED BRANCHES
        // -------------------------------------------------

        if (branchIds == null
                || branchIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one branch must be selected."
            );
        }


        if (branchFees != null
                && branchFees.size()
                > branchIds.size()) {

            throw new IllegalArgumentException(
                    "Invalid branch fee configuration."
            );
        }


        if (branchFees != null) {

            for (BigDecimal fee :
                    branchFees) {

                if (fee != null
                        && fee.signum() < 0) {

                    throw new IllegalArgumentException(
                            "Branch fee cannot be negative."
                    );
                }
            }
        }
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<Program> findById(
            Integer programId) {

        Optional<Program> result =
                programRepository.findById(
                        programId
                );


        result.ifPresent(
                this::loadBranchAssignments
        );


        return result;
    }


    // =====================================================
    // FIND ALL BY TENANT
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findByTenant(
            Integer tenantId) {

        List<Program> programs =
                programRepository
                        .findByTenant_TenantIdOrderByProgramNameAsc(
                                tenantId
                        );


        loadBranchAssignments(
                programs
        );


        return programs;
    }


    // =====================================================
    // FIND ACTIVE BY TENANT
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findActive(
            Integer tenantId) {

        List<Program> programs =
                programRepository
                        .findByTenant_TenantIdAndStatusTrueOrderByProgramNameAsc(
                                tenantId
                        );


        loadBranchAssignments(
                programs
        );


        return programs;
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findByBranch(
            Integer tenantId,
            Integer branchId) {

        List<ProgramBranch> assignments =
                programBranchRepository
                        .findByTenant_TenantIdAndBranch_BranchId(
                                tenantId,
                                branchId
                        );


        return assignments
                .stream()
                .map(
                        ProgramBranch::getProgram
                )
                .filter(
                        program ->
                                program != null
                                        && program.getTenant()
                                        .getTenantId()
                                        == tenantId
                )
                .distinct()
                .sorted(
                        (a, b) ->
                                a.getProgramName()
                                        .compareToIgnoreCase(
                                                b.getProgramName()
                                        )
                )
                .toList();
    }


    // =====================================================
    // FIND ACTIVE BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findActiveByBranch(
            Integer tenantId,
            Integer branchId) {

        List<ProgramBranch> assignments =
                programBranchRepository
                        .findByTenant_TenantIdAndBranch_BranchIdAndProgram_StatusTrueAndStatusTrue(
                                tenantId,
                                branchId
                        );


        return assignments
                .stream()
                .map(
                        ProgramBranch::getProgram
                )
                .filter(
                        program ->
                                program != null
                                        && Boolean.TRUE.equals(
                                        program.getStatus()
                                )
                )
                .distinct()
                .sorted(
                        (a, b) ->
                                a.getProgramName()
                                        .compareToIgnoreCase(
                                                b.getProgramName()
                                        )
                )
                .toList();
    }


    // =====================================================
    // FIND BY CODE
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<Program> findByCode(
            Integer tenantId,
            String programCode) {

        if (programCode == null
                || programCode.trim().isEmpty()) {

            return Optional.empty();
        }


        Optional<Program> result =
                programRepository
                        .findByTenant_TenantIdAndProgramCode(
                                tenantId,
                                programCode.trim()
                                        .toUpperCase()
                        );


        result.ifPresent(
                this::loadBranchAssignments
        );


        return result;
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    public Program activate(
            Integer programId) {

        Program program =
                programRepository.findById(
                        programId
                ).orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Program not found."
                                )
                );


        program.setStatus(
                true
        );


        return programRepository.save(
                program
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    public Program deactivate(
            Integer programId) {

        Program program =
                programRepository.findById(
                        programId
                ).orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Program not found."
                                )
                );


        program.setStatus(
                false
        );


        return programRepository.save(
                program
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Integer programId) {

        if (!programRepository.existsById(
                programId
        )) {

            throw new IllegalArgumentException(
                    "Program not found."
            );
        }


        programRepository.deleteById(
                programId
        );
    }


    // =====================================================
    // LOAD BRANCH ASSIGNMENTS
    // =====================================================
    //
    // IMPORTANT:
    // NEVER use:
    //
    // program.setBranchAssignments(assignments)
    //
    // because branchAssignments is a Hibernate managed
    // orphanRemoval collection.
    //
    // =====================================================

    private void loadBranchAssignments(
            Program program) {

        if (program == null
                || program.getTenant() == null
                || program.getProgramId() == null) {

            return;
        }


        List<ProgramBranch> assignments =
                programBranchRepository
                        .findByTenant_TenantIdAndProgram_ProgramId(
                                program.getTenant()
                                        .getTenantId(),
                                program.getProgramId()
                        );


        // -------------------------------------------------
        // KEEP ORIGINAL MANAGED COLLECTION
        // -------------------------------------------------

        program.getBranchAssignments()
                .clear();


        if (assignments != null
                && !assignments.isEmpty()) {

            program.getBranchAssignments()
                    .addAll(
                            assignments
                    );
        }
    }


    // =====================================================
    // LOAD BRANCH ASSIGNMENTS - LIST
    // =====================================================

    private void loadBranchAssignments(
            List<Program> programs) {

        if (programs == null) {
            return;
        }


        for (Program program :
                programs) {

            loadBranchAssignments(
                    program
            );
        }
    }


    // =====================================================
    // NORMALIZE BRANCH MODE
    // =====================================================

    private String normalizeBranchMode(
            String branchMode) {

        if (branchMode == null
                || branchMode.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Branch mode is required."
            );
        }


        return branchMode
                .trim()
                .toUpperCase();
    }


    // =====================================================
    // SAFE DECIMAL
    // =====================================================

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}