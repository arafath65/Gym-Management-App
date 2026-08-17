package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipPlanService(
            MembershipPlanRepository membershipPlanRepository) {

        this.membershipPlanRepository =
                membershipPlanRepository;
    }


    // SAVE

    public MembershipPlan save(
            MembershipPlan membershipPlan) {

        if (membershipPlan == null) {
            throw new IllegalArgumentException(
                    "Membership plan cannot be null."
            );
        }

        if (membershipPlan.getTenant() == null) {
            throw new IllegalArgumentException(
                    "Tenant is required."
            );
        }

        if (membershipPlan.getPlanCode() == null
                || membershipPlan.getPlanCode()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Plan code is required."
            );
        }

        if (membershipPlan.getPlanName() == null
                || membershipPlan.getPlanName()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Plan name is required."
            );
        }

        if (membershipPlan.getPrice() == null
                || membershipPlan.getPrice()
                .signum() < 0) {

            throw new IllegalArgumentException(
                    "Plan price cannot be negative."
            );
        }

        if (membershipPlan.getJoiningFee() == null
                || membershipPlan.getJoiningFee()
                .signum() < 0) {

            throw new IllegalArgumentException(
                    "Joining fee cannot be negative."
            );
        }

        if (membershipPlan.getRenewalFee() == null
                || membershipPlan.getRenewalFee()
                .signum() < 0) {

            throw new IllegalArgumentException(
                    "Renewal fee cannot be negative."
            );
        }

        String planCode =
                membershipPlan.getPlanCode()
                        .trim()
                        .toUpperCase();

        membershipPlan.setPlanCode(planCode);

        Integer tenantId =
                membershipPlan.getTenant()
                        .getTenantId();

        Integer branchId = null;

        if (membershipPlan.getBranch() != null) {
            branchId =
                    membershipPlan.getBranch()
                            .getBranchId();
        }

        boolean exists;

        if (membershipPlan.getMembershipPlanId() == null) {

            if (branchId == null) {

                exists =
                        membershipPlanRepository
                                .existsByTenant_TenantIdAndPlanCode(
                                        tenantId,
                                        planCode
                                );

            } else {

                exists =
                        membershipPlanRepository
                                .existsByTenant_TenantIdAndBranch_BranchIdAndPlanCode(
                                        tenantId,
                                        branchId,
                                        planCode
                                );
            }

        } else {

            Optional<MembershipPlan> existingPlan =
                    membershipPlanRepository.findById(
                            membershipPlan
                                    .getMembershipPlanId()
                    );

            if (existingPlan.isPresent()) {

                MembershipPlan existing =
                        existingPlan.get();

                if (!existing.getPlanCode()
                        .equalsIgnoreCase(planCode)) {

                    if (branchId == null) {

                        exists =
                                membershipPlanRepository
                                        .existsByTenant_TenantIdAndPlanCode(
                                                tenantId,
                                                planCode
                                        );

                    } else {

                        exists =
                                membershipPlanRepository
                                        .existsByTenant_TenantIdAndBranch_BranchIdAndPlanCode(
                                                tenantId,
                                                branchId,
                                                planCode
                                        );
                    }

                } else {

                    exists = false;
                }

            } else {

                exists = false;
            }
        }

        if (exists) {
            throw new IllegalArgumentException(
                    "Membership plan code already exists."
            );
        }

        validateDuration(membershipPlan);
        validateBilling(membershipPlan);
        validateFreeze(membershipPlan);

        return membershipPlanRepository.save(
                membershipPlan
        );
    }


    // FIND BY ID

    @Transactional(readOnly = true)
    public Optional<MembershipPlan> findById(
            Integer membershipPlanId) {

        return membershipPlanRepository.findById(
                membershipPlanId
        );
    }


    // ALL TENANT PLANS

    @Transactional(readOnly = true)
    public List<MembershipPlan> findByTenant(
            Integer tenantId) {

        return membershipPlanRepository
                .findByTenant_TenantIdOrderByPlanNameAsc(
                        tenantId
                );
    }


    // BRANCH PLANS

    @Transactional(readOnly = true)
    public List<MembershipPlan> findByBranch(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByPlanNameAsc(
                        tenantId,
                        branchId
                );
    }


    // ACTIVE TENANT PLANS

    @Transactional(readOnly = true)
    public List<MembershipPlan> findActiveByTenant(
            Integer tenantId) {

        return membershipPlanRepository
                .findByTenant_TenantIdAndStatusTrueOrderByPlanNameAsc(
                        tenantId
                );
    }


    // ACTIVE BRANCH PLANS

    @Transactional(readOnly = true)
    public List<MembershipPlan> findActiveByBranch(
            Integer tenantId,
            Integer branchId) {

        return membershipPlanRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndStatusTrueOrderByPlanNameAsc(
                        tenantId,
                        branchId
                );
    }


    // FIND BY CODE

    @Transactional(readOnly = true)
    public Optional<MembershipPlan> findByPlanCode(
            Integer tenantId,
            String planCode) {

        if (planCode == null
                || planCode.trim().isEmpty()) {

            return Optional.empty();
        }

        return membershipPlanRepository
                .findByTenant_TenantIdAndPlanCode(
                        tenantId,
                        planCode.trim().toUpperCase()
                );
    }


    // FIND BY BRANCH AND CODE

    @Transactional(readOnly = true)
    public Optional<MembershipPlan> findByBranchAndPlanCode(
            Integer tenantId,
            Integer branchId,
            String planCode) {

        if (planCode == null
                || planCode.trim().isEmpty()) {

            return Optional.empty();
        }

        return membershipPlanRepository
                .findByTenant_TenantIdAndBranch_BranchIdAndPlanCode(
                        tenantId,
                        branchId,
                        planCode.trim().toUpperCase()
                );
    }


    // ACTIVATE

    public MembershipPlan activate(
            Integer membershipPlanId) {

        MembershipPlan plan =
                membershipPlanRepository.findById(
                        membershipPlanId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership plan not found."
                        )
                );

        plan.setStatus(true);

        return membershipPlanRepository.save(plan);
    }


    // DEACTIVATE

    public MembershipPlan deactivate(
            Integer membershipPlanId) {

        MembershipPlan plan =
                membershipPlanRepository.findById(
                        membershipPlanId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "Membership plan not found."
                        )
                );

        plan.setStatus(false);

        return membershipPlanRepository.save(plan);
    }


    // DELETE

    public void delete(
            Integer membershipPlanId) {

        if (!membershipPlanRepository
                .existsById(membershipPlanId)) {

            throw new IllegalArgumentException(
                    "Membership plan not found."
            );
        }

        membershipPlanRepository.deleteById(
                membershipPlanId
        );
    }


    // VALIDATION

    private void validateDuration(
            MembershipPlan plan) {

        if (plan.getDurationType() == null) {

            throw new IllegalArgumentException(
                    "Duration type is required."
            );
        }

        if (plan.getDurationType()
                != MembershipPlan.DurationType.UNLIMITED) {

            if (plan.getDurationValue() == null
                    || plan.getDurationValue() <= 0) {

                throw new IllegalArgumentException(
                        "Duration value must be greater than zero."
                );
            }

        } else {

            plan.setDurationValue(null);
        }
    }


    private void validateBilling(
            MembershipPlan plan) {

        if (plan.getBillingType() == null) {

            throw new IllegalArgumentException(
                    "Billing type is required."
            );
        }

        if (plan.getBillingType()
                == MembershipPlan.BillingType.ONE_TIME) {

            plan.setBillingFrequency(null);
            plan.setBillingFrequencyValue(null);

            return;
        }

        if (plan.getBillingFrequency() == null) {

            throw new IllegalArgumentException(
                    "Billing frequency is required."
            );
        }

        if (plan.getBillingFrequencyValue() == null
                || plan.getBillingFrequencyValue() <= 0) {

            throw new IllegalArgumentException(
                    "Billing frequency value must be greater than zero."
            );
        }
    }


    private void validateFreeze(
            MembershipPlan plan) {

        if (plan.getFreezeAllowed() == null) {
            plan.setFreezeAllowed(false);
        }

        if (!plan.getFreezeAllowed()) {

            plan.setMaxFreezeDays(0);
            plan.setFreezeFee(
                    java.math.BigDecimal.ZERO
            );

            return;
        }

        if (plan.getMaxFreezeDays() == null
                || plan.getMaxFreezeDays() < 0) {

            throw new IllegalArgumentException(
                    "Maximum freeze days cannot be negative."
            );
        }

        if (plan.getFreezeFee() == null
                || plan.getFreezeFee().signum() < 0) {

            throw new IllegalArgumentException(
                    "Freeze fee cannot be negative."
            );
        }
    }
}