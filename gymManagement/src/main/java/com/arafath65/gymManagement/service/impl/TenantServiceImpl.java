package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.TenantCreateRequest;
import com.arafath65.gymManagement.dto.response.TenantResponse;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.DuplicateResourceException;
import com.arafath65.gymManagement.exception.ResourceNotFoundException;
import com.arafath65.gymManagement.mapper.TenantMapper;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.TenantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repository;

    public TenantServiceImpl(TenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public TenantResponse save(TenantCreateRequest request) {

        // Check duplicate tenant number
        if (repository.findByTenantNo(request.getTenantNo()).isPresent()) {
            throw new DuplicateResourceException("Tenant Number already exists.");
        }

        Tenant tenant = TenantMapper.toEntity(request);

        Tenant savedTenant = repository.save(tenant);

        return TenantMapper.toResponse(savedTenant);

    }

    @Override
    public TenantResponse update(Integer tenantId, TenantCreateRequest request) {

        Tenant tenant = repository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setTenantNo(request.getTenantNo());
        tenant.setGymName(request.getGymName());
        tenant.setOwnerName(request.getOwnerName());
        tenant.setEmail(request.getEmail());
        tenant.setMobile(request.getMobile());
        tenant.setAddress(request.getAddress());
        tenant.setLogo(request.getLogo());
        tenant.setStatus(request.getStatus());

        Tenant updatedTenant = repository.save(tenant);

        return TenantMapper.toResponse(updatedTenant);

    }

    @Override
    public TenantResponse findById(Integer tenantId) {

        Tenant tenant = repository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return TenantMapper.toResponse(tenant);

    }

    @Override
    public TenantResponse findByTenantNo(String tenantNo) {

        Tenant tenant = repository.findByTenantNo(tenantNo)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return TenantMapper.toResponse(tenant);

    }

    @Override
    public List<TenantResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(TenantMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Override
    public void delete(Integer tenantId) {

        repository.deleteById(tenantId);

    }

}