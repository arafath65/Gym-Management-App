package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.MainDashboard.AddBenefitActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.BenefitsAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BenefitBranchResponse;
import arafath65.minime.gymmanagementui.model.BenefitResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BenefitsFragment
        extends Fragment
        implements BenefitsAdapter.OnBenefitActionListener {


    // =====================================================
    // REQUEST CODE
    // =====================================================

    private static final int REQUEST_BENEFIT = 1002;


    // =====================================================
    // STORAGE
    // =====================================================

    private SessionStorage sessionStorage;

    private DeviceStorage deviceStorage;


    // =====================================================
    // API
    // =====================================================

    private ApiService apiService;


    // =====================================================
    // VIEWS
    // =====================================================

    private RecyclerView recyclerBenefits;

    private TextInputEditText txtSearchBenefit;

    private AutoCompleteTextView txtBenefitStatusFilter;

    private TextView txtBenefitCount;

    private View btnAddBenefit;


    // =====================================================
    // ADAPTER
    // =====================================================

    private BenefitsAdapter benefitsAdapter;


    // =====================================================
    // DATA
    // =====================================================

    private final List<BenefitResponse> allBenefits =
            new ArrayList<>();


    private final List<BenefitResponse> benefitList =
            new ArrayList<>();


    // =====================================================
    // STATUS FILTER
    // =====================================================

    private String selectedBenefitStatus =
            "ACTIVE";


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitsFragment() {

        // Required empty constructor
    }


    // =====================================================
    // REFRESH
    // =====================================================

    public void refreshBenefits() {

        loadBenefits();
    }


    // =====================================================
    // ON ATTACH
    // =====================================================

    @Override
    public void onAttach(
            @NonNull Context context) {

        super.onAttach(context);

        sessionStorage =
                new SessionStorage(context);

        deviceStorage =
                new DeviceStorage(context);
    }


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
                R.layout.fragment_benefits,
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


        // -------------------------------------------------
        // STORAGE
        // -------------------------------------------------

        if (sessionStorage == null) {

            sessionStorage =
                    new SessionStorage(
                            requireContext()
                    );
        }


        if (deviceStorage == null) {

            deviceStorage =
                    new DeviceStorage(
                            requireContext()
                    );
        }


        // -------------------------------------------------
        // API
        // -------------------------------------------------

        apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        // -------------------------------------------------
        // VIEWS
        // -------------------------------------------------

        initializeViews(view);


        // -------------------------------------------------
        // RECYCLER
        // -------------------------------------------------

        recyclerBenefits.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        2
                )
        );


        // -------------------------------------------------
        // ADAPTER
        // -------------------------------------------------

        benefitsAdapter =
                new BenefitsAdapter(
                        this
                );


        recyclerBenefits.setAdapter(
                benefitsAdapter
        );


        // -------------------------------------------------
        // STATUS FILTER
        // -------------------------------------------------

        setupStatusFilter();


        // -------------------------------------------------
        // SEARCH
        // -------------------------------------------------

        setupSearch();


        // -------------------------------------------------
        // ADD BENEFIT
        // -------------------------------------------------

        btnAddBenefit.setOnClickListener(
                v -> openAddBenefit()
        );


        // -------------------------------------------------
        // LOAD
        // -------------------------------------------------

        loadBenefits();
    }


    // =====================================================
    // RESUME
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();

        if (apiService != null
                && isAdded()
                && getView() != null) {

            loadBenefits();
        }
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews(
            View view) {

        recyclerBenefits =
                view.findViewById(
                        R.id.recyclerBenefits
                );


        txtSearchBenefit =
                view.findViewById(
                        R.id.txtSearchBenefit
                );


        txtBenefitStatusFilter =
                view.findViewById(
                        R.id.txtBenefitStatusFilter
                );


        txtBenefitCount =
                view.findViewById(
                        R.id.txtBenefitCount
                );


        btnAddBenefit =
                view.findViewById(
                        R.id.btnAddBenefit
                );
    }


    // =====================================================
    // STATUS FILTER
    // =====================================================

    private void setupStatusFilter() {

        if (txtBenefitStatusFilter == null) {
            return;
        }


        String[] statuses = {
                "Active",
                "Inactive",
                "All"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        statuses
                );


        txtBenefitStatusFilter.setAdapter(
                adapter
        );


        // -------------------------------------------------
        // DEFAULT
        // -------------------------------------------------

        txtBenefitStatusFilter.setText(
                "Active",
                false
        );


        selectedBenefitStatus =
                "ACTIVE";


        // -------------------------------------------------
        // SELECTION
        // -------------------------------------------------

        txtBenefitStatusFilter.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selected =
                            statuses[position];


                    if ("Active".equals(
                            selected
                    )) {

                        selectedBenefitStatus =
                                "ACTIVE";

                    } else if ("Inactive".equals(
                            selected
                    )) {

                        selectedBenefitStatus =
                                "INACTIVE";

                    } else {

                        selectedBenefitStatus =
                                "ALL";
                    }


                    applyBenefitFilters();
                }
        );
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void setupSearch() {

        if (txtSearchBenefit == null) {
            return;
        }


        txtSearchBenefit.addTextChangedListener(
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

                        applyBenefitFilters();
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }


    // =====================================================
// APPLY BENEFIT FILTERS
// =====================================================

    private void applyBenefitFilters() {

        if (benefitsAdapter == null) {
            return;
        }


        benefitList.clear();


        // =================================================
        // SEARCH TEXT
        // =================================================

        String searchText = "";


        if (txtSearchBenefit != null
                && txtSearchBenefit.getText() != null) {

            searchText =
                    txtSearchBenefit
                            .getText()
                            .toString()
                            .trim()
                            .toLowerCase(
                                    Locale.ROOT
                            );
        }


        // =================================================
        // GLOBAL BRANCH
        // =================================================

        boolean allBranchesSelected =
                sessionStorage == null
                        || sessionStorage.isAllBranches();


        Integer selectedBranchId =
                null;


        if (!allBranchesSelected
                && sessionStorage != null) {

            selectedBranchId =
                    sessionStorage
                            .getSelectedBranchId();
        }


        // =================================================
        // FILTER
        // =================================================

        for (BenefitResponse benefit :
                allBenefits) {

            if (benefit == null) {
                continue;
            }


            // =================================================
            // STATUS
            // =================================================

            boolean statusMatches;


            if ("ALL".equals(
                    selectedBenefitStatus
            )) {

                statusMatches = true;

            } else if ("ACTIVE".equals(
                    selectedBenefitStatus
            )) {

                statusMatches =
                        Boolean.TRUE.equals(
                                benefit.getStatus()
                        );

            } else {

                statusMatches =
                        Boolean.FALSE.equals(
                                benefit.getStatus()
                        );
            }


            if (!statusMatches) {
                continue;
            }


            // =================================================
            // BRANCH FILTER
            // =================================================

            if (!allBranchesSelected) {

                if (selectedBranchId == null
                        || selectedBranchId <= 0) {

                    continue;
                }


                boolean branchMatches =
                        benefitBelongsToBranch(
                                benefit,
                                selectedBranchId
                        );


                if (!branchMatches) {
                    continue;
                }
            }


            // =================================================
            // SEARCH
            // =================================================

            if (!searchText.isEmpty()) {

                String code =
                        safeSearchText(
                                benefit.getBenefitCode()
                        );


                String name =
                        safeSearchText(
                                benefit.getBenefitName()
                        );


                String description =
                        safeSearchText(
                                benefit.getDescription()
                        );


                String billing =
                        safeSearchText(
                                benefit.getBillingType()
                        );


                String branches =
                        safeSearchText(
                                getBranchSearchText(
                                        benefit
                                )
                        );


                if (!code.contains(searchText)
                        && !name.contains(searchText)
                        && !description.contains(searchText)
                        && !billing.contains(searchText)
                        && !branches.contains(searchText)) {

                    continue;
                }
            }


            // =================================================
            // MATCHED
            // =================================================

            benefitList.add(
                    benefit
            );
        }


        // =================================================
        // UPDATE ADAPTER
        // =================================================

        benefitsAdapter.setBenefits(
                benefitList
        );


        // =================================================
        // COUNT
        // =================================================

        updateCount();
    }

    // =====================================================
// CHECK BENEFIT BRANCH
// =====================================================

    private boolean benefitBelongsToBranch(
            BenefitResponse benefit,
            Integer selectedBranchId) {

        if (benefit == null
                || selectedBranchId == null
                || selectedBranchId <= 0) {

            return false;
        }


        // -------------------------------------------------
        // ALL BRANCHES
        // -------------------------------------------------

        if ("ALL_BRANCHES".equalsIgnoreCase(
                safeText(
                        benefit.getBranchMode()
                )
        )) {

            return true;
        }


        // -------------------------------------------------
        // SELECTED BRANCHES
        // -------------------------------------------------

        List<BenefitBranchResponse>
                assignments =
                benefit.getBranchAssignments();


        if (assignments == null
                || assignments.isEmpty()) {

            return false;
        }


        for (BenefitBranchResponse assignment :
                assignments) {

            if (assignment == null
                    || assignment.getBranchId() == null) {

                continue;
            }


            if (selectedBranchId.equals(
                    assignment.getBranchId()
            )) {

                return true;
            }
        }


        return false;
    }


// =====================================================
// BRANCH SEARCH TEXT
// =====================================================

    private String getBranchSearchText(
            BenefitResponse benefit) {

        if (benefit == null) {
            return "";
        }


        StringBuilder builder =
                new StringBuilder();


        if ("ALL_BRANCHES".equalsIgnoreCase(
                safeText(
                        benefit.getBranchMode()
                )
        )) {

            builder.append(
                    "all branches "
            );
        }


        List<BenefitBranchResponse>
                assignments =
                benefit.getBranchAssignments();


        if (assignments != null) {

            for (BenefitBranchResponse assignment :
                    assignments) {

                if (assignment == null) {
                    continue;
                }


                if (assignment.getBranchNo() != null) {

                    builder.append(
                            assignment.getBranchNo()
                    );

                    builder.append(" ");
                }


                if (assignment.getBranchName() != null) {

                    builder.append(
                            assignment.getBranchName()
                    );

                    builder.append(" ");
                }
            }
        }


        return builder.toString();
    }


// =====================================================
// SAFE TEXT
// =====================================================

    private String safeText(
            String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }


    // =====================================================
    // LOAD BENEFITS
    // =====================================================

    private void loadBenefits() {

        if (!isAdded()) {
            return;
        }


        // -------------------------------------------------
        // STORAGE
        // -------------------------------------------------

        if (sessionStorage == null) {

            sessionStorage =
                    new SessionStorage(
                            requireContext()
                    );
        }


        if (deviceStorage == null) {

            deviceStorage =
                    new DeviceStorage(
                            requireContext()
                    );
        }


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        String tenantNo =
                sessionStorage.getTenantNo();


        // -------------------------------------------------
        // DEVICE
        // -------------------------------------------------

        String deviceToken =
                deviceStorage.getDeviceToken();


        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showMessage(
                    "Tenant information is missing."
            );

            return;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showMessage(
                    "Device information is missing."
            );

            return;
        }


        // -------------------------------------------------
        // API
        // -------------------------------------------------

        apiService
                .getBenefits(
                        tenantNo,
                        deviceToken
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<BenefitResponse>>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<BenefitResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<BenefitResponse>>>
                                            response) {

                                if (!isAdded()
                                        || getView() == null) {

                                    return;
                                }


                                // -------------------------
                                // HTTP ERROR
                                // -------------------------

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showMessage(
                                            "Unable to load benefits."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<BenefitResponse>>
                                        apiResponse =
                                        response.body();


                                // -------------------------
                                // SERVER ERROR
                                // -------------------------

                                if (!apiResponse.isSuccess()) {

                                    showMessage(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load benefits."
                                            )
                                    );

                                    return;
                                }


                                // -------------------------
                                // DATA
                                // -------------------------

                                List<BenefitResponse>
                                        responseList =
                                        apiResponse.getData();


                                allBenefits.clear();


                                if (responseList != null) {

                                    allBenefits.addAll(
                                            responseList
                                    );
                                }


                                // -------------------------
                                // FILTER
                                // -------------------------

                                applyBenefitFilters();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<BenefitResponse>>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showMessage(
                                        "Unable to connect to server."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // ADD BENEFIT
    // =====================================================

    private void openAddBenefit() {

        Intent intent =
                new Intent(
                        requireContext(),
                        AddBenefitActivity.class
                );


        startActivityForResult(
                intent,
                REQUEST_BENEFIT
        );
    }


    // =====================================================
    // EDIT BENEFIT
    // =====================================================

    @Override
    public void onBenefitClick(
            BenefitResponse benefit) {

        if (benefit == null
                || benefit.getBenefitId() == null) {

            showMessage(
                    "Invalid benefit."
            );

            return;
        }


        openEditBenefit(
                benefit
        );
    }


    // =====================================================
    // MORE MENU
    // =====================================================

    @Override
    public void onBenefitMore(
            BenefitResponse benefit,
            View anchor) {

        if (benefit == null
                || benefit.getBenefitId() == null) {

            showMessage(
                    "Invalid benefit."
            );

            return;
        }

        PopupMenu popupMenu =
                new PopupMenu(
                        requireContext(),
                        anchor
                );

        // -------------------------------------------------
        // EDIT
        // -------------------------------------------------

        popupMenu.getMenu().add(
                "Edit"
        );


        // -------------------------------------------------
        // ACTIVATE / DEACTIVATE
        // -------------------------------------------------

        if (Boolean.TRUE.equals(
                benefit.getStatus()
        )) {

            popupMenu.getMenu().add(
                    "Deactivate"
            );

        } else {

            popupMenu.getMenu().add(
                    "Activate"
            );
        }


        popupMenu.setOnMenuItemClickListener(
                item -> {

                    String action =
                            item.getTitle()
                                    .toString();


                    // -----------------------------------------
                    // EDIT
                    // -----------------------------------------

                    if ("Edit".equals(action)) {

                        openEditBenefit(
                                benefit
                        );

                        return true;
                    }


                    // -----------------------------------------
                    // DEACTIVATE
                    // -----------------------------------------

                    if ("Deactivate".equals(action)) {

                        deactivateBenefit(
                                benefit
                        );

                        return true;
                    }


                    // -----------------------------------------
                    // ACTIVATE
                    // -----------------------------------------

                    if ("Activate".equals(action)) {

                        activateBenefit(
                                benefit
                        );

                        return true;
                    }


                    return false;
                }
        );


        popupMenu.show();
    }

    private void deactivateBenefit(
            BenefitResponse benefit) {

        if (benefit == null
                || benefit.getBenefitId() == null) {

            return;
        }


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Deactivate Benefit"
                )
                .setMessage(
                        "Are you sure you want to deactivate \""
                                + benefit.getBenefitName()
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Deactivate",
                        (dialog, which) ->
                                executeDeactivateBenefit(
                                        benefit
                                )
                )
                .show();
    }

    private void executeDeactivateBenefit(
            BenefitResponse benefit) {

        String tenantNo =
                sessionStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()
                || deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showMessage(
                    "Tenant or device information is missing."
            );

            return;
        }


        apiService
                .deactivateBenefit(
                        tenantNo,
                        deviceToken,
                        benefit.getBenefitId()
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<Void>> call,
                                    Response<ApiResponse<Void>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showMessage(
                                            "Unable to deactivate benefit."
                                    );

                                    return;
                                }


                                ApiResponse<Void> apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showMessage(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to deactivate benefit."
                                            )
                                    );

                                    return;
                                }


                                showMessage(
                                        "Benefit deactivated successfully."
                                );


                                loadBenefits();
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<Void>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showMessage(
                                        "Unable to connect to server."
                                );
                            }
                        }
                );
    }

    private void activateBenefit(
            BenefitResponse benefit) {

        if (benefit == null
                || benefit.getBenefitId() == null) {

            return;
        }


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Activate Benefit"
                )
                .setMessage(
                        "Are you sure you want to activate \""
                                + benefit.getBenefitName()
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Activate",
                        (dialog, which) ->
                                executeActivateBenefit(
                                        benefit
                                )
                )
                .show();
    }

    private void executeActivateBenefit(
            BenefitResponse benefit) {

        String tenantNo =
                sessionStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()
                || deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showMessage(
                    "Tenant or device information is missing."
            );

            return;
        }


        apiService
                .activateBenefit(
                        tenantNo,
                        deviceToken,
                        benefit.getBenefitId()
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<Void>> call,
                                    Response<ApiResponse<Void>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showMessage(
                                            "Unable to activate benefit."
                                    );

                                    return;
                                }


                                ApiResponse<Void> apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showMessage(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to activate benefit."
                                            )
                                    );

                                    return;
                                }


                                showMessage(
                                        "Benefit activated successfully."
                                );


                                loadBenefits();
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<Void>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showMessage(
                                        "Unable to connect to server."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // EDIT BENEFIT
    // =====================================================

    private void openEditBenefit(
            BenefitResponse benefit) {

        Intent intent =
                new Intent(
                        requireContext(),
                        AddBenefitActivity.class
                );


        intent.putExtra(
                "editMode",
                true
        );


        intent.putExtra(
                "benefitId",
                benefit.getBenefitId()
        );


        startActivityForResult(
                intent,
                REQUEST_BENEFIT
        );
    }


    // =====================================================
    // ACTIVITY RESULT
    // =====================================================

    @Override
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (requestCode == REQUEST_BENEFIT
                && resultCode ==
                requireActivity().RESULT_OK) {

            loadBenefits();
        }
    }


    // =====================================================
    // UPDATE COUNT
    // =====================================================

    private void updateCount() {

        if (txtBenefitCount != null) {

            txtBenefitCount.setText(
                    "Total Benefits: "
                            + benefitList.size()
            );
        }
    }


    // =====================================================
    // SAFE SEARCH TEXT
    // =====================================================

    private String safeSearchText(
            String value) {

        if (value == null) {
            return "";
        }


        return value
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }


    // =====================================================
    // SAFE MESSAGE
    // =====================================================

    private String safeMessage(
            String message,
            String fallback) {

        if (message == null
                || message.trim().isEmpty()) {

            return fallback;
        }


        return message;
    }


    // =====================================================
    // MESSAGE
    // =====================================================

    private void showMessage(
            String message) {

        if (!isAdded()) {
            return;
        }


        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // =====================================================
    // DETACH
    // =====================================================

    @Override
    public void onDetach() {

        super.onDetach();

        sessionStorage = null;

        deviceStorage = null;
    }
}