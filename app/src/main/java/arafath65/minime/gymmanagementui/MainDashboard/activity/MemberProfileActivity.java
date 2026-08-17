package arafath65.minime.gymmanagementui.MainDashboard.activity;

import arafath65.minime.gymmanagementui.MainDashboard.AddFitnessAssessmentActivity;
import arafath65.minime.gymmanagementui.MainDashboard.AddMemberActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.FitnessHistoryAdapter;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.MemberMembershipFragment;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentResponse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import arafath65.minime.gymmanagementui.MainDashboard.fragment.MemberOverviewFragment;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MemberResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberProfileActivity
        extends AppCompatActivity {


    // =====================================================
    // MEMBER
    // =====================================================

    private int memberId;

    private MemberResponse currentMember;


    // =====================================================
    // API
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;
    private final ExecutorService imageExecutor =
            Executors.newSingleThreadExecutor();


    // =====================================================
    // HEADER
    // =====================================================

    private ImageButton btnBack;

    private ImageButton btnEditMember;

    private ImageView imgMemberProfile;

    private TextView tabOverview;

    private TextView tabFitness;

    private TextView tabMembership;

    private TextView tabDocuments;


    // =====================================================
    // CONTAINER
    // =====================================================

    private FrameLayout memberProfileContainer;


    // =====================================================
    // MEMBER HEADER TEXT
    // =====================================================

    private TextView txtMemberName;

    private TextView txtMemberNo;

    private TextView txtMemberBranch;

    private TextView txtMemberStatus;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_member_profile
        );

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        deviceStorage =
                new DeviceStorage(
                        getApplicationContext()
                );

        tabOverview =
                findViewById(
                        R.id.tabOverview
                );


        tabFitness =
                findViewById(
                        R.id.tabFitness
                );


        tabMembership =
                findViewById(
                        R.id.tabMembership
                );


        tabDocuments =
                findViewById(
                        R.id.tabDocuments
                );


        // =================================================
        // CONTAINER
        // =================================================

        memberProfileContainer =
                findViewById(
                        R.id.memberProfileContainer
                );


        setupTabs();


        // =================================================
        // MEMBER ID
        // =================================================

        memberId =
                getIntent().getIntExtra(
                        "memberId",
                        0
                );


        if (memberId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid member.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        initializeViews();

        setupButtons();

        loadMember();
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {


        btnBack =
                findViewById(
                        R.id.btnBack
                );


        btnEditMember =
                findViewById(
                        R.id.btnEditMember
                );


        imgMemberProfile =
                findViewById(
                        R.id.imgMemberProfile
                );


        txtMemberName =
                findViewById(
                        R.id.txtMemberName
                );


        txtMemberNo =
                findViewById(
                        R.id.txtMemberNo
                );


        txtMemberBranch =
                findViewById(
                        R.id.txtMemberBranch
                );


        txtMemberStatus =
                findViewById(
                        R.id.txtMemberStatus
                );
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {


        btnBack.setOnClickListener(
                v -> finish()
        );


        btnEditMember.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    MemberProfileActivity.this,
                                    AddMemberActivity.class
                            );


                    intent.putExtra(
                            "memberId",
                            memberId
                    );


                    intent.putExtra(
                            "editMode",
                            true
                    );


                    startActivityForResult(
                            intent,
                            1001
                    );
                }
        );
    }



    // =====================================================
    // LOAD MEMBER
    // =====================================================

    private void loadMember() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // TENANT
        // =================================================

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =================================================
        // DEVICE
        // =================================================

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =================================================
        // API
        // =================================================

        apiService
                .getMemberById(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<MemberResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberResponse>>
                                            response) {


                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            MemberProfileActivity.this,
                                            "Unable to load member.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                ApiResponse<MemberResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess() ||
                                        apiResponse.getData() == null) {

                                    Toast.makeText(
                                            MemberProfileActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                MemberResponse member =
                                        apiResponse.getData();


                                currentMember =
                                        member;


                                displayMember(
                                        member
                                );


                                showOverview(
                                        member
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberResponse>>
                                            call,

                                    Throwable t) {

                                Toast.makeText(
                                        MemberProfileActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // DISPLAY MEMBER
    // =====================================================

    private void displayMember(
            MemberResponse member) {


        txtMemberName.setText(
                safeText(
                        member.getFullName()
                )
        );


        txtMemberNo.setText(
                safeText(
                        member.getMemberNo()
                )
        );


        String branchText =
                safeText(
                        member.getBranchNo()
                )
                        + " • "
                        + safeText(
                        member.getBranchName()
                );


        txtMemberBranch.setText(
                branchText
        );


        if (Boolean.TRUE.equals(
                member.getStatus())) {

            txtMemberStatus.setText(
                    "● ACTIVE"
            );

        } else {

            txtMemberStatus.setText(
                    "● INACTIVE"
            );
        }


        // =================================================
        // PROFILE PHOTO
        loadMemberProfileImage();
    }

    // =====================================================
// LOAD MEMBER PROFILE IMAGE
// =====================================================

    private void loadMemberProfileImage() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            setDefaultMemberImage();

            return;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            setDefaultMemberImage();

            return;
        }


        apiService
                .getMemberImage(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(
                                    Call<ResponseBody> call,
                                    Response<ResponseBody> response) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    setDefaultMemberImage();

                                    return;
                                }


                                ResponseBody body =
                                        response.body();


                                imageExecutor.execute(() -> {

                                    Bitmap bitmap = null;


                                    try {

                                        bitmap =
                                                BitmapFactory
                                                        .decodeStream(
                                                                body.byteStream()
                                                        );

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }


                                    Bitmap finalBitmap =
                                            bitmap;


                                    runOnUiThread(() -> {

                                        if (finalBitmap != null
                                                && !isFinishing()
                                                && !isDestroyed()) {

                                            imgMemberProfile.setImageBitmap(
                                                    finalBitmap
                                            );

                                        } else {

                                            setDefaultMemberImage();
                                        }
                                    });
                                });
                            }


                            @Override
                            public void onFailure(
                                    Call<ResponseBody> call,
                                    Throwable t) {

                                setDefaultMemberImage();
                            }
                        }
                );
    }

    // =====================================================
// DEFAULT MEMBER IMAGE
// =====================================================

    private void setDefaultMemberImage() {

        imgMemberProfile.setImageResource(
                R.drawable.ic_person
        );
    }


    // =====================================================
    // OVERVIEW
    // =====================================================

    private void showOverview(
            MemberResponse member) {


        // =================================================
        // REMOVE FITNESS LAYOUT
        // =================================================

        memberProfileContainer.removeAllViews();


        // =================================================
        // REMOVE EXISTING OVERVIEW
        // =================================================

        FragmentManager fragmentManager =
                getSupportFragmentManager();


        Fragment existingFragment =
                fragmentManager.findFragmentByTag(
                        "MEMBER_OVERVIEW"
                );


        if (existingFragment != null) {

            fragmentManager.beginTransaction()
                    .remove(existingFragment)
                    .commitNow();
        }


        // =================================================
        // CREATE OVERVIEW
        // =================================================

        MemberOverviewFragment fragment =
                MemberOverviewFragment.newInstance(
                        member
                );


        fragmentManager.beginTransaction()
                .replace(
                        R.id.memberProfileContainer,
                        fragment,
                        "MEMBER_OVERVIEW"
                )
                .commit();
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "-";
        }


        return value;
    }


    // =====================================================
    // FINISH
    // =====================================================

    @Override
    public void finish() {

        super.finish();


        overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_slide_out_right
        );
    }


    // =====================================================
    // SETUP TABS
    // =====================================================

    private void setupTabs() {


        // =================================================
        // OVERVIEW
        // =================================================

        tabOverview.setOnClickListener(
                v -> {

                    selectTab(
                            tabOverview
                    );


                    if (currentMember != null) {

                        showOverview(
                                currentMember
                        );
                    }
                }
        );


        // =================================================
        // FITNESS
        // =================================================

        tabFitness.setOnClickListener(
                v -> {

                    selectTab(
                            tabFitness
                    );


                    loadFitnessLayout();
                }
        );


        // =================================================
        // MEMBERSHIP
        // =================================================

        // =====================================================
// MEMBERSHIP
// =====================================================

        tabMembership.setOnClickListener(
                v -> {

                    selectTab(
                            tabMembership
                    );


                    if (currentMember != null) {

                        showMembership(
                                currentMember
                        );
                    }
                }
        );


        // =================================================
        // DOCUMENTS
        // =================================================

        tabDocuments.setOnClickListener(
                v -> {

                    selectTab(
                            tabDocuments
                    );


                    Toast.makeText(
                            this,
                            "Documents will be added next.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
    }

    // =====================================================
// SHOW MEMBERSHIP
// =====================================================

    // =====================================================
// SHOW MEMBERSHIP
// =====================================================

    private void showMembership(
            MemberResponse member) {

        // -------------------------------------------------
        // CLEAR CONTAINER
        // -------------------------------------------------

        memberProfileContainer.removeAllViews();


        FragmentManager fragmentManager =
                getSupportFragmentManager();


        // -------------------------------------------------
        // REMOVE OVERVIEW
        // -------------------------------------------------

        Fragment existingOverview =
                fragmentManager.findFragmentByTag(
                        "MEMBER_OVERVIEW"
                );


        if (existingOverview != null) {

            fragmentManager.beginTransaction()
                    .remove(existingOverview)
                    .commitNow();
        }


        // -------------------------------------------------
        // REMOVE MEMBERSHIP
        // -------------------------------------------------

        Fragment existingMembership =
                fragmentManager.findFragmentByTag(
                        "MEMBER_MEMBERSHIP"
                );


        if (existingMembership != null) {

            fragmentManager.beginTransaction()
                    .remove(existingMembership)
                    .commitNow();
        }


        // -------------------------------------------------
        // CREATE MEMBERSHIP FRAGMENT
        // -------------------------------------------------

        MemberMembershipFragment fragment =
                MemberMembershipFragment.newInstance(
                        member.getMemberId(),
                        member.getFullName()
                );


        // -------------------------------------------------
        // LOAD
        // -------------------------------------------------

        fragmentManager.beginTransaction()
                .replace(
                        R.id.memberProfileContainer,
                        fragment,
                        "MEMBER_MEMBERSHIP"
                )
                .commit();
    }


    // =====================================================
    // SELECT TAB
    // =====================================================

    private void selectTab(
            TextView selectedTab) {


        TextView[] tabs = {

                tabOverview,
                tabFitness,
                tabMembership,
                tabDocuments
        };


        for (TextView tab : tabs) {

            if (tab == null) {
                continue;
            }


            tab.setTextColor(
                    getResources().getColor(
                            R.color.text_secondary,
                            getTheme()
                    )
            );


            tab.setBackgroundResource(
                    android.R.color.transparent
            );
        }


        selectedTab.setTextColor(
                getResources().getColor(
                        R.color.green_light,
                        getTheme()
                )
        );


        selectedTab.setBackgroundResource(
                R.drawable.bg_tab_selected
        );
    }


    // =====================================================
    // LOAD FITNESS LAYOUT
    // =====================================================

    private void loadFitnessLayout() {


        // =================================================
        // REMOVE OVERVIEW FRAGMENT
        // =================================================

        FragmentManager fragmentManager =
                getSupportFragmentManager();


        Fragment overviewFragment =
                fragmentManager.findFragmentByTag(
                        "MEMBER_OVERVIEW"
                );


        if (overviewFragment != null) {

            fragmentManager.beginTransaction()
                    .remove(overviewFragment)
                    .commitNow();
        }


        // =================================================
        // CLEAR CONTAINER
        // =================================================

        memberProfileContainer.removeAllViews();


        // =================================================
        // LOAD FITNESS LAYOUT
        // =================================================

        View fitnessView =
                getLayoutInflater()
                        .inflate(
                                R.layout.layout_member_fitness,
                                memberProfileContainer,
                                false
                        );


        memberProfileContainer.addView(
                fitnessView
        );


        setupFitnessView(
                fitnessView
        );
    }


    // =====================================================
    // SETUP FITNESS VIEW
    // =====================================================

    private void setupFitnessView(
            View view) {


        TextView txtFitnessDate =
                view.findViewById(
                        R.id.txtFitnessDate
                );


        TextView txtFitnessWeight =
                view.findViewById(
                        R.id.txtFitnessWeight
                );


        TextView txtFitnessHeight =
                view.findViewById(
                        R.id.txtFitnessHeight
                );


        TextView txtFitnessBmi =
                view.findViewById(
                        R.id.txtFitnessBmi
                );


        TextView txtFitnessBodyFat =
                view.findViewById(
                        R.id.txtFitnessBodyFat
                );


        TextView txtFitnessChest =
                view.findViewById(
                        R.id.txtFitnessChest
                );


        TextView txtFitnessWaist =
                view.findViewById(
                        R.id.txtFitnessWaist
                );


        TextView txtFitnessHips =
                view.findViewById(
                        R.id.txtFitnessHips
                );


        TextView txtFitnessArms =
                view.findViewById(
                        R.id.txtFitnessArms
                );


        TextView txtFitnessThigh =
                view.findViewById(
                        R.id.txtFitnessThigh
                );


        RecyclerView recyclerFitnessHistory =
                view.findViewById(
                        R.id.recyclerFitnessHistory
                );


        TextView txtNoFitness =
                view.findViewById(
                        R.id.txtNoFitness
                );


        // =================================================
        // ADD FITNESS
        // =================================================

        MaterialButton btnAddFitness =
                view.findViewById(
                        R.id.btnAddFitness
                );


        btnAddFitness.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    MemberProfileActivity.this,
                                    AddFitnessAssessmentActivity.class
                            );


                    intent.putExtra(
                            "memberId",
                            memberId
                    );


                    startActivityForResult(
                            intent,
                            2001
                    );
                }
        );


        // =================================================
        // RECYCLER
        // =================================================

        recyclerFitnessHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );


        recyclerFitnessHistory.setNestedScrollingEnabled(
                true
        );


        recyclerFitnessHistory.setHasFixedSize(
                false
        );


        recyclerFitnessHistory.setItemViewCacheSize(
                10
        );


        recyclerFitnessHistory.setClipToPadding(
                false
        );


        // =================================================
        // LOAD FITNESS HISTORY
        // =================================================

        loadFitnessData(
                txtFitnessDate,
                txtFitnessWeight,
                txtFitnessHeight,
                txtFitnessBmi,
                txtFitnessBodyFat,
                txtFitnessChest,
                txtFitnessWaist,
                txtFitnessHips,
                txtFitnessArms,
                txtFitnessThigh,
                recyclerFitnessHistory,
                txtNoFitness
        );
    }


    // =====================================================
    // LOAD FITNESS DATA
    // =====================================================

    private void loadFitnessData(

            TextView txtFitnessDate,

            TextView txtFitnessWeight,

            TextView txtFitnessHeight,

            TextView txtFitnessBmi,

            TextView txtFitnessBodyFat,

            TextView txtFitnessChest,

            TextView txtFitnessWaist,

            TextView txtFitnessHips,

            TextView txtFitnessArms,

            TextView txtFitnessThigh,

            RecyclerView recyclerFitnessHistory,

            TextView txtNoFitness) {


        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // TENANT
        // =================================================

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            txtNoFitness.setVisibility(
                    View.VISIBLE
            );


            txtNoFitness.setText(
                    "Tenant information is missing."
            );


            return;
        }


        // =================================================
        // DEVICE
        // =================================================

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            txtNoFitness.setVisibility(
                    View.VISIBLE
            );


            txtNoFitness.setText(
                    "Device information is missing."
            );


            return;
        }


        // =================================================
        // GET FITNESS DATA
        // =================================================

        apiService
                .getFitnessAssessments(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                MemberFitnessAssessmentResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberFitnessAssessmentResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MemberFitnessAssessmentResponse>>>
                                            response) {


                                // =========================================
                                // HTTP ERROR
                                // =========================================

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    txtNoFitness.setVisibility(
                                            View.VISIBLE
                                    );


                                    txtNoFitness.setText(
                                            "Unable to load fitness data."
                                    );


                                    return;
                                }


                                // =========================================
                                // API RESPONSE
                                // =========================================

                                ApiResponse<
                                        List<
                                                MemberFitnessAssessmentResponse>>
                                        apiResponse =
                                        response.body();


                                // =========================================
                                // NO DATA
                                // =========================================

                                if (!apiResponse.isSuccess() ||
                                        apiResponse.getData() == null ||
                                        apiResponse.getData().isEmpty()) {

                                    txtNoFitness.setVisibility(
                                            View.VISIBLE
                                    );


                                    txtNoFitness.setText(
                                            "No fitness assessments yet."
                                    );


                                    return;
                                }


                                txtNoFitness.setVisibility(
                                        View.GONE
                                );


                                List<
                                        MemberFitnessAssessmentResponse>
                                        assessments =
                                        apiResponse.getData();


                                // =================================================
                                // LATEST ASSESSMENT
                                // =================================================

                                MemberFitnessAssessmentResponse latest =
                                        assessments.get(0);


                                displayLatestFitness(
                                        latest,
                                        txtFitnessDate,
                                        txtFitnessWeight,
                                        txtFitnessHeight,
                                        txtFitnessBmi,
                                        txtFitnessBodyFat,
                                        txtFitnessChest,
                                        txtFitnessWaist,
                                        txtFitnessHips,
                                        txtFitnessArms,
                                        txtFitnessThigh
                                );


                                // =================================================
// HISTORY
// =================================================

                                FitnessHistoryAdapter adapter =
                                        new FitnessHistoryAdapter(

                                                assessments,

                                                new FitnessHistoryAdapter.OnFitnessActionListener() {

                                                    @Override
                                                    public void onEditFitness(
                                                            MemberFitnessAssessmentResponse assessment) {

                                                        Intent intent =
                                                                new Intent(
                                                                        MemberProfileActivity.this,
                                                                        AddFitnessAssessmentActivity.class
                                                                );


                                                        // -----------------------------------------
                                                        // MEMBER
                                                        // -----------------------------------------

                                                        intent.putExtra(
                                                                "memberId",
                                                                memberId
                                                        );


                                                        // -----------------------------------------
                                                        // FITNESS
                                                        // -----------------------------------------

                                                        intent.putExtra(
                                                                "fitnessId",
                                                                assessment.getFitnessId()
                                                        );


                                                        // -----------------------------------------
                                                        // EDIT MODE
                                                        // -----------------------------------------

                                                        intent.putExtra(
                                                                "editMode",
                                                                true
                                                        );


                                                        startActivityForResult(
                                                                intent,
                                                                2001
                                                        );
                                                    }


                                                    @Override
                                                    public void onDeleteFitness(
                                                            MemberFitnessAssessmentResponse assessment) {

                                                        showDeleteFitnessConfirmation(
                                                                assessment
                                                        );
                                                    }
                                                },

                                                // =================================================
                                                // IMAGE API DATA
                                                // =================================================

                                                apiService,

                                                tenantNo,

                                                deviceToken
                                        );


// =================================================
// MEMBER ID FOR IMAGE REQUESTS
// =================================================

                                adapter.setMemberId(
                                        memberId
                                );


// =================================================
// SET ADAPTER
// =================================================


                                recyclerFitnessHistory.setAdapter(
                                        adapter
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberFitnessAssessmentResponse>>>
                                            call,

                                    Throwable t) {

                                txtNoFitness.setVisibility(
                                        View.VISIBLE
                                );


                                txtNoFitness.setText(
                                        "Connection failed."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // DISPLAY LATEST FITNESS
    // =====================================================

    private void displayLatestFitness(

            MemberFitnessAssessmentResponse data,

            TextView txtFitnessDate,

            TextView txtFitnessWeight,

            TextView txtFitnessHeight,

            TextView txtFitnessBmi,

            TextView txtFitnessBodyFat,

            TextView txtFitnessChest,

            TextView txtFitnessWaist,

            TextView txtFitnessHips,

            TextView txtFitnessArms,

            TextView txtFitnessThigh) {


        txtFitnessDate.setText(
                safeText(
                        data.getAssessmentDate()
                )
        );


        txtFitnessWeight.setText(
                formatValue(
                        data.getWeight(),
                        " kg"
                )
        );


        txtFitnessHeight.setText(
                formatValue(
                        data.getHeight(),
                        " cm"
                )
        );


        txtFitnessBmi.setText(
                formatValue(
                        data.getBmi(),
                        ""
                )
        );


        txtFitnessBodyFat.setText(
                formatValue(
                        data.getBodyFat(),
                        " %"
                )
        );


        txtFitnessChest.setText(
                "Chest    " +
                        formatValue(
                                data.getChest(),
                                " cm"
                        )
        );


        txtFitnessWaist.setText(
                "Waist   " +
                        formatValue(
                                data.getWaist(),
                                " cm"
                        )
        );


        txtFitnessHips.setText(
                "Hips     " +
                        formatValue(
                                data.getHips(),
                                " cm"
                        )
        );


        txtFitnessArms.setText(
                "Arms    " +
                        formatValue(
                                data.getArms(),
                                " cm"
                        )
        );


        txtFitnessThigh.setText(
                "Thigh   " +
                        formatValue(
                                data.getThigh(),
                                " cm"
                        )
        );
    }


    // =====================================================
    // FORMAT VALUE
    // =====================================================

    private String formatValue(
            BigDecimal value,
            String suffix) {

        if (value == null) {

            return "--";
        }


        return value
                .stripTrailingZeros()
                .toPlainString()
                + suffix;
    }


    // =====================================================
    // ACTIVITY RESULT
    // =====================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (requestCode == 2001 &&
                resultCode == RESULT_OK) {

            loadFitnessLayout();
        }
    }


    // =====================================================
    // DELETE FITNESS CONFIRMATION
    // =====================================================

    private void showDeleteFitnessConfirmation(
            MemberFitnessAssessmentResponse assessment) {


        new MaterialAlertDialogBuilder(this)

                .setTitle(
                        "Delete Fitness Assessment?"
                )

                .setMessage(
                        "Are you sure you want to delete the fitness assessment from "
                                + assessment.getAssessmentDate()
                                + "?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            deleteFitnessAssessment(
                                    assessment
                            );
                        }
                )

                .show();
    }


    // =====================================================
    // DELETE FITNESS ASSESSMENT
    // =====================================================

    // =====================================================
// DELETE FITNESS ASSESSMENT
// =====================================================

    private void deleteFitnessAssessment(
            MemberFitnessAssessmentResponse assessment) {

        // =================================================
        // GET DEVICE DATA
        // =================================================

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // VALIDATE TENANT
        // =================================================

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =================================================
        // VALIDATE DEVICE
        // =================================================

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =================================================
        // DELETE API
        // =================================================

        apiService
                .deleteFitnessAssessment(
                        tenantNo,
                        deviceToken,
                        memberId,
                        assessment.getFitnessId()
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(

                                    Call<ApiResponse<Void>> call,

                                    Response<ApiResponse<Void>>
                                            response) {


                                // =============================================
                                // HTTP ERROR
                                // =============================================

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            MemberProfileActivity.this,
                                            "Unable to delete fitness assessment. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // =============================================
                                // API RESPONSE
                                // =============================================

                                ApiResponse<Void>
                                        apiResponse =
                                        response.body();


                                // =============================================
                                // API ERROR
                                // =============================================

                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            MemberProfileActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // =============================================
                                // SUCCESS
                                // =============================================

                                Toast.makeText(
                                        MemberProfileActivity.this,
                                        "Fitness assessment deleted.",
                                        Toast.LENGTH_SHORT
                                ).show();


                                // =============================================
                                // REFRESH FITNESS TAB
                                // =============================================

                                loadFitnessLayout();
                            }


                            @Override
                            public void onFailure(

                                    Call<ApiResponse<Void>> call,

                                    Throwable t) {

                                Toast.makeText(
                                        MemberProfileActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    @Override
    protected void onDestroy() {

        imageExecutor.shutdownNow();

        super.onDestroy();
    }
}