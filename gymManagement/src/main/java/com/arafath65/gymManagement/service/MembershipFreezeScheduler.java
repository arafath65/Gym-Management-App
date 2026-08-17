package com.arafath65.gymManagement.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MembershipFreezeScheduler {

    private final MembershipFreezeService membershipFreezeService;


    public MembershipFreezeScheduler(
            MembershipFreezeService membershipFreezeService) {

        this.membershipFreezeService =
                membershipFreezeService;
    }


    // =====================================================
    // PROCESS EXPIRED FREEZES
    // =====================================================

    @Scheduled(
            cron = "0 0 0 * * *"
    )
    public void processExpiredFreezes() {

        membershipFreezeService
                .processExpiredFreezes();
    }
}