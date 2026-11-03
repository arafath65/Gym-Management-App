package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class ProgramBranchRequest {

    // =====================================================
    // BRANCH
    // =====================================================

    private Integer branchId;


    // =====================================================
    // PROGRAM FEE
    // =====================================================

    private BigDecimal fee;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramBranchRequest() {
    }


    // =====================================================
    // BRANCH ID
    // =====================================================

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;
    }


    // =====================================================
    // FEE
    // =====================================================

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(
            BigDecimal fee) {

        this.fee =
                fee;
    }
}