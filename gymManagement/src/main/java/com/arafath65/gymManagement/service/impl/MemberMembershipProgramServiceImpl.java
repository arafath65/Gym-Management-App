package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MemberMembershipProgramRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipProgramResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MemberMembershipProgram;
import com.arafath65.gymManagement.entity.Program;
import com.arafath65.gymManagement.entity.ProgramBranch;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MemberMembershipProgramRepository;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.ProgramBranchRepository;
import com.arafath65.gymManagement.repository.ProgramRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.MemberMembershipProgramService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MemberMembershipProgramServiceImpl
        implements MemberMembershipProgramService {

    private final TenantRepository tenantRepository;

    private final BranchRepository branchRepository;

    private final MemberMembershipRepository memberMembershipRepository;

    private final ProgramRepository programRepository;

    private final ProgramBranchRepository programBranchRepository;

    private final MemberMembershipProgramRepository
            memberMembershipProgramRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipProgramServiceImpl(
            TenantRepository tenantRepository,
            BranchRepository branchRepository,
            MemberMembershipRepository memberMembershipRepository,
            ProgramRepository programRepository,
            ProgramBranchRepository programBranchRepository,
            MemberMembershipProgramRepository memberMembershipProgramRepository) {

        this.tenantRepository =
                tenantRepository;

        this.branchRepository =
                branchRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;

        this.programRepository =
                programRepository;

        this.programBranchRepository =
                programBranchRepository;

        this.memberMembershipProgramRepository =
                memberMembershipProgramRepository;
    }


    // =====================================================
    // ASSIGN PROGRAM
    // =====================================================

    @Override
    public MemberMembershipProgramResponse assignProgram(
            Integer tenantId,
            Integer memberMembershipId,
            MemberMembershipProgramRequest request) {

        // -------------------------------------------------
        // BASIC VALIDATION
        // -------------------------------------------------

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipId == null) {

            throw new IllegalArgumentException(
                    "Member membership ID is required."
            );
        }

        if (request == null) {

            throw new IllegalArgumentException(
                    "Program request is required."
            );
        }

        if (request.getProgramId() == null) {

            throw new IllegalArgumentException(
                    "Program is required."
            );
        }


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        Tenant tenant =
                tenantRepository.findById(
                        tenantId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tenant not found."
                        )
                );


        // -------------------------------------------------
        // MEMBER MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                memberMembershipId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership not found."
                                )
                        );


        // -------------------------------------------------
        // MEMBERSHIP TENANT VALIDATION
        // -------------------------------------------------

        if (membership.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Member membership tenant is not available."
            );
        }



        if (!tenantId.equals(
                membership.getTenant().getTenantId()
        )) {

            throw new IllegalArgumentException(
                    "Member membership does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP BRANCH
        // -------------------------------------------------

        Branch branch =
                membership.getBranch();

        if (branch == null) {

            throw new IllegalArgumentException(
                    "Membership branch is not available."
            );
        }

        /*
         * Branch.getBranchId() is primitive int
         * in your project.
         *
         * Therefore we DO NOT compare it with null.
         */

        Integer branchId =
                branch.getBranchId();


        // -------------------------------------------------
        // VERIFY BRANCH
        // -------------------------------------------------

        Branch verifiedBranch =
                branchRepository.findById(
                        branchId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Membership branch not found."
                        )
                );


        // -------------------------------------------------
        // BRANCH TENANT VALIDATION
        // -------------------------------------------------

        if (verifiedBranch.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Branch tenant is not available."
            );
        }



        if (!tenantId.equals(
                verifiedBranch.getTenant().getTenantId()
        )) {

            throw new IllegalArgumentException(
                    "Membership branch does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // USE VERIFIED BRANCH
        // -------------------------------------------------

        branch = verifiedBranch;


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        Program program =
                programRepository.findById(
                        request.getProgramId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Program not found."
                        )
                );


        // -------------------------------------------------
        // PROGRAM TENANT VALIDATION
        // -------------------------------------------------

        if (program.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Program tenant is not available."
            );
        }



        if (!tenantId.equals(
                program.getTenant().getTenantId()
        )) {

            throw new IllegalArgumentException(
                    "Program does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // PROGRAM STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                program.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Program is inactive."
            );
        }


        // -------------------------------------------------
        // PROGRAM + BRANCH
        // -------------------------------------------------

        ProgramBranch programBranch =
                programBranchRepository
                        .findByTenant_TenantIdAndProgram_ProgramIdAndBranch_BranchId(
                                tenantId,
                                program.getProgramId(),
                                branchId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Program is not available at this branch."
                                )
                        );


        // -------------------------------------------------
        // PROGRAM BRANCH STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                programBranch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Program is inactive at this branch."
            );
        }


        // -------------------------------------------------
        // DUPLICATE CHECK
        // -------------------------------------------------

        boolean alreadyAssigned =
                memberMembershipProgramRepository
                        .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndProgram_ProgramIdAndStatusTrue(
                                tenantId,
                                memberMembershipId,
                                program.getProgramId()
                        );

        if (alreadyAssigned) {

            throw new IllegalArgumentException(
                    "This program is already assigned to this membership."
            );
        }


        // -------------------------------------------------
        // CREATE ASSIGNMENT
        // -------------------------------------------------

        MemberMembershipProgram assignment =
                new MemberMembershipProgram();


        assignment.setTenant(
                tenant
        );

        assignment.setBranch(
                branch
        );

        assignment.setMemberMembership(
                membership
        );

        assignment.setProgram(
                program
        );


        // -------------------------------------------------
        // FEE SNAPSHOT
        // -------------------------------------------------

        BigDecimal fee =
                programBranch.getFee();

        if (fee == null) {

            fee =
                    BigDecimal.ZERO;
        }

        assignment.setFee(
                fee
        );


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        LocalDate startDate =
                request.getStartDate();

        if (startDate == null) {

            startDate =
                    membership.getStartDate();
        }

        if (startDate == null) {

            startDate =
                    LocalDate.now();
        }

        assignment.setStartDate(
                startDate
        );


        // -------------------------------------------------
        // END DATE
        // -------------------------------------------------

        LocalDate endDate =
                request.getEndDate();

        if (endDate == null) {

            endDate =
                    membership.getEndDate();
        }

        assignment.setEndDate(
                endDate
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        assignment.setStatus(
                true
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        assignment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        MemberMembershipProgram saved =
                memberMembershipProgramRepository.save(
                        assignment
                );


        return toResponse(
                saved
        );
    }


    // =====================================================
    // GET PROGRAMS
    // =====================================================

    @Override
    @Transactional
    public List<MemberMembershipProgramResponse> getPrograms(
            Integer tenantId,
            Integer memberMembershipId) {

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipId == null) {

            throw new IllegalArgumentException(
                    "Member membership ID is required."
            );
        }


        List<MemberMembershipProgram> assignments =
                memberMembershipProgramRepository
                        .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndStatusTrue(
                                tenantId,
                                memberMembershipId
                        );


        List<MemberMembershipProgramResponse> responses =
                new ArrayList<>();


        for (MemberMembershipProgram assignment :
                assignments) {

            responses.add(
                    toResponse(
                            assignment
                    )
            );
        }


        return responses;
    }


    // =====================================================
    // GET ONE
    // =====================================================

    @Override
    @Transactional
    public MemberMembershipProgramResponse getProgram(
            Integer tenantId,
            Integer memberMembershipProgramId) {

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipProgramId == null) {

            throw new IllegalArgumentException(
                    "Member membership program ID is required."
            );
        }


        MemberMembershipProgram assignment =
                memberMembershipProgramRepository
                        .findByMemberMembershipProgramIdAndTenant_TenantId(
                                memberMembershipProgramId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership program not found."
                                )
                        );


        return toResponse(
                assignment
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public MemberMembershipProgramResponse updateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId,
            MemberMembershipProgramRequest request) {

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipProgramId == null) {

            throw new IllegalArgumentException(
                    "Member membership program ID is required."
            );
        }

        if (request == null) {

            throw new IllegalArgumentException(
                    "Program request is required."
            );
        }

        if (request.getProgramId() == null) {

            throw new IllegalArgumentException(
                    "Program is required."
            );
        }


        // -------------------------------------------------
        // EXISTING ASSIGNMENT
        // -------------------------------------------------

        MemberMembershipProgram assignment =
                memberMembershipProgramRepository
                        .findByMemberMembershipProgramIdAndTenant_TenantId(
                                memberMembershipProgramId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership program not found."
                                )
                        );


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------

        MemberMembership membership =
                assignment.getMemberMembership();

        if (membership == null) {

            throw new IllegalArgumentException(
                    "Member membership not found."
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        Branch branch =
                assignment.getBranch();

        if (branch == null) {

            throw new IllegalArgumentException(
                    "Branch not found."
            );
        }

        Integer branchId =
                branch.getBranchId();


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        Program program =
                programRepository.findById(
                        request.getProgramId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Program not found."
                        )
                );


        // -------------------------------------------------
        // PROGRAM TENANT
        // -------------------------------------------------

        if (program.getTenant() == null) {

            throw new IllegalArgumentException(
                    "Program tenant is not available."
            );
        }

        if (!tenantId.equals(
                program.getTenant().getTenantId()
        )) {

            throw new IllegalArgumentException(
                    "Program does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // PROGRAM STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                program.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Program is inactive."
            );
        }


        // -------------------------------------------------
        // PROGRAM + BRANCH
        // -------------------------------------------------

        ProgramBranch programBranch =
                programBranchRepository
                        .findByTenant_TenantIdAndProgram_ProgramIdAndBranch_BranchId(
                                tenantId,
                                program.getProgramId(),
                                branchId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Program is not available at this branch."
                                )
                        );


        if (!Boolean.TRUE.equals(
                programBranch.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Program is inactive at this branch."
            );
        }


        // -------------------------------------------------
        // DUPLICATE CHECK
        // -------------------------------------------------

        Program currentProgram =
                assignment.getProgram();

        if (currentProgram != null
                && currentProgram.getProgramId() != null
                && !program.getProgramId().equals(
                currentProgram.getProgramId()
        )) {

            boolean exists =
                    memberMembershipProgramRepository
                            .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndProgram_ProgramIdAndStatusTrue(
                                    tenantId,
                                    membership.getMemberMembershipId(),
                                    program.getProgramId()
                            );


            if (exists) {

                throw new IllegalArgumentException(
                        "This program is already assigned to this membership."
                );
            }
        }


        // -------------------------------------------------
        // UPDATE PROGRAM
        // -------------------------------------------------

        assignment.setProgram(
                program
        );


        // -------------------------------------------------
        // UPDATE FEE SNAPSHOT
        // -------------------------------------------------

        BigDecimal fee =
                programBranch.getFee();

        if (fee == null) {

            fee =
                    BigDecimal.ZERO;
        }

        assignment.setFee(
                fee
        );


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        if (request.getStartDate() != null) {

            assignment.setStartDate(
                    request.getStartDate()
            );
        }


        // -------------------------------------------------
        // END DATE
        // -------------------------------------------------

        if (request.getEndDate() != null) {

            assignment.setEndDate(
                    request.getEndDate()
            );
        }


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        assignment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        MemberMembershipProgram saved =
                memberMembershipProgramRepository.save(
                        assignment
                );


        return toResponse(
                saved
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @Override
    public void deactivateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId) {

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipProgramId == null) {

            throw new IllegalArgumentException(
                    "Member membership program ID is required."
            );
        }


        MemberMembershipProgram assignment =
                memberMembershipProgramRepository
                        .findByMemberMembershipProgramIdAndTenant_TenantId(
                                memberMembershipProgramId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership program not found."
                                )
                        );


        assignment.setStatus(
                false
        );


        memberMembershipProgramRepository.save(
                assignment
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @Override
    public void activateProgram(
            Integer tenantId,
            Integer memberMembershipProgramId) {

        if (tenantId == null) {

            throw new IllegalArgumentException(
                    "Tenant ID is required."
            );
        }

        if (memberMembershipProgramId == null) {

            throw new IllegalArgumentException(
                    "Member membership program ID is required."
            );
        }


        MemberMembershipProgram assignment =
                memberMembershipProgramRepository
                        .findByMemberMembershipProgramIdAndTenant_TenantId(
                                memberMembershipProgramId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Member membership program not found."
                                )
                        );


        assignment.setStatus(
                true
        );


        memberMembershipProgramRepository.save(
                assignment
        );
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private MemberMembershipProgramResponse toResponse(
            MemberMembershipProgram assignment) {

        MemberMembershipProgramResponse response =
                new MemberMembershipProgramResponse();


        // -------------------------------------------------
        // ID
        // -------------------------------------------------

        response.setMemberMembershipProgramId(
                assignment.getMemberMembershipProgramId()
        );


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (assignment.getTenant() != null) {

            response.setTenantId(
                    assignment.getTenant()
                            .getTenantId()
            );
        }


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (assignment.getBranch() != null) {

            Branch branch =
                    assignment.getBranch();

            response.setBranchId(
                    branch.getBranchId()
            );

            response.setBranchName(
                    branch.getBranchName()
            );
        }


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------

        if (assignment.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    assignment.getMemberMembership()
                            .getMemberMembershipId()
            );
        }


        // -------------------------------------------------
        // PROGRAM
        // -------------------------------------------------

        if (assignment.getProgram() != null) {

            Program program =
                    assignment.getProgram();

            response.setProgramId(
                    program.getProgramId()
            );

            response.setProgramCode(
                    program.getProgramCode()
            );

            response.setProgramName(
                    program.getProgramName()
            );

            response.setDescription(
                    program.getDescription()
            );

            response.setDurationDays(
                    program.getDurationDays()
            );
        }


        // -------------------------------------------------
        // FEE
        // -------------------------------------------------

        response.setFee(
                assignment.getFee()
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        response.setStartDate(
                assignment.getStartDate()
        );

        response.setEndDate(
                assignment.getEndDate()
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        response.setStatus(
                assignment.getStatus()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                assignment.getNotes()
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setCreatedAt(
                assignment.getCreatedAt()
        );

        response.setUpdatedAt(
                assignment.getUpdatedAt()
        );


        return response;
    }

    // =====================================================
// GET ALL PROGRAM HISTORY OF MEMBER
// =====================================================

    @Override
    @Transactional
    public List<MemberMembershipProgramResponse>
    getProgramHistory(
            Integer tenantId,
            Integer memberId) {

        List<MemberMembershipProgram> assignments =
                memberMembershipProgramRepository
                        .findByTenant_TenantIdAndMemberMembership_Member_MemberId(
                                tenantId,
                                memberId
                        );

        List<MemberMembershipProgramResponse> responses =
                new ArrayList<>();

        for (
                MemberMembershipProgram assignment
                : assignments) {

            responses.add(
                    toResponse(
                            assignment
                    )
            );
        }

        return responses;
    }
}