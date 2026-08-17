package com.arafath65.gymManagement.mapper;

import com.arafath65.gymManagement.dto.request.TenantCreateRequest;
import com.arafath65.gymManagement.dto.response.TenantResponse;
import com.arafath65.gymManagement.entity.Tenant;

public class TenantMapper {

    /**
     * Request DTO -> Entity
     */
    public static Tenant toEntity(TenantCreateRequest request) {

        Tenant tenant = new Tenant();

        tenant.setTenantNo(request.getTenantNo());
        tenant.setGymName(request.getGymName());
        tenant.setOwnerName(request.getOwnerName());
        tenant.setEmail(request.getEmail());
        tenant.setMobile(request.getMobile());
        tenant.setAddress(request.getAddress());
        tenant.setLogo(request.getLogo());
        tenant.setStatus(request.getStatus());

        return tenant;

    }

    /**
     * Entity -> Response DTO
     */
    public static TenantResponse toResponse(Tenant tenant) {

        TenantResponse response = new TenantResponse();

        response.setTenantId(tenant.getTenantId());
        response.setTenantNo(tenant.getTenantNo());
        response.setGymName(tenant.getGymName());
        response.setOwnerName(tenant.getOwnerName());
        response.setEmail(tenant.getEmail());
        response.setMobile(tenant.getMobile());
        response.setAddress(tenant.getAddress());
        response.setLogo(tenant.getLogo());
        response.setStatus(tenant.getStatus());

        response.setCreatedAt(tenant.getCreatedAt());
        response.setUpdatedAt(tenant.getUpdatedAt());

        return response;

    }

}