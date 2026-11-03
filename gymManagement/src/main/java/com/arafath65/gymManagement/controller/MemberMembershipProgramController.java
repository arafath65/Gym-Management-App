package com.arafath65.gymManagement.controller;

import com.arafath65.gymManagement.dto.request.MemberMembershipProgramRequest;
import com.arafath65.gymManagement.dto.response.MemberMembershipProgramResponse;
import com.arafath65.gymManagement.service.MemberMembershipProgramService;
import com.arafath65.gymManagement.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-membership-programs")
public class MemberMembershipProgramController {

    private final MemberMembershipProgramService
            memberMembershipProgramService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipProgramController(
            MemberMembershipProgramService
                    memberMembershipProgramService) {

        this.memberMembershipProgramService =
                memberMembershipProgramService;
    }


    // =====================================================
    // ASSIGN PROGRAM
    // =====================================================

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<MemberMembershipProgramResponse>>
    assignProgram(

            @RequestParam Integer tenantId,

            @RequestParam Integer memberMembershipId,

            @Valid
            @RequestBody
            MemberMembershipProgramRequest request) {

        MemberMembershipProgramResponse response =
                memberMembershipProgramService.assignProgram(
                        tenantId,
                        memberMembershipId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Program assigned successfully.",
                                response
                        )
                );
    }


    // =====================================================
    // GET PROGRAMS OF MEMBERSHIP
    // =====================================================

    @GetMapping("/membership/{memberMembershipId}")
    public ResponseEntity<ApiResponse<List<MemberMembershipProgramResponse>>>
    getPrograms(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberMembershipId) {

        List<MemberMembershipProgramResponse> response =
                memberMembershipProgramService.getPrograms(
                        tenantId,
                        memberMembershipId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Programs loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ALL PROGRAM HISTORY OF MEMBER
    // =====================================================

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<MemberMembershipProgramResponse>>>
    getProgramHistory(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberId) {

        List<MemberMembershipProgramResponse> response =
                memberMembershipProgramService.getProgramHistory(
                        tenantId,
                        memberId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Member program history loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // GET ONE
    // =====================================================

    @GetMapping("/{memberMembershipProgramId}")
    public ResponseEntity<ApiResponse<MemberMembershipProgramResponse>>
    getProgram(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberMembershipProgramId) {

        MemberMembershipProgramResponse response =
                memberMembershipProgramService.getProgram(
                        tenantId,
                        memberMembershipProgramId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program loaded successfully.",
                        response
                )
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{memberMembershipProgramId}")
    public ResponseEntity<ApiResponse<MemberMembershipProgramResponse>>
    updateProgram(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberMembershipProgramId,

            @Valid
            @RequestBody
            MemberMembershipProgramRequest request) {

        MemberMembershipProgramResponse response =
                memberMembershipProgramService.updateProgram(
                        tenantId,
                        memberMembershipProgramId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program updated successfully.",
                        response
                )
        );
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    @PatchMapping("/{memberMembershipProgramId}/deactivate")
    public ResponseEntity<ApiResponse<Void>>
    deactivateProgram(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberMembershipProgramId) {

        memberMembershipProgramService.deactivateProgram(
                tenantId,
                memberMembershipProgramId
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program deactivated successfully.",
                        null
                )
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    @PatchMapping("/{memberMembershipProgramId}/activate")
    public ResponseEntity<ApiResponse<Void>>
    activateProgram(

            @RequestParam Integer tenantId,

            @PathVariable Integer memberMembershipProgramId) {

        memberMembershipProgramService.activateProgram(
                tenantId,
                memberMembershipProgramId
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Program activated successfully.",
                        null
                )
        );
    }
}