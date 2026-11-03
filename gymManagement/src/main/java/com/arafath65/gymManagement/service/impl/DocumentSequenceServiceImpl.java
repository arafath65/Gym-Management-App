package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.entity.DocumentSequence;
import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.DocumentSequenceRepository;
import com.arafath65.gymManagement.repository.TenantRepository;
import com.arafath65.gymManagement.service.DocumentSequenceService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class DocumentSequenceServiceImpl
        implements DocumentSequenceService {

    private final DocumentSequenceRepository
            documentSequenceRepository;

    private final TenantRepository
            tenantRepository;


    public DocumentSequenceServiceImpl(
            DocumentSequenceRepository documentSequenceRepository,
            TenantRepository tenantRepository) {

        this.documentSequenceRepository =
                documentSequenceRepository;

        this.tenantRepository =
                tenantRepository;
    }


    @Override
    @Transactional
    public String nextNumber(
            Integer tenantId,
            DocumentSequence.DocumentType documentType) {

        DocumentSequence sequence =
                documentSequenceRepository
                        .findByTenant_TenantIdAndDocumentType(
                                tenantId,
                                documentType
                        )
                        .orElseGet(() -> {

                            Tenant tenant =
                                    tenantRepository
                                            .findById(
                                                    tenantId
                                            )
                                            .orElseThrow(() ->
                                                    new BadRequestException(
                                                            "Tenant not found."
                                                    )
                                            );

                            DocumentSequence newSequence =
                                    new DocumentSequence();

                            newSequence.setTenant(
                                    tenant
                            );

                            newSequence.setDocumentType(
                                    documentType
                            );

                            newSequence.setNextNumber(
                                    1L
                            );

                            return documentSequenceRepository
                                    .save(
                                            newSequence
                                    );
                        });


        Long currentNumber =
                sequence.getNextNumber();


        sequence.setNextNumber(
                currentNumber + 1
        );


        documentSequenceRepository.save(
                sequence
        );


        return formatNumber(
                documentType,
                currentNumber
        );
    }


    private String formatNumber(
            DocumentSequence.DocumentType documentType,
            Long number) {

        String prefix;

        switch (documentType) {

            case MEMBERSHIP:
                prefix = "MEM-";
                break;

            case MEMBERSHIP_PAYMENT:
                prefix = "PAY-";
                break;

            case MEMBERSHIP_RENEWAL:
                prefix = "REN-";
                break;

            default:
                throw new BadRequestException(
                        "Unsupported document type."
                );
        }


        return String.format(
                "%s%06d",
                prefix,
                number
        );
    }
}