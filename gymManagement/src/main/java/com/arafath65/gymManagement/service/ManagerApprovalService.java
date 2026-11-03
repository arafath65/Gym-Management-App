
package com.arafath65.gymManagement.service;

import com.arafath65.gymManagement.dto.request.ManagerApprovalRequest;
import com.arafath65.gymManagement.dto.response.ManagerApprovalResponse;
import com.arafath65.gymManagement.entity.ManagerApproval;
import com.arafath65.gymManagement.entity.Member;
import com.arafath65.gymManagement.entity.User;
import com.arafath65.gymManagement.repository.ManagerApprovalRepository;
import com.arafath65.gymManagement.repository.MemberRepository;
import com.arafath65.gymManagement.repository.RolePermissionRepository;
import com.arafath65.gymManagement.repository.UserRepository;
import com.arafath65.gymManagement.security.AuthenticatedUser;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class ManagerApprovalService {

    private static final String MEMBER_EDIT = "MEMBER_EDIT";
    private static final String MEMBER_DEACTIVATE = "MEMBER_DEACTIVATE";

    private static final int TOKEN_EXPIRY_MINUTES = 5;

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ManagerApprovalRepository managerApprovalRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    private final SecureRandom secureRandom = new SecureRandom();

    public ManagerApprovalService(
            UserRepository userRepository,
            MemberRepository memberRepository,
            ManagerApprovalRepository managerApprovalRepository,
            RolePermissionRepository rolePermissionRepository,
            PasswordEncoder passwordEncoder,
            AuditService auditService) {

        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.managerApprovalRepository = managerApprovalRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    // =====================================================
    // REQUEST MANAGER APPROVAL
    // =====================================================

    @Transactional
    public ManagerApprovalResponse requestApproval(
            Integer memberId,
            ManagerApprovalRequest request) {

        String operationCode = normalizeOperationCode(
                request.operationCode()
        );

        String requiredPermission = getRequiredPermission(operationCode);

        AuthenticatedUser requesterSession = getAuthenticatedUser();

        if (requesterSession.tenantId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "A valid tenant is required."
            );
        }

        User requester = userRepository
                .findByUserIdAndTenantTenantId(
                        requesterSession.userId(),
                        requesterSession.tenantId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Requesting user was not found."
                ));

        if (!Boolean.TRUE.equals(requester.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is inactive."
            );
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Member not found."
                ));

        if (!Objects.equals(
                member.getTenant().getTenantId(),
                requesterSession.tenantId())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Member not found."
            );
        }

        String managerUsername = request.username().trim();

        User approver = userRepository
                .findByTenantTenantIdAndUsername(
                        requesterSession.tenantId(),
                        managerUsername
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid manager username or password."
                ));

        if (!Boolean.TRUE.equals(approver.getStatus())
                || approver.getRole() == null
                || !Boolean.TRUE.equals(approver.getRole().getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The manager account or role is inactive."
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                approver.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid manager username or password."
            );
        }

        if (Objects.equals(
                requester.getUserId(),
                approver.getUserId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot approve your own operation."
            );
        }

        String managerRoleName = normalizeRoleName(
                approver.getRole().getRoleName()
        );

        boolean administrator =
                "GYM_OWNER".equals(managerRoleName)
                        || "SUPER_ADMIN".equals(managerRoleName);

        if (!administrator) {

            Integer managerRoleId = approver.getRole().getRoleId();

            boolean hasPermission =
                    managerRoleId != null
                            && rolePermissionRepository
                            .existsByRoleRoleIdAndPermissionPermissionCodeAndPermissionStatusTrue(
                                    managerRoleId,
                                    requiredPermission
                            );

            if (!hasPermission) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "The manager does not have permission to approve this operation."
                );
            }
        }

        // A branch-specific manager can approve only members
        // belonging to that branch. A null branch is tenant-wide.
        if (approver.getBranch() != null) {

            Integer managerBranchId =
                    approver.getBranch().getBranchId();

            Integer memberBranchId =
                    member.getBranch().getBranchId();

            if (!Objects.equals(managerBranchId, memberBranchId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "The manager cannot approve operations for members in another branch."
                );
            }
        }

        String rawToken = generateToken();

        ManagerApproval approval = new ManagerApproval();

        approval.setTenant(requester.getTenant());
        approval.setRequester(requester);
        approval.setApprover(approver);
        approval.setMember(member);
        approval.setOperationCode(operationCode);
        approval.setTokenHash(hashToken(rawToken));
        approval.setExpiresAt(
                LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)
        );

        managerApprovalRepository.save(approval);

        // Audit the granted approval.
        auditService.record(
                requester.getTenant().getTenantId(),
                member.getBranch().getBranchId(),
                "MANAGER_APPROVAL",
                "APPROVAL_GRANTED",
                "MEMBER",
                member.getMemberId(),
                "Manager approval granted for " + operationCode,
                null,
                Map.of(
                        "operationCode", operationCode,
                        "requestedByUserId", requester.getUserId(),
                        "requestedByUsername", requester.getUsername(),
                        "approvedByUserId", approver.getUserId(),
                        "approvedByUsername", approver.getUsername()
                )
        );

        return new ManagerApprovalResponse(
                rawToken,
                operationCode,
                member.getMemberId(),
                approver.getFullName(),
                approval.getExpiresAt()
        );
    }

    // =====================================================
    // CONSUME APPROVAL
    // =====================================================

    // Existing member-edit calls remain compatible.
    @Transactional
    public ManagerApproval consumeApproval(
            String rawToken,
            Integer memberId) {

        return consumeApproval(
                rawToken,
                memberId,
                MEMBER_EDIT
        );
    }

    @Transactional
    public ManagerApproval consumeApproval(
            String rawToken,
            Integer memberId,
            String expectedOperationCode) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Manager authorization is required."
            );
        }

        String expectedOperation =
                normalizeOperationCode(expectedOperationCode);

        AuthenticatedUser requester = getAuthenticatedUser();

        String tokenHash = hashToken(rawToken);

        ManagerApproval approval = managerApprovalRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Invalid manager authorization."
                ));

        LocalDateTime now = LocalDateTime.now();

        if (approval.getConsumedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This manager authorization has already been used."
            );
        }

        if (!approval.getExpiresAt().isAfter(now)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Manager authorization has expired. Please request approval again."
            );
        }

        if (!expectedOperation.equals(approval.getOperationCode())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This authorization is not valid for this operation."
            );
        }

        if (!Objects.equals(
                approval.getRequester().getUserId(),
                requester.userId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This authorization belongs to another user."
            );
        }

        if (!Objects.equals(
                approval.getTenant().getTenantId(),
                requester.tenantId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This authorization belongs to another gym."
            );
        }

        if (!Objects.equals(
                approval.getMember().getMemberId(),
                memberId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This authorization belongs to another member."
            );
        }

        approval.setConsumedAt(now);

        managerApprovalRepository.save(approval);

        return approval;
    }

    // =====================================================
    // OPERATION / PERMISSION MAPPING
    // =====================================================

    private String normalizeOperationCode(String operationCode) {

        String normalized = operationCode == null
                ? ""
                : operationCode.trim().toUpperCase(Locale.ROOT);

        if (MEMBER_EDIT.equals(normalized)
                || MEMBER_DEACTIVATE.equals(normalized)) {

            return normalized;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unsupported manager approval operation."
        );
    }

    private String getRequiredPermission(String operationCode) {

        return switch (operationCode) {
            case MEMBER_EDIT -> "MEMBERS_LIST_EDIT";
            case MEMBER_DEACTIVATE -> "MEMBERS_PROFILE_DEACTIVATE";
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported manager approval operation."
            );
        };
    }

    // =====================================================
    // AUTHENTICATED USER
    // =====================================================

    private AuthenticatedUser getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                instanceof AuthenticatedUser actor)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "A valid user session is required."
            );
        }

        return actor;
    }

    // =====================================================
    // ROLE NORMALIZATION
    // =====================================================

    private String normalizeRoleName(String roleName) {

        return roleName == null
                ? ""
                : roleName.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
    }

    // =====================================================
    // TOKEN GENERATION
    // =====================================================

    private String generateToken() {

        byte[] tokenBytes = new byte[32];

        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    // =====================================================
    // TOKEN HASH
    // =====================================================

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder();

            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }

            return hex.toString();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to generate authorization token hash.",
                    exception
            );
        }
    }
}
