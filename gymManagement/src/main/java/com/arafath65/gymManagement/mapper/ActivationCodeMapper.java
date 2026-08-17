package com.arafath65.gymManagement.mapper;

import com.arafath65.gymManagement.dto.request.ActivationCodeCreateRequest;
import com.arafath65.gymManagement.dto.response.ActivationCodeResponse;
import com.arafath65.gymManagement.entity.ActivationCode;

public class ActivationCodeMapper {

    /**
     * Convert Request DTO -> Entity
     *
     * Tenant and Branch are NOT assigned here.
     * They will be loaded by the Service.
     */
    public static ActivationCode toEntity(
            ActivationCodeCreateRequest request) {

        ActivationCode activationCode = new ActivationCode();

        activationCode.setDeviceType(request.getDeviceType());
        activationCode.setGeneratedBy(request.getGeneratedBy());
        activationCode.setRemarks(request.getRemarks());

        return activationCode;
    }

    /**
     * Convert Entity -> Response DTO
     */
    public static ActivationCodeResponse toResponse(
            ActivationCode activationCode) {

        ActivationCodeResponse response =
                new ActivationCodeResponse();

        response.setActivationId(
                activationCode.getActivationId());

        response.setActivationCode(
                activationCode.getActivationCode());

        response.setTenantId(
                activationCode.getTenant().getTenantId());

        response.setBranchId(
                activationCode.getBranch().getBranchId());

        response.setDeviceType(
                activationCode.getDeviceType());

        response.setExpiresAt(
                activationCode.getExpiresAt());

        response.setUsed(
                activationCode.getUsed());

        response.setUsedAt(
                activationCode.getUsedAt());

        response.setGeneratedBy(
                activationCode.getGeneratedBy());

        response.setRemarks(
                activationCode.getRemarks());

        response.setStatus(
                activationCode.getStatus());

        return response;
    }
}