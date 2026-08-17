package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.UserCreateRequest;
import com.arafath65.gymManagement.dto.response.UserResponse;
import com.arafath65.gymManagement.service.UserService;
import com.arafath65.gymManagement.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        UserResponse response =
                userService.save(request);

        ApiResponse<UserResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "User created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
}