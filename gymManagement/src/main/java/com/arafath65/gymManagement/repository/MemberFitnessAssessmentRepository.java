package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MemberFitnessAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberFitnessAssessmentRepository
        extends JpaRepository<MemberFitnessAssessment, Integer> {


    // =====================================================
    // GET ALL ASSESSMENTS FOR MEMBER
    // =====================================================

    List<MemberFitnessAssessment>
    findByMemberMemberIdOrderByAssessmentDateDesc(
            Integer memberId
    );


    // =====================================================
    // GET LATEST ASSESSMENT
    // =====================================================

    Optional<MemberFitnessAssessment>
    findFirstByMemberMemberIdOrderByAssessmentDateDesc(
            Integer memberId
    );
}