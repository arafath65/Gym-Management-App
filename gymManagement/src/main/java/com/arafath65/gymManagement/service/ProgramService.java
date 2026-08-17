package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.repository.ProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgramService {

    private final ProgramRepository programRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramService(
            ProgramRepository programRepository) {

        this.programRepository =
                programRepository;
    }


    // =====================================================
    // CREATE PROGRAM
    // =====================================================

    public Program create(
            Program program) {

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

        Integer tenantId =
                program.getTenant()
                        .getTenantId();


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

        Integer durationDays =
                program.getDurationDays();


        if (durationDays == null
                || durationDays <= 0) {

            throw new IllegalArgumentException(
                    "Program duration must be greater than zero."
            );
        }


        // -------------------------------------------------
        // FEE
        // -------------------------------------------------

        BigDecimal fee =
                program.getFee();


        if (fee == null) {

            fee =
                    BigDecimal.ZERO;
        }


        if (fee.signum() < 0) {

            throw new IllegalArgumentException(
                    "Program fee cannot be negative."
            );
        }


        program.setFee(
                fee
        );


        // -------------------------------------------------
        // BRANCH VALIDATION
        // -------------------------------------------------

        Branch branch =
                program.getBranch();


        if (branch != null) {

            if (branch.getTenant() == null
                    || branch.getTenant()
                    .getTenantId()
                    != tenantId) {

                throw new IllegalArgumentException(
                        "Branch does not belong to this tenant."
                );
            }


            if (!Boolean.TRUE.equals(
                    branch.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "Branch is inactive."
                );
            }
        }


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        if (program.getStatus() == null) {

            program.setStatus(
                    true
            );
        }


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        return programRepository.save(
                program
        );
    }


    // =====================================================
    // UPDATE PROGRAM
    // =====================================================

    public Program update(
            Integer programId,
            Program updatedProgram) {

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
        // CODE
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


        // Check duplicate only when code changes.

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
        // NAME
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
        // FEE
        // -------------------------------------------------

        BigDecimal fee =
                updatedProgram.getFee();


        if (fee == null) {

            fee =
                    BigDecimal.ZERO;
        }


        if (fee.signum() < 0) {

            throw new IllegalArgumentException(
                    "Program fee cannot be negative."
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch =
                updatedProgram.getBranch();


        if (branch != null) {

            if (branch.getTenant() == null
                    || branch.getTenant()
                    .getTenantId()
                    != tenantId) {

                throw new IllegalArgumentException(
                        "Branch does not belong to this tenant."
                );
            }


            if (!Boolean.TRUE.equals(
                    branch.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "Branch is inactive."
                );
            }
        }


        // -------------------------------------------------
        // UPDATE FIELDS
        // -------------------------------------------------

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


        existing.setFee(
                fee
        );


        existing.setBranch(
                branch
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        return programRepository.save(
                existing
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Optional<Program> findById(
            Integer programId) {

        return programRepository.findById(
                programId
        );
    }


    // =====================================================
    // FIND ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findByTenant(
            Integer tenantId) {

        return programRepository
                .findByTenant_TenantIdOrderByProgramNameAsc(
                        tenantId
                );
    }


    // =====================================================
    // FIND ACTIVE
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findActive(
            Integer tenantId) {

        return programRepository
                .findByTenant_TenantIdAndStatusTrueOrderByProgramNameAsc(
                        tenantId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findByBranch(
            Integer tenantId,
            Integer branchId) {

        return programRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByProgramNameAsc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND ACTIVE BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<Program> findActiveByBranch(
            Integer tenantId,
            Integer branchId) {

        return programRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueOrderByProgramNameAsc(
                        tenantId,
                        branchId
                );
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


        return programRepository
                .findByTenant_TenantIdAndProgramCode(
                        tenantId,
                        programCode.trim()
                                .toUpperCase()
                );
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
                        () -> new IllegalArgumentException(
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
                        () -> new IllegalArgumentException(
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
}