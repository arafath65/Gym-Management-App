package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.ActivationCodeCreateRequest;
import com.arafath65.gymManagement.dto.response.ActivationCodeResponse;
import com.arafath65.gymManagement.entity.ActivationCode;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.exception.ResourceNotFoundException;
import com.arafath65.gymManagement.mapper.ActivationCodeMapper;
import com.arafath65.gymManagement.repository.ActivationCodeRepository;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.ActivationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;
    private final TenantRepository tenantRepository;
    private final BranchRepository branchRepository;

    public ActivationCodeServiceImpl(
            ActivationCodeRepository activationCodeRepository,
            TenantRepository tenantRepository,
            BranchRepository branchRepository) {

        this.activationCodeRepository = activationCodeRepository;
        this.tenantRepository = tenantRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public ActivationCodeResponse generate(
            ActivationCodeCreateRequest request) {

        // 1. Check Tenant
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant not found."));

        // 2. Check Branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Branch not found."));

        // 3. Make sure Branch belongs to Tenant
        if (branch.getTenant().getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Branch does not belong to the selected tenant.");
        }

        // 4. Generate unique activation code
        String code;

        do {

            code = generateActivationCode();

        } while (activationCodeRepository
                .findByActivationCode(code)
                .isPresent());

        // 5. Convert request to entity
        ActivationCode activationCode =
                ActivationCodeMapper.toEntity(request);

        // 6. Set Tenant and Branch
        activationCode.setTenant(tenant);
        activationCode.setBranch(branch);

        // 7. Set generated activation code
        activationCode.setActivationCode(code);

        // 8. Expire after 15 minutes
        activationCode.setExpiresAt(
                LocalDateTime.now().plusMinutes(15));

        // 9. Initially unused
        activationCode.setUsed(false);

        // 10. No used time yet
        activationCode.setUsedAt(null);

        // 11. Active
        activationCode.setStatus(true);

        // 12. Save
        ActivationCode saved =
                activationCodeRepository.save(activationCode);

        // 13. Return response
        return ActivationCodeMapper.toResponse(saved);
    }

    @Override
    public ActivationCodeResponse findById(
            Integer activationId) {

        ActivationCode activationCode =
                activationCodeRepository.findById(activationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Activation code not found."));

        return ActivationCodeMapper.toResponse(
                activationCode);
    }

    @Override
    public ActivationCodeResponse findByCode(
            String activationCode) {

        ActivationCode code =
                activationCodeRepository
                        .findByActivationCode(activationCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Activation code not found."));

        return ActivationCodeMapper.toResponse(code);
    }

    @Override
    public List<ActivationCodeResponse> findAll() {

        return activationCodeRepository.findAll()
                .stream()
                .map(ActivationCodeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generates a code such as:
     *
     * FF-7A3K-92LM
     */
    private String generateActivationCode() {

        String uuid =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .toUpperCase();

        return "FF-"
                + uuid.substring(0, 4)
                + "-"
                + uuid.substring(4, 8)
                + "-"
                + uuid.substring(8, 12);
    }
}