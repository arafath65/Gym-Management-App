package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.BranchCreateRequest;
import com.arafath65.gymManagement.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {

    BranchResponse save(BranchCreateRequest request);

    BranchResponse update(Integer branchId,
                          BranchCreateRequest request);

    BranchResponse findById(Integer branchId);

    List<BranchResponse> findAll();

    List<BranchResponse> findByTenant(Integer tenantId);

    void delete(Integer branchId);

}