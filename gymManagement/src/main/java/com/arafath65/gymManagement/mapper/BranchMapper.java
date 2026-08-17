package com.arafath65.gymManagement.mapper;

import com.arafath65.gymManagement.dto.request.BranchCreateRequest;
import com.arafath65.gymManagement.dto.response.BranchResponse;
import com.arafath65.gymManagement.entity.Branch;

public class BranchMapper {

    /**
     * Request DTO -> Entity
     */
    public static Branch toEntity(BranchCreateRequest request) {

        Branch branch = new Branch();

        branch.setBranchNo(request.getBranchNo());
        branch.setBranchName(request.getBranchName());
        branch.setAddress(request.getAddress());
        branch.setContactNo(request.getContactNo());
        branch.setStatus(request.getStatus());

        return branch;

    }

    /**
     * Entity -> Response DTO
     */
    public static BranchResponse toResponse(Branch branch) {

        BranchResponse response = new BranchResponse();

        response.setBranchId(branch.getBranchId());

        response.setTenantId(branch.getTenant().getTenantId());
        response.setTenantNo(branch.getTenant().getTenantNo());
        response.setGymName(branch.getTenant().getGymName());

        response.setBranchNo(branch.getBranchNo());
        response.setBranchName(branch.getBranchName());
        response.setAddress(branch.getAddress());
        response.setContactNo(branch.getContactNo());
        response.setStatus(branch.getStatus());

        response.setCreatedAt(branch.getCreatedAt());

        return response;

    }

}