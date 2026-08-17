package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MemberRequest;
import com.arafath65.gymManagement.dto.response.MemberResponse;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;


@RestController
@RequestMapping("/api/members")
public class MemberController {


    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MemberService memberService;

    private final TenantRepository tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberController(

            MemberService memberService,

            TenantRepository tenantRepository,

            RegisteredDeviceRepository registeredDeviceRepository) {

        this.memberService =
                memberService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;
    }


    // =====================================================
    // CREATE MEMBER
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MemberResponse>>
    createMember(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @Valid
            @RequestBody
            MemberRequest request) {


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CREATE MEMBER
        // -------------------------------------------------

        Member member =
                memberService.createMember(
                        tenant,
                        request
                );


        // -------------------------------------------------
        // GET CREATED MEMBER RESPONSE
        // -------------------------------------------------

        List<MemberResponse> members =
                memberService.getMembers(
                        tenant,
                        member.getBranch()
                                .getBranchId()
                );


        MemberResponse createdMember =
                members.stream()
                        .filter(item ->
                                item.getMemberId()
                                        .equals(
                                                member.getMemberId()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Member created but response could not be generated."
                                )
                        );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<MemberResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Member created successfully.",
                        createdMember
                );


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET MEMBERS
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MemberResponse>>>
    getMembers(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @RequestParam(required = false)
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
        // GET MEMBERS
        // -------------------------------------------------

        List<MemberResponse> members =
                memberService.getMembers(
                        tenant,
                        branchId
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<List<MemberResponse>>
                response =
                new ApiResponse<>(
                        true,
                        "Members loaded successfully.",
                        members
                );


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // GET MEMBER BY ID
    // =====================================================

    @GetMapping("/{memberId}")
    public ResponseEntity<
            ApiResponse<MemberResponse>>
    getMemberById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId) {


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // GET MEMBER
        // -------------------------------------------------

        MemberResponse member =
                memberService.getMemberById(
                        tenant,
                        memberId
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<MemberResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Member loaded successfully.",
                        member
                );


        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
// UPDATE MEMBER
// =====================================================

    @PutMapping("/{memberId}")
    public ResponseEntity<
            ApiResponse<MemberResponse>>
    updateMember(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @Valid
            @RequestBody
            MemberRequest request) {


        // =================================================
        // VALIDATE TENANT + DEVICE
        // =================================================

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // =================================================
        // UPDATE MEMBER
        // =================================================

        Member updatedMember =
                memberService.updateMember(
                        tenant,
                        memberId,
                        request
                );


        // =================================================
        // GET UPDATED RESPONSE
        // =================================================

        MemberResponse memberResponse =
                memberService.getMemberById(
                        tenant,
                        updatedMember.getMemberId()
                );


        // =================================================
        // RESPONSE
        // =================================================

        ApiResponse<MemberResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Member updated successfully.",
                        memberResponse
                );


        return ResponseEntity.ok(
                response
        );
    }

    // UPDATE MEMBER IMAGE
    @PutMapping(
            value = "/{memberId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<
            ApiResponse<MemberResponse>>
    updateMemberImage(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @RequestPart("image")
            MultipartFile image)

            throws IOException {

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );

        Member updatedMember =
                memberService.updateMemberImage(
                        tenant,
                        memberId,
                        image
                );

        MemberResponse memberResponse =
                memberService.getMemberById(
                        tenant,
                        updatedMember.getMemberId()
                );

        ApiResponse<MemberResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Member image updated successfully.",
                        memberResponse
                );

        return ResponseEntity.ok(
                response
        );
    }

    // DEACTIVATE MEMBER
    @PutMapping("/{memberId}/deactivate")
    public ResponseEntity<
            ApiResponse<Void>>
    deactivateMember(

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

        memberService.deactivateMember(
                tenant,
                memberId
        );

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Member deactivated successfully.",
                        null
                );

        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // GET MEMBER PROFILE IMAGE
    // =====================================================

    @GetMapping("/{memberId}/image")
    public ResponseEntity<Resource> getMemberImage(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId)

            throws IOException {


        // =================================================
        // VALIDATE TENANT + DEVICE
        // =================================================

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // =================================================
        // GET MEMBER
        // =================================================

        MemberResponse member =
                memberService.getMemberById(
                        tenant,
                        memberId
                );


        // =================================================
        // IMAGE PATH
        // =================================================

        String imagePath =
                member.getProfileImage();


        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            throw new BadRequestException(
                    "Member profile image not found."
            );
        }


        // =================================================
        // FILE
        // =================================================

        Path path =
                Paths.get(
                        imagePath
                );


        if (!Files.exists(path) ||
                !Files.isRegularFile(path)) {

            throw new BadRequestException(
                    "Member profile image file not found."
            );
        }


        // =================================================
        // CONTENT TYPE
        // =================================================

        String contentType =
                Files.probeContentType(
                        path
                );


        if (contentType == null) {

            contentType =
                    "image/jpeg";
        }


        // =================================================
        // RESOURCE
        // =================================================

        InputStream inputStream =
                Files.newInputStream(
                        path
                );


        Resource resource =
                new InputStreamResource(
                        inputStream
                );


        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .body(
                        resource
                );
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
                tenant.getStatus())) {

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
                device.getStatus())) {

            throw new BadRequestException(
                    "Device is inactive."
            );
        }


        // -------------------------------------------------
        // DEVICE → TENANT
        // -------------------------------------------------

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