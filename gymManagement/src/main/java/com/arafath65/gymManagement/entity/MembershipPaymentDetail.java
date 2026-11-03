package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "membership_payment_details",
        indexes = {

                @Index(
                        name = "idx_membership_payment_detail_payment",
                        columnList = "membership_payment_id"
                ),

                @Index(
                        name = "idx_membership_payment_detail_method",
                        columnList = "payment_method"
                )
        }
)
public class MembershipPaymentDetail {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "membership_payment_detail_id"
    )
    private Integer membershipPaymentDetailId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "membership_payment_id",
            nullable = false
    )
    private MembershipPayment membershipPayment;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private MembershipPayment.PaymentMethod paymentMethod;


    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount =
            BigDecimal.ZERO;


    @Column(
            name = "reference_number",
            length = 100
    )
    private String referenceNumber;


    public Integer getMembershipPaymentDetailId() {
        return membershipPaymentDetailId;
    }

    public void setMembershipPaymentDetailId(
            Integer membershipPaymentDetailId) {

        this.membershipPaymentDetailId =
                membershipPaymentDetailId;
    }


    public MembershipPayment getMembershipPayment() {
        return membershipPayment;
    }

    public void setMembershipPayment(
            MembershipPayment membershipPayment) {

        this.membershipPayment =
                membershipPayment;
    }


    public MembershipPayment.PaymentMethod
    getPaymentMethod() {

        return paymentMethod;
    }

    public void setPaymentMethod(
            MembershipPayment.PaymentMethod
                    paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount = amount;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }
}