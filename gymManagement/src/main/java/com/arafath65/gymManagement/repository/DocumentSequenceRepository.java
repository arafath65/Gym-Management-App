package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.DocumentSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import java.util.Optional;

@Repository
public interface DocumentSequenceRepository
        extends JpaRepository<DocumentSequence, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DocumentSequence>
    findByTenant_TenantIdAndDocumentType(
            Integer tenantId,
            DocumentSequence.DocumentType documentType
    );
}