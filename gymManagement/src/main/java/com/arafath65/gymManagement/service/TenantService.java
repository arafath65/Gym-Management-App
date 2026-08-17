package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.TenantCreateRequest;
import com.arafath65.gymManagement.dto.response.TenantResponse;
import com.arafath65.gymManagement.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {

    TenantResponse save(TenantCreateRequest request);

    TenantResponse update(Integer id, TenantCreateRequest request);

    TenantResponse findById(Integer id);

    TenantResponse findByTenantNo(String tenantNo);

    List<TenantResponse> findAll();

    void delete(Integer id);

}