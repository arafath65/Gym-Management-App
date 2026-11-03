package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.BenefitBranchRequest;
import com.arafath65.gymManagement.dto.request.BenefitRequest;
import com.arafath65.gymManagement.dto.response.BenefitBranchResponse;
import com.arafath65.gymManagement.dto.response.BenefitResponse;
import com.arafath65.gymManagement.entity.Benefit;
import com.arafath65.gymManagement.entity.BenefitBranch;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.BenefitBranchRepository;
import com.arafath65.gymManagement.repository.BenefitRepository;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.BenefitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BenefitServiceImpl
        implements BenefitService {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final BenefitRepository benefitRepository;

    private final BenefitBranchRepository
            benefitBranchRepository;

    private final BranchRepository branchRepository;

    private final TenantRepository tenantRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitServiceImpl(
            BenefitRepository benefitRepository,
            BenefitBranchRepository benefitBranchRepository,
            BranchRepository branchRepository,
            TenantRepository tenantRepository) {

        this.benefitRepository =
                benefitRepository;

        this.benefitBranchRepository =
                benefitBranchRepository;

        this.branchRepository =
                branchRepository;

        this.tenantRepository =
                tenantRepository;
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public BenefitResponse createBenefit(
            Integer tenantId,
            BenefitRequest request) {

        validateTenantId(tenantId);

        validateRequest(request);

        String benefitCode =
                normalizeCode(
                        request.getBenefitCode()
                );

        if (benefitCode == null) {

            throw new BadRequestException(
                    "Benefit code is required."
            );
        }

        if (benefitRepository
                .existsByTenant_TenantIdAndBenefitCode(
                        tenantId,
                        benefitCode
                )) {

            throw new BadRequestException(
                    "Benefit code already exists."
            );
        }

        Tenant tenant =
                findTenant(
                        tenantId
                );

        String branchMode =
                normalizeBranchMode(
                        request.getBranchMode()
                );

        validateBranchMode(
                branchMode,
                request.getBranches()
        );

        Benefit benefit =
                new Benefit();

        benefit.setTenant(
                tenant
        );

        benefit.setBenefitCode(
                benefitCode
        );

        benefit.setBenefitName(
                normalizeName(
                        request.getBenefitName()
                )
        );

        benefit.setDescription(
                normalizeDescription(
                        request.getDescription()
                )
        );

        benefit.setBillingType(
                request.getBillingType()
        );

        benefit.setDefaultFee(
                normalizeFee(
                        request.getDefaultFee()
                )
        );

        benefit.setStatus(
                true
        );

        Benefit saved =
                benefitRepository.save(
                        benefit
                );

        createBranchAssignments(
                tenantId,
                saved,
                branchMode,
                request
        );

        return mapToResponse(
                saved,
                branchMode
        );
    }


    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> getAllBenefits(
            Integer tenantId) {

        validateTenantId(
                tenantId
        );

        return benefitRepository
                .findByTenant_TenantIdOrderByBenefitNameAsc(
                        tenantId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =====================================================
    // GET ACTIVE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> getActiveBenefits(
            Integer tenantId) {

        validateTenantId(
                tenantId
        );

        return benefitRepository
                .findByTenant_TenantIdAndStatusTrueOrderByBenefitNameAsc(
                        tenantId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =====================================================
    // GET ACTIVE BENEFITS BY BRANCH
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> getActiveBenefitsByBranch(
            Integer tenantId,
            Integer branchId) {

        // -------------------------------------------------
        // VALIDATE TENANT
        // -------------------------------------------------

        validateTenantId(
                tenantId
        );


        // -------------------------------------------------
        // VALIDATE BRANCH
        // -------------------------------------------------

        if (branchId == null
                || branchId <= 0) {

            throw new BadRequestException(
                    "Valid branch ID is required."
            );
        }


        // -------------------------------------------------
        // MAKE SURE BRANCH BELONGS TO TENANT
        // -------------------------------------------------

        Branch branch =
                findBranchForTenant(
                        tenantId,
                        branchId
                );


        // -------------------------------------------------
        // LOAD ACTIVE BENEFIT ASSIGNMENTS
        // -------------------------------------------------

        List<BenefitBranch> assignments =
                benefitBranchRepository
                        .findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueAndBenefit_StatusTrueOrderByBenefit_BenefitNameAsc(
                                tenantId,
                                branch.getBranchId()
                        );


        // -------------------------------------------------
        // MAP TO BENEFIT RESPONSE
        // -------------------------------------------------

        List<BenefitResponse> responses =
                new ArrayList<>();


        for (BenefitBranch assignment :
                assignments) {

            if (assignment == null
                    || assignment.getBenefit() == null) {

                continue;
            }


            Benefit benefit =
                    assignment.getBenefit();


            // -------------------------------------------------
            // EXTRA TENANT SAFETY CHECK
            // -------------------------------------------------

            if (benefit.getTenant() == null
                    || !tenantId.equals(
                    benefit.getTenant().getTenantId()
            )) {

                continue;
            }


            // -------------------------------------------------
            // MAP BENEFIT
            // -------------------------------------------------

            BenefitResponse response =
                    mapToResponse(
                            benefit
                    );


            // -------------------------------------------------
            // BRANCH-SPECIFIC INFORMATION
            // -------------------------------------------------

            /*
             * The normal BenefitResponse contains the
             * master benefit information.
             *
             * The branch assignment contains the actual
             * branch-specific fee.
             *
             * We keep the existing response structure
             * unchanged and expose the branch assignment
             * inside branchAssignments.
             */

            responses.add(
                    response
            );
        }


        return responses;
    }


    // =====================================================
    // GET ONE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public BenefitResponse getBenefit(
            Integer tenantId,
            Integer benefitId) {

        validateTenantId(
                tenantId
        );

        validateBenefitId(
                benefitId
        );

        Benefit benefit =
                findBenefit(
                        benefitId
                );

        validateTenantOwnership(
                benefit,
                tenantId
        );

        return mapToResponse(
                benefit
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public BenefitResponse updateBenefit(
            Integer tenantId,
            Integer benefitId,
            BenefitRequest request) {

        validateTenantId(
                tenantId
        );

        validateBenefitId(
                benefitId
        );

        validateRequest(
                request
        );

        Benefit benefit =
                findBenefit(
                        benefitId
                );

        validateTenantOwnership(
                benefit,
                tenantId
        );

        String newCode =
                normalizeCode(
                        request.getBenefitCode()
                );

        if (newCode == null) {

            throw new BadRequestException(
                    "Benefit code is required."
            );
        }

        boolean codeChanged =
                !newCode.equals(
                        benefit.getBenefitCode()
                );

        if (codeChanged
                && benefitRepository
                .existsByTenant_TenantIdAndBenefitCode(
                        tenantId,
                        newCode
                )) {

            throw new BadRequestException(
                    "Benefit code already exists."
            );
        }

        String branchMode =
                normalizeBranchMode(
                        request.getBranchMode()
                );

        validateBranchMode(
                branchMode,
                request.getBranches()
        );

        benefit.setBenefitCode(
                newCode
        );

        benefit.setBenefitName(
                normalizeName(
                        request.getBenefitName()
                )
        );

        benefit.setDescription(
                normalizeDescription(
                        request.getDescription()
                )
        );

        benefit.setBillingType(
                request.getBillingType()
        );

        benefit.setDefaultFee(
                normalizeFee(
                        request.getDefaultFee()
                )
        );

        Benefit updated =
                benefitRepository.save(
                        benefit
                );

        synchronizeBranchAssignments(
                tenantId,
                updated,
                branchMode,
                request
        );

        return mapToResponse(
                updated,
                branchMode
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @Override
    public void activateBenefit(
            Integer tenantId,
            Integer benefitId) {

        validateTenantId(
                tenantId
        );

        validateBenefitId(
                benefitId
        );

        Benefit benefit =
                findBenefit(
                        benefitId
                );

        validateTenantOwnership(
                benefit,
                tenantId
        );

        if (Boolean.TRUE.equals(
                benefit.getStatus()
        )) {

            throw new BadRequestException(
                    "Benefit is already active."
            );
        }

        benefit.setStatus(
                true
        );

        benefitRepository.save(
                benefit
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @Override
    public void deactivateBenefit(
            Integer tenantId,
            Integer benefitId) {

        validateTenantId(
                tenantId
        );

        validateBenefitId(
                benefitId
        );

        Benefit benefit =
                findBenefit(
                        benefitId
                );

        validateTenantOwnership(
                benefit,
                tenantId
        );

        if (Boolean.FALSE.equals(
                benefit.getStatus()
        )) {

            throw new BadRequestException(
                    "Benefit is already inactive."
            );
        }

        benefit.setStatus(
                false
        );

        benefitRepository.save(
                benefit
        );
    }


    // =====================================================
    // CREATE BRANCH ASSIGNMENTS
    // =====================================================

    private void createBranchAssignments(
            Integer tenantId,
            Benefit benefit,
            String branchMode,
            BenefitRequest request) {

        if ("ALL_BRANCHES".equals(
                branchMode
        )) {

            List<Branch> branches =
                    branchRepository
                            .findByTenant_TenantIdAndStatusTrue(
                                    tenantId
                            );

            for (Branch branch : branches) {

                BenefitBranch assignment =
                        new BenefitBranch();

                assignment.setTenant(
                        benefit.getTenant()
                );

                assignment.setBenefit(
                        benefit
                );

                assignment.setBranch(
                        branch
                );

                assignment.setFee(
                        null
                );

                assignment.setStatus(
                        true
                );

                benefitBranchRepository.save(
                        assignment
                );
            }

            return;
        }

        for (BenefitBranchRequest branchRequest :
                request.getBranches()) {

            Branch branch =
                    findBranchForTenant(
                            tenantId,
                            branchRequest.getBranchId()
                    );

            BenefitBranch assignment =
                    new BenefitBranch();

            assignment.setTenant(
                    benefit.getTenant()
            );

            assignment.setBenefit(
                    benefit
            );

            assignment.setBranch(
                    branch
            );

            assignment.setFee(
                    normalizeNullableFee(
                            branchRequest.getFee()
                    )
            );

            assignment.setStatus(
                    true
            );

            benefitBranchRepository.save(
                    assignment
            );
        }
    }


    // =====================================================
// SYNCHRONIZE BRANCH ASSIGNMENTS
// =====================================================

    private void synchronizeBranchAssignments(
            Integer tenantId,
            Benefit benefit,
            String branchMode,
            BenefitRequest request) {

        List<BenefitBranch> existing =
                benefitBranchRepository
                        .findByTenant_TenantIdAndBenefit_BenefitId(
                                tenantId,
                                benefit.getBenefitId()
                        );


        // =================================================
        // ALL BRANCHES
        // =================================================

        if ("ALL_BRANCHES".equals(
                branchMode
        )) {

            List<Branch> activeBranches =
                    branchRepository
                            .findByTenant_TenantIdAndStatusTrue(
                                    tenantId
                            );


            if (activeBranches.isEmpty()) {

                throw new BadRequestException(
                        "No active branches are available."
                );
            }


            Set<Integer> activeBranchIds =
                    new HashSet<>();


            for (Branch branch :
                    activeBranches) {

                activeBranchIds.add(
                        branch.getBranchId()
                );
            }


            List<BenefitBranch> toRemove =
                    existing.stream()
                            .filter(
                                    assignment ->
                                            assignment == null
                                                    || assignment.getBranch() == null
                                                    || !activeBranchIds.contains(
                                                    assignment.getBranch()
                                                            .getBranchId()
                                            )
                            )
                            .toList();


            if (!toRemove.isEmpty()) {

                benefitBranchRepository.deleteAll(
                        toRemove
                );

                existing.removeAll(
                        toRemove
                );
            }


            for (Branch branch :
                    activeBranches) {

                BenefitBranch assignment =
                        existing.stream()
                                .filter(
                                        item ->
                                                item != null
                                                        && item.getBranch() != null
                                                        && item.getBranch()
                                                        .getBranchId()
                                                        .equals(
                                                                branch.getBranchId()
                                                        )
                                )
                                .findFirst()
                                .orElse(null);


                if (assignment != null) {

                    assignment.setStatus(
                            true
                    );

                    assignment.setBenefit(
                            benefit
                    );

                    assignment.setTenant(
                            benefit.getTenant()
                    );

                    assignment.setBranch(
                            branch
                    );

                } else {

                    BenefitBranch newAssignment =
                            new BenefitBranch();

                    newAssignment.setTenant(
                            benefit.getTenant()
                    );

                    newAssignment.setBenefit(
                            benefit
                    );

                    newAssignment.setBranch(
                            branch
                    );

                    newAssignment.setFee(
                            null
                    );

                    newAssignment.setStatus(
                            true
                    );

                    benefitBranchRepository.save(
                            newAssignment
                    );
                }
            }

            return;
        }


        // =================================================
        // SELECTED BRANCHES
        // =================================================

        List<BenefitBranchRequest>
                requestedBranches =
                request.getBranches();


        if (requestedBranches == null
                || requestedBranches.isEmpty()) {

            throw new BadRequestException(
                    "At least one branch must be selected."
            );
        }


        Set<Integer> requestedBranchIds =
                new HashSet<>();


        for (BenefitBranchRequest branchRequest :
                requestedBranches) {

            if (branchRequest == null
                    || branchRequest.getBranchId() == null
                    || branchRequest.getBranchId() <= 0) {

                throw new BadRequestException(
                        "Valid branch ID is required."
                );
            }


            if (!requestedBranchIds.add(
                    branchRequest.getBranchId()
            )) {

                throw new BadRequestException(
                        "Duplicate branch assignment is not allowed."
                );
            }
        }


        List<BenefitBranch> toRemove =
                existing.stream()
                        .filter(
                                assignment ->
                                        assignment == null
                                                || assignment.getBranch() == null
                                                || !requestedBranchIds.contains(
                                                assignment.getBranch()
                                                        .getBranchId()
                                        )
                        )
                        .toList();


        if (!toRemove.isEmpty()) {

            benefitBranchRepository.deleteAll(
                    toRemove
            );

            existing.removeAll(
                    toRemove
            );
        }


        for (BenefitBranchRequest branchRequest :
                requestedBranches) {

            Integer branchId =
                    branchRequest.getBranchId();


            Branch branch =
                    findBranchForTenant(
                            tenantId,
                            branchId
                    );


            BenefitBranch assignment =
                    existing.stream()
                            .filter(
                                    item ->
                                            item != null
                                                    && item.getBranch() != null
                                                    && item.getBranch()
                                                    .getBranchId()
                                                    .equals(
                                                            branchId
                                                    )
                            )
                            .findFirst()
                            .orElse(null);


            if (assignment != null) {

                assignment.setTenant(
                        benefit.getTenant()
                );

                assignment.setBenefit(
                        benefit
                );

                assignment.setBranch(
                        branch
                );

                assignment.setFee(
                        normalizeNullableFee(
                                branchRequest.getFee()
                        )
                );

                assignment.setStatus(
                        true
                );


                benefitBranchRepository.save(
                        assignment
                );


            } else {

                BenefitBranch newAssignment =
                        new BenefitBranch();

                newAssignment.setTenant(
                        benefit.getTenant()
                );

                newAssignment.setBenefit(
                        benefit
                );

                newAssignment.setBranch(
                        branch
                );

                newAssignment.setFee(
                        normalizeNullableFee(
                                branchRequest.getFee()
                        )
                );

                newAssignment.setStatus(
                        true
                );


                benefitBranchRepository.save(
                        newAssignment
                );
            }
        }
    }


    // =====================================================
    // FIND TENANT
    // =====================================================

    private Tenant findTenant(
            Integer tenantId) {

        return tenantRepository
                .findById(
                        tenantId
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Tenant not found."
                        )
                );
    }


    // =====================================================
    // FIND BENEFIT
    // =====================================================

    private Benefit findBenefit(
            Integer benefitId) {

        return benefitRepository
                .findById(
                        benefitId
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Benefit not found."
                        )
                );
    }


    // =====================================================
    // FIND BRANCH
    // =====================================================

    private Branch findBranchForTenant(
            Integer tenantId,
            Integer branchId) {

        if (branchId == null
                || branchId <= 0) {

            throw new BadRequestException(
                    "Valid branch ID is required."
            );
        }

        Branch branch =
                branchRepository
                        .findById(
                                branchId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Branch not found."
                                )
                        );

        if (branch.getTenant() == null
                || !tenantId.equals(
                branch.getTenant().getTenantId()
        )) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }

        if (!Boolean.TRUE.equals(
                branch.getStatus()
        )) {

            throw new BadRequestException(
                    "Branch is inactive."
            );
        }

        return branch;
    }


    // =====================================================
    // TENANT OWNERSHIP
    // =====================================================

    private void validateTenantOwnership(
            Benefit benefit,
            Integer tenantId) {

        if (benefit == null
                || benefit.getTenant() == null) {

            throw new BadRequestException(
                    "Benefit does not belong to this tenant."
            );
        }

        if (!tenantId.equals(
                benefit.getTenant().getTenantId()
        )) {

            throw new BadRequestException(
                    "Benefit does not belong to this tenant."
            );
        }
    }


    // =====================================================
    // TENANT ID VALIDATION
    // =====================================================

    private void validateTenantId(
            Integer tenantId) {

        if (tenantId == null
                || tenantId <= 0) {

            throw new BadRequestException(
                    "Valid tenant ID is required."
            );
        }
    }


    // =====================================================
    // BENEFIT ID VALIDATION
    // =====================================================

    private void validateBenefitId(
            Integer benefitId) {

        if (benefitId == null
                || benefitId <= 0) {

            throw new BadRequestException(
                    "Valid benefit ID is required."
            );
        }
    }


    // =====================================================
    // REQUEST VALIDATION
    // =====================================================

    private void validateRequest(
            BenefitRequest request) {

        if (request == null) {

            throw new BadRequestException(
                    "Benefit request is required."
            );
        }

        if (request.getBenefitCode() == null
                || request.getBenefitCode()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "Benefit code is required."
            );
        }

        if (request.getBenefitName() == null
                || request.getBenefitName()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "Benefit name is required."
            );
        }

        if (request.getBillingType() == null) {

            throw new BadRequestException(
                    "Billing type is required."
            );
        }

        if (request.getDefaultFee() == null) {

            throw new BadRequestException(
                    "Default fee is required."
            );
        }

        if (request.getDefaultFee()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Default fee cannot be negative."
            );
        }
    }


    // =====================================================
    // BRANCH MODE NORMALIZATION
    // =====================================================

    private String normalizeBranchMode(
            String branchMode) {

        if (branchMode == null
                || branchMode.trim().isEmpty()) {

            return "ALL_BRANCHES";
        }

        return branchMode
                .trim()
                .toUpperCase();
    }


    // =====================================================
    // BRANCH MODE VALIDATION
    // =====================================================

    private void validateBranchMode(
            String branchMode,
            List<BenefitBranchRequest> branches) {

        if (!"ALL_BRANCHES".equals(branchMode)
                && !"SELECTED_BRANCHES".equals(
                branchMode)) {

            throw new BadRequestException(
                    "Invalid branch mode. " +
                            "Use ALL_BRANCHES or SELECTED_BRANCHES."
            );
        }

        if ("SELECTED_BRANCHES".equals(
                branchMode
        )) {

            if (branches == null
                    || branches.isEmpty()) {

                throw new BadRequestException(
                        "At least one branch must be selected."
                );
            }

            Set<Integer> branchIds =
                    new HashSet<>();

            for (BenefitBranchRequest branch :
                    branches) {

                if (branch == null
                        || branch.getBranchId() == null
                        || branch.getBranchId() <= 0) {

                    throw new BadRequestException(
                            "Valid branch ID is required."
                    );
                }

                if (!branchIds.add(
                        branch.getBranchId()
                )) {

                    throw new BadRequestException(
                            "Duplicate branch assignment is not allowed."
                    );
                }

                if (branch.getFee() != null
                        && branch.getFee()
                        .compareTo(
                                BigDecimal.ZERO
                        ) < 0) {

                    throw new BadRequestException(
                            "Branch benefit fee cannot be negative."
                    );
                }
            }
        }
    }


    // =====================================================
    // NORMALIZE CODE
    // =====================================================

    private String normalizeCode(
            String code) {

        if (code == null) {
            return null;
        }

        String normalized =
                code.trim()
                        .toUpperCase();

        return normalized.isEmpty()
                ? null
                : normalized;
    }


    // =====================================================
    // NORMALIZE NAME
    // =====================================================

    private String normalizeName(
            String name) {

        if (name == null) {
            return null;
        }

        return name.trim();
    }


    // =====================================================
    // NORMALIZE DESCRIPTION
    // =====================================================

    private String normalizeDescription(
            String description) {

        if (description == null) {
            return null;
        }

        String value =
                description.trim();

        return value.isEmpty()
                ? null
                : value;
    }


    // =====================================================
    // NORMALIZE FEE
    // =====================================================

    private BigDecimal normalizeFee(
            BigDecimal fee) {

        if (fee == null) {
            return BigDecimal.ZERO;
        }

        return fee.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // NORMALIZE NULLABLE FEE
    // =====================================================

    private BigDecimal normalizeNullableFee(
            BigDecimal fee) {

        if (fee == null) {
            return null;
        }

        return fee.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private BenefitResponse mapToResponse(
            Benefit benefit) {

        List<BenefitBranch> assignments =
                benefitBranchRepository
                        .findByTenant_TenantIdAndBenefit_BenefitId(
                                benefit.getTenant()
                                        .getTenantId(),
                                benefit.getBenefitId()
                        );

        String branchMode =
                determineBranchMode(
                        benefit,
                        assignments
                );

        return mapToResponse(
                benefit,
                branchMode
        );
    }


    // =====================================================
    // RESPONSE MAPPER WITH BRANCH MODE
    // =====================================================

    private BenefitResponse mapToResponse(
            Benefit benefit,
            String branchMode) {

        BenefitResponse response =
                new BenefitResponse();

        response.setBenefitId(
                benefit.getBenefitId()
        );

        response.setTenantId(
                benefit.getTenant()
                        .getTenantId()
        );

        response.setBenefitCode(
                benefit.getBenefitCode()
        );

        response.setBenefitName(
                benefit.getBenefitName()
        );

        response.setDescription(
                benefit.getDescription()
        );

        response.setBillingType(
                benefit.getBillingType()
        );

        response.setDefaultFee(
                benefit.getDefaultFee()
        );

        response.setStatus(
                benefit.getStatus()
        );

        response.setBranchMode(
                branchMode
        );

        response.setBranchAssignments(
                mapBranchAssignments(
                        benefit
                )
        );

        response.setCreatedAt(
                benefit.getCreatedAt()
        );

        response.setUpdatedAt(
                benefit.getUpdatedAt()
        );

        return response;
    }


    // =====================================================
// DETERMINE BRANCH MODE
// =====================================================

    private String determineBranchMode(
            Benefit benefit,
            List<BenefitBranch> assignments) {

        if (assignments == null
                || assignments.isEmpty()) {

            return "ALL_BRANCHES";
        }

        Integer tenantId =
                benefit.getTenant()
                        .getTenantId();

        long activeBranchCount =
                branchRepository
                        .findByTenant_TenantIdAndStatusTrue(
                                tenantId
                        )
                        .size();

        long activeAssignmentCount =
                assignments
                        .stream()
                        .filter(assignment ->
                                Boolean.TRUE.equals(
                                        assignment.getStatus()
                                )
                        )
                        .count();

        if (activeBranchCount > 0
                && activeAssignmentCount == activeBranchCount) {

            return "ALL_BRANCHES";
        }

        return "SELECTED_BRANCHES";
    }


    // =====================================================
    // MAP BRANCH ASSIGNMENTS
    // =====================================================

    private List<BenefitBranchResponse>
    mapBranchAssignments(
            Benefit benefit) {

        List<BenefitBranch> assignments =
                benefitBranchRepository
                        .findByTenant_TenantIdAndBenefit_BenefitId(
                                benefit.getTenant()
                                        .getTenantId(),
                                benefit.getBenefitId()
                        );

        List<BenefitBranchResponse> responses =
                new ArrayList<>();

        for (BenefitBranch assignment :
                assignments) {

            BenefitBranchResponse response =
                    new BenefitBranchResponse();

            response.setBenefitBranchId(
                    assignment.getBenefitBranchId()
            );

            if (assignment.getBranch() != null) {

                response.setBranchId(
                        assignment.getBranch()
                                .getBranchId()
                );

                response.setBranchNo(
                        assignment.getBranch()
                                .getBranchNo()
                );

                response.setBranchName(
                        assignment.getBranch()
                                .getBranchName()
                );
            }

            response.setFee(
                    assignment.getFee()
            );

            response.setStatus(
                    assignment.getStatus()
            );

            response.setCreatedAt(
                    assignment.getCreatedAt()
            );

            response.setUpdatedAt(
                    assignment.getUpdatedAt()
            );

            responses.add(
                    response
            );
        }

        return responses;
    }
}