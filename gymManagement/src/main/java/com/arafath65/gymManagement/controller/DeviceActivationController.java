package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.DeviceActivationRequest;
import com.arafath65.gymManagement.dto.response.DeviceActivationResponse;
import com.arafath65.gymManagement.service.DeviceActivationService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceActivationController {

    private final DeviceActivationService deviceActivationService;

    public DeviceActivationController(
            DeviceActivationService deviceActivationService) {

        this.deviceActivationService = deviceActivationService;
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<DeviceActivationResponse>> activateDevice(
            @Valid @RequestBody DeviceActivationRequest request) {

        DeviceActivationResponse response =
                deviceActivationService.activate(request);

        ApiResponse<DeviceActivationResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Device activated successfully.",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }
}