package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.entity.Tenant;
import com.arafath65.gymManagement.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MembershipExpiryService {

    private final TenantRepository tenantRepository;

    private final MemberMembershipService memberMembershipService;

    private final MembershipFreezeService membershipFreezeService;


    public MembershipExpiryService(
            TenantRepository tenantRepository,
            MemberMembershipService memberMembershipService,
            MembershipFreezeService membershipFreezeService) {

        this.tenantRepository =
                tenantRepository;

        this.memberMembershipService =
                memberMembershipService;

        this.membershipFreezeService =
                membershipFreezeService;
    }


    // =====================================================
    // PROCESS ALL TENANTS
    // =====================================================

    public void processAllTenants() {

        List<Tenant> tenants =
                tenantRepository.findAll();


        for (Tenant tenant : tenants) {

            if (!Boolean.TRUE.equals(
                    tenant.getStatus()
            )) {

                continue;
            }


            // -------------------------------------------------
            // PROCESS MEMBERSHIP EXPIRY
            // -------------------------------------------------

            memberMembershipService
                    .updateExpiredMemberships(
                            tenant.getTenantId()
                    );


            // -------------------------------------------------
            // PROCESS FREEZE COMPLETION
            // -------------------------------------------------

            membershipFreezeService
                    .completeExpiredFreezes(
                            tenant.getTenantId()
                    );
        }
    }
}