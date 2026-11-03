package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ProgramPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramPaymentRepository
        extends JpaRepository<ProgramPayment, Integer> {


    List<ProgramPayment>
    findByTenant_TenantIdAndProgramEnrollment_ProgramEnrollmentIdOrderByPaymentDateDescProgramPaymentIdDesc(
            Integer tenantId,
            Integer programEnrollmentId
    );


    List<ProgramPayment>
    findByTenant_TenantIdAndMember_MemberIdOrderByPaymentDateDescProgramPaymentIdDesc(
            Integer tenantId,
            Integer memberId
    );


    List<ProgramPayment>
    findByTenant_TenantIdAndProgramEnrollment_Program_ProgramIdOrderByPaymentDateDescProgramPaymentIdDesc(
            Integer tenantId,
            Integer programId
    );


    List<ProgramPayment>
    findByTenant_TenantIdAndPaymentDateBetweenOrderByPaymentDateDescProgramPaymentIdDesc(
            Integer tenantId,
            LocalDate startDate,
            LocalDate endDate
    );


    Optional<ProgramPayment>
    findByTenant_TenantIdAndPaymentNumber(
            Integer tenantId,
            String paymentNumber
    );


    boolean existsByTenant_TenantIdAndPaymentNumber(
            Integer tenantId,
            String paymentNumber
    );
}