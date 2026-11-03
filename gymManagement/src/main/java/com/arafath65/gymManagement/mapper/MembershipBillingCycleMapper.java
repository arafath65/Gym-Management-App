package com.arafath65.gymManagement.mapper;

import com.arafath65.gymManagement.dto.response.MembershipBillingCycleResponse;
import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import org.springframework.stereotype.Component;

@Component
public class MembershipBillingCycleMapper {

    public MembershipBillingCycleResponse toResponse(
            MembershipBillingCycle cycle) {

        if (cycle == null) {
            return null;
        }

        MembershipBillingCycleResponse response =
                new MembershipBillingCycleResponse();

        response.setMembershipBillingCycleId(
                cycle.getMembershipBillingCycleId()
        );

        // =================================================
        // TENANT
        // =================================================

        if (cycle.getTenant() != null) {

            response.setTenantId(
                    cycle.getTenant().getTenantId()
            );
        }

        // =================================================
        // BRANCH
        // =================================================

        if (cycle.getBranch() != null) {

            response.setBranchId(
                    cycle.getBranch().getBranchId()
            );

            response.setBranchName(
                    cycle.getBranch().getBranchName()
            );
        }

        // =================================================
        // MEMBER / MEMBERSHIP
        // =================================================

        if (cycle.getMemberMembership() != null) {

            response.setMemberMembershipId(
                    cycle.getMemberMembership()
                            .getMemberMembershipId()
            );

            response.setMembershipNumber(
                    cycle.getMemberMembership()
                            .getMembershipNumber()
            );

            if (cycle.getMemberMembership()
                    .getMember() != null) {

                response.setMemberId(
                        cycle.getMemberMembership()
                                .getMember()
                                .getMemberId()
                );
            }
        }

        // =================================================
        // CYCLE
        // =================================================

        response.setCycleNumber(
                cycle.getCycleNumber()
        );

        response.setBillingMode(
                cycle.getBillingMode()
        );

        // =================================================
        // PERIOD
        // =================================================

        response.setPeriodStart(
                cycle.getPeriodStart()
        );

        response.setPeriodEnd(
                cycle.getPeriodEnd()
        );

        // =================================================
        // FINANCIAL
        // =================================================

        response.setBaseAmount(
                cycle.getBaseAmount()
        );

        response.setDiscountAmount(
                cycle.getDiscountAmount()
        );

        response.setTotalAmount(
                cycle.getTotalAmount()
        );

        response.setPaidAmount(
                cycle.getPaidAmount()
        );

        response.setBalanceAmount(
                cycle.getBalanceAmount()
        );

        // =================================================
        // ACTIVATION
        // =================================================

        response.setActivatedDate(
                cycle.getActivatedDate()
        );

        // =================================================
        // STATUS
        // =================================================

        response.setStatus(
                cycle.getStatus()
        );

        // =================================================
        // NOTES
        // =================================================

        response.setNotes(
                cycle.getNotes()
        );

        // =================================================
        // AUDIT
        // =================================================

        response.setCreatedAt(
                cycle.getCreatedAt()
        );

        response.setUpdatedAt(
                cycle.getUpdatedAt()
        );

        return response;
    }
}