package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.MainDashboard.AddMembershipPlanActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.MembershipPlanAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBranchResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MembershipPlansFragment
        extends Fragment {


    // =====================================================
    // VIEWS
    // =====================================================

    private RecyclerView recyclerMembershipPlans;

    private TextView txtMembershipPlanCount;

    private AutoCompleteTextView
            txtMembershipPlanStatusFilter;


    // =====================================================
    // DATA
    // =====================================================

    private final List<MembershipPlanResponse>
            allPlans =
            new ArrayList<>();


    private final List<MembershipPlanResponse>
            filteredPlans =
            new ArrayList<>();


    private MembershipPlanAdapter adapter;


    // =====================================================
    // API / SESSION
    // =====================================================

    private ApiService apiService;

    private SessionStorage sessionStorage;

    private DeviceStorage deviceStorage;


    // =====================================================
    // CREATE VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_membership_plans,
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


        // =================================================
        // SESSION
        // =================================================

        sessionStorage =
                new SessionStorage(
                        requireContext()
                );


        deviceStorage =
                new DeviceStorage(
                        requireContext()
                );


        // =================================================
        // API
        // =================================================

        apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        // =================================================
        // ADD MEMBERSHIP PLAN
        // =================================================

        View btnAddMembershipPlan =
                view.findViewById(
                        R.id.btnAddMembershipPlan
                );


        btnAddMembershipPlan.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    requireContext(),
                                    AddMembershipPlanActivity.class
                            );

                    startActivity(intent);
                }
        );


        // =================================================
        // FIND VIEWS
        // =================================================

        recyclerMembershipPlans =
                view.findViewById(
                        R.id.recyclerMembershipPlans
                );


        txtMembershipPlanCount =
                view.findViewById(
                        R.id.txtMembershipPlanCount
                );


        txtMembershipPlanStatusFilter =
                view.findViewById(
                        R.id.txtMembershipPlanStatusFilter
                );


        // =================================================
        // RECYCLER
        // =================================================

        recyclerMembershipPlans.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        adapter =
                new MembershipPlanAdapter(
                        filteredPlans
                );


        // =================================================
        // THREE DOT MENU
        // =================================================

        adapter.setOnPlanMoreClickListener(
                this::showPlanMenu
        );


        recyclerMembershipPlans.setAdapter(
                adapter
        );


        // =================================================
        // STATUS FILTER
        // =================================================

        setupStatusFilter();


        // =================================================
        // SEARCH
        // =================================================

        TextView txtSearch =
                view.findViewById(
                        R.id.txtSearchMembershipPlan
                );


        txtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        applyFilters(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );


        // =================================================
        // LOAD MEMBERSHIP PLANS
        // =================================================

        loadMembershipPlans();
    }


    // =====================================================
    // THREE DOT MENU
    // =====================================================

    private void showPlanMenu(
            MembershipPlanResponse plan,
            View anchor) {

        if (!isAdded()
                || plan == null) {

            return;
        }


        PopupMenu popupMenu =
                new PopupMenu(
                        requireContext(),
                        anchor
                );


        // =================================================
        // EDIT
        // =================================================

        popupMenu.getMenu()
                .add(
                        "Edit"
                )
                .setOnMenuItemClickListener(
                        item -> {

                            openEditPlan(
                                    plan
                            );

                            return true;
                        }
                );


        // =================================================
        // ACTIVATE / DEACTIVATE
        // =================================================

        if (Boolean.TRUE.equals(
                plan.getStatus()
        )) {

            popupMenu.getMenu()
                    .add(
                            "Deactivate"
                    )
                    .setOnMenuItemClickListener(
                            item -> {

                                showDeactivateConfirmation(
                                        plan
                                );

                                return true;
                            }
                    );

        } else {

            popupMenu.getMenu()
                    .add(
                            "Activate"
                    )
                    .setOnMenuItemClickListener(
                            item -> {

                                showActivateConfirmation(
                                        plan
                                );

                                return true;
                            }
                    );
        }


        popupMenu.show();
    }


    // =====================================================
    // EDIT PLAN
    // =====================================================

    private void openEditPlan(
            MembershipPlanResponse plan) {

        if (plan == null
                || plan.getMembershipPlanId() == null) {

            showError(
                    "Invalid membership plan."
            );

            return;
        }


        Intent intent =
                new Intent(
                        requireContext(),
                        AddMembershipPlanActivity.class
                );


        intent.putExtra(
                "membershipPlanId",
                plan.getMembershipPlanId()
        );


        intent.putExtra(
                "editMode",
                true
        );


        startActivity(
                intent
        );
    }


    // =====================================================
    // DEACTIVATE CONFIRMATION
    // =====================================================

    private void showDeactivateConfirmation(
            MembershipPlanResponse plan) {

        if (!isAdded()
                || plan == null) {

            return;
        }


        String planName =
                safeText(
                        plan.getPlanName()
                );


        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Deactivate Membership Plan?"
                )
                .setMessage(
                        "Are you sure you want to deactivate\n\n"
                                + "\""
                                + planName
                                + "\"?\n\n"
                                + "This plan will no longer be available "
                                + "for new memberships."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Deactivate",
                        (dialog, which) ->
                                deactivatePlan(
                                        plan
                                )
                )
                .show();
    }


    // =====================================================
    // ACTIVATE CONFIRMATION
    // =====================================================

    private void showActivateConfirmation(
            MembershipPlanResponse plan) {

        if (!isAdded()
                || plan == null) {

            return;
        }


        String planName =
                safeText(
                        plan.getPlanName()
                );


        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Activate Membership Plan?"
                )
                .setMessage(
                        "Are you sure you want to activate\n\n"
                                + "\""
                                + planName
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Activate",
                        (dialog, which) ->
                                activatePlan(
                                        plan
                                )
                )
                .show();
    }


    // =====================================================
    // DEACTIVATE API
    // =====================================================

    private void deactivatePlan(
            MembershipPlanResponse plan) {

        String tenantNo =
                sessionStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (!validateDeviceData(
                tenantNo,
                deviceToken
        )) {

            return;
        }


        Integer planId =
                plan.getMembershipPlanId();


        if (planId == null) {

            showError(
                    "Invalid membership plan."
            );

            return;
        }


        apiService
                .deactivateMembershipPlan(
                        tenantNo,
                        deviceToken,
                        planId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<
                                            ApiResponse<Void>>
                                            call,

                                    @NonNull Response<
                                            ApiResponse<Void>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Membership plan deactivated successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    loadMembershipPlans();

                                } else {

                                    String message =
                                            response.body() == null
                                                    ? "Unable to deactivate membership plan."
                                                    : response.body()
                                                    .getMessage();


                                    showError(
                                            message
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<
                                            ApiResponse<Void>>
                                            call,

                                    @NonNull Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showError(
                                        "Connection failed while deactivating plan."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // ACTIVATE API
    // =====================================================

    private void activatePlan(
            MembershipPlanResponse plan) {

        String tenantNo =
                sessionStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (!validateDeviceData(
                tenantNo,
                deviceToken
        )) {

            return;
        }


        Integer planId =
                plan.getMembershipPlanId();


        if (planId == null) {

            showError(
                    "Invalid membership plan."
            );

            return;
        }


        apiService
                .activateMembershipPlan(
                        tenantNo,
                        deviceToken,
                        planId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<
                                            ApiResponse<Void>>
                                            call,

                                    @NonNull Response<
                                            ApiResponse<Void>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Membership plan activated successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    loadMembershipPlans();

                                } else {

                                    String message =
                                            response.body() == null
                                                    ? "Unable to activate membership plan."
                                                    : response.body()
                                                    .getMessage();


                                    showError(
                                            message
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<
                                            ApiResponse<Void>>
                                            call,

                                    @NonNull Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showError(
                                        "Connection failed while activating plan."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // STATUS FILTER
    // =====================================================

    private void setupStatusFilter() {

        String[] statuses = {
                "Active",
                "Inactive",
                "All"
        };


        ArrayAdapter<String>
                statusAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout
                                .simple_dropdown_item_1line,
                        statuses
                );


        txtMembershipPlanStatusFilter.setAdapter(
                statusAdapter
        );


        txtMembershipPlanStatusFilter.setText(
                "Active",
                false
        );


        txtMembershipPlanStatusFilter.setOnItemClickListener(
                (parent, view, position, id) ->
                        applyFilters(
                                getSearchText()
                        )
        );


        txtMembershipPlanStatusFilter.setOnClickListener(
                v ->
                        txtMembershipPlanStatusFilter
                                .showDropDown()
        );
    }


    // =====================================================
    // LOAD MEMBERSHIP PLANS
    // =====================================================

    private void loadMembershipPlans() {

        String tenantNo =
                sessionStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // VALIDATE TENANT
        // =================================================

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            return;
        }


        // =================================================
        // VALIDATE DEVICE
        // =================================================

        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            return;
        }


        // =================================================
        // API
        // =================================================

        Call<
                ApiResponse<
                        List<MembershipPlanResponse>>>
                call =
                apiService.getMembershipPlans(
                        tenantNo,
                        deviceToken
                );


        call.enqueue(
                new Callback<
                        ApiResponse<
                                List<MembershipPlanResponse>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<
                                    ApiResponse<
                                            List<MembershipPlanResponse>>>
                                    call,

                            @NonNull Response<
                                    ApiResponse<
                                            List<MembershipPlanResponse>>>
                                    response) {

                        if (!isAdded()) {
                            return;
                        }


                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            List<MembershipPlanResponse>
                                    plans =
                                    response.body()
                                            .getData();


                            allPlans.clear();


                            if (plans != null) {

                                allPlans.addAll(
                                        plans
                                );
                            }


                            applyFilters(
                                    getSearchText()
                            );


                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Failed to load membership plans.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<
                                    ApiResponse<
                                            List<MembershipPlanResponse>>>
                                    call,

                            @NonNull Throwable t) {

                        if (!isAdded()) {
                            return;
                        }


                        Toast.makeText(
                                requireContext(),
                                "Unable to connect to server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // =====================================================
    // APPLY FILTERS
    // =====================================================

    private void applyFilters(
            String searchText) {

        filteredPlans.clear();


        String search =
                searchText == null
                        ? ""
                        : searchText
                        .trim()
                        .toLowerCase(
                                Locale.ENGLISH
                        );


        String status =
                txtMembershipPlanStatusFilter
                        .getText()
                        .toString();


        // =================================================
        // FILTER
        // =================================================

        for (
                MembershipPlanResponse plan
                : allPlans
        ) {


            // =============================================
            // STATUS
            // =============================================

            if ("Active".equals(status)
                    && !Boolean.TRUE.equals(
                    plan.getStatus()
            )) {

                continue;
            }


            if ("Inactive".equals(status)
                    && Boolean.TRUE.equals(
                    plan.getStatus()
            )) {

                continue;
            }

            // =====================================================
// GLOBAL BRANCH FILTER
// =====================================================

            Integer selectedBranchId =
                    sessionStorage.getSelectedBranchId();


            if (selectedBranchId != null) {

                if (!planHasBranch(
                        plan,
                        selectedBranchId
                )) {

                    continue;
                }
            }


            // =============================================
            // SEARCH
            // =============================================

            if (!search.isEmpty()) {

                String code =
                        safeLower(
                                plan.getPlanCode()
                        );


                String name =
                        safeLower(
                                plan.getPlanName()
                        );


                String description =
                        safeLower(
                                plan.getDescription()
                        );


                /*
                 * Branch names are now inside
                 * branchAssignments.
                 */
                boolean branchMatches =
                        branchAssignmentsContain(
                                plan,
                                search
                        );


                if (!code.contains(search)
                        && !name.contains(search)
                        && !description.contains(search)
                        && !branchMatches) {

                    continue;
                }
            }


            filteredPlans.add(
                    plan
            );
        }




        // =================================================
        // REFRESH
        // =================================================

        adapter.notifyDataSetChanged();


        // =================================================
        // COUNT
        // =================================================

        txtMembershipPlanCount.setText(
                "Total Plans: "
                        + filteredPlans.size()
        );
    }

    // =====================================================
// CHECK PLAN BRANCH
// =====================================================

    private boolean planHasBranch(
            MembershipPlanResponse plan,
            Integer branchId) {

        if (plan == null
                || branchId == null
                || plan.getBranchAssignments() == null) {

            return false;
        }


        for (
                MembershipPlanBranchResponse assignment
                : plan.getBranchAssignments()
        ) {

            if (assignment == null
                    || assignment.getBranchId() == null) {

                continue;
            }


            if (branchId.equals(
                    assignment.getBranchId()
            )) {

                return true;
            }
        }


        return false;
    }


    // =====================================================
    // SEARCH BRANCH ASSIGNMENTS
    // =====================================================

    private boolean branchAssignmentsContain(
            MembershipPlanResponse plan,
            String search) {

        if (plan == null
                || plan.getBranchAssignments() == null) {

            return false;
        }


        for (
                MembershipPlanBranchResponse assignment
                : plan.getBranchAssignments()
        ) {

            if (assignment == null) {
                continue;
            }


            String branchName =
                    safeLower(
                            assignment.getBranchName()
                    );


            String branchNo =
                    safeLower(
                            assignment.getBranchNo()
                    );


            if (branchName.contains(search)
                    || branchNo.contains(search)) {

                return true;
            }
        }


        return false;
    }


    // =====================================================
    // SEARCH TEXT
    // =====================================================

    private String getSearchText() {

        if (getView() == null) {
            return "";
        }


        TextView search =
                getView()
                        .findViewById(
                                R.id.txtSearchMembershipPlan
                        );


        if (search == null) {
            return "";
        }


        return search
                .getText()
                .toString();
    }


    // =====================================================
    // SAFE LOWER
    // =====================================================

    private String safeLower(
            String value) {

        return value == null
                ? ""
                : value.toLowerCase(
                Locale.ENGLISH
        );
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }


        return value;
    }


    // =====================================================
    // VALIDATE DEVICE
    // =====================================================

    private boolean validateDeviceData(
            String tenantNo,
            String deviceToken) {

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showError(
                    "Tenant information is missing."
            );

            return false;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showError(
                    "Device information is missing."
            );

            return false;
        }


        return true;
    }


    // =====================================================
    // ERROR
    // =====================================================

    private void showError(
            String message) {

        if (!isAdded()) {
            return;
        }


        Toast.makeText(
                requireContext(),
                message == null
                        || message.trim().isEmpty()
                        ? "Something went wrong."
                        : message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =====================================================
    // REFRESH
    // =====================================================

    public void refreshMembershipPlans() {

        if (!isAdded()) {
            return;
        }


        loadMembershipPlans();
    }


    // =====================================================
    // RESUME
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();


        if (apiService != null
                && sessionStorage != null
                && deviceStorage != null) {

            loadMembershipPlans();
        }
    }
}