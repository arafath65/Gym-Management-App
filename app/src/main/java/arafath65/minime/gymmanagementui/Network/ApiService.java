package arafath65.minime.gymmanagementui.Network;

import java.util.List;

import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BenefitRequest;
import arafath65.minime.gymmanagementui.model.BenefitResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationRequest;
import arafath65.minime.gymmanagementui.model.DeviceVerifyRequest;
import arafath65.minime.gymmanagementui.model.DeviceVerifyResponse;
import arafath65.minime.gymmanagementui.model.LoginRequest;
import arafath65.minime.gymmanagementui.model.LoginResponse;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentRequest;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipPlanRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import arafath65.minime.gymmanagementui.model.MemberRequest;
import arafath65.minime.gymmanagementui.model.MemberResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBenefitResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanRequest;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;
import arafath65.minime.gymmanagementui.model.MembershipRenewalRequest;
import arafath65.minime.gymmanagementui.model.MembershipRenewalResponse;
import arafath65.minime.gymmanagementui.model.ProgramEnrollmentRequest;
import arafath65.minime.gymmanagementui.model.ProgramEnrollmentResponse;
import arafath65.minime.gymmanagementui.model.ProgramRequest;
import arafath65.minime.gymmanagementui.model.ProgramResponse;
import arafath65.minime.gymmanagementui.model.ProgramPaymentRequest;
import arafath65.minime.gymmanagementui.model.ProgramPaymentResponse;
import arafath65.minime.gymmanagementui.model.ManagerApprovalRequest;
import arafath65.minime.gymmanagementui.model.ManagerApprovalResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // =====================================================
    // HEALTH
    // =====================================================

    @GET("api/health")
    Call<HealthResponse> health();


    // =====================================================
    // DEVICE ACTIVATION
    // =====================================================

    @POST("api/devices/activate")
    Call<ApiResponse<DeviceActivationResponse>> activateDevice(
            @Body DeviceActivationRequest request
    );


    // =====================================================
    // DEVICE VERIFY
    // =====================================================

    @POST("api/devices/verify")
    Call<ApiResponse<DeviceVerifyResponse>> verifyDevice(
            @Body DeviceVerifyRequest request
    );


    // =====================================================
    // LOGIN
    // =====================================================

    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(
            @Body LoginRequest request
    );


    // =====================================================
    // BRANCHES
    // =====================================================

    @GET("api/branches")
    Call<ApiResponse<List<BranchResponse>>> getBranches(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );


    // =====================================================
    // MEMBERS
    // =====================================================

    @GET("api/members")
    Call<ApiResponse<List<MemberResponse>>> getMembers(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Query("branchId") Integer branchId
    );


    @POST("api/members")
    Call<ApiResponse<MemberResponse>> createMember(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body MemberRequest request
    );


    @PUT("api/members/{memberId}")
    Call<ApiResponse<MemberResponse>> updateMember(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Body MemberRequest request,
            @Header("X-Manager-Authorization") String managerAuthorizationToken
    );

    // =====================================================
// MANAGER APPROVAL - MEMBER EDIT
// =====================================================

    @POST("api/members/{memberId}/manager-approval")
    Call<ApiResponse<ManagerApprovalResponse>> requestManagerApproval(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Body ManagerApprovalRequest request
    );


    @Multipart
    @PUT("api/members/{memberId}/image")
    Call<ApiResponse<MemberResponse>> updateMemberImage(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Part MultipartBody.Part image
    );


    @PUT("api/members/{memberId}/deactivate")
    Call<ApiResponse<Void>> deactivateMember(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    @GET("api/members/{memberId}")
    Call<ApiResponse<MemberResponse>> getMemberById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    // =====================================================
    // MEMBER IMAGE
    // =====================================================

    @GET("api/members/{memberId}/image")
    Call<ResponseBody> getMemberImage(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    // =====================================================
    // MEMBER FITNESS
    // =====================================================

    @GET("api/members/{memberId}/fitness")
    Call<ApiResponse<List<MemberFitnessAssessmentResponse>>>
    getFitnessAssessments(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    @GET("api/members/{memberId}/fitness/latest")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    getLatestFitnessAssessment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    @GET("api/members/{memberId}/fitness/{fitnessId}")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    getFitnessAssessmentById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId
    );


    @POST("api/members/{memberId}/fitness")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    createFitnessAssessment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Body MemberFitnessAssessmentRequest request
    );


    @Multipart
    @POST("api/members/{memberId}/fitness")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    createFitnessAssessmentWithImages(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Part("assessment") RequestBody assessment,
            @Part MultipartBody.Part frontImage,
            @Part MultipartBody.Part sideImage,
            @Part MultipartBody.Part backImage
    );


    @DELETE("api/members/{memberId}/fitness/{fitnessId}")
    Call<ApiResponse<Void>> deleteFitnessAssessment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId
    );


    @PUT("api/members/{memberId}/fitness/{fitnessId}")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    updateFitnessAssessment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId,
            @Body MemberFitnessAssessmentRequest request
    );


    @Multipart
    @PUT("api/members/{memberId}/fitness/{fitnessId}")
    Call<ApiResponse<MemberFitnessAssessmentResponse>>
    updateFitnessAssessmentWithImages(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId,
            @Part("assessment") RequestBody assessment,
            @Part MultipartBody.Part frontImage,
            @Part MultipartBody.Part sideImage,
            @Part MultipartBody.Part backImage,
            @Part("replaceFront") RequestBody replaceFront,
            @Part("replaceSide") RequestBody replaceSide,
            @Part("replaceBack") RequestBody replaceBack
    );


    @GET("api/members/{memberId}/fitness/{fitnessId}/image/{imageType}")
    Call<ResponseBody> getFitnessImage(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId,
            @Path("imageType") String imageType
    );


    // =====================================================
    // MEMBERSHIPS
    // =====================================================

    @GET("api/member-memberships")
    Call<ApiResponse<List<MemberMembershipResponse>>>
    getMemberships(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );


    @GET("api/member-memberships/member/{memberId}")
    Call<ApiResponse<List<MemberMembershipResponse>>>
    getMemberMemberships(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    @GET("api/member-memberships/{membershipId}")
    Call<ApiResponse<MemberMembershipResponse>>
    getMembershipById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipId") int membershipId
    );


    @GET("api/member-memberships/state/{membershipState}")
    Call<ApiResponse<List<MemberMembershipResponse>>>
    getMembershipsByState(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipState") String membershipState
    );


    @POST("api/member-memberships")
    Call<ApiResponse<MemberMembershipResponse>>
    createMembership(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body MemberMembershipRequest request
    );


    // =====================================================
    // EXISTING MEMBERSHIP PLAN GET ALL
    // =====================================================

    @GET("api/membership-plans")
    Call<ApiResponse<List<MembershipPlanResponse>>>
    getMembershipPlans(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );

    // =====================================================
// MEMBER MEMBERSHIP PROGRAMS
// =====================================================

    @POST("api/member-membership-programs/assign")
    Call<ApiResponse<MemberMembershipProgramResponse>>
    assignMembershipProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Query("tenantId") Integer tenantId,
            @Query("memberMembershipId") Integer memberMembershipId,
            @Body MemberMembershipProgramRequest request
    );


    @GET("api/member-membership-programs/membership/{memberMembershipId}")
    Call<ApiResponse<List<MemberMembershipProgramResponse>>>
    getMembershipPrograms(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipId") Integer memberMembershipId,
            @Query("tenantId") Integer tenantId
    );

    // =====================================================
// MEMBER PROGRAM HISTORY
// =====================================================

    @GET("api/member-membership-programs/member/{memberId}")
    Call<ApiResponse<List<MemberMembershipProgramResponse>>>
    getMemberProgramHistory(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") Integer memberId,
            @Query("tenantId") Integer tenantId
    );

    @GET("api/member-membership-programs/{memberMembershipProgramId}")
    Call<ApiResponse<MemberMembershipProgramResponse>>
    getMembershipProgramById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipProgramId")
            Integer memberMembershipProgramId
    );


    @PUT("api/member-membership-programs/{memberMembershipProgramId}")
    Call<ApiResponse<MemberMembershipProgramResponse>>
    updateMembershipProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipProgramId")
            Integer memberMembershipProgramId,
            @Body MemberMembershipProgramRequest request
    );


    @PUT("api/member-membership-programs/{memberMembershipProgramId}/deactivate")
    Call<ApiResponse<Void>>
    deactivateMembershipProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipProgramId")
            Integer memberMembershipProgramId
    );


    @PUT("api/member-membership-programs/{memberMembershipProgramId}/activate")
    Call<ApiResponse<Void>>
    activateMembershipProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipProgramId")
            Integer memberMembershipProgramId
    );

    // =====================================================
// MEMBER MEMBERSHIP BENEFITS
// =====================================================

    @POST("api/member-memberships/{memberMembershipId}/benefits")
    Call<ApiResponse<MemberMembershipBenefitResponse>>
    addMembershipBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipId") Integer memberMembershipId,
            @Body MemberMembershipBenefitRequest request
    );

    @GET("api/benefits/branch/{branchId}")
    Call<ApiResponse<List<BenefitResponse>>> getActiveBenefitsByBranch(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("branchId") Integer branchId
    );

    // =====================================================
// ASSIGN / CHANGE MEMBERSHIP PLAN
// =====================================================

    @PUT("api/member-memberships/{membershipId}/plan")
    Call<ApiResponse<MemberMembershipResponse>>
    assignMembershipPlan(
            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("membershipId")
            Integer membershipId,

            @Body
            MemberMembershipPlanRequest request
    );

    // =====================================================
// MEMBERSHIP PLAN BENEFITS
// =====================================================

    @GET("api/membership-plans/{membershipPlanId}/benefits")
    Call<ApiResponse<List<MembershipPlanBenefitResponse>>>
    getMembershipPlanBenefits(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId
    );


    @GET("api/member-memberships/{memberMembershipId}/benefits")
    Call<ApiResponse<List<MemberMembershipBenefitResponse>>>
    getMembershipBenefits(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipId") Integer memberMembershipId
    );

    // =====================================================
// MEMBER BENEFIT HISTORY
// =====================================================

    @GET("api/member-memberships/benefits/member/{memberId}")
    Call<ApiResponse<List<MemberMembershipBenefitResponse>>>
    getMemberBenefitHistory(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") Integer memberId
    );


    @GET("api/member-memberships/benefits/{memberMembershipBenefitId}")
    Call<ApiResponse<MemberMembershipBenefitResponse>>
    getMembershipBenefitById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipBenefitId")
            Integer memberMembershipBenefitId
    );


    @PUT("api/member-memberships/benefits/{memberMembershipBenefitId}/deactivate")
    Call<ApiResponse<Void>>
    deactivateMembershipBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberMembershipBenefitId")
            Integer memberMembershipBenefitId
    );


    // =====================================================
    // MEMBERSHIP PLAN MANAGEMENT
    // =====================================================

    @POST("api/membership-plans")
    Call<ApiResponse<MembershipPlanResponse>>
    createMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body MembershipPlanRequest request
    );


    @GET("api/membership-plans/active")
    Call<ApiResponse<List<MembershipPlanResponse>>>
    getActiveMembershipPlans(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );


    @GET("api/membership-plans/{membershipPlanId}")
    Call<ApiResponse<MembershipPlanResponse>>
    getMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId
    );


    @PUT("api/membership-plans/{membershipPlanId}")
    Call<ApiResponse<MembershipPlanResponse>>
    updateMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId,
            @Body MembershipPlanRequest request
    );


    @PUT("api/membership-plans/{membershipPlanId}/activate")
    Call<ApiResponse<Void>>
    activateMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId
    );


    @PUT("api/membership-plans/{membershipPlanId}/deactivate")
    Call<ApiResponse<Void>>
    deactivateMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId
    );


    @DELETE("api/membership-plans/{membershipPlanId}")
    Call<ApiResponse<Void>>
    deleteMembershipPlan(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("membershipPlanId") Integer membershipPlanId
    );


    // =====================================================
    // PROGRAM MANAGEMENT
    // =====================================================

    @GET("api/programs")
    Call<ApiResponse<List<ProgramResponse>>> getPrograms(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Query("branchId") Integer branchId,
            @Query("active") Boolean active
    );


    @GET("api/programs/{programId}")
    Call<ApiResponse<ProgramResponse>> getProgramById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId
    );


    @GET("api/programs/code/{programCode}")
    Call<ApiResponse<ProgramResponse>> getProgramByCode(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programCode") String programCode
    );


    @POST("api/programs")
    Call<ApiResponse<ProgramResponse>> createProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body ProgramRequest request
    );


    @PUT("api/programs/{programId}")
    Call<ApiResponse<ProgramResponse>> updateProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId,
            @Body ProgramRequest request
    );


    @PUT("api/programs/{programId}/activate")
    Call<ApiResponse<ProgramResponse>> activateProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId
    );


    @PUT("api/programs/{programId}/deactivate")
    Call<ApiResponse<ProgramResponse>> deactivateProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId
    );


    @DELETE("api/programs/{programId}")
    Call<ApiResponse<Void>> deleteProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId
    );


    // =====================================================
    // PROGRAM PAYMENTS
    // =====================================================

    @POST("api/program-payments")
    Call<ApiResponse<ProgramPaymentResponse>>
    createProgramPayment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body ProgramPaymentRequest request
    );


    @GET("api/program-payments/{paymentId}")
    Call<ApiResponse<ProgramPaymentResponse>>
    getProgramPaymentById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("paymentId") int paymentId
    );


    @GET("api/program-payments/enrollment/{enrollmentId}")
    Call<ApiResponse<List<ProgramPaymentResponse>>>
    getProgramPaymentsByEnrollment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("enrollmentId") int enrollmentId
    );


    @GET("api/program-payments/member/{memberId}")
    Call<ApiResponse<List<ProgramPaymentResponse>>>
    getProgramPaymentsByMember(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );


    @GET("api/program-payments/program/{programId}")
    Call<ApiResponse<List<ProgramPaymentResponse>>>
    getProgramPaymentsByProgram(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("programId") int programId
    );


    @GET("api/program-payments/number/{paymentNumber}")
    Call<ApiResponse<ProgramPaymentResponse>>
    getProgramPaymentByNumber(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("paymentNumber") String paymentNumber
    );


    @GET("api/program-payments/date-range")
    Call<ApiResponse<List<ProgramPaymentResponse>>>
    getProgramPaymentsByDateRange(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );


    // =====================================================
    // PROGRAM ENROLLMENTS
    // =====================================================

    @POST("api/program-enrollments")
    Call<ApiResponse<ProgramEnrollmentResponse>>
    createProgramEnrollment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body ProgramEnrollmentRequest request
    );


    @GET("api/program-enrollments")
    Call<ApiResponse<List<ProgramEnrollmentResponse>>>
    getProgramEnrollments(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Query("memberId") Integer memberId,
            @Query("programId") Integer programId,
            @Query("branchId") Integer branchId
    );


    @GET("api/program-enrollments/{enrollmentId}")
    Call<ApiResponse<ProgramEnrollmentResponse>>
    getProgramEnrollmentById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("enrollmentId") int enrollmentId
    );


    @GET("api/program-enrollments/member/{memberId}/active")
    Call<ApiResponse<List<ProgramEnrollmentResponse>>>
    getActiveMemberPrograms(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );

    @PUT("api/program-enrollments/{enrollmentId}/cancel")
    Call<ApiResponse<ProgramEnrollmentResponse>> cancelProgramEnrollment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("enrollmentId") int enrollmentId
    );

    @GET("api/program-enrollments/number/{enrollmentNumber}")
    Call<ApiResponse<ProgramEnrollmentResponse>>
    getProgramEnrollmentByNumber(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("enrollmentNumber") String enrollmentNumber
    );

    // =====================================================
    // BENEFIT MANAGEMENT
    // =====================================================

    @GET("api/benefits")
    Call<ApiResponse<List<BenefitResponse>>> getBenefits(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );

    @GET("api/benefits/active")
    Call<ApiResponse<List<BenefitResponse>>> getActiveBenefits(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );

    @GET("api/benefits/{benefitId}")
    Call<ApiResponse<BenefitResponse>> getBenefitById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("benefitId") int benefitId
    );

    @POST("api/benefits")
    Call<ApiResponse<BenefitResponse>> createBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Body BenefitRequest request
    );

    @PUT("api/benefits/{benefitId}")
    Call<ApiResponse<BenefitResponse>> updateBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("benefitId") int benefitId,
            @Body BenefitRequest request
    );

    @PUT("api/benefits/{benefitId}/activate")
    Call<ApiResponse<Void>> activateBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("benefitId") int benefitId
    );

    @PUT("api/benefits/{benefitId}/deactivate")
    Call<ApiResponse<Void>> deactivateBenefit(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("benefitId") int benefitId
    );

    // =====================================================
// MEMBERSHIP RENEWAL
// =====================================================

    @POST("api/membership-renewals")
    Call<ApiResponse<MembershipRenewalResponse>>
    createMembershipRenewal(
            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Body MembershipRenewalRequest request
    );


    @GET("api/membership-renewals/membership/{memberMembershipId}")
    Call<ApiResponse<List<MembershipRenewalResponse>>>
    getMembershipRenewals(
            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberMembershipId")
            Integer memberMembershipId
    );

    // =====================================================
// CANCEL MEMBERSHIP
// =====================================================

    @PUT("api/member-memberships/{membershipId}/cancel")
    Call<ApiResponse<MemberMembershipResponse>>
    cancelMembership(
            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("membershipId")
            Integer membershipId
    );
}
