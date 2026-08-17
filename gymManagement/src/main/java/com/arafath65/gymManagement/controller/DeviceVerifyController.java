package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.DeviceVerifyRequest;
import com.arafath65.gymManagement.dto.response.DeviceVerifyResponse;
import com.arafath65.gymManagement.service.DeviceVerifyService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceVerifyController {

    private final DeviceVerifyService deviceVerifyService;

    public DeviceVerifyController(
            DeviceVerifyService deviceVerifyService) {

        this.deviceVerifyService = deviceVerifyService;
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<DeviceVerifyResponse>> verifyDevice(
            @Valid @RequestBody DeviceVerifyRequest request) {

        DeviceVerifyResponse response =
                deviceVerifyService.verify(request);

        ApiResponse<DeviceVerifyResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Device verified successfully.",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }
}