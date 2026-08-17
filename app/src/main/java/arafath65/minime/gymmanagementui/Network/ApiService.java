package arafath65.minime.gymmanagementui.Network;

import java.util.List;

import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationRequest;
import arafath65.minime.gymmanagementui.model.DeviceVerifyRequest;
import arafath65.minime.gymmanagementui.model.DeviceVerifyResponse;
import arafath65.minime.gymmanagementui.model.LoginRequest;
import arafath65.minime.gymmanagementui.model.LoginResponse;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentRequest;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import arafath65.minime.gymmanagementui.model.MemberRequest;
import arafath65.minime.gymmanagementui.model.MemberResponse;

import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;
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

    @GET("api/health")
    Call<HealthResponse> health();

    @POST("api/devices/activate")
    Call<ApiResponse<DeviceActivationResponse>> activateDevice(
            @Body DeviceActivationRequest request
    );

    @POST("api/devices/verify")
    Call<ApiResponse<DeviceVerifyResponse>> verifyDevice(
            @Body DeviceVerifyRequest request
    );

    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(
            @Body LoginRequest request
    );

    @GET("api/branches")
    Call<ApiResponse<List<BranchResponse>>> getBranches(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken
    );

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

    // =====================================================
// MEMBERSHIP PLANS
// =====================================================

    @GET("api/membership-plans")
    Call<ApiResponse<List<MembershipPlanResponse>>> getMembershipPlans(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken
    );

    // =====================================================
// CREATE MEMBERSHIP
// =====================================================

    @POST("api/member-memberships")
    Call<ApiResponse<MemberMembershipResponse>> createMembership(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Body
            MemberMembershipRequest request
    );

    // =====================================================
// UPDATE MEMBER
// =====================================================

    @PUT("api/members/{memberId}")
    Call<ApiResponse<MemberResponse>> updateMember(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberId")
            int memberId,

            @Body MemberRequest request
    );

    // UPDATE MEMBER IMAGE
    @Multipart
    @PUT("api/members/{memberId}/image")
    Call<ApiResponse<MemberResponse>> updateMemberImage(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberId")
            int memberId,

            @Part
            MultipartBody.Part image
    );

    // DEACTIVATE MEMBER
    @PUT("api/members/{memberId}/deactivate")
    Call<ApiResponse<Void>> deactivateMember(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberId")
            int memberId
    );

    @GET("api/members/{memberId}")
    Call<ApiResponse<MemberResponse>> getMemberById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );

    @GET("api/members/{memberId}/fitness")
    Call<ApiResponse<List<MemberFitnessAssessmentResponse>>> getFitnessAssessments(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );

    @GET("api/members/{memberId}/fitness/latest")
    Call<ApiResponse<MemberFitnessAssessmentResponse>> getLatestFitnessAssessment(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId
    );

    @GET("api/members/{memberId}/fitness/{fitnessId}")
    Call<ApiResponse<MemberFitnessAssessmentResponse>> getFitnessAssessmentById(
            @Header("X-Tenant-No") String tenantNo,
            @Header("X-Device-Token") String deviceToken,
            @Path("memberId") int memberId,
            @Path("fitnessId") int fitnessId
    );

    @POST("api/members/{memberId}/fitness")
    Call<ApiResponse<MemberFitnessAssessmentResponse>> createFitnessAssessment(
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
    Call<ApiResponse<MemberFitnessAssessmentResponse>> updateFitnessAssessment(
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
// GET MEMBER IMAGE
// =====================================================

    @GET("api/members/{memberId}/image")
    Call<ResponseBody> getMemberImage(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberId")
            int memberId
    );

    // =====================================================
// MEMBERSHIPS
// =====================================================

    @GET("api/member-memberships")
    Call<ApiResponse<List<MemberMembershipResponse>>> getMemberships(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken
    );


// =====================================================
// MEMBERSHIPS BY MEMBER
// =====================================================

    @GET("api/member-memberships/member/{memberId}")
    Call<ApiResponse<List<MemberMembershipResponse>>> getMemberMemberships(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("memberId")
            int memberId
    );


// =====================================================
// MEMBERSHIP BY ID
// =====================================================

    @GET("api/member-memberships/{membershipId}")
    Call<ApiResponse<MemberMembershipResponse>> getMembershipById(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("membershipId")
            int membershipId
    );


// =====================================================
// MEMBERSHIPS BY STATE
// =====================================================

    @GET("api/member-memberships/state/{membershipState}")
    Call<ApiResponse<List<MemberMembershipResponse>>> getMembershipsByState(

            @Header("X-Tenant-No")
            String tenantNo,

            @Header("X-Device-Token")
            String deviceToken,

            @Path("membershipState")
            String membershipState
    );
}
