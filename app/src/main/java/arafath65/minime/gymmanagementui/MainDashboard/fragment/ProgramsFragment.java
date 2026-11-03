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

import arafath65.minime.gymmanagementui.MainDashboard.AddProgramActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.ProgramAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.ProgramResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramsFragment
        extends Fragment
        implements ProgramAdapter.OnProgramActionListener {


    // =====================================================
    // REQUEST CODE
    // =====================================================

    private static final int REQUEST_PROGRAM = 1001;


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

    private RecyclerView recyclerPrograms;

    private TextInputEditText txtSearchProgram;

    private AutoCompleteTextView txtProgramStatusFilter;

    private TextView txtProgramCount;

    private View btnAddProgram;


    // =====================================================
    // ADAPTER
    // =====================================================

    private ProgramAdapter programAdapter;


    // =====================================================
    // DATA
    // =====================================================

    private final List<ProgramResponse> allPrograms =
            new ArrayList<>();


    private final List<ProgramResponse> programList =
            new ArrayList<>();


    // =====================================================
    // STATUS FILTER
    // =====================================================

    private String selectedProgramStatus =
            "ACTIVE";


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramsFragment() {

        // Required empty constructor
    }


    // =====================================================
    // REFRESH PROGRAMS
    // =====================================================

    public void refreshPrograms() {

        loadPrograms();
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
                R.layout.fragment_programs,
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

        recyclerPrograms.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        2
                )
        );


        // -------------------------------------------------
        // ADAPTER
        // -------------------------------------------------

        programAdapter =
                new ProgramAdapter(
                        programList,
                        this
                );


        recyclerPrograms.setAdapter(
                programAdapter
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
        // ADD PROGRAM
        // -------------------------------------------------

        btnAddProgram.setOnClickListener(
                v -> openAddProgram()
        );


        // -------------------------------------------------
        // LOAD
        // -------------------------------------------------

        loadPrograms();
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

            loadPrograms();
        }
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews(
            View view) {

        recyclerPrograms =
                view.findViewById(
                        R.id.recyclerPrograms
                );


        txtSearchProgram =
                view.findViewById(
                        R.id.txtSearchProgram
                );


        txtProgramStatusFilter =
                view.findViewById(
                        R.id.txtProgramStatusFilter
                );


        txtProgramCount =
                view.findViewById(
                        R.id.txtProgramCount
                );


        btnAddProgram =
                view.findViewById(
                        R.id.btnAddProgram
                );
    }


    // =====================================================
    // STATUS FILTER SETUP
    // =====================================================

    private void setupStatusFilter() {

        if (txtProgramStatusFilter == null) {
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


        txtProgramStatusFilter.setAdapter(
                adapter
        );


        txtProgramStatusFilter.setText(
                "Active",
                false
        );


        selectedProgramStatus =
                "ACTIVE";


        txtProgramStatusFilter.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selected =
                            statuses[position];


                    if ("Active".equals(
                            selected
                    )) {

                        selectedProgramStatus =
                                "ACTIVE";

                    } else if ("Inactive".equals(
                            selected
                    )) {

                        selectedProgramStatus =
                                "INACTIVE";

                    } else {

                        selectedProgramStatus =
                                "ALL";
                    }


                    applyProgramFilters();
                }
        );
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void setupSearch() {

        if (txtSearchProgram == null) {
            return;
        }


        txtSearchProgram.addTextChangedListener(
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

                        applyProgramFilters();
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }


    // =====================================================
// APPLY PROGRAM FILTERS
// =====================================================

    private void applyProgramFilters() {

        if (programAdapter == null) {
            return;
        }


        programList.clear();


        String searchText = "";


        if (txtSearchProgram != null
                && txtSearchProgram.getText() != null) {

            searchText =
                    txtSearchProgram
                            .getText()
                            .toString()
                            .trim()
                            .toLowerCase(
                                    Locale.ROOT
                            );
        }


        for (ProgramResponse program :
                allPrograms) {

            if (program == null) {
                continue;
            }


            // =================================================
            // STATUS FILTER
            // =================================================

            boolean statusMatches;


            if ("ALL".equals(
                    selectedProgramStatus
            )) {

                statusMatches = true;

            } else if ("ACTIVE".equals(
                    selectedProgramStatus
            )) {

                statusMatches =
                        Boolean.TRUE.equals(
                                program.getStatus()
                        );

            } else {

                statusMatches =
                        Boolean.FALSE.equals(
                                program.getStatus()
                        );
            }


            if (!statusMatches) {
                continue;
            }


            // =================================================
            // SEARCH FILTER
            // =================================================

            if (!searchText.isEmpty()) {

                String code =
                        safeSearchText(
                                program.getProgramCode()
                        );


                String name =
                        safeSearchText(
                                program.getProgramName()
                        );


                String description =
                        safeSearchText(
                                program.getDescription()
                        );


                /*
                 * Program can now belong to multiple branches.
                 *
                 * Build one searchable string containing
                 * all assigned branch names.
                 */
                StringBuilder branchSearch =
                        new StringBuilder();


                if (program.getBranchAssignments() != null) {

                    for (
                            arafath65.minime.gymmanagementui.model.ProgramBranchResponse branch
                            : program.getBranchAssignments()
                    ) {

                        if (branch == null) {
                            continue;
                        }


                        String branchName =
                                safeSearchText(
                                        branch.getBranchName()
                                );


                        String branchNo =
                                safeSearchText(
                                        branch.getBranchNo()
                                );


                        if (!branchName.isEmpty()) {

                            branchSearch
                                    .append(branchName)
                                    .append(" ");
                        }


                        if (!branchNo.isEmpty()) {

                            branchSearch
                                    .append(branchNo)
                                    .append(" ");
                        }
                    }
                }


                String branch =
                        branchSearch
                                .toString()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                                .trim();


                // =============================================
                // MATCH
                // =============================================

                if (!code.contains(searchText)
                        && !name.contains(searchText)
                        && !description.contains(searchText)
                        && !branch.contains(searchText)) {

                    continue;
                }
            }


            // =================================================
            // ADD
            // =================================================

            programList.add(
                    program
            );
        }


        // =====================================================
        // UPDATE UI
        // =====================================================

        programAdapter.notifyDataSetChanged();


        updateCount();
    }


    // =====================================================
    // LOAD PROGRAMS
    // =====================================================

    private void loadPrograms() {

        if (!isAdded()) {
            return;
        }


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


        String tenantNo =
                sessionStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


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


        Integer branchId = null;


        if (!sessionStorage.isAllBranches()) {

            branchId =
                    sessionStorage
                            .getSelectedBranchId();
        }


        apiService
                .getPrograms(
                        tenantNo,
                        deviceToken,
                        branchId,
                        null
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<ProgramResponse>>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<ProgramResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<ProgramResponse>>>
                                            response) {

                                if (!isAdded()
                                        || getView() == null) {

                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showMessage(
                                            "Unable to load programs."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<ProgramResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showMessage(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load programs."
                                            )
                                    );

                                    return;
                                }


                                List<ProgramResponse>
                                        responseList =
                                        apiResponse.getData();


                                allPrograms.clear();


                                if (responseList != null) {

                                    allPrograms.addAll(
                                            responseList
                                    );
                                }


                                applyProgramFilters();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<ProgramResponse>>>
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
    // ADD PROGRAM
    // =====================================================

    private void openAddProgram() {

        Intent intent =
                new Intent(
                        requireContext(),
                        AddProgramActivity.class
                );


        startActivityForResult(
                intent,
                REQUEST_PROGRAM
        );
    }


    // =====================================================
    // EDIT PROGRAM
    // =====================================================

    @Override
    public void onEditProgram(
            ProgramResponse program) {

        if (program == null
                || program.getProgramId() == null) {

            showMessage(
                    "Invalid program."
            );

            return;
        }


        Intent intent =
                new Intent(
                        requireContext(),
                        AddProgramActivity.class
                );


        intent.putExtra(
                "editMode",
                true
        );


        intent.putExtra(
                "programId",
                program.getProgramId()
        );


        startActivityForResult(
                intent,
                REQUEST_PROGRAM
        );
    }


    // =====================================================
    // ACTIVATE PROGRAM
    // =====================================================

    @Override
    public void onActivateProgram(
            ProgramResponse program) {

        if (program == null
                || program.getProgramId() == null) {

            showMessage(
                    "Invalid program."
            );

            return;
        }


        updateProgramStatus(
                program,
                true
        );
    }


    // =====================================================
    // DEACTIVATE PROGRAM
    // =====================================================

    @Override
    public void onDeactivateProgram(
            ProgramResponse program) {

        if (program == null
                || program.getProgramId() == null) {

            showMessage(
                    "Invalid program."
            );

            return;
        }


        // -------------------------------------------------
        // CONFIRM DEACTIVATION
        // -------------------------------------------------

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Deactivate Program"
                )
                .setMessage(
                        "Are you sure you want to deactivate \""
                                + program.getProgramName()
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Deactivate",
                        (dialog, which) -> {

                            updateProgramStatus(
                                    program,
                                    false
                            );
                        }
                )
                .show();
    }


    // =====================================================
    // UPDATE PROGRAM STATUS
    // =====================================================

    private void updateProgramStatus(
            ProgramResponse program,
            boolean activate) {

        String tenantNo =
                sessionStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()
                || deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showMessage(
                    "Device or tenant information is missing."
            );

            return;
        }


        Call<ApiResponse<ProgramResponse>> call;


        if (activate) {

            call =
                    apiService.activateProgram(
                            tenantNo,
                            deviceToken,
                            program.getProgramId()
                    );

        } else {

            call =
                    apiService.deactivateProgram(
                            tenantNo,
                            deviceToken,
                            program.getProgramId()
                    );
        }


        call.enqueue(
                new Callback<
                        ApiResponse<ProgramResponse>>() {

                    @Override
                    public void onResponse(
                            Call<
                                    ApiResponse<ProgramResponse>>
                                    call,

                            Response<
                                    ApiResponse<ProgramResponse>>
                                    response) {

                        if (!isAdded()) {
                            return;
                        }


                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showMessage(
                                    "Unable to update program status."
                            );

                            return;
                        }


                        ApiResponse<ProgramResponse>
                                apiResponse =
                                response.body();


                        if (!apiResponse.isSuccess()) {

                            showMessage(
                                    safeMessage(
                                            apiResponse.getMessage(),
                                            "Unable to update program status."
                                    )
                            );

                            return;
                        }


                        showMessage(
                                activate
                                        ? "Program activated successfully."
                                        : "Program deactivated successfully."
                        );


                        loadPrograms();
                    }


                    @Override
                    public void onFailure(
                            Call<
                                    ApiResponse<ProgramResponse>>
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


        if (requestCode == REQUEST_PROGRAM
                && resultCode ==
                requireActivity().RESULT_OK) {

            loadPrograms();
        }
    }


    // =====================================================
    // UPDATE COUNT
    // =====================================================

    private void updateCount() {

        if (txtProgramCount != null) {

            txtProgramCount.setText(
                    "Total Programs: "
                            + programList.size()
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