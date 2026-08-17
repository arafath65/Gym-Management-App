package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MemberRequest;
import com.arafath65.gymManagement.dto.response.MemberResponse;
import com.arafath65.gymManagement.entity.Branch;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.exception.ResourceNotFoundException;
import com.arafath65.gymManagement.repository.BranchRepository;
import com.arafath65.gymManagement.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final BranchRepository branchRepository;

    private final MemberImageStorageService memberImageStorageService;


    public MemberService(

            MemberRepository memberRepository,

            BranchRepository branchRepository,

            MemberImageStorageService
                    memberImageStorageService) {

        this.memberRepository =
                memberRepository;

        this.branchRepository =
                branchRepository;

        this.memberImageStorageService =
                memberImageStorageService;
    }

    // UPDATE MEMBER IMAGE
    @Transactional
    public Member updateMemberImage(

            Tenant tenant,

            Integer memberId,

            MultipartFile image) throws IOException {

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found."
                                )
                        );

        if (member.getTenant() == null ||
                member.getTenant()
                        .getTenantId()
                        != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }

        if (image == null ||
                image.isEmpty()) {

            throw new BadRequestException(
                    "Member image is required."
            );
        }

        String oldImage =
                member.getProfileImage();

        String newImage = null;

        try {

            newImage =
                    memberImageStorageService.saveImage(
                            image,
                            tenant.getTenantId(),
                            memberId
                    );

            member.setProfileImage(
                    newImage
            );

            Member savedMember =
                    memberRepository.save(
                            member
                    );

            if (oldImage != null &&
                    !oldImage.trim().isEmpty() &&
                    !oldImage.equals(newImage)) {

                memberImageStorageService.deleteImage(
                        oldImage
                );
            }

            return savedMember;

        } catch (Exception e) {

            if (newImage != null) {

                memberImageStorageService.deleteImage(
                        newImage
                );
            }

            throw e;
        }
    }

    // =====================================================
// GET MEMBER BY ID
// =====================================================

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(
            Tenant tenant,
            Integer memberId) {

        // -------------------------------------------------
        // FIND MEMBER
        // -------------------------------------------------

        Member member =
                memberRepository.findById(
                        memberId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member not found."
                        )
                );


        // -------------------------------------------------
        // MEMBER → TENANT VALIDATION
        // -------------------------------------------------

        if (member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // RETURN RESPONSE
        // -------------------------------------------------

        return mapToResponse(
                member
        );
    }


    // =====================================================
    // GET MEMBERS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(
            Tenant tenant,
            Integer branchId) {

        List<Member> members;


        // =================================================
        // SPECIFIC BRANCH
        // =================================================

        if (branchId != null) {

            Branch branch =
                    branchRepository
                            .findById(branchId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Branch not found."
                                    )
                            );


            // ---------------------------------------------
            // BRANCH MUST BELONG TO TENANT
            // ---------------------------------------------

            if (branch.getTenant()
                    .getTenantId()
                    != tenant.getTenantId()) {

                throw new BadRequestException(
                        "Branch does not belong to this tenant."
                );
            }


            members =
                    memberRepository
                            .findByTenant_TenantIdAndBranch_BranchIdAndStatus(
                                    tenant.getTenantId(),
                                    branchId,
                                    true
                            );

        }


        // =================================================
        // ALL BRANCHES
        // =================================================

        else {

            members =
                    memberRepository
                            .findByTenant_TenantIdAndStatus(
                                    tenant.getTenantId(),
                                    true
                            );
        }


        // =================================================
        // MAP RESPONSE
        // =================================================

        return members.stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =====================================================
    // MAP MEMBER → RESPONSE
    // =====================================================

    private MemberResponse mapToResponse(
            Member member) {

        MemberResponse response =
                new MemberResponse();


        // =================================================
        // MEMBER
        // =================================================

        response.setMemberId(
                member.getMemberId()
        );

        response.setMemberNo(
                member.getMemberNo()
        );

        response.setFirstName(
                member.getFirstName()
        );

        response.setLastName(
                member.getLastName()
        );

        response.setFullName(
                member.getFullName()
        );

        response.setGender(
                member.getGender()
        );


        // =================================================
        // DATE OF BIRTH
        // =================================================

        response.setDateOfBirth(
                member.getDateOfBirth()
        );


        // =================================================
        // PERSONAL INFORMATION
        // =================================================

        response.setNic(
                member.getNic()
        );

        response.setMobile(
                member.getMobile()
        );


        response.setEmergencyContactName(
                member.getEmergencyContactName()
        );

        response.setEmergencyContactMobile(
                member.getEmergencyContactMobile()
        );

        response.setEmergencyContactRelationship(
                member.getEmergencyContactRelationship()
        );

        response.setEmail(
                member.getEmail()
        );

        response.setOccupation(
                member.getOccupation()
        );


        response.setBloodGroup(
                member.getBloodGroup()
        );

        response.setMedicalConditions(
                member.getMedicalConditions()
        );

        response.setGoal(
                member.getGoal()
        );

        response.setNotes(
                member.getNotes()
        );


        response.setAddress(
                member.getAddress()
        );

        response.setCity(
                member.getCity()
        );

        response.setProfileImage(
                member.getProfileImage()
        );


        // =================================================
        // JOIN DATE
        // =================================================

        response.setJoinDate(
                member.getJoinDate()
        );


        // =================================================
        // STATUS
        // =================================================

        response.setStatus(
                member.isStatus()
        );




        // =================================================
        // TENANT
        // =================================================

        Tenant tenant =
                member.getTenant();

        if (tenant != null) {

            response.setTenantId(
                    tenant.getTenantId()
            );

            response.setTenantNo(
                    tenant.getTenantNo()
            );

            response.setGymName(
                    tenant.getGymName()
            );
        }


        // =================================================
        // BRANCH
        // =================================================

        Branch branch =
                member.getBranch();

        if (branch != null) {

            response.setBranchId(
                    branch.getBranchId()
            );

            response.setBranchNo(
                    branch.getBranchNo()
            );

            response.setBranchName(
                    branch.getBranchName()
            );
        }


        return response;
    }


    // =====================================================
    // CREATE MEMBER
    // =====================================================

    @Transactional
    public Member createMember(
            Tenant tenant,
            MemberRequest request) {


        // -------------------------------------------------
        // TENANT STATUS
        // -------------------------------------------------

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // -------------------------------------------------
        // FIND BRANCH
        // -------------------------------------------------

        Branch branch =
                branchRepository
                        .findById(
                                request.getBranchId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found."
                                )
                        );


        // -------------------------------------------------
        // BRANCH → TENANT VALIDATION
        // -------------------------------------------------

        if (branch.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // GENERATE MEMBER NUMBER
        // -------------------------------------------------

        String memberNo =
                generateMemberNo(
                        tenant
                );


        // -------------------------------------------------
        // CREATE MEMBER
        // -------------------------------------------------

        Member member =
                new Member();


        member.setTenant(
                tenant
        );

        member.setBranch(
                branch
        );

        member.setMemberNo(
                memberNo
        );


        // =================================================
        // BASIC INFORMATION
        // =================================================

        member.setFirstName(
                request.getFirstName()
        );

        member.setLastName(
                request.getLastName()
        );

        member.setFullName(
                request.getFullName()
        );

        member.setGender(
                request.getGender()
        );


        // =================================================
        // DATE OF BIRTH
        // =================================================

        if (request.getDateOfBirth() != null &&
                !request.getDateOfBirth()
                        .trim()
                        .isEmpty()) {

            member.setDateOfBirth(
                    LocalDate.parse(
                            request.getDateOfBirth()
                    )
            );
        }


        // =================================================
        // CONTACT INFORMATION
        // =================================================

        member.setNic(
                request.getNic()
        );

        member.setMobile(
                request.getMobile()
        );

        member.setEmergencyContactName(
                request.getEmergencyContactName()
        );

        member.setEmergencyContactMobile(
                request.getEmergencyContactMobile()
        );

        member.setEmergencyContactRelationship(
                request.getEmergencyContactRelationship()
        );

        member.setEmail(
                request.getEmail()
        );

        member.setOccupation(
                request.getOccupation()
        );

        member.setBloodGroup(
                request.getBloodGroup()
        );

        member.setMedicalConditions(
                request.getMedicalConditions()
        );

        member.setGoal(
                request.getGoal()
        );

        member.setNotes(
                request.getNotes()
        );


        member.setAddress(
                request.getAddress()
        );

        member.setCity(
                request.getCity()
        );


        member.setProfileImage(
                request.getProfileImage()
        );


        // =================================================
        // JOIN DATE
        // =================================================

        if (request.getJoinDate() != null &&
                !request.getJoinDate()
                        .trim()
                        .isEmpty()) {

            member.setJoinDate(
                    LocalDate.parse(
                            request.getJoinDate()
                    )
            );

        } else {

            member.setJoinDate(
                    LocalDate.now()
            );
        }




        // =================================================
        // STATUS
        // =================================================

        member.setStatus(
                true
        );


        return memberRepository.save(
                member
        );
    }

    // =====================================================
// UPDATE MEMBER
// =====================================================

    @Transactional
    public Member updateMember(

            Tenant tenant,

            Integer memberId,

            MemberRequest request) {


        // =================================================
        // TENANT STATUS
        // =================================================

        if (!Boolean.TRUE.equals(
                tenant.getStatus())) {

            throw new BadRequestException(
                    "Tenant is inactive."
            );
        }


        // =================================================
        // FIND MEMBER
        // =================================================

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found."
                                )
                        );


        // =================================================
        // VERIFY MEMBER BELONGS TO TENANT
        // =================================================

        if (member.getTenant() == null ||
                member.getTenant()
                        .getTenantId()
                        != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        // =================================================
        // FIND BRANCH
        // =================================================

        Branch branch =
                branchRepository
                        .findById(
                                request.getBranchId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found."
                                )
                        );


        // =================================================
        // BRANCH → TENANT
        // =================================================

        if (branch.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Branch does not belong to this tenant."
            );
        }


        // =================================================
        // BASIC INFORMATION
        // =================================================

        member.setBranch(
                branch
        );


        member.setFirstName(
                request.getFirstName()
        );


        member.setLastName(
                request.getLastName()
        );


        member.setFullName(
                request.getFullName()
        );


        member.setGender(
                request.getGender()
        );


        // =================================================
        // DATE OF BIRTH
        // =================================================

        if (request.getDateOfBirth() != null &&
                !request.getDateOfBirth()
                        .trim()
                        .isEmpty()) {

            member.setDateOfBirth(
                    LocalDate.parse(
                            request.getDateOfBirth()
                    )
            );

        } else {

            member.setDateOfBirth(
                    null
            );
        }


        // =================================================
        // CONTACT
        // =================================================

        member.setNic(
                request.getNic()
        );


        member.setMobile(
                request.getMobile()
        );


        member.setEmail(
                request.getEmail()
        );


        member.setAddress(
                request.getAddress()
        );


        member.setCity(
                request.getCity()
        );


        // =================================================
        // EMERGENCY
        // =================================================

        member.setEmergencyContactName(
                request.getEmergencyContactName()
        );


        member.setEmergencyContactMobile(
                request.getEmergencyContactMobile()
        );


        member.setEmergencyContactRelationship(
                request.getEmergencyContactRelationship()
        );


        // =================================================
        // HEALTH / PROFILE
        // =================================================

        member.setOccupation(
                request.getOccupation()
        );


        member.setBloodGroup(
                request.getBloodGroup()
        );


        member.setMedicalConditions(
                request.getMedicalConditions()
        );


        member.setGoal(
                request.getGoal()
        );


        member.setNotes(
                request.getNotes()
        );


        // =================================================
        // JOIN DATE
        // =================================================

        if (request.getJoinDate() != null &&
                !request.getJoinDate()
                        .trim()
                        .isEmpty()) {

            member.setJoinDate(
                    LocalDate.parse(
                            request.getJoinDate()
                    )
            );

        } else {

            member.setJoinDate(
                    null
            );
        }

        return memberRepository.save(
                member
        );
    }

    // DEACTIVATE MEMBER
    @Transactional
    public void deactivateMember(

            Tenant tenant,

            Integer memberId) {

        Member member =
                memberRepository
                        .findById(
                                memberId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found."
                                )
                        );

        if (member.getTenant() == null ||
                member.getTenant()
                        .getTenantId()
                        != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }

        member.setStatus(
                false
        );

        memberRepository.save(
                member
        );
    }

    // =====================================================
    // GENERATE MEMBER NUMBER
    // =====================================================

    private String generateMemberNo(
            Tenant tenant) {

        int tenantId =
                tenant.getTenantId();


        String prefix =
                tenant.getTenantNo();


        int number = 1;

        String memberNo;


        do {

            memberNo =
                    String.format(
                            "%s-M%05d",
                            prefix,
                            number
                    );

            number++;

        } while (
                memberRepository
                        .existsByTenant_TenantIdAndMemberNo(
                                tenantId,
                                memberNo
                        )
        );


        return memberNo;
    }
}