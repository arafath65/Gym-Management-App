package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.LoginRequest;
import com.arafath65.gymManagement.dto.response.LoginResponse;
import com.arafath65.gymManagement.service.AuthService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {

        this.authService = authService;
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authService.login(request);

        ApiResponse<LoginResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Login successful.",
                        response
                );

        return ResponseEntity.ok(
                apiResponse
        );
    }
}