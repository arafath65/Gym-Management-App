package com.arafath65.gymManagement.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MembershipExpiryScheduler {

    private final MembershipExpiryService membershipExpiryService;

    public MembershipExpiryScheduler(
            MembershipExpiryService membershipExpiryService) {

        this.membershipExpiryService =
                membershipExpiryService;
    }

    @Scheduled(
            cron = "0 0 0 * * *"
    )
    public void processMembershipExpiry() {

        membershipExpiryService
                .processAllTenants();
    }
}