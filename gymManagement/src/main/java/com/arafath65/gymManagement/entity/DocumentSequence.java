package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "document_sequences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_document_sequence_tenant_type",
                        columnNames = {
                                "tenant_id",
                                "document_type"
                        }
                )
        }
)
public class DocumentSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_sequence_id")
    private Integer documentSequenceId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "document_type",
            nullable = false,
            length = 30
    )
    private DocumentType documentType;


    @Column(
            name = "next_number",
            nullable = false
    )
    private Long nextNumber = 1L;


    public Integer getDocumentSequenceId() {
        return documentSequenceId;
    }

    public void setDocumentSequenceId(
            Integer documentSequenceId) {
        this.documentSequenceId =
                documentSequenceId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }


    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(
            DocumentType documentType) {
        this.documentType =
                documentType;
    }


    public Long getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(
            Long nextNumber) {
        this.nextNumber =
                nextNumber;
    }


    public enum DocumentType {

        MEMBERSHIP,
        MEMBERSHIP_PAYMENT,
        MEMBERSHIP_RENEWAL
    }
}