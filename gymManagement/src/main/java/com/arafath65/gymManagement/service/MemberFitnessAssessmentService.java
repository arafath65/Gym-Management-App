package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.MemberFitnessAssessmentRequest;
import com.arafath65.gymManagement.dto.response.MemberFitnessAssessmentResponse;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.MemberFitnessAssessment;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.MemberFitnessAssessmentRepository;
import com.arafath65.gymManagement.repository.MemberRepository;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberFitnessAssessmentService {

    private final MemberFitnessAssessmentRepository
            fitnessRepository;

    private final MemberRepository memberRepository;
    private final FitnessImageStorageService imageStorageService;



    public MemberFitnessAssessmentService(
            MemberFitnessAssessmentRepository fitnessRepository,
            MemberRepository memberRepository,
            FitnessImageStorageService imageStorageService) {

        this.fitnessRepository =
                fitnessRepository;

        this.memberRepository =
                memberRepository;

        this.imageStorageService =
                imageStorageService;
    }


    // =====================================================
    // CREATE ASSESSMENT
    // =====================================================

    public MemberFitnessAssessment
    createAssessment(
            Tenant tenant,
            Integer memberId,
            MemberFitnessAssessmentRequest request) {


        // -------------------------------------------------
        // FIND MEMBER
        // -------------------------------------------------

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Member not found."
                                )
                        );


        // -------------------------------------------------
        // MEMBER → TENANT
        // -------------------------------------------------

        if (member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        MemberFitnessAssessment assessment =
                new MemberFitnessAssessment();


        assessment.setMember(
                member
        );


        assessment.setAssessmentDate(
                request.getAssessmentDate()
        );


        // -------------------------------------------------
        // BODY
        // -------------------------------------------------

        assessment.setWeight(
                request.getWeight()
        );


        assessment.setHeight(
                request.getHeight()
        );


        assessment.setBodyFat(
                request.getBodyFat()
        );


        // -------------------------------------------------
        // MEASUREMENTS
        // -------------------------------------------------

        assessment.setChest(
                request.getChest()
        );


        assessment.setWaist(
                request.getWaist()
        );


        assessment.setHips(
                request.getHips()
        );


        assessment.setArms(
                request.getArms()
        );


        assessment.setThigh(
                request.getThigh()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        assessment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // BMI
        // -------------------------------------------------

        assessment.setBmi(
                calculateBmi(
                        request.getWeight(),
                        request.getHeight()
                )
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        return fitnessRepository.save(
                assessment
        );
    }


    // =====================================================
    // GET MEMBER FITNESS HISTORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<MemberFitnessAssessmentResponse>
    getMemberAssessments(
            Tenant tenant,
            Integer memberId) {


        verifyMemberTenant(
                tenant,
                memberId
        );


        List<MemberFitnessAssessment>
                assessments =
                fitnessRepository
                        .findByMemberMemberIdOrderByAssessmentDateDesc(
                                memberId
                        );


        return assessments.stream()
                .map(
                        this::mapToResponse
                )
                .collect(
                        Collectors.toList()
                );
    }


    // =====================================================
    // GET LATEST ASSESSMENT
    // =====================================================

    @Transactional(readOnly = true)
    public MemberFitnessAssessmentResponse
    getLatestAssessment(
            Tenant tenant,
            Integer memberId) {


        verifyMemberTenant(
                tenant,
                memberId
        );


        MemberFitnessAssessment assessment =
                fitnessRepository
                        .findFirstByMemberMemberIdOrderByAssessmentDateDesc(
                                memberId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "No fitness assessment found."
                                )
                        );


        return mapToResponse(
                assessment
        );
    }


    // =====================================================
    // VERIFY MEMBER TENANT
    // =====================================================

    private Member verifyMemberTenant(
            Tenant tenant,
            Integer memberId) {


        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Member not found."
                                )
                        );


        if (member.getTenant()
                .getTenantId()
                != tenant.getTenantId()) {

            throw new BadRequestException(
                    "Member does not belong to this tenant."
            );
        }


        return member;
    }



    // =====================================================
// DELETE FITNESS ASSESSMENT
// =====================================================

    public void deleteAssessment(

            Tenant tenant,

            Integer memberId,

            Integer fitnessId) {


        // =================================================
        // VERIFY MEMBER
        // =================================================

        Member member =
                verifyMemberTenant(
                        tenant,
                        memberId
                );


        // =================================================
        // FIND ASSESSMENT
        // =================================================

        MemberFitnessAssessment assessment =
                fitnessRepository
                        .findById(fitnessId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Fitness assessment not found."
                                )
                        );


        // =================================================
        // VERIFY MEMBER OWNERSHIP
        // =================================================

        if (assessment.getMember()
                .getMemberId()
                != member.getMemberId()) {

            throw new BadRequestException(
                    "Fitness assessment does not belong to this member."
            );
        }


        // =================================================
        // GET OLD IMAGE PATHS
        // =================================================

        String frontImage =
                assessment.getFrontImage();


        String sideImage =
                assessment.getSideImage();


        String backImage =
                assessment.getBackImage();


        // =================================================
        // DELETE DATABASE RECORD FIRST
        // =================================================

        fitnessRepository.delete(
                assessment
        );


        // =================================================
        // DELETE PHYSICAL FILES
        // =================================================

        imageStorageService.deleteImage(
                frontImage
        );


        imageStorageService.deleteImage(
                sideImage
        );


        imageStorageService.deleteImage(
                backImage
        );
    }


    // =====================================================
    // CALCULATE BMI
    // =====================================================

    private BigDecimal calculateBmi(
            BigDecimal weight,
            BigDecimal height) {


        // -------------------------------------------------
        // BMI cannot be calculated without both values
        // -------------------------------------------------

        if (weight == null ||
                height == null) {

            return null;
        }


        // -------------------------------------------------
        // Height must be greater than zero
        // -------------------------------------------------

        if (height.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "Height must be greater than zero."
            );
        }


        // -------------------------------------------------
        // Weight must be greater than zero
        // -------------------------------------------------

        if (weight.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BadRequestException(
                    "Weight must be greater than zero."
            );
        }


        // -------------------------------------------------
        // Height is stored in centimeters
        //
        // Example:
        //
        // 175 cm = 1.75 m
        // -------------------------------------------------

        BigDecimal heightInMeters =
                height.divide(
                        BigDecimal.valueOf(100),
                        10,
                        RoundingMode.HALF_UP
                );


        // -------------------------------------------------
        // BMI = weight / height²
        // -------------------------------------------------

        BigDecimal heightSquared =
                heightInMeters.multiply(
                        heightInMeters
                );


        return weight.divide(
                heightSquared,
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MemberFitnessAssessmentResponse
    mapToResponse(
            MemberFitnessAssessment assessment) {


        MemberFitnessAssessmentResponse response =
                new MemberFitnessAssessmentResponse();


        response.setFitnessId(
                assessment.getFitnessId()
        );


        response.setMemberId(
                assessment.getMember()
                        .getMemberId()
        );


        response.setAssessmentDate(
                assessment.getAssessmentDate()
        );


        // -------------------------------------------------
        // BODY
        // -------------------------------------------------

        response.setWeight(
                assessment.getWeight()
        );


        response.setHeight(
                assessment.getHeight()
        );


        response.setBmi(
                assessment.getBmi()
        );


        response.setBodyFat(
                assessment.getBodyFat()
        );


        // -------------------------------------------------
        // MEASUREMENTS
        // -------------------------------------------------

        response.setChest(
                assessment.getChest()
        );


        response.setWaist(
                assessment.getWaist()
        );


        response.setHips(
                assessment.getHips()
        );


        response.setArms(
                assessment.getArms()
        );


        response.setThigh(
                assessment.getThigh()
        );


        // -------------------------------------------------
        // PHOTOS
        // -------------------------------------------------

        response.setFrontImage(
                assessment.getFrontImage()
        );


        response.setSideImage(
                assessment.getSideImage()
        );


        response.setBackImage(
                assessment.getBackImage()
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        response.setNotes(
                assessment.getNotes()
        );


        // -------------------------------------------------
        // AUDIT
        // -------------------------------------------------

        response.setRecordedBy(
                assessment.getRecordedBy()
        );


        return response;
    }

    // =====================================================
// GET ASSESSMENT BY ID
// =====================================================

    public MemberFitnessAssessmentResponse getAssessmentById(
            Tenant tenant,
            Integer memberId,
            Integer fitnessId) {

        Member member =
                verifyMemberTenant(
                        tenant,
                        memberId
                );


        MemberFitnessAssessment assessment =
                fitnessRepository
                        .findById(fitnessId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Fitness assessment not found."
                                )
                        );


        // -------------------------------------------------
        // VERIFY ASSESSMENT BELONGS TO MEMBER
        // -------------------------------------------------

        if (assessment.getMember()
                .getMemberId()
                != member.getMemberId()) {

            throw new BadRequestException(
                    "Fitness assessment does not belong to this member."
            );
        }


        return mapToResponse(
                assessment
        );
    }

    // =====================================================
// UPDATE FITNESS ASSESSMENT
// =====================================================

    public MemberFitnessAssessmentResponse updateAssessment(

            Tenant tenant,

            Integer memberId,

            Integer fitnessId,

            MemberFitnessAssessmentRequest request) {


        // -------------------------------------------------
        // VERIFY MEMBER BELONGS TO TENANT
        // -------------------------------------------------

        Member member =
                verifyMemberTenant(
                        tenant,
                        memberId
                );


        // -------------------------------------------------
        // FIND FITNESS ASSESSMENT
        // -------------------------------------------------

        MemberFitnessAssessment assessment =
                fitnessRepository
                        .findById(fitnessId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Fitness assessment not found."
                                )
                        );


        // -------------------------------------------------
        // VERIFY ASSESSMENT BELONGS TO MEMBER
        // -------------------------------------------------

        if (assessment.getMember()
                .getMemberId()
                != member.getMemberId()) {

            throw new BadRequestException(
                    "Fitness assessment does not belong to this member."
            );
        }


        // -------------------------------------------------
        // UPDATE DATE
        // -------------------------------------------------

        assessment.setAssessmentDate(
                request.getAssessmentDate()
        );


        // -------------------------------------------------
        // UPDATE BODY
        // -------------------------------------------------

        assessment.setWeight(
                request.getWeight()
        );


        assessment.setHeight(
                request.getHeight()
        );


        assessment.setBodyFat(
                request.getBodyFat()
        );


        // -------------------------------------------------
        // UPDATE MEASUREMENTS
        // -------------------------------------------------

        assessment.setChest(
                request.getChest()
        );


        assessment.setWaist(
                request.getWaist()
        );


        assessment.setHips(
                request.getHips()
        );


        assessment.setArms(
                request.getArms()
        );


        assessment.setThigh(
                request.getThigh()
        );


        // -------------------------------------------------
        // RECALCULATE BMI
        // -------------------------------------------------

        if (request.getWeight() != null &&
                request.getHeight() != null &&
                request.getHeight()
                        .compareTo(
                                java.math.BigDecimal.ZERO
                        ) > 0) {

            java.math.BigDecimal heightInMeters =
                    request.getHeight()
                            .divide(
                                    new java.math.BigDecimal("100"),
                                    4,
                                    java.math.RoundingMode.HALF_UP
                            );


            java.math.BigDecimal bmi =
                    request.getWeight()
                            .divide(
                                    heightInMeters
                                            .multiply(
                                                    heightInMeters
                                            ),
                                    2,
                                    java.math.RoundingMode.HALF_UP
                            );


            assessment.setBmi(
                    bmi
            );

        } else {

            assessment.setBmi(
                    null
            );
        }


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        assessment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        MemberFitnessAssessment saved =
                fitnessRepository.save(
                        assessment
                );


        // -------------------------------------------------
        // RESPONSE
        // -------------------------------------------------

        return mapToResponse(
                saved
        );
    }

    // =====================================================
// UPDATE ASSESSMENT WITH IMAGES
// =====================================================

    public MemberFitnessAssessmentResponse
    updateAssessmentWithImages(

            Tenant tenant,

            Integer memberId,

            Integer fitnessId,

            MemberFitnessAssessmentRequest request,

            MultipartFile frontImage,

            MultipartFile sideImage,

            MultipartFile backImage,

            Boolean replaceFront,

            Boolean replaceSide,

            Boolean replaceBack)
            throws IOException {


        // -------------------------------------------------
        // VERIFY MEMBER
        // -------------------------------------------------

        Member member =
                verifyMemberTenant(
                        tenant,
                        memberId
                );


        // -------------------------------------------------
        // FIND ASSESSMENT
        // -------------------------------------------------

        MemberFitnessAssessment assessment =
                fitnessRepository
                        .findById(fitnessId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Fitness assessment not found."
                                )
                        );


        // -------------------------------------------------
        // VERIFY MEMBER OWNERSHIP
        // -------------------------------------------------

        if (assessment.getMember()
                .getMemberId()
                != member.getMemberId()) {

            throw new BadRequestException(
                    "Fitness assessment does not belong to this member."
            );
        }


        // -------------------------------------------------
        // KEEP OLD IMAGE PATHS
        // -------------------------------------------------

        String oldFront =
                assessment.getFrontImage();

        String oldSide =
                assessment.getSideImage();

        String oldBack =
                assessment.getBackImage();


        String newFront =
                oldFront;

        String newSide =
                oldSide;

        String newBack =
                oldBack;


        try {

            // =================================================
            // FRONT
            // =================================================

            if (Boolean.TRUE.equals(replaceFront)) {

                if (frontImage != null &&
                        !frontImage.isEmpty()) {

                    newFront =
                            imageStorageService.saveImage(
                                    frontImage,
                                    tenant.getTenantId(),
                                    memberId,
                                    fitnessId,
                                    "front"
                            );

                } else {

                    newFront = null;
                }
            }


            // =================================================
            // SIDE
            // =================================================

            if (Boolean.TRUE.equals(replaceSide)) {

                if (sideImage != null &&
                        !sideImage.isEmpty()) {

                    newSide =
                            imageStorageService.saveImage(
                                    sideImage,
                                    tenant.getTenantId(),
                                    memberId,
                                    fitnessId,
                                    "side"
                            );

                } else {

                    newSide = null;
                }
            }


            // =================================================
            // BACK
            // =================================================

            if (Boolean.TRUE.equals(replaceBack)) {

                if (backImage != null &&
                        !backImage.isEmpty()) {

                    newBack =
                            imageStorageService.saveImage(
                                    backImage,
                                    tenant.getTenantId(),
                                    memberId,
                                    fitnessId,
                                    "back"
                            );

                } else {

                    newBack = null;
                }
            }


            // =================================================
            // UPDATE TEXT / MEASUREMENTS
            // =================================================

            assessment.setAssessmentDate(
                    request.getAssessmentDate()
            );


            assessment.setWeight(
                    request.getWeight()
            );


            assessment.setHeight(
                    request.getHeight()
            );


            assessment.setBodyFat(
                    request.getBodyFat()
            );


            assessment.setChest(
                    request.getChest()
            );


            assessment.setWaist(
                    request.getWaist()
            );


            assessment.setHips(
                    request.getHips()
            );


            assessment.setArms(
                    request.getArms()
            );


            assessment.setThigh(
                    request.getThigh()
            );


            assessment.setNotes(
                    request.getNotes()
            );


            // =================================================
            // BMI
            // =================================================

            assessment.setBmi(
                    calculateBmi(
                            request.getWeight(),
                            request.getHeight()
                    )
            );


            // =================================================
            // IMAGE PATHS
            // =================================================

            assessment.setFrontImage(
                    newFront
            );


            assessment.setSideImage(
                    newSide
            );


            assessment.setBackImage(
                    newBack
            );


            // =================================================
            // SAVE DATABASE
            // =================================================

            MemberFitnessAssessment saved =
                    fitnessRepository.save(
                            assessment
                    );


            // =================================================
            // DELETE OLD FILES
            //
            // Only after successful DB save.
            // =================================================

            if (Boolean.TRUE.equals(replaceFront) &&
                    oldFront != null &&
                    !oldFront.equals(newFront)) {

                imageStorageService.deleteImage(
                        oldFront
                );
            }


            if (Boolean.TRUE.equals(replaceSide) &&
                    oldSide != null &&
                    !oldSide.equals(newSide)) {

                imageStorageService.deleteImage(
                        oldSide
                );
            }


            if (Boolean.TRUE.equals(replaceBack) &&
                    oldBack != null &&
                    !oldBack.equals(newBack)) {

                imageStorageService.deleteImage(
                        oldBack
                );
            }


            return mapToResponse(
                    saved
            );

        } catch (Exception e) {

            // -------------------------------------------------
            // If new files were created but DB failed,
            // remove newly created files.
            // -------------------------------------------------

            if (newFront != null &&
                    !newFront.equals(oldFront)) {

                imageStorageService.deleteImage(
                        newFront
                );
            }


            if (newSide != null &&
                    !newSide.equals(oldSide)) {

                imageStorageService.deleteImage(
                        newSide
                );
            }


            if (newBack != null &&
                    !newBack.equals(oldBack)) {

                imageStorageService.deleteImage(
                        newBack
                );
            }


            throw e;
        }
    }

    // =====================================================
// CREATE ASSESSMENT WITH IMAGES
// =====================================================

    public MemberFitnessAssessmentResponse
    createAssessmentWithImages(

            Tenant tenant,

            Integer memberId,

            MemberFitnessAssessmentRequest request,

            MultipartFile frontImage,

            MultipartFile sideImage,

            MultipartFile backImage)
            throws IOException {


        // -------------------------------------------------
        // VERIFY MEMBER
        // -------------------------------------------------

        Member member =
                verifyMemberTenant(
                        tenant,
                        memberId
                );


        // -------------------------------------------------
        // CREATE ENTITY
        // -------------------------------------------------

        MemberFitnessAssessment assessment =
                new MemberFitnessAssessment();


        assessment.setMember(
                member
        );


        assessment.setAssessmentDate(
                request.getAssessmentDate()
        );


        assessment.setWeight(
                request.getWeight()
        );


        assessment.setHeight(
                request.getHeight()
        );


        assessment.setBodyFat(
                request.getBodyFat()
        );


        assessment.setChest(
                request.getChest()
        );


        assessment.setWaist(
                request.getWaist()
        );


        assessment.setHips(
                request.getHips()
        );


        assessment.setArms(
                request.getArms()
        );


        assessment.setThigh(
                request.getThigh()
        );


        assessment.setNotes(
                request.getNotes()
        );


        // -------------------------------------------------
        // CALCULATE BMI
        // -------------------------------------------------

        assessment.setBmi(
                calculateBmi(
                        request.getWeight(),
                        request.getHeight()
                )
        );


        // -------------------------------------------------
        // FIRST SAVE
        //
        // We need fitnessId before creating image folder.
        // -------------------------------------------------

        MemberFitnessAssessment saved =
                fitnessRepository.save(
                        assessment
                );


        Integer fitnessId =
                saved.getFitnessId();


        String frontPath = null;

        String sidePath = null;

        String backPath = null;


        try {

            // -------------------------------------------------
            // FRONT IMAGE
            // -------------------------------------------------

            if (frontImage != null &&
                    !frontImage.isEmpty()) {

                frontPath =
                        imageStorageService.saveImage(
                                frontImage,
                                tenant.getTenantId(),
                                memberId,
                                fitnessId,
                                "front"
                        );
            }


            // -------------------------------------------------
            // SIDE IMAGE
            // -------------------------------------------------

            if (sideImage != null &&
                    !sideImage.isEmpty()) {

                sidePath =
                        imageStorageService.saveImage(
                                sideImage,
                                tenant.getTenantId(),
                                memberId,
                                fitnessId,
                                "side"
                        );
            }


            // -------------------------------------------------
            // BACK IMAGE
            // -------------------------------------------------

            if (backImage != null &&
                    !backImage.isEmpty()) {

                backPath =
                        imageStorageService.saveImage(
                                backImage,
                                tenant.getTenantId(),
                                memberId,
                                fitnessId,
                                "back"
                        );
            }


            // -------------------------------------------------
            // SAVE IMAGE PATHS
            // -------------------------------------------------

            saved.setFrontImage(
                    frontPath
            );


            saved.setSideImage(
                    sidePath
            );


            saved.setBackImage(
                    backPath
            );


            saved =
                    fitnessRepository.save(
                            saved
                    );


            return mapToResponse(
                    saved
            );


        } catch (Exception e) {

            // -------------------------------------------------
            // CLEAN UP FILES
            // -------------------------------------------------

            imageStorageService.deleteImage(
                    frontPath
            );


            imageStorageService.deleteImage(
                    sidePath
            );


            imageStorageService.deleteImage(
                    backPath
            );


            fitnessRepository.delete(
                    saved
            );


            throw e;
        }
    }
}