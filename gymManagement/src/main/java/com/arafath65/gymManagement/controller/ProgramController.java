package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ProgramRequest;
import com.arafath65.gymManagement.dto.response.ProgramResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Program;
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch = null;


        if (request.getBranchId() != null) {

            branch =
                    branchRepository.findById(
                            request.getBranchId()
                    ).orElseThrow(() ->
                            new BadRequestException(
                                    "Branch not found."
                            )
                    );


            validateBranch(
                    tenant,
                    branch
            );
        }


        // -------------------------------------------------
        // BUILD PROGRAM
        // -------------------------------------------------

        Program program =
                new Program();


        program.setTenant(
                tenant
        );


        program.setBranch(
                branch
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


        program.setFee(
                request.getFee()
        );


        program.setStatus(
                true
        );


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        Program saved =
                programService.create(
                        program
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<ProgramResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Program created successfully.",
                        toResponse(saved)
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
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

            @RequestParam(required = false)
            Integer branchId,

            @RequestParam(required = false)
            Boolean active) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<Program> programs;


        // -------------------------------------------------
        // BRANCH + ACTIVE
        // -------------------------------------------------

        if (branchId != null
                && Boolean.TRUE.equals(active)) {

            validateBranch(
                    tenant,
                    branchId
            );


            programs =
                    programService
                            .findActiveByBranch(
                                    tenant.getTenantId(),
                                    branchId
                            );
        }


        // -------------------------------------------------
        // BRANCH ONLY
        // -------------------------------------------------

        else if (branchId != null) {

            validateBranch(
                    tenant,
                    branchId
            );


            programs =
                    programService
                            .findByBranch(
                                    tenant.getTenantId(),
                                    branchId
                            );
        }


        // -------------------------------------------------
        // ACTIVE ONLY
        // -------------------------------------------------

        else if (Boolean.TRUE.equals(active)) {

            programs =
                    programService
                            .findActive(
                                    tenant.getTenantId()
                            );
        }


        // -------------------------------------------------
        // ALL
        // -------------------------------------------------

        else {

            programs =
                    programService
                            .findByTenant(
                                    tenant.getTenantId()
                            );
        }


        List<ProgramResponse> responses =
                programs.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<ProgramResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Programs loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        validateProgramTenant(
                tenant,
                program
        );


        ApiResponse<ProgramResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Program loaded successfully.",
                        toResponse(program)
                );


        return ResponseEntity.ok(
                apiResponse
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        ApiResponse<ProgramResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Program loaded successfully.",
                        toResponse(program)
                );


        return ResponseEntity.ok(
                apiResponse
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        Program existing =
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
                tenant,
                existing
        );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch = null;


        if (request.getBranchId() != null) {

            branch =
                    branchRepository.findById(
                            request.getBranchId()
                    ).orElseThrow(() ->
                            new BadRequestException(
                                    "Branch not found."
                            )
                    );


            validateBranch(
                    tenant,
                    branch
            );
        }


        // -------------------------------------------------
        // BUILD UPDATE OBJECT
        // -------------------------------------------------

        Program updated =
                new Program();


        updated.setTenant(
                tenant
        );


        updated.setBranch(
                branch
        );


        updated.setProgramCode(
                request.getProgramCode()
        );


        updated.setProgramName(
                request.getProgramName()
        );


        updated.setDescription(
                request.getDescription()
        );


        updated.setDurationDays(
                request.getDurationDays()
        );


        updated.setFee(
                request.getFee()
        );


        // -------------------------------------------------
        // UPDATE
        // -------------------------------------------------

        Program saved =
                programService.update(
                        programId,
                        updated
                );


        ApiResponse<ProgramResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Program updated successfully.",
                        toResponse(saved)
                );


        return ResponseEntity.ok(
                apiResponse
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        validateProgramTenant(
                tenant,
                program
        );


        Program updated =
                programService.activate(
                        programId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program activated successfully.",
                        toResponse(updated)
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        validateProgramTenant(
                tenant,
                program
        );


        Program updated =
                programService.deactivate(
                        programId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program deactivated successfully.",
                        toResponse(updated)
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


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


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


        validateProgramTenant(
                tenant,
                program
        );


        programService.delete(
                programId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program deleted successfully.",
                        null
                )
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private ProgramResponse toResponse(
            Program program) {

        ProgramResponse response =
                new ProgramResponse();


        response.setProgramId(
                program.getProgramId()
        );


        response.setTenantId(
                program.getTenant()
                        .getTenantId()
        );


        if (program.getBranch() != null) {

            response.setBranchId(
                    program.getBranch()
                            .getBranchId()
            );


            response.setBranchName(
                    program.getBranch()
                            .getBranchName()
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


        response.setFee(
                program.getFee()
        );


        response.setStatus(
                program.getStatus()
        );


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
            Tenant tenant,
            Program program) {

        if (program.getTenant() == null
                || program.getTenant()
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

        Branch branch =
                branchRepository.findById(
                        branchId
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Branch not found."
                        )
                );


        validateBranch(
                tenant,
                branch
        );
    }


    private void validateBranch(
            Tenant tenant,
            Branch branch) {

        if (branch.getTenant() == null
                || branch.getTenant()
                .getTenantId()
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


        if (!Boolean.TRUE.equals(
                tenant.getStatus()
        )) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


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


        if (!Boolean.TRUE.equals(
                device.getStatus()
        )) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


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