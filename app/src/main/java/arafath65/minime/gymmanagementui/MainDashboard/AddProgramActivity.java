package arafath65.minime.gymmanagementui.MainDashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.ProgramBranchRequest;
import arafath65.minime.gymmanagementui.model.ProgramBranchResponse;
import arafath65.minime.gymmanagementui.model.ProgramRequest;
import arafath65.minime.gymmanagementui.model.ProgramResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddProgramActivity
        extends AppCompatActivity {


    // =====================================================
    // API / STORAGE
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;


    // =====================================================
    // MODE
    // =====================================================

    private boolean editMode = false;

    private int programId = 0;


    // =====================================================
    // DATA
    // =====================================================

    private final List<BranchResponse> branches =
            new ArrayList<>();


    /*
     * Branch ID -> Fee field
     */
    private final Map<Integer, TextInputEditText>
            branchFeeFields =
            new HashMap<>();


    /*
     * Branch ID -> Checkbox
     */
    private final Map<Integer, MaterialCheckBox>
            branchCheckBoxes =
            new HashMap<>();


    /*
     * Existing fees loaded from backend.
     *
     * This is important when editing.
     *
     * Example:
     *
     * Colombo = 15000
     * Kandy   = 17500
     *
     * Then adding Nugegoda will NOT wipe
     * Colombo or Kandy.
     */
    private final Map<Integer, BigDecimal>
            existingBranchFees =
            new HashMap<>();


    /*
     * Used to remember fees entered during
     * the current screen session.
     *
     * If user selects a new branch, enters a fee,
     * then changes another branch, the value remains.
     *
     * If a branch is unchecked, its value is
     * intentionally removed.
     */
    private final Map<Integer, BigDecimal>
            branchFeeMemory =
            new HashMap<>();


    private ProgramResponse existingProgram;


    /*
     * Used because branches and program are loaded
     * asynchronously.
     */
    private boolean branchesLoaded = false;

    private boolean programLoaded = false;


    // =====================================================
    // VIEWS
    // =====================================================

    private TextInputLayout layoutProgramCode;
    private TextInputLayout layoutProgramName;
    private TextInputLayout layoutDuration;

    private TextInputEditText txtProgramCode;
    private TextInputEditText txtProgramName;
    private TextInputEditText txtDescription;
    private TextInputEditText txtDuration;
    private TextInputEditText txtProgramFee;


    private MaterialCheckBox checkAllBranches;


    private LinearLayout layoutBranchList;

    private LinearLayout layoutDefaultPricing;

    private LinearLayout layoutBranchPricing;

    private LinearLayout layoutBranchPricingList;


    private TextView txtNoBranches;


    private MaterialButton btnCancel;

    private MaterialButton btnSaveProgram;


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(
                R.layout.activity_add_program
        );

        ImageButton btnBack =
                findViewById(
                        R.id.btnBack
                );

        btnBack.setOnClickListener(
                v -> finish()
        );


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
        // STORAGE
        // -------------------------------------------------

        deviceStorage =
                new DeviceStorage(
                        this
                );


        // -------------------------------------------------
        // VIEWS
        // -------------------------------------------------

        initializeViews();


        // -------------------------------------------------
        // INPUT FILTERS
        // -------------------------------------------------

        setupInputFilters();


        // -------------------------------------------------
        // MODE
        // -------------------------------------------------

        readIntent();


        // -------------------------------------------------
        // BUTTONS
        // -------------------------------------------------

        setupButtons();


        // -------------------------------------------------
        // BRANCH MODE
        // -------------------------------------------------

        setupBranchMode();


        // -------------------------------------------------
        // LOAD BRANCHES
        // -------------------------------------------------

        loadBranches();


        // -------------------------------------------------
        // LOAD PROGRAM IF EDIT
        // -------------------------------------------------

        if (editMode) {

            loadProgram();

        } else {

            /*
             * New program:
             *
             * All Branches is the default.
             */
            checkAllBranches.setChecked(
                    true
            );

            updateBranchModeUI();
        }
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        layoutProgramCode =
                findViewById(
                        R.id.layoutProgramCode
                );


        layoutProgramName =
                findViewById(
                        R.id.layoutProgramName
                );


        layoutDuration =
                findViewById(
                        R.id.layoutDuration
                );


        txtProgramCode =
                findViewById(
                        R.id.txtProgramCode
                );


        txtProgramName =
                findViewById(
                        R.id.txtProgramName
                );


        txtDescription =
                findViewById(
                        R.id.txtProgramDescription
                );


        txtDuration =
                findViewById(
                        R.id.txtProgramDuration
                );


        txtProgramFee =
                findViewById(
                        R.id.txtProgramFee
                );


        checkAllBranches =
                findViewById(
                        R.id.checkAllBranches
                );


        layoutBranchList =
                findViewById(
                        R.id.layoutBranchList
                );


        layoutDefaultPricing =
                findViewById(
                        R.id.layoutDefaultPricing
                );


        layoutBranchPricing =
                findViewById(
                        R.id.layoutBranchPricing
                );


        layoutBranchPricingList =
                findViewById(
                        R.id.layoutBranchPricingList
                );


        txtNoBranches =
                findViewById(
                        R.id.txtNoBranches
                );


        btnCancel =
                findViewById(
                        R.id.btnCancel
                );


        btnSaveProgram =
                findViewById(
                        R.id.btnSaveProgram
                );
    }


    // =====================================================
    // INPUT FILTERS
    // =====================================================

    private void setupInputFilters() {

        txtProgramCode.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(50)
                }
        );


        txtProgramName.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(150)
                }
        );
    }


    // =====================================================
    // READ INTENT
    // =====================================================

    private void readIntent() {

        editMode =
                getIntent()
                        .getBooleanExtra(
                                "editMode",
                                false
                        );


        programId =
                getIntent()
                        .getIntExtra(
                                "programId",
                                0
                        );


        if (editMode && programId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid program.",
                    Toast.LENGTH_LONG
            ).show();


            finish();

            return;
        }


        if (editMode) {

            setTitleText(
                    "Edit Program"
            );


            btnSaveProgram.setText(
                    "Update Program"
            );

        } else {

            setTitleText(
                    "Add Program"
            );


            btnSaveProgram.setText(
                    "Save Program"
            );
        }
    }


    // =====================================================
    // TITLE
    // =====================================================

    private void setTitleText(
            String title) {

        TextView txtTitle =
                findViewById(
                        R.id.txtScreenTitle
                );


        if (txtTitle != null) {

            txtTitle.setText(
                    title
            );
        }
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnCancel.setOnClickListener(
                v -> finish()
        );


        btnSaveProgram.setOnClickListener(
                v -> saveProgram()
        );
    }


    // =====================================================
    // BRANCH MODE
    // =====================================================

    private void setupBranchMode() {

        checkAllBranches.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        /*
                         * ALL_BRANCHES
                         *
                         * Uncheck individual branches.
                         */
                        for (
                                MaterialCheckBox checkbox
                                : branchCheckBoxes.values()
                        ) {

                            checkbox.setChecked(
                                    false
                            );
                        }
                    }


                    updateBranchModeUI();
                }
        );
    }


    // =====================================================
    // UPDATE BRANCH MODE UI
    // =====================================================

    private void updateBranchModeUI() {

        boolean allBranches =
                checkAllBranches.isChecked();


        if (allBranches) {

            layoutDefaultPricing.setVisibility(
                    View.VISIBLE
            );


            layoutBranchPricing.setVisibility(
                    View.GONE
            );

        } else {

            layoutDefaultPricing.setVisibility(
                    View.GONE
            );


            layoutBranchPricing.setVisibility(
                    View.VISIBLE
            );
        }
    }


    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (!validateDeviceData(
                tenantNo,
                deviceToken
        )) {

            return;
        }


        apiService
                .getBranches(
                        tenantNo,
                        deviceToken
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<BranchResponse>>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<BranchResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<BranchResponse>>>
                                            response) {

                                if (isFinishing()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to load branches."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<BranchResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load branches."
                                            )
                                    );

                                    return;
                                }


                                branches.clear();


                                for (
                                        BranchResponse branch
                                        : apiResponse.getData()
                                ) {

                                    if (branch == null) {
                                        continue;
                                    }


                                    if (!Boolean.TRUE.equals(
                                            branch.getStatus()
                                    )) {

                                        continue;
                                    }


                                    branches.add(
                                            branch
                                    );
                                }


                                branchesLoaded = true;


                                buildBranchList();


                                if (editMode
                                        && programLoaded
                                        && existingProgram != null) {

                                    displayProgram(
                                            existingProgram
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<BranchResponse>>>
                                            call,

                                    Throwable t) {

                                if (isFinishing()) {
                                    return;
                                }


                                showError(
                                        "Connection failed while loading branches."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // BUILD BRANCH LIST
    // =====================================================

    private void buildBranchList() {

        layoutBranchList.removeAllViews();


        branchCheckBoxes.clear();


        if (branches.isEmpty()) {

            txtNoBranches.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        txtNoBranches.setVisibility(
                View.GONE
        );


        for (BranchResponse branch :
                branches) {

            addBranchCheckbox(
                    branch
            );
        }
    }


    // =====================================================
    // ADD BRANCH CHECKBOX
    // =====================================================

    private void addBranchCheckbox(
            BranchResponse branch) {

        if (branch == null
                || branch.getBranchId() == null) {

            return;
        }


        MaterialCheckBox checkbox =
                new MaterialCheckBox(
                        this
                );


        checkbox.setText(
                buildBranchDisplayName(
                        branch
                )
        );


        checkbox.setTextColor(
                getColor(
                        R.color.text_primary
                )
        );


        checkbox.setTextSize(
                14
        );


        checkbox.setPadding(
                4,
                4,
                4,
                4
        );


        checkbox.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );


        Integer branchId =
                branch.getBranchId();


        branchCheckBoxes.put(
                branchId,
                checkbox
        );


        layoutBranchList.addView(
                checkbox
        );


        checkbox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        /*
                         * Selecting an individual branch
                         * means we are no longer using
                         * ALL_BRANCHES.
                         */
                        checkAllBranches.setChecked(
                                false
                        );


                        /*
                         * Restore previous fee if one
                         * exists.
                         */
                        restoreBranchFee(
                                branchId
                        );

                    } else {

                        /*
                         * User intentionally removed
                         * this branch.
                         *
                         * Clear its fee so when it is
                         * selected again it starts empty.
                         */
                        clearBranchFee(
                                branchId
                        );
                    }


                    updateBranchPricingUI();
                }
        );
    }


    // =====================================================
    // BRANCH DISPLAY NAME
    // =====================================================

    private String buildBranchDisplayName(
            BranchResponse branch) {

        String branchNo =
                safeText(
                        branch.getBranchNo()
                );


        String branchName =
                safeText(
                        branch.getBranchName()
                );


        if (!branchNo.isEmpty()
                && !branchName.isEmpty()) {

            return branchNo
                    + " - "
                    + branchName;
        }


        if (!branchName.isEmpty()) {

            return branchName;
        }


        return branchNo;
    }


    // =====================================================
    // UPDATE BRANCH PRICING UI
    // =====================================================

    private void updateBranchPricingUI() {

        if (checkAllBranches.isChecked()) {

            layoutDefaultPricing.setVisibility(
                    View.VISIBLE
            );


            layoutBranchPricing.setVisibility(
                    View.GONE
            );

            return;
        }


        layoutDefaultPricing.setVisibility(
                View.GONE
        );


        layoutBranchPricing.setVisibility(
                View.VISIBLE
        );


        rebuildBranchPricingFields();
    }


    // =====================================================
    // REBUILD BRANCH PRICING
    // =====================================================

    private void rebuildBranchPricingFields() {

        /*
         * Remember current values before rebuilding.
         */
        rememberCurrentBranchFees();


        layoutBranchPricingList.removeAllViews();


        branchFeeFields.clear();


        boolean hasSelectedBranch =
                false;


        for (BranchResponse branch :
                branches) {

            if (branch == null
                    || branch.getBranchId() == null) {

                continue;
            }


            Integer branchId =
                    branch.getBranchId();


            MaterialCheckBox checkbox =
                    branchCheckBoxes.get(
                            branchId
                    );


            if (checkbox == null
                    || !checkbox.isChecked()) {

                continue;
            }


            hasSelectedBranch = true;


            addBranchFeeField(
                    branch
            );
        }


        if (!hasSelectedBranch) {

            TextView emptyText =
                    new TextView(
                            this
                    );


            emptyText.setText(
                    "Select at least one branch to enter branch-specific fees."
            );


            emptyText.setTextColor(
                    getColor(
                            R.color.text_secondary
                    )
            );


            emptyText.setTextSize(
                    12
            );


            emptyText.setPadding(
                    4,
                    10,
                    4,
                    10
            );


            layoutBranchPricingList.addView(
                    emptyText
            );
        }
    }


    // =====================================================
    // ADD BRANCH FEE FIELD
    // =====================================================

    private void addBranchFeeField(
            BranchResponse branch) {

        Integer branchId =
                branch.getBranchId();


        /*
         * Outer card.
         */
        MaterialCardView card =
                new MaterialCardView(
                        this
                );


        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        cardParams.setMargins(
                0,
                8,
                0,
                4
        );


        card.setLayoutParams(
                cardParams
        );


        card.setRadius(
                12
        );


        card.setCardElevation(
                0
        );


        card.setStrokeWidth(
                1
        );


        card.setStrokeColor(
                getColor(
                        R.color.login_stroke
                )
        );


        card.setCardBackgroundColor(
                getColor(
                        R.color.login_card
                )
        );


        /*
         * Card content.
         */
        LinearLayout content =
                new LinearLayout(
                        this
                );


        content.setOrientation(
                LinearLayout.VERTICAL
        );


        content.setPadding(
                14,
                12,
                14,
                14
        );


        card.addView(
                content
        );


        /*
         * Branch name.
         */
        TextView branchName =
                new TextView(
                        this
                );


        branchName.setText(
                buildBranchDisplayName(
                        branch
                )
        );


        branchName.setTextColor(
                getColor(
                        R.color.text_primary
                )
        );


        branchName.setTextSize(
                14
        );


        branchName.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );


        content.addView(
                branchName
        );


        /*
         * Fee layout.
         */
        TextInputLayout feeLayout =
                new TextInputLayout(
                        this
                );


        feeLayout.setHint(
                "Program Fee (Rs.) *"
        );


        feeLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        feeLayout.setBoxStrokeColor(
                getColor(
                        R.color.text_input_stroke
                )
        );


        feeLayout.setHintTextColor(
                getColorStateList(
                        R.color.text_input_hint
                )
        );


        LinearLayout.LayoutParams feeLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        feeLayoutParams.setMargins(
                0,
                10,
                0,
                0
        );


        feeLayout.setLayoutParams(
                feeLayoutParams
        );


        TextInputEditText feeEditText =
                new TextInputEditText(
                        this
                );


        feeEditText.setSingleLine(
                true
        );


        feeEditText.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );


        feeEditText.setTextColor(
                getColor(
                        R.color.text_primary
                )
        );


        feeEditText.setBackground(
                null
        );


        feeEditText.setPadding(
                12,
                0,
                12,
                0
        );


        feeLayout.addView(
                feeEditText
        );


        content.addView(
                feeLayout
        );


        layoutBranchPricingList.addView(
                card
        );


        branchFeeFields.put(
                branchId,
                feeEditText
        );


        /*
         * Restore existing/current value.
         */
        restoreBranchFee(
                branchId
        );
    }


    // =====================================================
    // REMEMBER CURRENT FEES
    // =====================================================

    private void rememberCurrentBranchFees() {

        for (
                Map.Entry<Integer, TextInputEditText>
                        entry
                : branchFeeFields.entrySet()
        ) {

            Integer branchId =
                    entry.getKey();


            TextInputEditText field =
                    entry.getValue();


            if (field == null
                    || field.getText() == null) {

                continue;
            }


            String value =
                    field.getText()
                            .toString()
                            .trim();


            if (value.isEmpty()) {

                /*
                 * Do not store an empty value.
                 *
                 * This allows a newly selected branch
                 * to remain empty.
                 */
                branchFeeMemory.remove(
                        branchId
                );

                continue;
            }


            try {

                BigDecimal fee =
                        new BigDecimal(
                                value
                        );


                branchFeeMemory.put(
                        branchId,
                        fee
                );

            } catch (NumberFormatException ignored) {
            }
        }
    }


    // =====================================================
    // RESTORE BRANCH FEE
    // =====================================================

    private void restoreBranchFee(
            Integer branchId) {

        if (branchId == null) {
            return;
        }


        TextInputEditText field =
                branchFeeFields.get(
                        branchId
                );


        if (field == null) {
            return;
        }


        BigDecimal fee =
                branchFeeMemory.get(
                        branchId
                );


        if (fee == null) {

            fee =
                    existingBranchFees.get(
                            branchId
                    );
        }


        if (fee != null) {

            field.setText(
                    fee.toPlainString()
            );

        } else {

            /*
             * Newly selected branch.
             *
             * Keep empty.
             */
            field.setText(
                    ""
            );
        }
    }


    // =====================================================
    // CLEAR BRANCH FEE
    // =====================================================

    private void clearBranchFee(
            Integer branchId) {

        if (branchId == null) {
            return;
        }


        branchFeeMemory.remove(
                branchId
        );


        existingBranchFees.remove(
                branchId
        );


        TextInputEditText field =
                branchFeeFields.get(
                        branchId
                );


        if (field != null) {

            field.setText(
                    ""
            );
        }
    }


    // =====================================================
// LOAD PROGRAM
// =====================================================

    private void loadProgram() {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (!validateDeviceData(
                tenantNo,
                deviceToken
        )) {

            return;
        }


        apiService
                .getProgramById(
                        tenantNo,
                        deviceToken,
                        programId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        ProgramResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            response) {

                                // -------------------------------------------------
                                // HTTP ERROR
                                // -------------------------------------------------

                                if (!response.isSuccessful()) {

                                    String error =
                                            extractApiError(
                                                    response,
                                                    "Unable to load program."
                                            );


                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + error
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // EMPTY RESPONSE
                                // -------------------------------------------------

                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty response."
                                    );


                                    return;
                                }


                                ApiResponse<
                                        ProgramResponse>
                                        apiResponse =
                                        response.body();


                                // -------------------------------------------------
                                // API ERROR
                                // -------------------------------------------------

                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load program."
                                            )
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // SUCCESS
                                // -------------------------------------------------

                                existingProgram =
                                        apiResponse.getData();


                                programLoaded =
                                        true;


                                if (branchesLoaded) {

                                    displayProgram(
                                            existingProgram
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Throwable t) {

                                t.printStackTrace();


                                showError(
                                        "Connection error: "
                                                + safeMessage(
                                                t.getMessage(),
                                                "Unable to connect to server."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // DISPLAY PROGRAM
    // =====================================================

    private void displayProgram(
            ProgramResponse program) {

        if (program == null) {
            return;
        }


        txtProgramCode.setText(
                safeText(
                        program.getProgramCode()
                )
        );


        txtProgramName.setText(
                safeText(
                        program.getProgramName()
                )
        );


        txtDescription.setText(
                safeText(
                        program.getDescription()
                )
        );


        if (program.getDurationDays() != null) {

            txtDuration.setText(
                    String.valueOf(
                            program.getDurationDays()
                    )
            );
        }


        /*
         * Clear previous branch data.
         */
        existingBranchFees.clear();

        branchFeeMemory.clear();


        List<ProgramBranchResponse>
                assignments =
                program.getBranchAssignments();


        /*
         * No assignments.
         */
        if (assignments == null
                || assignments.isEmpty()) {

            checkAllBranches.setChecked(
                    true
            );


            updateBranchModeUI();

            return;
        }


        /*
         * Store existing branch fees.
         */
        Set<Integer> assignedBranchIds =
                new HashSet<>();


        for (
                ProgramBranchResponse assignment
                : assignments
        ) {

            if (assignment == null
                    || assignment.getBranchId() == null) {

                continue;
            }


            Integer branchId =
                    assignment.getBranchId();


            assignedBranchIds.add(
                    branchId
            );


            if (assignment.getFee() != null) {

                existingBranchFees.put(
                        branchId,
                        assignment.getFee()
                );


                branchFeeMemory.put(
                        branchId,
                        assignment.getFee()
                );
            }
        }


        /*
         * Determine whether all active branches
         * are assigned.
         *
         * If yes, use ALL_BRANCHES mode.
         */
        boolean allBranchesAssigned =
                !branches.isEmpty()
                        && assignedBranchIds.size()
                        == branches.size();


        if (allBranchesAssigned) {

            checkAllBranches.setChecked(
                    true
            );


            /*
             * If all branches have the same fee,
             * show it as the default fee.
             *
             * If fees are different, use the first
             * fee as the displayed default only.
             *
             * Backend ALL_BRANCHES is intended for
             * same/default pricing.
             */
            BigDecimal commonFee =
                    findCommonBranchFee(
                            assignments
                    );


            if (commonFee != null) {

                txtProgramFee.setText(
                        commonFee.toPlainString()
                );
            }


            updateBranchModeUI();

            return;
        }


        /*
         * Selected branch mode.
         */
        checkAllBranches.setChecked(
                false
        );


        /*
         * Check assigned branches.
         */
        for (
                Map.Entry<Integer, MaterialCheckBox>
                        entry
                : branchCheckBoxes.entrySet()
        ) {

            Integer branchId =
                    entry.getKey();


            MaterialCheckBox checkbox =
                    entry.getValue();


            boolean assigned =
                    assignedBranchIds.contains(
                            branchId
                    );


            checkbox.setChecked(
                    assigned
            );
        }


        updateBranchPricingUI();
    }


    // =====================================================
    // FIND COMMON FEE
    // =====================================================

    private BigDecimal findCommonBranchFee(
            List<ProgramBranchResponse> assignments) {

        BigDecimal commonFee = null;


        for (
                ProgramBranchResponse assignment
                : assignments
        ) {

            if (assignment == null) {
                continue;
            }


            BigDecimal fee =
                    assignment.getFee();


            if (fee == null) {
                continue;
            }


            if (commonFee == null) {

                commonFee = fee;

                continue;
            }


            if (commonFee.compareTo(
                    fee
            ) != 0) {

                /*
                 * Different branch fees.
                 */
                return null;
            }
        }


        return commonFee;
    }


    // =====================================================
    // SAVE PROGRAM
    // =====================================================

    private void saveProgram() {

        clearErrors();


        // -------------------------------------------------
        // DEVICE
        // -------------------------------------------------

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (!validateDeviceData(
                tenantNo,
                deviceToken
        )) {

            return;
        }


        // -------------------------------------------------
        // CODE
        // -------------------------------------------------

        String code =
                text(
                        txtProgramCode
                );


        if (code.isEmpty()) {

            layoutProgramCode.setError(
                    "Program code is required."
            );


            txtProgramCode.requestFocus();

            return;
        }


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        String name =
                text(
                        txtProgramName
                );


        if (name.isEmpty()) {

            layoutProgramName.setError(
                    "Program name is required."
            );


            txtProgramName.requestFocus();

            return;
        }


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        String durationText =
                text(
                        txtDuration
                );


        if (durationText.isEmpty()) {

            layoutDuration.setError(
                    "Duration is required."
            );


            txtDuration.requestFocus();

            return;
        }


        int duration;


        try {

            duration =
                    Integer.parseInt(
                            durationText
                    );

        } catch (NumberFormatException e) {

            layoutDuration.setError(
                    "Enter a valid duration."
            );


            txtDuration.requestFocus();

            return;
        }


        if (duration <= 0) {

            layoutDuration.setError(
                    "Duration must be greater than zero."
            );


            txtDuration.requestFocus();

            return;
        }


        // -------------------------------------------------
        // BUILD REQUEST
        // -------------------------------------------------

        ProgramRequest request =
                new ProgramRequest();


        request.setProgramCode(
                code
                        .trim()
                        .toUpperCase()
        );


        request.setProgramName(
                name.trim()
        );


        String description =
                text(
                        txtDescription
                );


        request.setDescription(
                description.isEmpty()
                        ? null
                        : description
        );


        request.setDurationDays(
                duration
        );


        // -------------------------------------------------
        // ALL BRANCHES
        // -------------------------------------------------

        if (checkAllBranches.isChecked()) {

            String feeText =
                    text(
                            txtProgramFee
                    );


            if (feeText.isEmpty()) {

                showFieldError(
                        txtProgramFee,
                        "Program fee is required."
                );

                return;
            }


            BigDecimal defaultFee;


            try {

                defaultFee =
                        new BigDecimal(
                                feeText
                        );

            } catch (NumberFormatException e) {

                showFieldError(
                        txtProgramFee,
                        "Enter a valid fee."
                );

                return;
            }


            if (defaultFee.signum() < 0) {

                showFieldError(
                        txtProgramFee,
                        "Fee cannot be negative."
                );

                return;
            }


            request.setBranchMode(
                    "ALL_BRANCHES"
            );


            request.setDefaultFee(
                    defaultFee
            );


            request.setBranches(
                    null
            );
        }


        // -------------------------------------------------
        // SELECTED BRANCHES
        // -------------------------------------------------

        else {

            rememberCurrentBranchFees();


            List<ProgramBranchRequest>
                    selectedBranches =
                    new ArrayList<>();


            for (BranchResponse branch :
                    branches) {

                if (branch == null
                        || branch.getBranchId() == null) {

                    continue;
                }


                Integer branchId =
                        branch.getBranchId();


                MaterialCheckBox checkbox =
                        branchCheckBoxes.get(
                                branchId
                        );


                if (checkbox == null
                        || !checkbox.isChecked()) {

                    continue;
                }


                TextInputEditText feeField =
                        branchFeeFields.get(
                                branchId
                        );


                String feeText = "";


                if (feeField != null
                        && feeField.getText() != null) {

                    feeText =
                            feeField.getText()
                                    .toString()
                                    .trim();
                }


                if (feeText.isEmpty()) {

                    showError(
                            "Please enter the fee for "
                                    + buildBranchDisplayName(
                                    branch
                            )
                    );


                    if (feeField != null) {

                        feeField.requestFocus();
                    }


                    return;
                }


                BigDecimal fee;


                try {

                    fee =
                            new BigDecimal(
                                    feeText
                            );

                } catch (NumberFormatException e) {

                    showError(
                            "Invalid fee for "
                                    + buildBranchDisplayName(
                                    branch
                            )
                    );


                    if (feeField != null) {

                        feeField.requestFocus();
                    }


                    return;
                }


                if (fee.signum() < 0) {

                    showError(
                            "Fee cannot be negative for "
                                    + buildBranchDisplayName(
                                    branch
                            )
                    );


                    if (feeField != null) {

                        feeField.requestFocus();
                    }


                    return;
                }


                ProgramBranchRequest
                        branchRequest =
                        new ProgramBranchRequest();


                branchRequest.setBranchId(
                        branchId
                );


                branchRequest.setFee(
                        fee
                );


                selectedBranches.add(
                        branchRequest
                );
            }


            if (selectedBranches.isEmpty()) {

                showError(
                        "Please select at least one branch."
                );

                return;
            }


            request.setBranchMode(
                    "SELECTED_BRANCHES"
            );


            request.setDefaultFee(
                    null
            );


            request.setBranches(
                    selectedBranches
            );
        }


        // -------------------------------------------------
        // DISABLE
        // -------------------------------------------------

        btnSaveProgram.setEnabled(
                false
        );


        // -------------------------------------------------
        // CREATE / UPDATE
        // -------------------------------------------------

        if (editMode) {

            updateProgram(
                    tenantNo,
                    deviceToken,
                    request
            );

        } else {

            createProgram(
                    tenantNo,
                    deviceToken,
                    request
            );
        }
    }


    // =====================================================
// CREATE PROGRAM
// =====================================================

    private void createProgram(
            String tenantNo,
            String deviceToken,
            ProgramRequest request) {

        apiService
                .createProgram(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        ProgramResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            response) {

                                btnSaveProgram.setEnabled(
                                        true
                                );


                                // -------------------------------------------------
                                // HTTP ERROR
                                // -------------------------------------------------

                                if (!response.isSuccessful()) {

                                    String error =
                                            extractApiError(
                                                    response,
                                                    "Unable to create program."
                                            );


                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + error
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // EMPTY RESPONSE
                                // -------------------------------------------------

                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty response."
                                    );


                                    return;
                                }


                                ApiResponse<
                                        ProgramResponse>
                                        apiResponse =
                                        response.body();


                                // -------------------------------------------------
                                // API ERROR
                                // -------------------------------------------------

                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to create program."
                                            )
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // SUCCESS
                                // -------------------------------------------------

                                Toast.makeText(
                                        AddProgramActivity.this,
                                        "Program created successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();


                                setResult(
                                        RESULT_OK
                                );


                                finish();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Throwable t) {

                                btnSaveProgram.setEnabled(
                                        true
                                );


                                t.printStackTrace();


                                showError(
                                        "Connection error: "
                                                + safeMessage(
                                                t.getMessage(),
                                                "Unable to connect to server."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
// UPDATE PROGRAM
// =====================================================

    private void updateProgram(
            String tenantNo,
            String deviceToken,
            ProgramRequest request) {

        apiService
                .updateProgram(
                        tenantNo,
                        deviceToken,
                        programId,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        ProgramResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            response) {

                                btnSaveProgram.setEnabled(
                                        true
                                );


                                // -------------------------------------------------
                                // HTTP ERROR
                                // -------------------------------------------------

                                if (!response.isSuccessful()) {

                                    String error =
                                            extractApiError(
                                                    response,
                                                    "Unable to update program."
                                            );


                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + error
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // EMPTY RESPONSE
                                // -------------------------------------------------

                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty response."
                                    );


                                    return;
                                }


                                ApiResponse<
                                        ProgramResponse>
                                        apiResponse =
                                        response.body();


                                // -------------------------------------------------
                                // API ERROR
                                // -------------------------------------------------

                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to update program."
                                            )
                                    );


                                    return;
                                }


                                // -------------------------------------------------
                                // SUCCESS
                                // -------------------------------------------------

                                Toast.makeText(
                                        AddProgramActivity.this,
                                        "Program updated successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();


                                setResult(
                                        RESULT_OK
                                );


                                finish();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    ProgramResponse>>
                                            call,

                                    Throwable t) {

                                btnSaveProgram.setEnabled(
                                        true
                                );


                                t.printStackTrace();


                                showError(
                                        "Connection error: "
                                                + safeMessage(
                                                t.getMessage(),
                                                "Unable to connect to server."
                                        )
                                );
                            }
                        }
                );
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
    // CLEAR ERRORS
    // =====================================================

    private void clearErrors() {

        layoutProgramCode.setError(
                null
        );


        layoutProgramName.setError(
                null
        );


        layoutDuration.setError(
                null
        );
    }


    // =====================================================
    // SHOW FIELD ERROR
    // =====================================================

    private void showFieldError(
            TextInputEditText editText,
            String message) {

        if (editText == null) {

            showError(
                    message
            );

            return;
        }


        editText.setError(
                message
        );


        editText.requestFocus();
    }


    // =====================================================
    // TEXT
    // =====================================================

    private String text(
            TextInputEditText editText) {

        if (editText == null
                || editText.getText() == null) {

            return "";
        }


        return editText.getText()
                .toString()
                .trim();
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "";
        }


        return value.trim();
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
    // ERROR
    // =====================================================

    private void showError(
            String message) {

        if (isFinishing()) {
            return;
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =====================================================
// EXTRACT API ERROR
// =====================================================

    private String extractApiError(
            Response<?> response,
            String fallback) {

        try {

            if (response == null) {
                return fallback;
            }


            // -------------------------------------------------
            // NORMAL API RESPONSE
            // -------------------------------------------------

            if (response.body() != null) {

                Object body =
                        response.body();

                if (body instanceof ApiResponse) {

                    ApiResponse<?> apiResponse =
                            (ApiResponse<?>) body;

                    if (apiResponse.getMessage() != null
                            && !apiResponse.getMessage()
                            .trim()
                            .isEmpty()) {

                        return apiResponse.getMessage();
                    }
                }
            }


            // -------------------------------------------------
            // HTTP ERROR BODY
            // -------------------------------------------------

            ResponseBody errorBody =
                    response.errorBody();


            if (errorBody != null) {

                String errorText =
                        errorBody.string();


                if (errorText != null
                        && !errorText.trim().isEmpty()) {

                    return errorText;
                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


        return fallback;
    }
}