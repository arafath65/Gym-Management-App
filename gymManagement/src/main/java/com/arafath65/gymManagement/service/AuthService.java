package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.LoginRequest;
import com.arafath65.gymManagement.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}