package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "program_payment_details",
        indexes = {

                @Index(
                        name = "idx_program_payment_detail_payment",
                        columnList = "program_payment_id"
                ),

                @Index(
                        name = "idx_program_payment_detail_method",
                        columnList = "payment_method"
                )
        }
)
public class ProgramPaymentDetail {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "program_payment_detail_id"
    )
    private Integer programPaymentDetailId;


    // =====================================================
    // PROGRAM PAYMENT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "program_payment_id",
            nullable = false
    )
    private ProgramPayment programPayment;


    // =====================================================
    // PAYMENT METHOD
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;


    // =====================================================
    // AMOUNT
    // =====================================================

    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount =
            BigDecimal.ZERO;


    // =====================================================
    // REFERENCE
    // =====================================================

    @Column(
            name = "reference_number",
            length = 100
    )
    private String referenceNumber;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getProgramPaymentDetailId() {

        return programPaymentDetailId;
    }

    public void setProgramPaymentDetailId(
            Integer programPaymentDetailId) {

        this.programPaymentDetailId =
                programPaymentDetailId;
    }


    public ProgramPayment getProgramPayment() {

        return programPayment;
    }

    public void setProgramPayment(
            ProgramPayment programPayment) {

        this.programPayment =
                programPayment;
    }


    public PaymentMethod getPaymentMethod() {

        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }


    public BigDecimal getAmount() {

        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount =
                amount;
    }


    public String getReferenceNumber() {

        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }


    // =====================================================
    // ENUM
    // =====================================================

    public enum PaymentMethod {

        CASH,

        CARD,

        BANK_TRANSFER,

        CHEQUE
    }
}