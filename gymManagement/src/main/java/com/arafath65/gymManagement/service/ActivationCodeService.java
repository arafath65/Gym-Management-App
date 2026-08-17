package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.ActivationCodeCreateRequest;
import com.arafath65.gymManagement.dto.response.ActivationCodeResponse;

import java.util.List;

public interface ActivationCodeService {

    ActivationCodeResponse generate(
            ActivationCodeCreateRequest request
    );

    ActivationCodeResponse findById(
            Integer activationId
    );

    ActivationCodeResponse findByCode(
            String activationCode
    );

    List<ActivationCodeResponse> findAll();

}