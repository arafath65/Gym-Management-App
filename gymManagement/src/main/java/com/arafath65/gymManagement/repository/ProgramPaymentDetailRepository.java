package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ProgramPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramPaymentDetailRepository
        extends JpaRepository<
        ProgramPaymentDetail,
        Integer> {


    List<ProgramPaymentDetail>
    findByProgramPayment_ProgramPaymentId(
            Integer programPaymentId
    );
}