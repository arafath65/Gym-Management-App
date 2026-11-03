package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class BenefitBranchRequest {

    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;


    // =====================================================
    // FEE
    // =====================================================

    /*
     * NULL = use Benefit.defaultFee
     *
     * Value = branch-specific fee
     */

    private BigDecimal fee;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitBranchRequest() {
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }
}