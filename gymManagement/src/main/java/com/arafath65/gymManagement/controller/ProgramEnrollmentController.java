package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ProgramEnrollmentRequest;
import com.arafath65.gymManagement.dto.response.ProgramEnrollmentResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.ProgramEnrollment;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MemberRepository;
import com.arafath65.gymManagement.repository.ProgramRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.ProgramEnrollmentService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/program-enrollments")
public class ProgramEnrollmentController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final ProgramEnrollmentService
            programEnrollmentService;

    private final TenantRepository
            tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final BranchRepository
            branchRepository;

    private final MemberRepository
            memberRepository;

    private final ProgramRepository
            programRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramEnrollmentController(
            ProgramEnrollmentService programEnrollmentService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository,
            MemberRepository memberRepository,
            ProgramRepository programRepository) {

        this.programEnrollmentService =
                programEnrollmentService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.branchRepository =
                branchRepository;

        this.memberRepository =
                memberRepository;

        this.programRepository =
                programRepository;
    }


    // =====================================================
    // CREATE ENROLLMENT
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<ProgramEnrollmentResponse>>
    createEnrollment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            ProgramEnrollmentRequest request) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch =
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


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        Member member =
                memberRepository.findById(
                        request.getMemberId()
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Member not found."
                        )
                );


        if (member.getTenant() == null
                || member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        if (member.getBranch() == null
                || member.getBranch()
                .getBranchId()
                != branch.getBranchId()) {

            throw new BadRequestException(
                    "Member does not belong to the selected branch."
            );
        }


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        Program program =
                programRepository.findById(
                        request.getProgramId()
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Program not found."
                        )
                );


        if (program.getTenant() == null
                || program.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Program does not belong to this tenant."
            );
        }


        if (!Boolean.TRUE.equals(
                program.getStatus()
        )) {

            throw new BadRequestException(
                    "Program is inactive."
            );
        }


        if (program.getBranch() != null
                && program.getBranch()
                .getBranchId()
                != branch.getBranchId()) {

            throw new BadRequestException(
                    "Program is not available for this branch."
            );
        }


        // -------------------------------------------------
        // BUILD ENROLLMENT
        // -------------------------------------------------

        ProgramEnrollment enrollment =
                new ProgramEnrollment();


        enrollment.setTenant(
                tenant
        );


        enrollment.setBranch(
                branch
        );


        enrollment.setMember(
                member
        );


        enrollment.setProgram(
                program
        );


        enrollment.setStartDate(
                request.getStartDate()
        );


        enrollment.setPaymentType(
                request.getPaymentType()
        );


        enrollment.setDiscountAmount(
                request.getDiscountAmount()
        );


        /*
         * The service expects the initial amount
         * inside paidAmount.
         */

        enrollment.setPaidAmount(
                request.getInitialPayment()
        );


        enrollment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        ProgramEnrollment saved =
                programEnrollmentService.create(
                        enrollment
                );


        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Program enrollment created successfully.",
                        toResponse(saved)
                ),
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET ALL ENROLLMENTS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<ProgramEnrollmentResponse>>>
    getEnrollments(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(required = false)
            Integer memberId,

            @RequestParam(required = false)
            Integer programId,

            @RequestParam(required = false)
            Integer branchId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        List<ProgramEnrollment> enrollments;


        // -------------------------------------------------
        // MEMBER
        // -------------------------------------------------

        if (memberId != null) {

            validateMember(
                    tenant,
                    memberId
            );


            enrollments =
                    programEnrollmentService
                            .findByMember(
                                    tenant.getTenantId(),
                                    memberId
                            );
        }


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        else if (programId != null) {

            Program program =
                    programRepository
                            .findById(
                                    programId
                            )
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Program not found."
                                    )
                            );


            if (program.getTenant() == null
                    || program.getTenant()
                    .getTenantId()
                    != tenant.getTenantId()) {

                throw new BadRequestException(
                        "Program does not belong to this tenant."
                );
            }


            enrollments =
                    programEnrollmentService
                            .findByProgram(
                                    tenant.getTenantId(),
                                    programId
                            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        else if (branchId != null) {

            validateBranch(
                    tenant,
                    branchId
            );


            enrollments =
                    programEnrollmentService
                            .findByBranch(
                                    tenant.getTenantId(),
                                    branchId
                            );
        }


        // -------------------------------------------------
        // ALL
        // -------------------------------------------------

        else {

            enrollments =
                    programEnrollmentService
                            .findByTenant(
                                    tenant.getTenantId()
                            );
        }


        List<ProgramEnrollmentResponse>
                responses =
                enrollments.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program enrollments loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET ACTIVE PROGRAMS FOR MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<
            ApiResponse<List<ProgramEnrollmentResponse>>>
    getActiveMemberPrograms(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        validateMember(
                tenant,
                memberId
        );


        List<ProgramEnrollment> enrollments =
                programEnrollmentService
                        .findActiveByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        List<ProgramEnrollmentResponse>
                responses =
                enrollments.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active member programs loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<
            ApiResponse<ProgramEnrollmentResponse>>
    getEnrollmentById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer enrollmentId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        ProgramEnrollment enrollment =
                programEnrollmentService
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program enrollment not found."
                                )
                        );


        validateEnrollmentTenant(
                tenant,
                enrollment
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program enrollment loaded successfully.",
                        toResponse(enrollment)
                )
        );
    }


    // =====================================================
    // GET BY ENROLLMENT NUMBER
    // =====================================================

    @GetMapping("/number/{enrollmentNumber}")
    public ResponseEntity<
            ApiResponse<ProgramEnrollmentResponse>>
    getByEnrollmentNumber(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            String enrollmentNumber) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        ProgramEnrollment enrollment =
                programEnrollmentService
                        .findByEnrollmentNumber(
                                tenant.getTenantId(),
                                enrollmentNumber
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program enrollment not found."
                                )
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program enrollment loaded successfully.",
                        toResponse(enrollment)
                )
        );
    }


    // =====================================================
    // COMPLETE
    // =====================================================

    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<
            ApiResponse<ProgramEnrollmentResponse>>
    completeEnrollment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer enrollmentId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        ProgramEnrollment enrollment =
                programEnrollmentService
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program enrollment not found."
                                )
                        );


        validateEnrollmentTenant(
                tenant,
                enrollment
        );


        ProgramEnrollment updated =
                programEnrollmentService.complete(
                        enrollmentId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program enrollment completed successfully.",
                        toResponse(updated)
                )
        );
    }


    // =====================================================
    // CANCEL
    // =====================================================

    @PutMapping("/{enrollmentId}/cancel")
    public ResponseEntity<
            ApiResponse<ProgramEnrollmentResponse>>
    cancelEnrollment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer enrollmentId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        ProgramEnrollment enrollment =
                programEnrollmentService
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Program enrollment not found."
                                )
                        );


        validateEnrollmentTenant(
                tenant,
                enrollment
        );


        ProgramEnrollment updated =
                programEnrollmentService.cancel(
                        enrollmentId
                );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program enrollment cancelled successfully.",
                        toResponse(updated)
                )
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private ProgramEnrollmentResponse toResponse(
            ProgramEnrollment enrollment) {

        ProgramEnrollmentResponse response =
                new ProgramEnrollmentResponse();


        response.setProgramEnrollmentId(
                enrollment.getProgramEnrollmentId()
        );


        response.setTenantId(
                enrollment.getTenant()
                        .getTenantId()
        );


        response.setBranchId(
                enrollment.getBranch()
                        .getBranchId()
        );


        response.setBranchName(
                enrollment.getBranch()
                        .getBranchName()
        );


        response.setMemberId(
                enrollment.getMember()
                        .getMemberId()
        );


        response.setMemberNo(
                enrollment.getMember()
                        .getMemberNo()
        );


        response.setMemberName(
                enrollment.getMember()
                        .getFullName()
        );


        response.setProgramId(
                enrollment.getProgram()
                        .getProgramId()
        );


        response.setProgramCode(
                enrollment.getProgram()
                        .getProgramCode()
        );


        response.setProgramName(
                enrollment.getProgram()
                        .getProgramName()
        );


        response.setEnrollmentNumber(
                enrollment.getEnrollmentNumber()
        );


        response.setStartDate(
                enrollment.getStartDate()
        );


        response.setEndDate(
                enrollment.getEndDate()
        );


        response.setProgramFee(
                enrollment.getProgramFee()
        );


        response.setDiscountAmount(
                enrollment.getDiscountAmount()
        );


        response.setTotalAmount(
                enrollment.getTotalAmount()
        );


        response.setPaidAmount(
                enrollment.getPaidAmount()
        );


        response.setBalanceAmount(
                enrollment.getBalanceAmount()
        );


        response.setPaymentType(
                enrollment.getPaymentType()
        );


        response.setEnrollmentState(
                enrollment.getEnrollmentState()
        );


        response.setStatus(
                enrollment.getStatus()
        );


        response.setNotes(
                enrollment.getNotes()
        );


        response.setCreatedAt(
                enrollment.getCreatedAt()
        );


        response.setUpdatedAt(
                enrollment.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // VALIDATE MEMBER
    // =====================================================

    private void validateMember(
            Tenant tenant,
            Integer memberId) {

        Member member =
                memberRepository.findById(
                        memberId
                ).orElseThrow(() ->
                        new BadRequestException(
                                "Member not found."
                        )
                );


        if (member.getTenant() == null
                || member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // VALIDATE ENROLLMENT TENANT
    // =====================================================

    private void validateEnrollmentTenant(
            Tenant tenant,
            ProgramEnrollment enrollment) {

        if (enrollment.getTenant() == null
                || enrollment.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Program enrollment does not belong to this tenant."
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
    // TENANT + DEVICE
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