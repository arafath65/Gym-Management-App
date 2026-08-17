package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipPaymentRequest;
import com.arafath65.gymManagement.dto.response.MembershipPaymentResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipPayment;
import com.arafath65.gymManagement.entity.MembershipRenewal;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipService;
import com.arafath65.gymManagement.service.MembershipPaymentService;
import com.arafath65.gymManagement.service.MembershipRenewalService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership-payments")
public class MembershipPaymentController {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MembershipPaymentService membershipPaymentService;

    private final MemberMembershipService memberMembershipService;

    private final MembershipRenewalService membershipRenewalService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final BranchRepository branchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPaymentController(
            MembershipPaymentService membershipPaymentService,
            MemberMembershipService memberMembershipService,
            MembershipRenewalService membershipRenewalService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            BranchRepository branchRepository) {

        this.membershipPaymentService =
                membershipPaymentService;

        this.memberMembershipService =
                memberMembershipService;

        this.membershipRenewalService =
                membershipRenewalService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.branchRepository =
                branchRepository;
    }


    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MembershipPaymentResponse>>
    createPayment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MembershipPaymentRequest request) {


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // LOAD MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipService
                        .findById(
                                request.getMemberMembershipId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        validateMembership(
                tenant,
                membership
        );


        // -------------------------------------------------
        // BUILD PAYMENT
        // -------------------------------------------------

        MembershipPayment payment =
                new MembershipPayment();

        payment.setTenant(
                tenant
        );

        payment.setBranch(
                membership.getBranch()
        );

        payment.setMember(
                membership.getMember()
        );

        payment.setMemberMembership(
                membership
        );

        payment.setAmount(
                request.getAmount()
        );

        payment.setPaymentMethod(
                request.getPaymentMethod()
        );

        payment.setPaymentDate(
                request.getPaymentDate()
        );

        payment.setReferenceNumber(
                request.getReferenceNumber()
        );

        payment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // RENEWAL PAYMENT
        // -------------------------------------------------

        if (request.getMembershipRenewalId() != null) {

            MembershipRenewal renewal =
                    membershipRenewalService
                            .findById(
                                    request.getMembershipRenewalId()
                            )
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Membership renewal not found."
                                    )
                            );


            if (renewal.getMemberMembership()
                    .getMemberMembershipId()
                    != membership.getMemberMembershipId()) {

                throw new BadRequestException(
                        "Renewal does not belong to this membership."
                );
            }


            payment.setMembershipRenewal(
                    renewal
            );
        }


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        MembershipPayment savedPayment =
                membershipPaymentService.save(
                        payment
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MembershipPaymentResponse response =
                toResponse(
                        savedPayment
                );


        ApiResponse<MembershipPaymentResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership payment created successfully.",
                        response
                );


        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<
            ApiResponse<MembershipPaymentResponse>>
    getPaymentById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer paymentId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipPayment payment =
                membershipPaymentService
                        .findById(
                                paymentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Payment not found."
                                )
                        );


        if (payment.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Payment does not belong to this tenant."
            );
        }


        ApiResponse<MembershipPaymentResponse>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Payment loaded successfully.",
                        toResponse(payment)
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET PAYMENTS BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{membershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipPaymentResponse>>>
    getPaymentsByMembership(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MemberMembership membership =
                memberMembershipService
                        .findById(
                                membershipId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        validateMembership(
                tenant,
                membership
        );


        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByMembership(
                                tenant.getTenantId(),
                                membershipId
                        );


        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipPaymentResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership payments loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET PAYMENTS BY MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<
            ApiResponse<List<MembershipPaymentResponse>>>
    getPaymentsByMember(

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


        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipPaymentResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Member payments loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // GET PAYMENTS BY BRANCH
    // =====================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<
            ApiResponse<List<MembershipPaymentResponse>>>
    getPaymentsByBranch(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer branchId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        validateBranch(
                tenant,
                branchId
        );


        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByBranch(
                                tenant.getTenantId(),
                                branchId
                        );


        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());


        ApiResponse<List<MembershipPaymentResponse>>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Branch payments loaded successfully.",
                        responses
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // DELETE PAYMENT
    // =====================================================

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deletePayment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer paymentId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MembershipPayment payment =
                membershipPaymentService
                        .findById(
                                paymentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Payment not found."
                                )
                        );


        if (payment.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Payment does not belong to this tenant."
            );
        }


        membershipPaymentService.delete(
                paymentId
        );


        ApiResponse<Void>
                apiResponse =
                new ApiResponse<>(
                        true,
                        "Membership payment deleted successfully.",
                        null
                );


        return ResponseEntity.ok(
                apiResponse
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipPaymentResponse toResponse(
            MembershipPayment payment) {

        MembershipPaymentResponse response =
                new MembershipPaymentResponse();

        response.setMembershipPaymentId(
                payment.getMembershipPaymentId()
        );

        response.setTenantId(
                payment.getTenant()
                        .getTenantId()
        );

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

        if (payment.getMember() != null) {

            response.setMemberId(
                    payment.getMember()
                            .getMemberId()
            );
        }

        if (payment.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    payment.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    payment.getMemberMembership()
                            .getMembershipNumber()
            );
        }

        if (payment.getMembershipRenewal() != null) {

            response.setMembershipRenewalId(
                    payment.getMembershipRenewal()
                            .getMembershipRenewalId()
            );
        }

        response.setPaymentNumber(
                payment.getPaymentNumber()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setPaymentMethod(
                payment.getPaymentMethod()
        );

        response.setPaymentDate(
                payment.getPaymentDate()
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

        return response;
    }


    // =====================================================
    // VALIDATE MEMBERSHIP
    // =====================================================

    private void validateMembership(
            Tenant tenant,
            MemberMembership membership) {

        if (membership.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
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

        if (branch.getTenant()
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


        if (device.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        return tenant;
    }
}