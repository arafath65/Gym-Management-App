package com.arafath65.gymManagement.service.impl;

import com.arafath65.gymManagement.dto.request.MembershipBillingCycleRequest;
import com.arafath65.gymManagement.entity.MemberMembership;
import com.arafath65.gymManagement.entity.MembershipBillingCycle;
import com.arafath65.gymManagement.entity.MembershipPlan;
import com.arafath65.gymManagement.exception.BadRequestException;
import com.arafath65.gymManagement.repository.MemberMembershipRepository;
import com.arafath65.gymManagement.repository.MembershipBillingCycleRepository;
import com.arafath65.gymManagement.service.MembershipBillingCycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembershipBillingCycleServiceImpl
        implements MembershipBillingCycleService {

    private final MembershipBillingCycleRepository
            billingCycleRepository;

    private final MemberMembershipRepository
            memberMembershipRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipBillingCycleServiceImpl(
            MembershipBillingCycleRepository billingCycleRepository,
            MemberMembershipRepository memberMembershipRepository) {

        this.billingCycleRepository =
                billingCycleRepository;

        this.memberMembershipRepository =
                memberMembershipRepository;
    }


    // =====================================================
    // CREATE CYCLE
    // =====================================================

    @Override
    public MembershipBillingCycle createCycle(
            Integer tenantId,
            MembershipBillingCycleRequest request) {

        validateTenantId(tenantId);

        if (request == null) {

            throw new BadRequestException(
                    "Billing cycle request is required."
            );
        }

        if (request.getMemberMembershipId() == null) {

            throw new BadRequestException(
                    "Membership is required."
            );
        }

        if (request.getBillingMode() == null) {

            throw new BadRequestException(
                    "Billing mode is required."
            );
        }

        if (request.getPeriodStart() == null) {

            throw new BadRequestException(
                    "Period start date is required."
            );
        }

        if (request.getPeriodEnd() == null) {

            throw new BadRequestException(
                    "Period end date is required."
            );
        }

        if (request.getPeriodEnd()
                .isBefore(request.getPeriodStart())) {

            throw new BadRequestException(
                    "Period end date cannot be before period start date."
            );
        }


        // =================================================
        // AMOUNTS
        // =================================================

        BigDecimal baseAmount =
                normalizeAmount(
                        request.getBaseAmount()
                );

        BigDecimal discountAmount =
                normalizeAmount(
                        request.getDiscountAmount()
                );


        if (discountAmount.compareTo(
                baseAmount
        ) > 0) {

            throw new BadRequestException(
                    "Discount amount cannot be greater than base amount."
            );
        }


        BigDecimal totalAmount =
                baseAmount
                        .subtract(discountAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // =================================================
        // LOAD MEMBERSHIP
        // =================================================

        MemberMembership membership =
                getMembership(
                        tenantId,
                        request.getMemberMembershipId()
                );


        // =================================================
        // VALIDATE MEMBERSHIP
        // =================================================

        validateMembershipForBilling(
                membership,
                request.getBillingMode()
        );


        // =================================================
        // VALIDATE PERIOD
        // =================================================

        validatePeriodForBillingMode(
                request.getBillingMode(),
                request.getPeriodStart(),
                request.getPeriodEnd()
        );


        // =================================================
        // CHECK DUPLICATE PERIOD
        // =================================================

        boolean periodExists =
                billingCycleRepository
                        .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndPeriodStartAndPeriodEnd(
                                tenantId,
                                membership.getMemberMembershipId(),
                                request.getPeriodStart(),
                                request.getPeriodEnd()
                        );


        if (periodExists) {

            throw new BadRequestException(
                    "A billing cycle already exists for this membership and period."
            );
        }


        // =================================================
        // DAILY DUPLICATE CHECK
        // =================================================

        if (request.getBillingMode()
                == MembershipPlan.AccessBillingMode.DAILY) {

            boolean dailyCycleExists =
                    billingCycleRepository
                            .existsByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndPeriodStartAndPeriodEnd(
                                    tenantId,
                                    membership.getMemberMembershipId(),
                                    request.getPeriodStart(),
                                    request.getPeriodStart()
                            );


            if (dailyCycleExists) {

                throw new BadRequestException(
                        "A daily billing cycle already exists for this date."
                );
            }
        }


        // =================================================
        // CREATE ENTITY
        // =================================================

        MembershipBillingCycle cycle =
                new MembershipBillingCycle();


        cycle.setTenant(
                membership.getTenant()
        );


        cycle.setBranch(
                membership.getBranch()
        );


        cycle.setMemberMembership(
                membership
        );


        cycle.setCycleNumber(
                generateCycleNumber(
                        tenantId
                )
        );


        cycle.setBillingMode(
                request.getBillingMode()
        );


        cycle.setPeriodStart(
                request.getPeriodStart()
        );


        cycle.setPeriodEnd(
                request.getPeriodEnd()
        );


        cycle.setBaseAmount(
                baseAmount
        );


        cycle.setDiscountAmount(
                discountAmount
        );


        cycle.setTotalAmount(
                totalAmount
        );


        cycle.setPaidAmount(
                BigDecimal.ZERO.setScale(2)
        );


        cycle.setBalanceAmount(
                totalAmount
        );


        /*
         * New billing cycle starts as NOT_ACTIVATED.
         *
         * Activation is a separate business action.
         */
        cycle.setStatus(
                MembershipBillingCycle.CycleStatus.NOT_ACTIVATED
        );


        cycle.setNotes(
                request.getNotes()
        );


        return billingCycleRepository.save(
                cycle
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipBillingCycle> findById(
            Integer tenantId,
            Integer billingCycleId) {

        validateTenantId(tenantId);

        if (billingCycleId == null) {

            throw new BadRequestException(
                    "Billing cycle ID is required."
            );
        }


        return billingCycleRepository
                .findById(billingCycleId)
                .filter(cycle ->
                        cycle.getTenant() != null
                                && cycle.getTenant()
                                .getTenantId()
                                == tenantId
                );
    }


    // =====================================================
    // FIND BY MEMBERSHIP
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipBillingCycle> findByMembership(
            Integer tenantId,
            Integer memberMembershipId) {

        validateTenantId(tenantId);

        if (memberMembershipId == null) {

            throw new BadRequestException(
                    "Membership ID is required."
            );
        }


        return billingCycleRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdOrderByPeriodStartDesc(
                        tenantId,
                        memberMembershipId
                );
    }


    // =====================================================
    // FIND BY MEMBER
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipBillingCycle> findByMember(
            Integer tenantId,
            Integer memberId) {

        validateTenantId(tenantId);

        if (memberId == null) {

            throw new BadRequestException(
                    "Member ID is required."
            );
        }


        return billingCycleRepository
                .findByTenant_TenantIdAndMemberMembership_Member_MemberIdOrderByPeriodStartDesc(
                        tenantId,
                        memberId
                );
    }


    // =====================================================
    // FIND BY BRANCH
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipBillingCycle> findByBranch(
            Integer tenantId,
            Integer branchId) {

        validateTenantId(tenantId);

        if (branchId == null) {

            throw new BadRequestException(
                    "Branch ID is required."
            );
        }


        return billingCycleRepository
                .findByTenant_TenantIdAndBranch_BranchIdOrderByPeriodStartDesc(
                        tenantId,
                        branchId
                );
    }


    // =====================================================
    // FIND CURRENT CYCLE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipBillingCycle> findCurrentCycle(
            Integer tenantId,
            Integer memberMembershipId,
            LocalDate date) {

        validateTenantId(tenantId);

        if (memberMembershipId == null) {

            throw new BadRequestException(
                    "Membership ID is required."
            );
        }

        if (date == null) {

            throw new BadRequestException(
                    "Date is required."
            );
        }


        return billingCycleRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                        tenantId,
                        memberMembershipId,
                        date,
                        date
                );
    }


    // =====================================================
    // ACTIVATE CYCLE
    // =====================================================

    @Override
    public MembershipBillingCycle activateCycle(
            Integer tenantId,
            Integer billingCycleId) {

        validateTenantId(tenantId);

        MembershipBillingCycle cycle =
                getCycle(
                        tenantId,
                        billingCycleId
                );


        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled billing cycle cannot be activated."
            );
        }


        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.PAID) {

            throw new BadRequestException(
                    "Billing cycle is already fully paid."
            );
        }


        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.EXPIRED) {

            throw new BadRequestException(
                    "Expired billing cycle cannot be activated."
            );
        }


        if (cycle.getTotalAmount() == null
                || cycle.getTotalAmount()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Billing cycle has an invalid total amount."
            );
        }


        if (cycle.getActivatedDate() == null) {

            cycle.setActivatedDate(
                    LocalDate.now()
            );
        }


        BigDecimal total =
                normalizeAmount(
                        cycle.getTotalAmount()
                );

        BigDecimal paid =
                normalizeAmount(
                        cycle.getPaidAmount()
                );


        if (paid.compareTo(total) >= 0) {

            cycle.setPaidAmount(total);

            cycle.setBalanceAmount(
                    BigDecimal.ZERO.setScale(2)
            );

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PAID
            );

        } else if (paid.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            cycle.setBalanceAmount(
                    total.subtract(paid)
            );

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PARTIALLY_PAID
            );

        } else {

            cycle.setBalanceAmount(
                    total
            );

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.ACTIVE
            );
        }


        return billingCycleRepository.save(
                cycle
        );
    }


    // =====================================================
    // CANCEL CYCLE
    // =====================================================

    @Override
    public void cancelCycle(
            Integer tenantId,
            Integer billingCycleId) {

        validateTenantId(tenantId);

        MembershipBillingCycle cycle =
                getCycle(
                        tenantId,
                        billingCycleId
                );


        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.CANCELLED) {

            throw new BadRequestException(
                    "Billing cycle is already cancelled."
            );
        }


        if (cycle.getPaidAmount() != null
                && cycle.getPaidAmount()
                .compareTo(BigDecimal.ZERO) > 0) {

            throw new BadRequestException(
                    "A billing cycle with payments cannot be cancelled directly. Cancel or reverse its payments first."
            );
        }


        cycle.setStatus(
                MembershipBillingCycle.CycleStatus.CANCELLED
        );


        billingCycleRepository.save(
                cycle
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void delete(
            Integer tenantId,
            Integer billingCycleId) {

        validateTenantId(tenantId);

        MembershipBillingCycle cycle =
                getCycle(
                        tenantId,
                        billingCycleId
                );


        if (cycle.getPaidAmount() != null
                && cycle.getPaidAmount()
                .compareTo(BigDecimal.ZERO) > 0) {

            throw new BadRequestException(
                    "A billing cycle with payments cannot be deleted. Cancel or reverse payments first."
            );
        }


        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.ACTIVE
                || cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.PARTIALLY_PAID
                || cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.PAID) {

            throw new BadRequestException(
                    "Active or paid billing cycles cannot be deleted. Cancel them instead."
            );
        }


        billingCycleRepository.delete(
                cycle
        );
    }


    // =====================================================
    // REFRESH STATUS
    // =====================================================

    @Override
    @Transactional
    public MembershipBillingCycle refreshStatus(
            Integer tenantId,
            Integer billingCycleId) {

        validateTenantId(tenantId);

        MembershipBillingCycle cycle =
                getCycle(
                        tenantId,
                        billingCycleId
                );


        // =================================================
        // CANCELLED NEVER CHANGES AUTOMATICALLY
        // =================================================

        if (cycle.getStatus()
                == MembershipBillingCycle.CycleStatus.CANCELLED) {

            return cycle;
        }


        LocalDate today =
                LocalDate.now();


        // =================================================
        // EXPIRED PERIOD
        // =================================================

        if (cycle.getPeriodEnd() != null
                && cycle.getPeriodEnd()
                .isBefore(today)) {

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.EXPIRED
            );

            return billingCycleRepository.save(
                    cycle
            );
        }


        // =================================================
        // FINANCIAL STATUS
        // =================================================

        BigDecimal total =
                normalizeAmount(
                        cycle.getTotalAmount()
                );

        BigDecimal paid =
                normalizeAmount(
                        cycle.getPaidAmount()
                );


        if (paid.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            /*
             * If the cycle was created but never activated,
             * do not automatically activate it.
             */

            if (cycle.getStatus()
                    != MembershipBillingCycle.CycleStatus.NOT_ACTIVATED) {

                cycle.setStatus(
                        MembershipBillingCycle.CycleStatus.ACTIVE
                );
            }

            cycle.setBalanceAmount(
                    total
            );

        } else if (paid.compareTo(total) >= 0) {

            cycle.setPaidAmount(
                    total
            );

            cycle.setBalanceAmount(
                    BigDecimal.ZERO.setScale(2)
            );

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PAID
            );

        } else {

            cycle.setBalanceAmount(
                    total.subtract(paid)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            )
            );

            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.PARTIALLY_PAID
            );
        }


        return billingCycleRepository.save(
                cycle
        );
    }


    // =====================================================
    // EXPIRE COMPLETED CYCLES
    // =====================================================

    @Override
    @Transactional
    public int expireCompletedCycles(
            Integer tenantId,
            LocalDate date) {

        validateTenantId(tenantId);

        if (date == null) {
            date = LocalDate.now();
        }


        List<MembershipBillingCycle> cycles =
                billingCycleRepository
                        .findByTenant_TenantIdAndPeriodEndBeforeAndStatusNot(
                                tenantId,
                                date,
                                MembershipBillingCycle.CycleStatus.CANCELLED
                        );


        int count = 0;


        for (MembershipBillingCycle cycle :
                cycles) {

            if (cycle.getStatus()
                    == MembershipBillingCycle.CycleStatus.CANCELLED) {

                continue;
            }


            /*
             * A fully paid historical cycle is also no longer
             * an active period.
             */
            cycle.setStatus(
                    MembershipBillingCycle.CycleStatus.EXPIRED
            );

            count++;
        }


        if (!cycles.isEmpty()) {

            billingCycleRepository.saveAll(
                    cycles
            );
        }


        return count;
    }


    // =====================================================
    // FIND OUTSTANDING CYCLES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MembershipBillingCycle>
    findOutstandingCycles(
            Integer tenantId,
            Integer memberMembershipId) {

        validateTenantId(tenantId);

        if (memberMembershipId == null) {

            throw new BadRequestException(
                    "Membership ID is required."
            );
        }


        return billingCycleRepository
                .findByTenant_TenantIdAndMemberMembership_MemberMembershipIdAndBalanceAmountGreaterThanAndStatusNotOrderByPeriodStartAsc(
                        tenantId,
                        memberMembershipId,
                        BigDecimal.ZERO,
                        MembershipBillingCycle.CycleStatus.CANCELLED
                );
    }


    // =====================================================
    // LOAD MEMBERSHIP
    // =====================================================

    private MemberMembership getMembership(
            Integer tenantId,
            Integer memberMembershipId) {

        MemberMembership membership =
                memberMembershipRepository
                        .findById(
                                memberMembershipId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Membership not found."
                                )
                        );


        if (membership.getTenant() == null) {

            throw new BadRequestException(
                    "Membership tenant information is missing."
            );
        }


        if (membership.getTenant()
                .getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Membership does not belong to this tenant."
            );
        }


        return membership;
    }


    // =====================================================
    // LOAD CYCLE
    // =====================================================

    private MembershipBillingCycle getCycle(
            Integer tenantId,
            Integer billingCycleId) {

        if (billingCycleId == null) {

            throw new BadRequestException(
                    "Billing cycle ID is required."
            );
        }


        MembershipBillingCycle cycle =
                billingCycleRepository
                        .findById(
                                billingCycleId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Billing cycle not found."
                                )
                        );


        if (cycle.getTenant() == null) {

            throw new BadRequestException(
                    "Billing cycle tenant information is missing."
            );
        }


        if (cycle.getTenant()
                .getTenantId()
                != tenantId) {

            throw new BadRequestException(
                    "Billing cycle does not belong to this tenant."
            );
        }


        return cycle;
    }


    // =====================================================
    // VALIDATE MEMBERSHIP
    // =====================================================

    private void validateMembershipForBilling(
            MemberMembership membership,
            MembershipPlan.AccessBillingMode billingMode) {

        if (membership.getStatus() == null
                || !membership.getStatus()) {

            throw new BadRequestException(
                    "Membership is inactive."
            );
        }


        if (membership.getMembershipState()
                == MemberMembership.MembershipState.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled membership cannot be billed."
            );
        }


        if (membership.getMembershipPlan() == null) {

            throw new BadRequestException(
                    "Membership plan is missing."
            );
        }


        MembershipPlan plan =
                membership.getMembershipPlan();


        if (plan.getAccessBillingMode()
                != billingMode) {

            throw new BadRequestException(
                    "Billing mode does not match the membership plan."
            );
        }
    }


    // =====================================================
    // VALIDATE BILLING PERIOD
    // =====================================================

    private void validatePeriodForBillingMode(
            MembershipPlan.AccessBillingMode billingMode,
            LocalDate start,
            LocalDate end) {


        if (billingMode
                == MembershipPlan.AccessBillingMode.DAILY) {

            if (!start.equals(end)) {

                throw new BadRequestException(
                        "Daily billing cycle must have the same start and end date."
                );
            }
        }


        if (billingMode
                == MembershipPlan.AccessBillingMode.MONTHLY_FLEX
                || billingMode
                == MembershipPlan.AccessBillingMode.MONTHLY_CONTRACT) {

            if (start.getDayOfMonth() != 1) {

                throw new BadRequestException(
                        "Monthly billing cycle must start on the first day of the month."
                );
            }


            if (!end.equals(
                    start.withDayOfMonth(
                            start.lengthOfMonth()
                    )
            )) {

                throw new BadRequestException(
                        "Monthly billing cycle must end on the last day of the month."
                );
            }
        }
    }


    // =====================================================
    // GENERATE CYCLE NUMBER
    // =====================================================

    private String generateCycleNumber(
            Integer tenantId) {

        return "CYC-"
                + tenantId
                + "-"
                + System.currentTimeMillis();
    }


    // =====================================================
    // NORMALIZE AMOUNT
    // =====================================================

    private BigDecimal normalizeAmount(
            BigDecimal amount) {

        if (amount == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        if (amount.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new BadRequestException(
                    "Amount cannot be negative."
            );
        }


        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // VALIDATE TENANT
    // =====================================================

    private void validateTenantId(
            Integer tenantId) {

        if (tenantId == null
                || tenantId <= 0) {

            throw new BadRequestException(
                    "Valid tenant ID is required."
            );
        }
    }
}