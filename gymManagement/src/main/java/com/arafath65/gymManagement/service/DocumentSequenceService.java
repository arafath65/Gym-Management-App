package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.DocumentSequence;

public interface DocumentSequenceService {

    String nextNumber(
            Integer tenantId,
            DocumentSequence.DocumentType documentType
    );
}