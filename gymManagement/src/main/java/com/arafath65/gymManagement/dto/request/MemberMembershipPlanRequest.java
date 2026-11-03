
package com.arafath65.gymManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MemberMembershipPlanRequest {

    @NotNull(message = "Membership plan is required.")
    private Integer membershipPlanId;

    private LocalDate startDate;

    /*
     * Settlement choices:
     * NONE        - No adjustment.
     * CARRY_FORWARD - Apply an approved credit to the new membership.
     * REFUND      - Record an approved refund decision.
     * WAIVE       - Record an approved waiver.
     */
    @NotNull(message = "Settlement type is required.")
    private SettlementType settlementType = SettlementType.NONE;

    @DecimalMin(
            value = "0.00",
            message = "Settlement amount cannot be negative."
    )
    private BigDecimal settlementAmount = BigDecimal.ZERO;

    @Size(max = 1000, message = "Settlement reason cannot exceed 1000 characters.")
    private String settlementReason;

    private String notes;

    public enum SettlementType {
        NONE,
        CARRY_FORWARD,
        REFUND,
        WAIVE
    }

    public MemberMembershipPlanRequest() {
    }

    public Integer getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(Integer membershipPlanId) {
        this.membershipPlanId = membershipPlanId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public SettlementType getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getSettlementReason() {
        return settlementReason;
    }

    public void setSettlementReason(String settlementReason) {
        this.settlementReason = settlementReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
