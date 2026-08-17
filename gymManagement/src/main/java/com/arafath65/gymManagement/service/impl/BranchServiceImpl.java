package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.BranchCreateRequest;
import com.arafath65.gymManagement.dto.response.BranchResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.exception.DuplicateResourceException;
import com.arafath65.gymManagement.exception.ResourceNotFoundException;
import com.arafath65.gymManagement.mapper.BranchMapper;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.BranchService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl
        implements BranchService {

    private final BranchRepository branchRepository;

    private final TenantRepository tenantRepository;


    public BranchServiceImpl(
            BranchRepository branchRepository,
            TenantRepository tenantRepository) {

        this.branchRepository =
                branchRepository;

        this.tenantRepository =
                tenantRepository;
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public BranchResponse save(
            BranchCreateRequest request) {

        // -------------------------------------------------
        // FIND TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository
                        .findById(
                                request.getTenantId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found."
                                )
                        );


        // -------------------------------------------------
        // CHECK TENANT STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // -------------------------------------------------
        // CHECK DUPLICATE BRANCH NO
        // -------------------------------------------------

        if (branchRepository
                .findByTenant_TenantIdAndBranchNo(
                        request.getTenantId(),
                        request.getBranchNo()
                )
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Branch number already exists for this tenant."
            );
        }


        // -------------------------------------------------
        // CREATE ENTITY
        // -------------------------------------------------

        Branch branch =
                BranchMapper.toEntity(
                        request
                );

        branch.setTenant(
                tenant
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        Branch savedBranch =
                branchRepository.save(
                        branch
                );


        return BranchMapper.toResponse(
                savedBranch
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public BranchResponse update(
            Integer branchId,
            BranchCreateRequest request) {

        Branch branch =
                branchRepository
                        .findById(branchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found."
                                )
                        );


        Tenant tenant =
                tenantRepository
                        .findById(
                                request.getTenantId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found."
                                )
                        );


        // -------------------------------------------------
        // DUPLICATE BRANCH NUMBER
        // -------------------------------------------------

        branchRepository
                .findByTenant_TenantIdAndBranchNo(
                        request.getTenantId(),
                        request.getBranchNo()
                )
                .ifPresent(existing -> {

                    if (!existing
                            .getBranchId()
                            .equals(branchId)) {

                        throw new DuplicateResourceException(
                                "Branch number already exists for this tenant."
                        );
                    }
                });


        // -------------------------------------------------
        // UPDATE FIELDS
        // -------------------------------------------------

        branch.setTenant(
                tenant
        );

        branch.setBranchNo(
                request.getBranchNo()
        );

        branch.setBranchName(
                request.getBranchName()
        );

        branch.setAddress(
                request.getAddress()
        );

        branch.setContactNo(
                request.getContactNo()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        if (request.getStatus() != null) {

            branch.setStatus(
                    request.getStatus()
            );
        }


        Branch updatedBranch =
                branchRepository.save(
                        branch
                );


        return BranchMapper.toResponse(
                updatedBranch
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Override
    public BranchResponse findById(
            Integer branchId) {

        Branch branch =
                branchRepository
                        .findById(branchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found."
                                )
                        );


        return BranchMapper.toResponse(
                branch
        );
    }


    // =====================================================
    // FIND ALL
    // =====================================================

    @Override
    public List<BranchResponse> findAll() {

        return branchRepository
                .findAll()
                .stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }


    // =====================================================
    // FIND BY TENANT
    // =====================================================

    @Override
    public List<BranchResponse> findByTenant(
            Integer tenantId) {

        return branchRepository
                .findByTenant_TenantIdAndStatusTrue(
                        tenantId
                )
                .stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }


    // =====================================================
    // DELETE / DEACTIVATE
    // =====================================================

    @Override
    public void delete(
            Integer branchId) {

        Branch branch =
                branchRepository
                        .findById(branchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found."
                                )
                        );


        // -------------------------------------------------
        // SOFT DELETE
        // -------------------------------------------------

        branch.setStatus(
                false
        );

        branchRepository.save(
                branch
        );
    }
}