package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MemberFitnessAssessmentRequest;
import com.arafath65.gymManagement.dto.response.MemberFitnessAssessmentResponse;
import com.arafath65.gymManagement.entity.RegisteredDevice;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.RegisteredDeviceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberFitnessAssessmentService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import tools.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/members/{memberId}/fitness")
public class MemberFitnessAssessmentController {


    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final MemberFitnessAssessmentService
            fitnessService;

    private final TenantRepository
            tenantRepository;

    private final RegisteredDeviceRepository
            registeredDeviceRepository;

    private final ObjectMapper objectMapper;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberFitnessAssessmentController(

            MemberFitnessAssessmentService fitnessService,

            TenantRepository tenantRepository,

            RegisteredDeviceRepository registeredDeviceRepository,

            ObjectMapper objectMapper) {

        this.fitnessService =
                fitnessService;

        this.tenantRepository =
                tenantRepository;

        this.registeredDeviceRepository =
                registeredDeviceRepository;

        this.objectMapper =
                objectMapper;
    }


    // =====================================================
    // CREATE FITNESS ASSESSMENT
    // NORMAL JSON VERSION
    // =====================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    createAssessment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @Valid
            @RequestBody
            MemberFitnessAssessmentRequest request) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        var assessment =
                fitnessService.createAssessment(
                        tenant,
                        memberId,
                        request
                );


        List<MemberFitnessAssessmentResponse>
                assessments =
                fitnessService.getMemberAssessments(
                        tenant,
                        memberId
                );


        MemberFitnessAssessmentResponse
                createdAssessment =
                assessments.stream()
                        .filter(item ->
                                item.getFitnessId()
                                        .equals(
                                                assessment
                                                        .getFitnessId()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment created but response could not be generated."
                                )
                        );


        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment created successfully.",
                        createdAssessment
                );


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // CREATE FITNESS ASSESSMENT
    // MULTIPART + IMAGES
    // =====================================================

    @PostMapping(
            value = "",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    createAssessmentWithImages(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @RequestPart("assessment")
            String assessmentJson,

            @RequestPart(
                    value = "frontImage",
                    required = false
            )
            MultipartFile frontImage,

            @RequestPart(
                    value = "sideImage",
                    required = false
            )
            MultipartFile sideImage,

            @RequestPart(
                    value = "backImage",
                    required = false
            )
            MultipartFile backImage)
            throws IOException {


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CONVERT JSON STRING → REQUEST DTO
        // -------------------------------------------------

        MemberFitnessAssessmentRequest request;

        try {

            request =
                    objectMapper.readValue(
                            assessmentJson,
                            MemberFitnessAssessmentRequest.class
                    );

        } catch (Exception e) {

            throw new BadRequestException(
                    "Invalid fitness assessment data."
            );
        }


        // -------------------------------------------------
        // CREATE WITH IMAGES
        // -------------------------------------------------

        MemberFitnessAssessmentResponse created =
                fitnessService.createAssessmentWithImages(
                        tenant,
                        memberId,
                        request,
                        frontImage,
                        sideImage,
                        backImage
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment created successfully.",
                        created
                );


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET FITNESS HISTORY
    // =====================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    List<MemberFitnessAssessmentResponse>>>
    getAssessments(

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


        List<MemberFitnessAssessmentResponse>
                assessments =
                fitnessService.getMemberAssessments(
                        tenant,
                        memberId
                );


        ApiResponse<
                List<MemberFitnessAssessmentResponse>>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessments loaded successfully.",
                        assessments
                );


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // GET LATEST FITNESS ASSESSMENT
    // =====================================================

    @GetMapping("/latest")
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    getLatestAssessment(

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


        MemberFitnessAssessmentResponse
                assessment =
                fitnessService.getLatestAssessment(
                        tenant,
                        memberId
                );


        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Latest fitness assessment loaded successfully.",
                        assessment
                );


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // GET FITNESS ASSESSMENT BY ID
    // =====================================================

    @GetMapping("/{fitnessId}")
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    getAssessmentById(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @PathVariable
            Integer fitnessId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MemberFitnessAssessmentResponse
                assessment =
                fitnessService.getAssessmentById(
                        tenant,
                        memberId,
                        fitnessId
                );


        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment loaded successfully.",
                        assessment
                );


        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
// GET FITNESS IMAGE
// =====================================================

    @GetMapping("/{fitnessId}/image/{imageType}")
    public ResponseEntity<Resource> getFitnessImage(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @PathVariable
            Integer fitnessId,

            @PathVariable
            String imageType)

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
        // GET ASSESSMENT
        // =================================================

        MemberFitnessAssessmentResponse assessment =
                fitnessService.getAssessmentById(
                        tenant,
                        memberId,
                        fitnessId
                );


        // =================================================
        // SELECT IMAGE PATH
        // =================================================

        String imagePath;


        switch (
                imageType.toLowerCase()
        ) {

            case "front":

                imagePath =
                        assessment.getFrontImage();

                break;


            case "side":

                imagePath =
                        assessment.getSideImage();

                break;


            case "back":

                imagePath =
                        assessment.getBackImage();

                break;


            default:

                throw new BadRequestException(
                        "Invalid fitness image type."
                );
        }


        // =================================================
        // IMAGE NOT FOUND IN DATABASE
        // =================================================

        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            throw new BadRequestException(
                    "Fitness image not found."
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
                    "Fitness image file not found."
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


        return ResponseEntity.ok()
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
    // UPDATE FITNESS ASSESSMENT
    // NORMAL JSON VERSION
    // =====================================================

    @PutMapping("/{fitnessId}")
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    updateAssessment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @PathVariable
            Integer fitnessId,

            @Valid
            @RequestBody
            MemberFitnessAssessmentRequest request) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        MemberFitnessAssessmentResponse updated =
                fitnessService.updateAssessment(
                        tenant,
                        memberId,
                        fitnessId,
                        request
                );


        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment updated successfully.",
                        updated
                );


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // UPDATE FITNESS ASSESSMENT
    // MULTIPART + IMAGES
    // =====================================================

    @PutMapping(
            value = "/{fitnessId}",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<
            ApiResponse<MemberFitnessAssessmentResponse>>
    updateAssessmentWithImages(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @PathVariable
            Integer fitnessId,

            @RequestPart("assessment")
            String assessmentJson,

            @RequestPart(
                    value = "frontImage",
                    required = false
            )
            MultipartFile frontImage,

            @RequestPart(
                    value = "sideImage",
                    required = false
            )
            MultipartFile sideImage,

            @RequestPart(
                    value = "backImage",
                    required = false
            )
            MultipartFile backImage,

            @RequestPart(
                    value = "replaceFront",
                    required = false
            )
            String replaceFront,

            @RequestPart(
                    value = "replaceSide",
                    required = false
            )
            String replaceSide,

            @RequestPart(
                    value = "replaceBack",
                    required = false
            )
            String replaceBack)
            throws IOException {


        // -------------------------------------------------
        // VALIDATE TENANT + DEVICE
        // -------------------------------------------------

        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        // -------------------------------------------------
        // CONVERT JSON STRING → REQUEST DTO
        // -------------------------------------------------

        MemberFitnessAssessmentRequest request;

        try {

            request =
                    objectMapper.readValue(
                            assessmentJson,
                            MemberFitnessAssessmentRequest.class
                    );

        } catch (Exception e) {

            throw new BadRequestException(
                    "Invalid fitness assessment data."
            );
        }


        // -------------------------------------------------
        // UPDATE
        // -------------------------------------------------

        MemberFitnessAssessmentResponse updated =
                fitnessService.updateAssessmentWithImages(
                        tenant,
                        memberId,
                        fitnessId,
                        request,
                        frontImage,
                        sideImage,
                        backImage,
                        Boolean.parseBoolean(
                                replaceFront
                        ),
                        Boolean.parseBoolean(
                                replaceSide
                        ),
                        Boolean.parseBoolean(
                                replaceBack
                        )
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        ApiResponse<
                MemberFitnessAssessmentResponse>
                response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment updated successfully.",
                        updated
                );


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // DELETE FITNESS ASSESSMENT
    // =====================================================

    @DeleteMapping("/{fitnessId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deleteAssessment(

            @RequestHeader("X-Tenant-No")
            String tenantNo,

            @RequestHeader("X-Device-Token")
            String deviceToken,

            @PathVariable
            Integer memberId,

            @PathVariable
            Integer fitnessId) {


        Tenant tenant =
                validateTenantAndDevice(
                        tenantNo,
                        deviceToken
                );


        fitnessService.deleteAssessment(
                tenant,
                memberId,
                fitnessId
        );


        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Fitness assessment deleted successfully.",
                        null
                );


        return ResponseEntity.ok(
                response
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
                        .findByTenantNo(tenantNo)
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
