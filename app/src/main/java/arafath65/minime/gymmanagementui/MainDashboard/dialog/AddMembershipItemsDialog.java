package arafath65.minime.gymmanagementui.MainDashboard.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.MainDashboard.adapter.AddBenefitAdapter;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.AddProgramAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BenefitResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import arafath65.minime.gymmanagementui.model.ProgramResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMembershipItemsDialog
        extends DialogFragment {


    // =====================================================
    // ARGUMENT KEYS
    // =====================================================

    private static final String ARG_MEMBER_ID =
            "memberId";

    private static final String ARG_MEMBER_MEMBERSHIP_ID =
            "memberMembershipId";

    private static final String ARG_MEMBER_NAME =
            "memberName";

    private static final String ARG_MEMBERSHIP_NUMBER =
            "membershipNumber";

    private static final String ARG_BRANCH_ID =
            "branchId";

    private static final String ARG_MEMBERSHIP_PLAN_ID =
            "membershipPlanId";


    // =====================================================
    // CALLBACK
    // =====================================================

    public interface OnItemAddedListener {

        void onItemAdded();
    }


    private OnItemAddedListener
            onItemAddedListener;


    // =====================================================
    // CHANGES
    // =====================================================

    private boolean membershipChanged =
            false;


    // =====================================================
    // API
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;


    // =====================================================
    // ARGUMENT DATA
    // =====================================================

    private int memberId;

    private int memberMembershipId;

    private String memberName;

    private String membershipNumber;

    private Integer branchId;

    private Integer membershipPlanId;


    // =====================================================
    // MEMBERSHIP CONTEXT
    // =====================================================

    private Integer tenantId;


    // =====================================================
    // ASSIGNED IDS
    // =====================================================

    private final Set<Integer>
            assignedProgramIds =
            new HashSet<>();


    private final Set<Integer>
            assignedBenefitIds =
            new HashSet<>();


    // =====================================================
    // VIEWS
    // =====================================================

    private TextView txtAddMembershipMember;

    private ImageButton btnCloseAddMembership;


    /*
     * Membership Plan tab is intentionally removed
     * from the existing-membership Add dialog.
     */

    private TextView tabAddProgram;

    private TextView tabAddBenefit;


    private View containerAddPrograms;

    private View containerAddBenefits;


    private RecyclerView recyclerAddPrograms;

    private RecyclerView recyclerAddBenefits;


    private TextView txtNoAddPrograms;

    private TextView txtNoAddBenefits;


    // =====================================================
    // ADAPTERS
    // =====================================================

    private AddProgramAdapter
            programAdapter;

    private AddBenefitAdapter
            benefitAdapter;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AddMembershipItemsDialog() {
    }


    // =====================================================
    // NEW INSTANCE
    // =====================================================

    public static AddMembershipItemsDialog newInstance(

            int memberId,

            int memberMembershipId,

            String memberName,

            String membershipNumber,

            Integer branchId,

            Integer membershipPlanId) {


        AddMembershipItemsDialog dialog =
                new AddMembershipItemsDialog();


        Bundle args =
                new Bundle();


        args.putInt(
                ARG_MEMBER_ID,
                memberId
        );


        args.putInt(
                ARG_MEMBER_MEMBERSHIP_ID,
                memberMembershipId
        );


        args.putString(
                ARG_MEMBER_NAME,
                memberName
        );


        args.putString(
                ARG_MEMBERSHIP_NUMBER,
                membershipNumber
        );


        args.putInt(
                ARG_BRANCH_ID,
                branchId == null
                        ? 0
                        : branchId
        );


        args.putInt(
                ARG_MEMBERSHIP_PLAN_ID,
                membershipPlanId == null
                        ? 0
                        : membershipPlanId
        );


        dialog.setArguments(
                args
        );


        return dialog;
    }


    // =====================================================
    // LISTENER
    // =====================================================

    public void setOnItemAddedListener(
            OnItemAddedListener listener) {

        this.onItemAddedListener =
                listener;
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


        if (getArguments() != null) {

            memberId =
                    getArguments().getInt(
                            ARG_MEMBER_ID,
                            0
                    );


            memberMembershipId =
                    getArguments().getInt(
                            ARG_MEMBER_MEMBERSHIP_ID,
                            0
                    );


            memberName =
                    getArguments().getString(
                            ARG_MEMBER_NAME,
                            ""
                    );


            membershipNumber =
                    getArguments().getString(
                            ARG_MEMBERSHIP_NUMBER,
                            ""
                    );


            branchId =
                    getArguments().getInt(
                            ARG_BRANCH_ID,
                            0
                    );


            membershipPlanId =
                    getArguments().getInt(
                            ARG_MEMBERSHIP_PLAN_ID,
                            0
                    );
        }


        apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        deviceStorage =
                new DeviceStorage(
                        requireContext()
                                .getApplicationContext()
                );
    }


    // =====================================================
    // VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(

            @NonNull LayoutInflater inflater,

            @Nullable ViewGroup container,

            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_membership_items,
                container,
                false
        );
    }


    // =====================================================
    // VIEW CREATED
    // =====================================================

    @Override
    public void onViewCreated(

            @NonNull View view,

            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        initializeViews(
                view
        );


        initializeAdapters();


        setupTabs();


        loadMembershipContext();


        loadAssignedBenefits();


        /*
         * Existing membership dialog starts with
         * Programs.
         *
         * Membership Plan is NOT part of this flow.
         */

        showTab(
                0
        );
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews(
            View view) {


        txtAddMembershipMember =
                view.findViewById(
                        R.id.txtAddMembershipMember
                );


        btnCloseAddMembership =
                view.findViewById(
                        R.id.btnCloseAddMembership
                );


        tabAddProgram =
                view.findViewById(
                        R.id.tabAddProgram
                );


        tabAddBenefit =
                view.findViewById(
                        R.id.tabAddBenefit
                );


        containerAddPrograms =
                view.findViewById(
                        R.id.containerAddPrograms
                );


        containerAddBenefits =
                view.findViewById(
                        R.id.containerAddBenefits
                );


        recyclerAddPrograms =
                view.findViewById(
                        R.id.recyclerAddPrograms
                );


        recyclerAddBenefits =
                view.findViewById(
                        R.id.recyclerAddBenefits
                );


        txtNoAddPrograms =
                view.findViewById(
                        R.id.txtNoAddPrograms
                );


        txtNoAddBenefits =
                view.findViewById(
                        R.id.txtNoAddBenefits
                );


        txtAddMembershipMember.setText(
                safe(
                        memberName
                )
                        + " • "
                        + safe(
                        membershipNumber
                )
        );


        btnCloseAddMembership.setOnClickListener(
                v -> dismiss()
        );
    }


    // =====================================================
    // ADAPTERS
    // =====================================================

    private void initializeAdapters() {


        programAdapter =
                new AddProgramAdapter(
                        this::onProgramAddClicked
                );


        programAdapter.setAssignedProgramIds(
                assignedProgramIds
        );


        recyclerAddPrograms
                .setLayoutManager(
                        new LinearLayoutManager(
                                requireContext()
                        )
                );


        recyclerAddPrograms.setAdapter(
                programAdapter
        );


        benefitAdapter =
                new AddBenefitAdapter(
                        this::onBenefitAddClicked
                );


        /*
         * IMPORTANT:
         *
         * Benefit fees can be different by branch.
         */

        benefitAdapter.setBranchId(
                branchId
        );


        benefitAdapter.setAssignedBenefitIds(
                assignedBenefitIds
        );


        recyclerAddBenefits
                .setLayoutManager(
                        new LinearLayoutManager(
                                requireContext()
                        )
                );


        recyclerAddBenefits.setAdapter(
                benefitAdapter
        );
    }


    // =====================================================
    // TABS
    // =====================================================

    private void setupTabs() {

        tabAddProgram.setOnClickListener(
                v -> showTab(
                        0
                )
        );


        tabAddBenefit.setOnClickListener(
                v -> showTab(
                        1
                )
        );
    }


    // =====================================================
    // SHOW TAB
    // =====================================================

    private void showTab(
            int tab) {


        containerAddPrograms.setVisibility(
                View.GONE
        );


        containerAddBenefits.setVisibility(
                View.GONE
        );


        resetTab(
                tabAddProgram
        );


        resetTab(
                tabAddBenefit
        );


        if (tab == 0) {

            containerAddPrograms.setVisibility(
                    View.VISIBLE
            );


            selectTab(
                    tabAddProgram
            );


            loadPrograms();


        } else {

            containerAddBenefits.setVisibility(
                    View.VISIBLE
            );


            selectTab(
                    tabAddBenefit
            );


            if (branchId == null
                    || branchId <= 0) {

                txtNoAddBenefits.setText(
                        "Loading membership information..."
                );


                txtNoAddBenefits.setVisibility(
                        View.VISIBLE
                );


                recyclerAddBenefits.setVisibility(
                        View.GONE
                );


                loadMembershipContextForBenefits();

            } else {

                loadBenefits();
            }
        }
    }


    // =====================================================
    // RESET TAB
    // =====================================================

    private void resetTab(
            TextView tab) {

        tab.setTextColor(
                requireContext()
                        .getColor(
                                R.color.text_secondary
                        )
        );


        tab.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );


        tab.setBackgroundResource(
                android.R.color.transparent
        );
    }


    // =====================================================
    // SELECT TAB
    // =====================================================

    private void selectTab(
            TextView tab) {

        tab.setTextColor(
                requireContext()
                        .getColor(
                                R.color.green_light
                        )
        );


        tab.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );


        tab.setBackgroundResource(
                R.drawable.bg_tab_selected
        );
    }


    // =====================================================
    // MEMBERSHIP CONTEXT
    // =====================================================

    private void loadMembershipContext() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)
                || memberMembershipId <= 0) {

            return;
        }


        apiService
                .getMembershipById(
                        tenantNo,
                        deviceToken,
                        memberMembershipId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()
                                        || response.body().getData() == null) {

                                    return;
                                }


                                MemberMembershipResponse membership =
                                        response.body()
                                                .getData();


                                tenantId =
                                        membership.getTenantId();


                                membershipPlanId =
                                        membership.getMembershipPlanId();


                                branchId =
                                        membership.getBranchId();


                                benefitAdapter.setBranchId(
                                        branchId
                                );


                                loadAssignedPrograms();
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Throwable t) {
                            }
                        }
                );
    }


    // =====================================================
    // MEMBERSHIP CONTEXT FOR BENEFITS
    // =====================================================

    private void loadMembershipContextForBenefits() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)
                || memberMembershipId <= 0) {

            showNoBenefits(
                    "Membership information is missing."
            );

            return;
        }


        apiService
                .getMembershipById(
                        tenantNo,
                        deviceToken,
                        memberMembershipId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()
                                        || response.body().getData() == null) {

                                    showNoBenefits(
                                            extractErrorMessage(
                                                    response,
                                                    "Unable to load membership information."
                                            )
                                    );

                                    return;
                                }


                                MemberMembershipResponse membership =
                                        response.body()
                                                .getData();


                                tenantId =
                                        membership.getTenantId();


                                membershipPlanId =
                                        membership.getMembershipPlanId();


                                branchId =
                                        membership.getBranchId();


                                benefitAdapter.setBranchId(
                                        branchId
                                );


                                if (branchId == null
                                        || branchId <= 0) {

                                    showNoBenefits(
                                            "Branch information is unavailable."
                                    );

                                    return;
                                }


                                loadBenefits();
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showNoBenefits(
                                        "Connection failed: "
                                                + safe(
                                                t.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // LOAD ASSIGNED PROGRAMS
    // =====================================================

    private void loadAssignedPrograms() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)
                || memberMembershipId <= 0
                || tenantId == null
                || tenantId <= 0) {

            return;
        }


        apiService
                .getMembershipPrograms(
                        tenantNo,
                        deviceToken,
                        memberMembershipId,
                        tenantId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                MemberMembershipProgramResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipProgramResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipProgramResponse>>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                assignedProgramIds.clear();


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getData() != null) {

                                    for (
                                            MemberMembershipProgramResponse item
                                            : response.body().getData()
                                    ) {

                                        if (item == null
                                                || item.getProgramId() == null) {

                                            continue;
                                        }


                                        if (!Boolean.TRUE.equals(
                                                item.getStatus()
                                        )) {

                                            continue;
                                        }


                                        assignedProgramIds.add(
                                                item.getProgramId()
                                        );
                                    }
                                }


                                programAdapter
                                        .setAssignedProgramIds(
                                                assignedProgramIds
                                        );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipProgramResponse>>>
                                            call,

                                    Throwable t) {
                            }
                        }
                );
    }


    // =====================================================
    // LOAD ASSIGNED BENEFITS
    // =====================================================

    private void loadAssignedBenefits() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)
                || memberMembershipId <= 0) {

            return;
        }


        apiService
                .getMembershipBenefits(
                        tenantNo,
                        deviceToken,
                        memberMembershipId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                MemberMembershipBenefitResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipBenefitResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipBenefitResponse>>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                assignedBenefitIds.clear();


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getData() != null) {

                                    for (
                                            MemberMembershipBenefitResponse item
                                            : response.body().getData()
                                    ) {

                                        if (item == null
                                                || item.getBenefitId() == null) {

                                            continue;
                                        }


                                        if (!Boolean.TRUE.equals(
                                                item.getStatus()
                                        )) {

                                            continue;
                                        }


                                        assignedBenefitIds.add(
                                                item.getBenefitId()
                                        );
                                    }
                                }


                                benefitAdapter
                                        .setAssignedBenefitIds(
                                                assignedBenefitIds
                                        );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipBenefitResponse>>>
                                            call,

                                    Throwable t) {
                            }
                        }
                );
    }


    // =====================================================
    // LOAD PROGRAMS
    // =====================================================

    private void loadPrograms() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)) {

            showNoPrograms(
                    "Tenant/device information is missing."
            );

            return;
        }


        if (branchId == null
                || branchId <= 0) {

            showNoPrograms(
                    "Branch information is missing."
            );

            return;
        }


        apiService
                .getPrograms(
                        tenantNo,
                        deviceToken,
                        branchId,
                        true
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                ProgramResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            ProgramResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            ProgramResponse>>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()
                                        || response.body().getData() == null) {

                                    showNoPrograms(
                                            "Unable to load programs."
                                    );

                                    return;
                                }


                                programAdapter.setPrograms(
                                        response.body()
                                                .getData()
                                );


                                programAdapter
                                        .setAssignedProgramIds(
                                                assignedProgramIds
                                        );


                                txtNoAddPrograms.setVisibility(
                                        View.GONE
                                );


                                recyclerAddPrograms.setVisibility(
                                        View.VISIBLE
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            ProgramResponse>>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showNoPrograms(
                                        "Connection failed."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // LOAD BENEFITS
    // =====================================================

    private void loadBenefits() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (isBlank(tenantNo)
                || isBlank(deviceToken)) {

            showNoBenefits(
                    "Tenant/device information is missing."
            );

            return;
        }


        if (branchId == null
                || branchId <= 0) {

            showNoBenefits(
                    "Branch information is not available."
            );

            return;
        }


        benefitAdapter.setBranchId(
                branchId
        );


        apiService
                .getActiveBenefitsByBranch(
                        tenantNo,
                        deviceToken,
                        branchId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                BenefitResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            BenefitResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            BenefitResponse>>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showNoBenefits(
                                            extractErrorMessage(
                                                    response,
                                                    "Unable to load benefits."
                                            )
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<
                                                BenefitResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showNoBenefits(
                                            apiResponse.getMessage() == null
                                                    ? "Unable to load benefits."
                                                    : apiResponse.getMessage()
                                    );

                                    return;
                                }


                                List<BenefitResponse> data =
                                        apiResponse.getData();


                                if (data == null
                                        || data.isEmpty()) {

                                    showNoBenefits(
                                            "No active benefits are available for this branch."
                                    );

                                    return;
                                }


                                List<BenefitResponse>
                                        activeBenefits =
                                        new ArrayList<>();


                                for (
                                        BenefitResponse benefit
                                        : data
                                ) {

                                    if (benefit == null) {
                                        continue;
                                    }


                                    if (!Boolean.TRUE.equals(
                                            benefit.getStatus()
                                    )) {

                                        continue;
                                    }


                                    activeBenefits.add(
                                            benefit
                                    );
                                }


                                if (activeBenefits.isEmpty()) {

                                    showNoBenefits(
                                            "No active benefits are available for this branch."
                                    );

                                    return;
                                }


                                benefitAdapter.setBenefits(
                                        activeBenefits
                                );


                                benefitAdapter
                                        .setAssignedBenefitIds(
                                                assignedBenefitIds
                                        );


                                txtNoAddBenefits.setVisibility(
                                        View.GONE
                                );


                                recyclerAddBenefits
                                        .setVisibility(
                                                View.VISIBLE
                                        );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            BenefitResponse>>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showNoBenefits(
                                        "Connection failed while loading benefits."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // PROGRAM CLICK
    // =====================================================

    private void onProgramAddClicked(
            ProgramResponse program) {

        if (program == null
                || program.getProgramId() == null) {

            return;
        }


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Add Program"
                )
                .setMessage(
                        "Are you sure you want to add \"" +
                                safe(
                                        program.getProgramName()
                                ) +
                                "\" to " +
                                safe(
                                        memberName
                                ) +
                                "?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Add",
                        (dialog, which) ->
                                assignProgram(
                                        program
                                )
                )
                .show();
    }


    // =====================================================
    // ASSIGN PROGRAM
    // =====================================================

    private void assignProgram(
            ProgramResponse program) {

        if (program == null
                || program.getProgramId() == null) {

            return;
        }


        if (assignedProgramIds.contains(
                program.getProgramId()
        )) {

            showToast(
                    "This program is already added."
            );

            return;
        }


        if (tenantId == null
                || tenantId <= 0) {

            loadTenantAndAssignProgram(
                    program
            );

            return;
        }


        performAssignProgram(
                program
        );
    }


    // =====================================================
    // LOAD TENANT AND RETRY PROGRAM
    // =====================================================

    private void loadTenantAndAssignProgram(
            ProgramResponse program) {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        apiService
                .getMembershipById(
                        tenantNo,
                        deviceToken,
                        memberMembershipId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()
                                        || response.body().getData() == null) {

                                    showToast(
                                            extractErrorMessage(
                                                    response,
                                                    "Unable to load membership information."
                                            )
                                    );

                                    return;
                                }


                                tenantId =
                                        response.body()
                                                .getData()
                                                .getTenantId();


                                if (tenantId == null
                                        || tenantId <= 0) {

                                    showToast(
                                            "Tenant information is missing."
                                    );

                                    return;
                                }


                                performAssignProgram(
                                        program
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse>>
                                            call,

                                    Throwable t) {

                                showToast(
                                        "Connection failed."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // PERFORM PROGRAM ASSIGN
    // =====================================================

    private void performAssignProgram(
            ProgramResponse program) {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantId == null
                || tenantId <= 0) {

            showToast(
                    "Tenant information is missing."
            );

            return;
        }


        int duration =
                program.getDurationDays() == null
                        ? 0
                        : program.getDurationDays();


        LocalDate start =
                LocalDate.now();


        LocalDate end =
                duration > 0
                        ? start.plusDays(
                        duration - 1L
                )
                        : start;


        MemberMembershipProgramRequest request =
                new MemberMembershipProgramRequest();


        request.setProgramId(
                program.getProgramId()
        );


        request.setStartDate(
                start.toString()
        );


        request.setEndDate(
                end.toString()
        );


        request.setNotes(
                ""
        );


        apiService
                .assignMembershipProgram(
                        tenantNo,
                        deviceToken,
                        tenantId,
                        memberMembershipId,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipProgramResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipProgramResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipProgramResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()) {

                                    showToast(
                                            extractErrorMessage(
                                                    response,
                                                    "Unable to add program."
                                            )
                                    );

                                    return;
                                }


                                assignedProgramIds.add(
                                        program.getProgramId()
                                );


                                programAdapter
                                        .setAssignedProgramIds(
                                                assignedProgramIds
                                        );


                                membershipChanged =
                                        true;


                                showToast(
                                        "Program added successfully."
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipProgramResponse>>
                                            call,

                                    Throwable t) {

                                showToast(
                                        "Connection failed."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // BENEFIT CLICK
    // =====================================================

    private void onBenefitAddClicked(
            BenefitResponse benefit) {

        if (benefit == null
                || benefit.getBenefitId() == null) {

            return;
        }


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Add Benefit"
                )
                .setMessage(
                        "Are you sure you want to add \"" +
                                safe(
                                        benefit.getBenefitName()
                                ) +
                                "\" to " +
                                safe(
                                        memberName
                                ) +
                                "?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Add",
                        (dialog, which) ->
                                addBenefit(
                                        benefit
                                )
                )
                .show();
    }


    // =====================================================
    // ADD BENEFIT
    // =====================================================

    private void addBenefit(
            BenefitResponse benefit) {

        Integer benefitId =
                benefit.getBenefitId();


        if (benefitId == null) {
            return;
        }


        if (assignedBenefitIds.contains(
                benefitId
        )) {

            showToast(
                    "This benefit is already added."
            );

            return;
        }


        MemberMembershipBenefitRequest request =
                new MemberMembershipBenefitRequest();


        request.setBenefitId(
                benefitId
        );


        /*
         * Do NOT set LocalDate here.
         *
         * Gson/Jackson previously caused:
         *
         * JSON parse error:
         * Expected array or string.
         *
         * Backend defaults startDate to LocalDate.now().
         */

        request.setNotes(
                ""
        );


        apiService
                .addMembershipBenefit(
                        deviceStorage.getTenantNo(),
                        deviceStorage.getDeviceToken(),
                        memberMembershipId,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipBenefitResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipBenefitResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipBenefitResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()) {

                                    showLongError(
                                            extractErrorMessage(
                                                    response,
                                                    "Unable to add benefit. HTTP "
                                                            + response.code()
                                            )
                                    );

                                    return;
                                }


                                if (response.body() == null) {

                                    showLongError(
                                            "Response body is empty."
                                    );

                                    return;
                                }


                                if (!response.body().isSuccess()) {

                                    showLongError(
                                            safe(
                                                    response.body()
                                                            .getMessage()
                                            )
                                    );

                                    return;
                                }


                                assignedBenefitIds.add(
                                        benefitId
                                );


                                benefitAdapter
                                        .setAssignedBenefitIds(
                                                assignedBenefitIds
                                        );


                                membershipChanged =
                                        true;


                                showToast(
                                        "Benefit added successfully."
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberMembershipBenefitResponse>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showLongError(
                                        "Connection failed:\n\n"
                                                + safe(
                                                t == null
                                                        ? null
                                                        : t.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // EMPTY STATES
    // =====================================================

    private void showNoPrograms(
            String message) {

        programAdapter.setPrograms(
                null
        );


        recyclerAddPrograms.setVisibility(
                View.GONE
        );


        txtNoAddPrograms.setText(
                message
        );


        txtNoAddPrograms.setVisibility(
                View.VISIBLE
        );
    }


    private void showNoBenefits(
            String message) {

        benefitAdapter.setBenefits(
                null
        );


        recyclerAddBenefits.setVisibility(
                View.GONE
        );


        txtNoAddBenefits.setText(
                message
        );


        txtNoAddBenefits.setVisibility(
                View.VISIBLE
        );
    }


    // =====================================================
    // ERROR MESSAGE
    // =====================================================

    private String extractErrorMessage(
            Response<?> response,
            String fallback) {

        if (response == null) {

            return fallback;
        }


        try {

            if (response.errorBody() != null) {

                String raw =
                        response.errorBody()
                                .string();


                if (raw != null
                        && !raw.trim().isEmpty()) {

                    try {

                        JSONObject object =
                                new JSONObject(
                                        raw
                                );


                        String message =
                                object.optString(
                                        "message",
                                        ""
                                );


                        if (!message.trim().isEmpty()) {

                            return message;
                        }

                    } catch (Exception ignored) {

                        return raw;
                    }
                }
            }

        } catch (Exception ignored) {
        }


        if (response.body() instanceof ApiResponse) {

            ApiResponse<?> body =
                    (ApiResponse<?>) response.body();


            if (body.getMessage() != null
                    && !body.getMessage()
                    .trim()
                    .isEmpty()) {

                return body.getMessage();
            }
        }


        return fallback;
    }


    // =====================================================
    // LONG ERROR
    // =====================================================

    private void showLongError(
            String message) {

        if (!isAdded()) {
            return;
        }


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "API Error"
                )
                .setMessage(
                        message
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    // =====================================================
    // TOAST
    // =====================================================

    private void showToast(
            String message) {

        if (!isAdded()) {
            return;
        }


        Toast.makeText(
                requireContext(),
                safe(
                        message
                ),
                Toast.LENGTH_LONG
        ).show();
    }


    // =====================================================
    // SAFE
    // =====================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }


        return value;
    }


    // =====================================================
    // BLANK
    // =====================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }


    // =====================================================
    // CLOSE → REFRESH PARENT
    // =====================================================

    @Override
    public void onDismiss(
            @NonNull DialogInterface dialog) {

        super.onDismiss(
                dialog
        );


        if (membershipChanged
                && onItemAddedListener != null) {

            onItemAddedListener
                    .onItemAdded();
        }
    }


    // =====================================================
    // WINDOW
    // =====================================================

    @Override
    public void onStart() {

        super.onStart();


        Dialog dialog =
                getDialog();


        if (dialog == null
                || dialog.getWindow() == null) {

            return;
        }


        dialog.getWindow()
                .setBackgroundDrawableResource(
                        android.R.color.transparent
                );


        dialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
    }
}