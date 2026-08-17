package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.UserCreateRequest;
import com.arafath65.gymManagement.dto.response.UserResponse;

public interface UserService {

    UserResponse save(UserCreateRequest request);

}