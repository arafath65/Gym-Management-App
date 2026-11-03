package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ProgramRequest;
import com.arafath65.gymManagement.dto.response.ProgramBranchResponse;
import com.arafath65.gymManagement.dto.response.ProgramResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.ProgramBranch;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.ProgramService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final ProgramService programService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramController(
            ProgramService programService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository) {

        this.programService =
                programService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE PROGRAM
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    createProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            ProgramRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        Program savedProgram;

        try {

            Program program =
                    new Program();

            program.setTenant(
                    tenant
            );

            program.setProgramCode(
                    request.getProgramCode()
            );

            program.setProgramName(
                    request.getProgramName()
            );

            program.setDescription(
                    request.getDescription()
            );

            program.setDurationDays(
                    request.getDurationDays()
            );

            program.setStatus(
                    true
            );


            // -------------------------------------------------
            // BRANCH DATA
            // -------------------------------------------------

            List<Integer> branchIds =
                    new ArrayList<>();

            List<java.math.BigDecimal> branchFees =
                    new ArrayList<>();


            if ("SELECTED_BRANCHES".equalsIgnoreCase(
                    request.getBranchMode()
            )) {

                if (request.getBranches() != null) {

                    request.getBranches()
                            .forEach(
                                    branch -> {

                                        branchIds.add(
                                                branch.getBranchId()
                                        );

                                        branchFees.add(
                                                branch.getFee()
                                        );
                                    }
                            );
                }

            }


            // -------------------------------------------------
            // SERVICE
            // -------------------------------------------------

            savedProgram =
                    programService.create(
                            program,
                            request.getBranchMode(),
                            request.getDefaultFee(),
                            branchIds,
                            branchFees
                    );


        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Program created successfully.",
                        toResponse(savedProgram)
                ),
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // UPDATE PROGRAM
    // =====================================================

    @PutMapping("/{programId}")
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    updateProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId,

            @Valid
            @RequestBody
            ProgramRequest request) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND EXISTING PROGRAM
        // -------------------------------------------------

        Program existingProgram =
                programService
                        .findById(
                                programId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        // -------------------------------------------------
        // TENANT SECURITY
        // -------------------------------------------------

        validateProgramTenant(
                existingProgram,
                tenant
        );


        // -------------------------------------------------
        // BUILD UPDATED PROGRAM
        // -------------------------------------------------

        Program updatedProgram =
                new Program();

        updatedProgram.setTenant(
                tenant
        );

        updatedProgram.setProgramCode(
                request.getProgramCode()
        );

        updatedProgram.setProgramName(
                request.getProgramName()
        );

        updatedProgram.setDescription(
                request.getDescription()
        );

        updatedProgram.setDurationDays(
                request.getDurationDays()
        );

        updatedProgram.setStatus(
                existingProgram.getStatus()
        );


        // -------------------------------------------------
        // BRANCH DATA
        // -------------------------------------------------

        List<Integer> branchIds =
                new ArrayList<>();

        List<java.math.BigDecimal> branchFees =
                new ArrayList<>();


        if ("SELECTED_BRANCHES".equalsIgnoreCase(
                request.getBranchMode()
        )) {

            if (request.getBranches() != null) {

                request.getBranches()
                        .forEach(
                                branch -> {

                                    branchIds.add(
                                            branch.getBranchId()
                                    );

                                    branchFees.add(
                                            branch.getFee()
                                    );
                                }
                        );
            }
        }


        // -------------------------------------------------
        // UPDATE
        // -------------------------------------------------

        Program savedProgram;

        try {

            savedProgram =
                    programService.update(
                            programId,
                            updatedProgram,
                            request.getBranchMode(),
                            request.getDefaultFee(),
                            branchIds,
                            branchFees
                    );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program updated successfully.",
                        toResponse(savedProgram)
                )
        );
    }


    // =====================================================
    // GET ALL PROGRAMS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<ProgramResponse>>>
    getPrograms(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(
                    required = false
            )
            Integer branchId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // GET PROGRAMS
        // -------------------------------------------------

        List<Program> programs;

        if (branchId == null) {

            programs =
                    programService.findByTenant(
                            tenant.getTenantId()
                    );

        } else {

            validateBranch(
                    tenant,
                    branchId
            );

            programs =
                    programService.findByBranch(
                            tenant.getTenantId(),
                            branchId
                    );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        List<ProgramResponse>
                responses =
                programs.stream()
                        .map(
                                this::toResponse
                        )
                        .collect(
                                Collectors.toList()
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Programs loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET ACTIVE PROGRAMS
    // =====================================================

    @GetMapping("/active")
    public ResponseEntity<
            ApiResponse<List<ProgramResponse>>>
    getActivePrograms(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(
                    required = false
            )
            Integer branchId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // GET ACTIVE PROGRAMS
        // -------------------------------------------------

        List<Program> programs;

        if (branchId == null) {

            programs =
                    programService.findActive(
                            tenant.getTenantId()
                    );

        } else {

            validateBranch(
                    tenant,
                    branchId
            );

            programs =
                    programService.findActiveByBranch(
                            tenant.getTenantId(),
                            branchId
                    );
        }


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        List<ProgramResponse>
                responses =
                programs.stream()
                        .map(
                                this::toResponse
                        )
                        .collect(
                                Collectors.toList()
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active programs loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET PROGRAM BY ID
    // =====================================================

    @GetMapping("/{programId}")
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    getProgramById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PROGRAM
        // -------------------------------------------------

        Program program =
                programService
                        .findById(
                                programId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        // -------------------------------------------------
        // TENANT SECURITY
        // -------------------------------------------------

        validateProgramTenant(
                program,
                tenant
        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program loaded successfully.",
                        toResponse(program)
                )
        );
    }


    // =====================================================
    // GET PROGRAM BY CODE
    // =====================================================

    @GetMapping("/code/{programCode}")
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    getProgramByCode(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            String programCode) {

        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND
        // -------------------------------------------------

        Program program =
                programService
                        .findByCode(
                                tenant.getTenantId(),
                                programCode
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program loaded successfully.",
                        toResponse(program)
                )
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @PutMapping("/{programId}/activate")
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    activateProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId) {

        // -------------------------------------------------
        // VALIDATE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        Program existingProgram =
                programService
                        .findById(
                                programId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        validateProgramTenant(
                existingProgram,
                tenant
        );


        // -------------------------------------------------
        // ACTIVATE
        // -------------------------------------------------

        Program updatedProgram;

        try {

            updatedProgram =
                    programService.activate(
                            programId
                    );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program activated successfully.",
                        toResponse(updatedProgram)
                )
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @PutMapping("/{programId}/deactivate")
    public ResponseEntity<
            ApiResponse<ProgramResponse>>
    deactivateProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId) {

        // -------------------------------------------------
        // VALIDATE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        Program existingProgram =
                programService
                        .findById(
                                programId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        validateProgramTenant(
                existingProgram,
                tenant
        );


        // -------------------------------------------------
        // DEACTIVATE
        // -------------------------------------------------

        try {

            programService.deactivate(
                    programId
            );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program deactivated successfully.",
                        null
                )
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{programId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deleteProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId) {

        // -------------------------------------------------
        // VALIDATE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        Program existingProgram =
                programService
                        .findById(
                                programId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program not found."
                                )
                        );


        validateProgramTenant(
                existingProgram,
                tenant
        );


        // -------------------------------------------------
        // DELETE
        // -------------------------------------------------

        try {

            programService.delete(
                    programId
            );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    e.getMessage()
            );
        }


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program deleted successfully.",
                        null
                )
        );
    }


    // =====================================================
    // PROGRAM → RESPONSE
    // =====================================================

    private ProgramResponse toResponse(
            Program program) {

        ProgramResponse response =
                new ProgramResponse();


        // -------------------------------------------------
        // BASIC
        // -------------------------------------------------

        response.setProgramId(
                program.getProgramId()
        );


        if (program.getTenant() != null) {

            response.setTenantId(
                    program.getTenant()
                            .getTenantId()
            );
        }


        response.setProgramCode(
                program.getProgramCode()
        );


        response.setProgramName(
                program.getProgramName()
        );


        response.setDescription(
                program.getDescription()
        );


        response.setDurationDays(
                program.getDurationDays()
        );


        response.setStatus(
                program.getStatus()
        );


        // -------------------------------------------------
        // BRANCH ASSIGNMENTS
        // -------------------------------------------------

        List<ProgramBranchResponse>
                branchResponses =
                new ArrayList<>();


        if (program.getBranchAssignments()
                != null) {

            for (
                    ProgramBranch assignment
                    : program.getBranchAssignments()
            ) {

                if (assignment == null) {
                    continue;
                }


                ProgramBranchResponse
                        branchResponse =
                        new ProgramBranchResponse();


                branchResponse.setProgramBranchId(
                        assignment
                                .getProgramBranchId()
                );


                if (assignment.getBranch()
                        != null) {

                    Branch branch =
                            assignment.getBranch();


                    branchResponse.setBranchId(
                            branch.getBranchId()
                    );


                    branchResponse.setBranchNo(
                            branch.getBranchNo()
                    );


                    branchResponse.setBranchName(
                            branch.getBranchName()
                    );
                }


                branchResponse.setFee(
                        assignment.getFee()
                );


                branchResponse.setStatus(
                        assignment.getStatus()
                );


                branchResponse.setCreatedAt(
                        assignment.getCreatedAt()
                );


                branchResponse.setUpdatedAt(
                        assignment.getUpdatedAt()
                );


                branchResponses.add(
                        branchResponse
                );
            }
        }


        response.setBranchAssignments(
                branchResponses
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                program.getCreatedAt()
        );


        response.setUpdatedAt(
                program.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // VALIDATE PROGRAM TENANT
    // =====================================================

    private void validateProgramTenant(
            Program program,
            Tenant tenant) {

        if (program.getTenant() == null) {

            throw new BadRequestException(
                    "Program tenant is missing."
            );
        }


        if (program.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Program does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // VALIDATE BRANCH
    // =====================================================

    private void validateBranch(
            Tenant tenant,
            Integer branchId) {

        if (branchId == null) {

            throw new BadRequestException(
                    "Branch ID is required."
            );
        }


        Branch branch =
                branchRepository
                        .findById(
                                branchId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Branch not found."
                                )
                        );


        if (branch.getTenant() == null
                || branch.getTenant().getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                branch.getStatus()
        )) {

            throw new BadRequestException(
                    "Branch is inactive."
            );
        }
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {

        // -------------------------------------------------
        // BASIC VALIDATION
        // -------------------------------------------------

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            throw new BadRequestException(
                    "Tenant number is required."
            );
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            throw new BadRequestException(
                    "Device token is required."
            );
        }


        // -------------------------------------------------
        // FIND TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository
                        .findByTenantNo(
                                tenantNo
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid tenant number."
                                )
                        );


        // -------------------------------------------------
        // TENANT STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus()
        )) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // -------------------------------------------------
        // FIND DEVICE
        // -------------------------------------------------

        RegisteredDevice device =
                registeredDeviceRepository
                        .findByDeviceToken(
                                deviceToken
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Device is not registered."
                                )
                        );


        // -------------------------------------------------
        // DEVICE STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                device.getStatus()
        )) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


        // -------------------------------------------------
        // DEVICE → TENANT
        // -------------------------------------------------

        if (device.getTenant() == null
                || device.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        return tenant;
    }
}