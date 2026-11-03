package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MembershipPaymentRequest;
import com.arafath65.gymManagement.dto.response.MembershipPaymentDetailResponse;
import com.arafath65.gymManagement.dto.response.MembershipPaymentResponse;
import com.arafath65.gymManagement.dto.response.MembershipPaymentSummaryResponse;
import com.arafath65.gymManagement.entity.MembershipPayment;
import com.arafath65.gymManagement.entity.MembershipPaymentDetail;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MembershipPaymentService;
import com.arafath65.gymManagement.service.PermissionAuthorizationService;
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

    private final PermissionAuthorizationService permissionAuthorizationService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPaymentController(
            MembershipPaymentService membershipPaymentService,
            TenantRepository tenantRepository,
            RegisteredDeviceRepository registeredDeviceRepository,
            PermissionAuthorizationService permissionAuthorizationService) {

        this.membershipPaymentService =
                membershipPaymentService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.permissionAuthorizationService = permissionAuthorizationService;
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
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_COLLECT");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CREATE PAYMENT
        // -------------------------------------------------

        MembershipPayment payment =
                membershipPaymentService.createPayment(
                        tenant.getTenantId(),
                        request
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        MembershipPaymentResponse response =
                mapToResponse(payment);


        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Membership payment created successfully.",
                        response
                ),
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET PAYMENTS BY BILLING CYCLE
    // =====================================================

    @GetMapping("/billing-cycle/{membershipBillingCycleId}")
    public ResponseEntity<
            ApiResponse<List<MembershipPaymentResponse>>>
    getByBillingCycle(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer membershipBillingCycleId) {
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PAYMENTS
        // -------------------------------------------------

        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByBillingCycle(
                                tenant.getTenantId(),
                                membershipBillingCycleId
                        );


        // -------------------------------------------------
        // MAP RESPONSE
        // -------------------------------------------------

        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Billing cycle payments loaded successfully.",
                        responses
                )
        );
    }


    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<
            ApiResponse<MembershipPaymentResponse>>
    getPayment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer paymentId) {
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PAYMENT
        // -------------------------------------------------

        MembershipPayment payment =
                membershipPaymentService
                        .findById(
                                tenant.getTenantId(),
                                paymentId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership payment not found."
                                )
                        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership payment loaded successfully.",
                        mapToResponse(payment)
                )
        );
    }


    // =====================================================
    // GET PAYMENTS BY MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}")
    public ResponseEntity<
            ApiResponse<List<MembershipPaymentResponse>>>
    getPaymentsByMembership(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipId) {
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PAYMENTS
        // -------------------------------------------------

        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByMembership(
                                tenant.getTenantId(),
                                memberMembershipId
                        );


        // -------------------------------------------------
        // MAP RESPONSE
        // -------------------------------------------------

        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership payments loaded successfully.",
                        responses
                )
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
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PAYMENTS
        // -------------------------------------------------

        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByMember(
                                tenant.getTenantId(),
                                memberId
                        );


        // -------------------------------------------------
        // MAP RESPONSE
        // -------------------------------------------------

        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member membership payments loaded successfully.",
                        responses
                )
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
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // FIND PAYMENTS
        // -------------------------------------------------

        List<MembershipPayment> payments =
                membershipPaymentService
                        .findByBranch(
                                tenant.getTenantId(),
                                branchId
                        );


        // -------------------------------------------------
        // MAP RESPONSE
        // -------------------------------------------------

        List<MembershipPaymentResponse> responses =
                payments.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Branch membership payments loaded successfully.",
                        responses
                )
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
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VOID");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // DELETE
        // -------------------------------------------------

        membershipPaymentService.delete(
                tenant.getTenantId(),
                paymentId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership payment deleted successfully.",
                        null
                )
        );
    }


    // =====================================================
    // CANCEL / REVERSE PAYMENT
    // =====================================================

    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<
            ApiResponse<Void>>
    cancelPayment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer paymentId) {
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VOID");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CANCEL PAYMENT
        // -------------------------------------------------

        membershipPaymentService.cancelPayment(
                tenant.getTenantId(),
                paymentId
        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership payment cancelled successfully.",
                        null
                )
        );
    }


    // =====================================================
    // MEMBERSHIP PAYMENT SUMMARY
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}/summary")
    public ResponseEntity<
            ApiResponse<MembershipPaymentSummaryResponse>>
    getMembershipPaymentSummary(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberMembershipId) {
        permissionAuthorizationService.requirePermission("PAYMENTS_COLLECTION_VIEW");



        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // LOAD SUMMARY
        // -------------------------------------------------

        MembershipPaymentSummaryResponse response =
                membershipPaymentService
                        .getMembershipPaymentSummary(
                                tenant.getTenantId(),
                                memberMembershipId
                        );


        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Membership payment summary loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MembershipPaymentResponse mapToResponse(
            MembershipPayment payment) {

        MembershipPaymentResponse response =
                new MembershipPaymentResponse();


        // -------------------------------------------------
        // ID
        // -------------------------------------------------

        response.setMembershipPaymentId(
                payment.getMembershipPaymentId()
        );


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (payment.getTenant() != null) {

            response.setTenantId(
                    payment.getTenant().getTenantId()
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (payment.getBranch() != null) {

            response.setBranchId(
                    payment.getBranch().getBranchId()
            );

            response.setBranchName(
                    payment.getBranch().getBranchName()
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------

        if (payment.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    payment.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    payment.getMemberMembership()
                            .getMembershipNumber()
            );


            // -------------------------------------------------
            // MEMBER
            // -------------------------------------------------

            if (payment.getMemberMembership()
                    .getMember() != null) {

                response.setMemberId(
                        payment.getMemberMembership()
                                .getMember()
                                .getMemberId()
                );
            }
        }


        // -------------------------------------------------
        // RENEWAL
        // -------------------------------------------------

        if (payment.getMembershipRenewal() != null) {

            response.setMembershipRenewalId(
                    payment.getMembershipRenewal()
                            .getMembershipRenewalId()
            );
        }


        // -------------------------------------------------
        // BILLING CYCLE
        // -------------------------------------------------

        if (payment.getMembershipBillingCycle() != null) {

            response.setMembershipBillingCycleId(
                    payment.getMembershipBillingCycle()
                            .getMembershipBillingCycleId()
            );

            response.setCycleNumber(
                    payment.getMembershipBillingCycle()
                            .getCycleNumber()
            );
        }


        // -------------------------------------------------
        // PAYMENT
        // -------------------------------------------------

        response.setPaymentNumber(
                payment.getPaymentNumber()
        );

        response.setAmount(
                payment.getTotalAmount()
        );


        // -------------------------------------------------
        // PAYMENT METHOD
        // -------------------------------------------------

        /*
         * If there is exactly one payment detail,
         * expose its payment method for backward compatibility.
         *
         * For split payments, paymentMethod remains null
         * and paymentDetails contains the individual methods.
         */

        if (payment.getPaymentDetails() != null
                && payment.getPaymentDetails().size() == 1) {

            response.setPaymentMethod(
                    payment.getPaymentDetails()
                            .get(0)
                            .getPaymentMethod()
            );
        }


        // -------------------------------------------------
        // PAYMENT DATE
        // -------------------------------------------------

        if (payment.getPaymentDate() != null) {

            response.setPaymentDate(
                    payment.getPaymentDate()
                            .atStartOfDay()
            );
        }


        // -------------------------------------------------
        // REFERENCE
        // -------------------------------------------------

        response.setReferenceNumber(
                payment.getReferenceNumber()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                payment.getNotes()
        );


        // -------------------------------------------------
        // PAYMENT DETAILS
        // -------------------------------------------------

        if (payment.getPaymentDetails() != null) {

            List<MembershipPaymentDetailResponse>
                    details =
                    payment.getPaymentDetails()
                            .stream()
                            .map(this::mapDetailToResponse)
                            .collect(Collectors.toList());

            response.setPaymentDetails(
                    details
            );
        }


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        response.setUpdatedAt(
                payment.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // PAYMENT DETAIL → RESPONSE
    // =====================================================

    private MembershipPaymentDetailResponse
    mapDetailToResponse(
            MembershipPaymentDetail detail) {

        MembershipPaymentDetailResponse response =
                new MembershipPaymentDetailResponse();


        // -------------------------------------------------
        // ID
        // -------------------------------------------------

        response.setMembershipPaymentDetailId(
                detail.getMembershipPaymentDetailId()
        );


        // -------------------------------------------------
        // METHOD
        // -------------------------------------------------

        response.setPaymentMethod(
                detail.getPaymentMethod()
        );


        // -------------------------------------------------
        // AMOUNT
        // -------------------------------------------------

        response.setAmount(
                detail.getAmount()
        );


        // -------------------------------------------------
        // REFERENCE
        // -------------------------------------------------

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

        if (device.getTenant() == null ||
                device.getTenant().getTenantId()
                        != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Device does not belong to this tenant."
            );
        }


        return tenant;
    }
}