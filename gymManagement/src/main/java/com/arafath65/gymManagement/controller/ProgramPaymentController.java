package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ProgramPaymentRequest;
import com.arafath65.gymManagement.dto.response.ProgramPaymentDetailResponse;
import com.arafath65.gymManagement.dto.response.ProgramPaymentResponse;
import com.arafath65.gymManagement.entity.ProgramPayment;
import com.arafath65.gymManagement.entity.ProgramPaymentDetail;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.ProgramPaymentService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/program-payments")
public class ProgramPaymentController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final ProgramPaymentService
            programPaymentService;

    private final TenantRepository
            tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramPaymentController(
            ProgramPaymentService programPaymentService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository) {

        this.programPaymentService =
                programPaymentService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    @PostMapping
    public ResponseEntity<ApiResponse<ProgramPaymentResponse>>
    createPayment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            ProgramPaymentRequest request) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            ProgramPayment payment =
                    programPaymentService.createPayment(
                            tenantId,
                            request
                    );


            ProgramPaymentResponse response =
                    convertToResponse(
                            payment
                    );


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payment created successfully.",
                            response
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<ProgramPaymentResponse>>
    getPaymentById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer paymentId) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            ProgramPayment payment =
                    programPaymentService.findById(
                            tenantId,
                            paymentId
                    );


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payment loaded successfully.",
                            convertToResponse(payment)
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENTS BY ENROLLMENT
    // =====================================================

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<
            ApiResponse<List<ProgramPaymentResponse>>>
    getPaymentsByEnrollment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer enrollmentId) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            List<ProgramPayment> payments =
                    programPaymentService.findByEnrollment(
                            tenantId,
                            enrollmentId
                    );


            List<ProgramPaymentResponse>
                    responses =
                    payments.stream()
                            .map(this::convertToResponse)
                            .collect(Collectors.toList());


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payments loaded successfully.",
                            responses
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENTS BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<ProgramPaymentResponse>>>
    getPaymentsByMember(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            List<ProgramPayment> payments =
                    programPaymentService.findByMember(
                            tenantId,
                            memberId
                    );


            List<ProgramPaymentResponse>
                    responses =
                    payments.stream()
                            .map(this::convertToResponse)
                            .collect(Collectors.toList());


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Member program payments loaded successfully.",
                            responses
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENTS BY PROGRAM
    // =====================================================

    @GetMapping("/program/{programId}")
    public ResponseEntity<
            ApiResponse<List<ProgramPaymentResponse>>>
    getPaymentsByProgram(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer programId) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            List<ProgramPayment> payments =
                    programPaymentService.findByProgram(
                            tenantId,
                            programId
                    );


            List<ProgramPaymentResponse>
                    responses =
                    payments.stream()
                            .map(this::convertToResponse)
                            .collect(Collectors.toList());


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payments loaded successfully.",
                            responses
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENT BY NUMBER
    // =====================================================

    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<
            ApiResponse<ProgramPaymentResponse>>
    getPaymentByNumber(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            String paymentNumber) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            ProgramPayment payment =
                    programPaymentService.findByPaymentNumber(
                            tenantId,
                            paymentNumber
                    );


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payment loaded successfully.",
                            convertToResponse(payment)
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // GET PAYMENTS BY DATE RANGE
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<
            ApiResponse<List<ProgramPaymentResponse>>>
    getPaymentsByDateRange(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate) {

        try {

            Tenant tenant =
                    validateTenantAndDevice(
                            tenantNo,
                            deviceToken
                    );


            Integer tenantId =
                    tenant.getTenantId();


            List<ProgramPayment> payments =
                    programPaymentService.findByDateRange(
                            tenantId,
                            startDate,
                            endDate
                    );


            List<ProgramPaymentResponse>
                    responses =
                    payments.stream()
                            .map(this::convertToResponse)
                            .collect(Collectors.toList());


            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Program payments loaded successfully.",
                            responses
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private ProgramPaymentResponse
    convertToResponse(
            ProgramPayment payment) {

        ProgramPaymentResponse response =
                new ProgramPaymentResponse();


        // =================================================
        // PAYMENT
        // =================================================

        response.setProgramPaymentId(
                payment.getProgramPaymentId()
        );

        response.setPaymentNumber(
                payment.getPaymentNumber()
        );

        response.setPaymentDate(
                payment.getPaymentDate()
        );

        response.setTotalAmount(
                payment.getTotalAmount()
        );

        response.setReferenceNumber(
                payment.getReferenceNumber()
        );

        response.setNotes(
                payment.getNotes()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        response.setUpdatedAt(
                payment.getUpdatedAt()
        );


        // =================================================
        // TENANT
        // =================================================

        if (payment.getTenant() != null) {

            response.setTenantId(
                    payment.getTenant()
                            .getTenantId()
            );
        }


        // =================================================
        // BRANCH
        // =================================================

        if (payment.getBranch() != null) {

            response.setBranchId(
                    payment.getBranch()
                            .getBranchId()
            );

            response.setBranchName(
                    payment.getBranch()
                            .getBranchName()
            );
        }


        // =================================================
        // MEMBER
        // =================================================

        if (payment.getMember() != null) {

            response.setMemberId(
                    payment.getMember()
                            .getMemberId()
            );

            response.setMemberNo(
                    payment.getMember()
                            .getMemberNo()
            );

            response.setMemberName(
                    payment.getMember()
                            .getFullName()
            );
        }


        // =================================================
        // ENROLLMENT
        // =================================================

        if (payment.getProgramEnrollment() != null) {

            var enrollment =
                    payment.getProgramEnrollment();


            response.setProgramEnrollmentId(
                    enrollment.getProgramEnrollmentId()
            );

            response.setEnrollmentNumber(
                    enrollment.getEnrollmentNumber()
            );


            // ---------------------------------------------
            // PROGRAM
            // ---------------------------------------------

            if (enrollment.getProgram() != null) {

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
            }
        }


        // =================================================
        // PAYMENT DETAILS
        // =================================================

        List<ProgramPaymentDetailResponse>
                detailResponses =
                payment.getPaymentDetails()
                        .stream()
                        .map(
                                this::convertDetailToResponse
                        )
                        .collect(Collectors.toList());


        response.setPaymentDetails(
                detailResponses
        );


        return response;
    }


    // =====================================================
    // CONVERT DETAIL → RESPONSE
    // =====================================================

    private ProgramPaymentDetailResponse
    convertDetailToResponse(
            ProgramPaymentDetail detail) {

        ProgramPaymentDetailResponse response =
                new ProgramPaymentDetailResponse();


        response.setProgramPaymentDetailId(
                detail.getProgramPaymentDetailId()
        );

        response.setPaymentMethod(
                detail.getPaymentMethod()
        );

        response.setAmount(
                detail.getAmount()
        );

        response.setReferenceNumber(
                detail.getReferenceNumber()
        );


        return response;
    }


    // =====================================================
    // TENANT + DEVICE VALIDATION
    // =====================================================

    private Tenant validateTenantAndDevice(
            String tenantNo,
            String deviceToken) {

        // -------------------------------------------------
        // TENANT
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
        // DEVICE
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
        // DEVICE TENANT
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