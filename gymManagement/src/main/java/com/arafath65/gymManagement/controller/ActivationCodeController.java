package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.ActivationCodeCreateRequest;
import com.arafath65.gymManagement.dto.response.ActivationCodeResponse;
import com.arafath65.gymManagement.service.ActivationCodeService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activation-codes")
public class ActivationCodeController {

    private final ActivationCodeService activationCodeService;

    public ActivationCodeController(
            ActivationCodeService activationCodeService) {

        this.activationCodeService = activationCodeService;
    }

    // Generate a new activation code
    @PostMapping
    public ResponseEntity<ApiResponse<ActivationCodeResponse>> generate(
            @Valid @RequestBody ActivationCodeCreateRequest request) {

        ActivationCodeResponse response =
                activationCodeService.generate(request);

        ApiResponse<ActivationCodeResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Activation code generated successfully.",
                        response
                );

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.CREATED
        );
    }

    // Find activation code by ID
    @GetMapping("/{activationId}")
    public ResponseEntity<ApiResponse<ActivationCodeResponse>> findById(
            @PathVariable Integer activationId) {

        ActivationCodeResponse response =
                activationCodeService.findById(activationId);

        ApiResponse<ActivationCodeResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Activation code found.",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // Find activation code by code
    @GetMapping("/code/{activationCode}")
    public ResponseEntity<ApiResponse<ActivationCodeResponse>> findByCode(
            @PathVariable String activationCode) {

        ActivationCodeResponse response =
                activationCodeService.findByCode(activationCode);

        ApiResponse<ActivationCodeResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Activation code found.",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // Get all activation codes
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivationCodeResponse>>> findAll() {

        List<ActivationCodeResponse> response =
                activationCodeService.findAll();

        ApiResponse<List<ActivationCodeResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        "Activation codes retrieved successfully.",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }
}