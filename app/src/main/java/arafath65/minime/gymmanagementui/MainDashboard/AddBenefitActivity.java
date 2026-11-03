package arafath65.minime.gymmanagementui.MainDashboard;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BenefitBranchRequest;
import arafath65.minime.gymmanagementui.model.BenefitBranchResponse;
import arafath65.minime.gymmanagementui.model.BenefitRequest;
import arafath65.minime.gymmanagementui.model.BenefitResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBenefitActivity
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

    private int benefitId = 0;


    // =====================================================
    // VIEWS
    // =====================================================

    private ImageButton btnBack;

    private TextInputLayout layoutBenefitCode;
    private TextInputLayout layoutBenefitName;
    private TextInputLayout layoutBenefitDescription;
    private TextInputLayout layoutBenefitBillingType;
    private TextInputLayout layoutBenefitFee;

    private TextInputEditText txtBenefitCode;
    private TextInputEditText txtBenefitName;
    private TextInputEditText txtBenefitDescription;
    private TextInputEditText txtBenefitFee;

    private AutoCompleteTextView txtBenefitBillingType;

    private RadioGroup radioBenefitBranchMode;

    private View radioBenefitAllBranches;
    private View radioBenefitSelectedBranches;

    private LinearLayout layoutBenefitBranches;
    private LinearLayout branchContainer;

    private MaterialButton btnCancel;
    private MaterialButton btnSaveBenefit;


    // =====================================================
    // DATA
    // =====================================================

    private final List<BranchResponse> branches =
            new ArrayList<>();


    /*
     * Existing branch assignments loaded during edit.
     *
     * branchId -> existing branch assignment
     */
    private final Map<Integer, BenefitBranchResponse>
            existingBranchAssignments =
            new HashMap<>();


    /*
     * Branch fee fields.
     *
     * branchId -> TextInputEditText
     */
    private final Map<Integer, TextInputEditText>
            branchFeeFields =
            new HashMap<>();


    /*
     * Branch checkboxes.
     *
     * branchId -> CheckBox
     */
    private final Map<Integer, CheckBox>
            branchCheckBoxes =
            new HashMap<>();


    /*
     * Prevents listener from reacting while
     * existing edit data is being loaded.
     */
    private boolean loadingEditData = false;


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_benefit
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
        // BILLING TYPE
        // -------------------------------------------------

        setupBillingTypeDropdown();


        // -------------------------------------------------
        // MODE
        // -------------------------------------------------

        readIntent();


        // -------------------------------------------------
        // BRANCH MODE
        // -------------------------------------------------

        setupBranchMode();


        // -------------------------------------------------
        // BUTTONS
        // -------------------------------------------------

        setupButtons();


        // -------------------------------------------------
        // LOAD BRANCHES
        // -------------------------------------------------

        loadBranches();
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );


        layoutBenefitCode =
                findViewById(
                        R.id.layoutBenefitCode
                );


        layoutBenefitName =
                findViewById(
                        R.id.layoutBenefitName
                );


        layoutBenefitDescription =
                findViewById(
                        R.id.layoutBenefitDescription
                );


        layoutBenefitBillingType =
                findViewById(
                        R.id.layoutBenefitBillingType
                );


        layoutBenefitFee =
                findViewById(
                        R.id.layoutBenefitFee
                );


        txtBenefitCode =
                findViewById(
                        R.id.txtBenefitCode
                );


        txtBenefitName =
                findViewById(
                        R.id.txtBenefitName
                );


        txtBenefitDescription =
                findViewById(
                        R.id.txtBenefitDescription
                );


        txtBenefitFee =
                findViewById(
                        R.id.txtBenefitFee
                );


        txtBenefitBillingType =
                findViewById(
                        R.id.txtBenefitBillingType
                );


        radioBenefitBranchMode =
                findViewById(
                        R.id.radioBenefitBranchMode
                );


        radioBenefitAllBranches =
                findViewById(
                        R.id.radioBenefitAllBranches
                );


        radioBenefitSelectedBranches =
                findViewById(
                        R.id.radioBenefitSelectedBranches
                );


        layoutBenefitBranches =
                findViewById(
                        R.id.layoutBenefitBranches
                );


        branchContainer =
                findViewById(
                        R.id.branchContainer
                );


        btnCancel =
                findViewById(
                        R.id.btnCancel
                );


        btnSaveBenefit =
                findViewById(
                        R.id.btnSaveBenefit
                );
    }


    // =====================================================
    // INPUT FILTERS
    // =====================================================

    private void setupInputFilters() {

        if (txtBenefitCode != null) {

            txtBenefitCode.setFilters(
                    new InputFilter[]{
                            new InputFilter.LengthFilter(50)
                    }
            );

            txtBenefitCode.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            );
        }


        if (txtBenefitName != null) {

            txtBenefitName.setFilters(
                    new InputFilter[]{
                            new InputFilter.LengthFilter(150)
                    }
            );
        }
    }


    // =====================================================
    // BILLING TYPE
    // =====================================================

    private void setupBillingTypeDropdown() {

        String[] billingTypes = {
                "One Time",
                "Monthly",
                "Membership Period"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        billingTypes
                );


        txtBenefitBillingType.setAdapter(
                adapter
        );


        txtBenefitBillingType.setOnClickListener(
                v ->
                        txtBenefitBillingType.showDropDown()
        );


        txtBenefitBillingType.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selected =
                            billingTypes[position];

                    txtBenefitBillingType.setText(
                            selected,
                            false
                    );
                }
        );
    }


    // =====================================================
    // BRANCH MODE
    // =====================================================

    private void setupBranchMode() {

        radioBenefitBranchMode
                .setOnCheckedChangeListener(
                        (group, checkedId) -> {

                            if (checkedId ==
                                    R.id.radioBenefitSelectedBranches) {

                                layoutBenefitBranches
                                        .setVisibility(
                                                View.VISIBLE
                                        );

                            } else {

                                layoutBenefitBranches
                                        .setVisibility(
                                                View.GONE
                                        );
                            }
                        }
                );
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnBack.setOnClickListener(
                v -> finish()
        );


        btnCancel.setOnClickListener(
                v -> finish()
        );


        btnSaveBenefit.setOnClickListener(
                v -> saveBenefit()
        );
    }


    // =====================================================
    // INTENT
    // =====================================================

    private void readIntent() {

        editMode =
                getIntent()
                        .getBooleanExtra(
                                "editMode",
                                false
                        );


        benefitId =
                getIntent()
                        .getIntExtra(
                                "benefitId",
                                0
                        );


        if (editMode) {

            setTitle(
                    "Edit Benefit"
            );

        } else {

            setTitle(
                    "Add Benefit"
            );
        }
    }


    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        String tenantNo =
                deviceStorage
                        .getTenantNo();

        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()
                || deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showError(
                    "Tenant or device information is missing."
            );

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

                                if (isFinishing()
                                        || isDestroyed()) {

                                    return;
                                }


                                if (!response.isSuccessful()) {

                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + extractApiError(
                                                    response,
                                                    "Unable to load branches."
                                            )
                                    );

                                    return;
                                }


                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty branch response."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<BranchResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load branches."
                                            )
                                    );

                                    return;
                                }


                                branches.clear();


                                if (apiResponse.getData() != null) {

                                    branches.addAll(
                                            apiResponse.getData()
                                    );
                                }


                                buildBranchViews();


                                if (editMode) {

                                    loadBenefit();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<BranchResponse>>>
                                            call,

                                    Throwable t) {

                                if (isFinishing()
                                        || isDestroyed()) {

                                    return;
                                }


                                t.printStackTrace();


                                showError(
                                        "Connection error: "
                                                + safeMessage(
                                                t.getMessage(),
                                                "Unable to load branches."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // BUILD BRANCH VIEWS
    // =====================================================

    private void buildBranchViews() {

        branchContainer.removeAllViews();

        branchCheckBoxes.clear();

        branchFeeFields.clear();


        for (BranchResponse branch :
                branches) {

            if (branch == null
                    || branch.getBranchId() == null) {

                continue;
            }


            int branchId =
                    branch.getBranchId();


            // -------------------------------------------------
            // ROW
            // -------------------------------------------------

            LinearLayout row =
                    new LinearLayout(
                            this
                    );

            row.setOrientation(
                    LinearLayout.VERTICAL
            );


            LinearLayout.LayoutParams
                    rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            rowParams.setMargins(
                    0,
                    6,
                    0,
                    6
            );


            row.setLayoutParams(
                    rowParams
            );


            // -------------------------------------------------
            // CHECKBOX
            // -------------------------------------------------

            CheckBox checkBox =
                    new CheckBox(
                            this
                    );


            checkBox.setText(
                    buildBranchDisplayName(
                            branch
                    )
            );


            checkBox.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );


            checkBox.setTextSize(
                    14
            );


            branchCheckBoxes.put(
                    branchId,
                    checkBox
            );


            row.addView(
                    checkBox
            );


            // -------------------------------------------------
            // FEE FIELD
            // -------------------------------------------------

            TextInputLayout feeLayout =
                    new TextInputLayout(
                            this
                    );


            feeLayout.setHint(
                    "Branch Fee (optional)"
            );


            feeLayout.setBoxBackgroundMode(
                    TextInputLayout.BOX_BACKGROUND_OUTLINE
            );


            LinearLayout.LayoutParams
                    feeLayoutParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            feeLayoutParams.setMargins(
                    36,
                    0,
                    0,
                    8
            );


            feeLayout.setLayoutParams(
                    feeLayoutParams
            );


            TextInputEditText feeField =
                    new TextInputEditText(
                            this
                    );


            feeField.setSingleLine(
                    true
            );


            feeField.setInputType(
                    InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL
            );


            feeField.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );


            feeLayout.addView(
                    feeField
            );


            feeField.setVisibility(
                    View.GONE
            );


            feeField.setEnabled(
                    false
            );


            branchFeeFields.put(
                    branchId,
                    feeField
            );


            row.addView(
                    feeLayout
            );


            // -------------------------------------------------
            // CHECKBOX LISTENER
            // -------------------------------------------------

            checkBox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {

                        if (isChecked) {

                            feeField.setVisibility(
                                    View.VISIBLE
                            );

                            feeField.setEnabled(
                                    true
                            );

                        } else {

                            /*
                             * User explicitly unchecked the
                             * branch.
                             *
                             * Clear its fee so selecting it
                             * again starts empty.
                             */

                            feeField.setText(
                                    ""
                            );

                            feeField.setVisibility(
                                    View.GONE
                            );

                            feeField.setEnabled(
                                    false
                            );
                        }
                    }
            );


            branchContainer.addView(
                    row
            );
        }
    }


    // =====================================================
    // LOAD BENEFIT
    // =====================================================

    private void loadBenefit() {

        String tenantNo =
                deviceStorage
                        .getTenantNo();

        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()
                || deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showError(
                    "Tenant or device information is missing."
            );

            return;
        }


        if (benefitId <= 0) {

            showError(
                    "Invalid benefit ID."
            );

            return;
        }


        loadingEditData = true;


        apiService
                .getBenefitById(
                        tenantNo,
                        deviceToken,
                        benefitId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        BenefitResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            response) {

                                if (isFinishing()
                                        || isDestroyed()) {

                                    return;
                                }


                                if (!response.isSuccessful()) {

                                    loadingEditData =
                                            false;

                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + extractApiError(
                                                    response,
                                                    "Unable to load benefit."
                                            )
                                    );

                                    return;
                                }


                                if (response.body() == null) {

                                    loadingEditData =
                                            false;

                                    showError(
                                            "Server returned an empty benefit response."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        BenefitResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    loadingEditData =
                                            false;

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to load benefit."
                                            )
                                    );

                                    return;
                                }


                                BenefitResponse benefit =
                                        apiResponse.getData();


                                populateBenefit(
                                        benefit
                                );


                                loadingEditData =
                                        false;
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            call,

                                    Throwable t) {

                                loadingEditData =
                                        false;

                                if (isFinishing()
                                        || isDestroyed()) {

                                    return;
                                }


                                t.printStackTrace();


                                showError(
                                        "Connection error: "
                                                + safeMessage(
                                                t.getMessage(),
                                                "Unable to load benefit."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // POPULATE BENEFIT
    // =====================================================

    private void populateBenefit(
            BenefitResponse benefit) {

        txtBenefitCode.setText(
                safeText(
                        benefit.getBenefitCode()
                )
        );


        txtBenefitName.setText(
                safeText(
                        benefit.getBenefitName()
                )
        );


        txtBenefitDescription.setText(
                safeText(
                        benefit.getDescription()
                )
        );


        // -------------------------------------------------
        // BILLING
        // -------------------------------------------------

        String billingText =
                convertBillingTypeToDisplay(
                        benefit.getBillingType()
                );


        txtBenefitBillingType.setText(
                billingText,
                false
        );


        // -------------------------------------------------
        // DEFAULT FEE
        // -------------------------------------------------

        if (benefit.getDefaultFee() != null) {

            txtBenefitFee.setText(
                    benefit.getDefaultFee()
                            .toPlainString()
            );

        } else {

            txtBenefitFee.setText(
                    ""
            );
        }


        // -------------------------------------------------
        // EXISTING BRANCHES
        // -------------------------------------------------

        existingBranchAssignments.clear();


        List<BenefitBranchResponse>
                assignments =
                benefit.getBranchAssignments();


        if (assignments != null) {

            for (BenefitBranchResponse assignment :
                    assignments) {

                if (assignment == null
                        || assignment.getBranchId() == null) {

                    continue;
                }


                existingBranchAssignments.put(
                        assignment.getBranchId(),
                        assignment
                );
            }
        }


        // -------------------------------------------------
        // BRANCH MODE
        // -------------------------------------------------

        String branchMode =
                benefit.getBranchMode();


        if ("SELECTED_BRANCHES".equals(
                branchMode
        )) {

            radioBenefitBranchMode.check(
                    R.id.radioBenefitSelectedBranches
            );


            layoutBenefitBranches.setVisibility(
                    View.VISIBLE
            );


            applyExistingBranchAssignments();


        } else {

            radioBenefitBranchMode.check(
                    R.id.radioBenefitAllBranches
            );


            layoutBenefitBranches.setVisibility(
                    View.GONE
            );
        }
    }


    // =====================================================
    // APPLY EXISTING BRANCH ASSIGNMENTS
    // =====================================================

    private void applyExistingBranchAssignments() {

        for (Map.Entry<Integer,
                BenefitBranchResponse> entry :
                existingBranchAssignments.entrySet()) {

            Integer branchId =
                    entry.getKey();


            CheckBox checkBox =
                    branchCheckBoxes.get(
                            branchId
                    );


            TextInputEditText feeField =
                    branchFeeFields.get(
                            branchId
                    );


            if (checkBox == null) {
                continue;
            }


            checkBox.setChecked(
                    true
            );


            BenefitBranchResponse
                    assignment =
                    entry.getValue();


            if (feeField != null) {

                BigDecimal fee =
                        assignment.getFee();


                if (fee != null) {

                    feeField.setText(
                            fee.toPlainString()
                    );

                } else {

                    /*
                     * NULL means use default fee.
                     */

                    feeField.setText(
                            ""
                    );
                }
            }
        }
    }


    // =====================================================
    // SAVE BENEFIT
    // =====================================================

    private void saveBenefit() {

        clearErrors();


        String benefitCode =
                getText(
                        txtBenefitCode
                );


        String benefitName =
                getText(
                        txtBenefitName
                );


        String description =
                getText(
                        txtBenefitDescription
                );


        String billingDisplay =
                getText(
                        txtBenefitBillingType
                );


        String defaultFeeText =
                getText(
                        txtBenefitFee
                );


        // -------------------------------------------------
        // CODE
        // -------------------------------------------------

        if (benefitCode.isEmpty()) {

            showFieldError(
                    layoutBenefitCode,
                    "Benefit code is required."
            );

            txtBenefitCode.requestFocus();

            return;
        }


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (benefitName.isEmpty()) {

            showFieldError(
                    layoutBenefitName,
                    "Benefit name is required."
            );

            txtBenefitName.requestFocus();

            return;
        }


        // -------------------------------------------------
        // BILLING
        // -------------------------------------------------

        String billingType =
                convertBillingTypeToApi(
                        billingDisplay
                );


        if (billingType == null) {

            showFieldError(
                    layoutBenefitBillingType,
                    "Billing type is required."
            );

            txtBenefitBillingType.requestFocus();

            return;
        }


        // -------------------------------------------------
        // DEFAULT FEE
        // -------------------------------------------------

        if (defaultFeeText.isEmpty()) {

            showFieldError(
                    layoutBenefitFee,
                    "Default fee is required."
            );

            txtBenefitFee.requestFocus();

            return;
        }


        BigDecimal defaultFee;


        try {

            defaultFee =
                    new BigDecimal(
                            defaultFeeText
                    );

        } catch (NumberFormatException e) {

            showFieldError(
                    layoutBenefitFee,
                    "Enter a valid fee."
            );

            txtBenefitFee.requestFocus();

            return;
        }


        if (defaultFee.signum() < 0) {

            showFieldError(
                    layoutBenefitFee,
                    "Fee cannot be negative."
            );

            txtBenefitFee.requestFocus();

            return;
        }


        // =================================================
        // REQUEST
        // =================================================

        BenefitRequest request =
                new BenefitRequest();


        request.setBenefitCode(
                benefitCode
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
        );


        request.setBenefitName(
                benefitName.trim()
        );


        request.setDescription(
                description.isEmpty()
                        ? null
                        : description.trim()
        );


        request.setBillingType(
                billingType
        );


        request.setDefaultFee(
                defaultFee
        );


        // =================================================
        // BRANCH MODE
        // =================================================

        int checkedId =
                radioBenefitBranchMode
                        .getCheckedRadioButtonId();


        if (checkedId ==
                R.id.radioBenefitAllBranches) {

            request.setBranchMode(
                    "ALL_BRANCHES"
            );


            request.setBranches(
                    null
            );

        } else if (checkedId ==
                R.id.radioBenefitSelectedBranches) {

            request.setBranchMode(
                    "SELECTED_BRANCHES"
            );


            List<BenefitBranchRequest>
                    selectedBranches =
                    collectSelectedBranches();


            if (selectedBranches == null
                    || selectedBranches.isEmpty()) {

                showError(
                        "Please select at least one branch."
                );

                return;
            }


            request.setBranches(
                    selectedBranches
            );

        } else {

            showError(
                    "Please select branch availability."
            );

            return;
        }


        // =================================================
        // DISABLE
        // =================================================

        btnSaveBenefit.setEnabled(
                false
        );


        // =================================================
        // CREATE / UPDATE
        // =================================================

        if (editMode) {

            updateBenefit(
                    request
            );

        } else {

            createBenefit(
                    request
            );
        }
    }


    // =====================================================
    // COLLECT SELECTED BRANCHES
    // =====================================================

    private List<BenefitBranchRequest>
    collectSelectedBranches() {

        List<BenefitBranchRequest>
                selected =
                new ArrayList<>();


        Set<Integer> usedBranchIds =
                new HashSet<>();


        for (BranchResponse branch :
                branches) {

            if (branch == null
                    || branch.getBranchId() == null) {

                continue;
            }


            int branchId =
                    branch.getBranchId();


            CheckBox checkBox =
                    branchCheckBoxes.get(
                            branchId
                    );


            if (checkBox == null
                    || !checkBox.isChecked()) {

                continue;
            }


            if (!usedBranchIds.add(
                    branchId
            )) {

                showError(
                        "Duplicate branch selected: "
                                + buildBranchDisplayName(
                                branch
                        )
                );

                return null;
            }


            TextInputEditText feeField =
                    branchFeeFields.get(
                            branchId
                    );


            String feeText =
                    feeField == null
                            ? ""
                            : getText(
                            feeField
                    );


            BigDecimal fee =
                    null;


            /*
             * Empty fee is allowed.
             *
             * NULL means use defaultFee.
             */

            if (!feeText.isEmpty()) {

                try {

                    fee =
                            new BigDecimal(
                                    feeText
                            );

                } catch (NumberFormatException e) {

                    if (feeField != null) {

                        feeField.requestFocus();
                    }


                    showError(
                            "Enter a valid fee for "
                                    + buildBranchDisplayName(
                                    branch
                            )
                    );

                    return null;
                }


                if (fee.signum() < 0) {

                    if (feeField != null) {

                        feeField.requestFocus();
                    }


                    showError(
                            "Fee cannot be negative for "
                                    + buildBranchDisplayName(
                                    branch
                            )
                    );

                    return null;
                }
            }


            BenefitBranchRequest
                    branchRequest =
                    new BenefitBranchRequest();


            branchRequest.setBranchId(
                    branchId
            );


            branchRequest.setFee(
                    fee
            );


            selected.add(
                    branchRequest
            );
        }


        return selected;
    }


    // =====================================================
    // CREATE BENEFIT
    // =====================================================

    private void createBenefit(
            BenefitRequest request) {

        String tenantNo =
                deviceStorage
                        .getTenantNo();

        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        apiService
                .createBenefit(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        BenefitResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            response) {

                                btnSaveBenefit.setEnabled(
                                        true
                                );


                                if (!response.isSuccessful()) {

                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + extractApiError(
                                                    response,
                                                    "Unable to create benefit."
                                            )
                                    );

                                    return;
                                }


                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty response."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        BenefitResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to create benefit."
                                            )
                                    );

                                    return;
                                }


                                Toast.makeText(
                                        thisActivity(),
                                        "Benefit created successfully.",
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
                                                    BenefitResponse>>
                                            call,

                                    Throwable t) {

                                btnSaveBenefit.setEnabled(
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
    // UPDATE BENEFIT
    // =====================================================

    private void updateBenefit(
            BenefitRequest request) {

        String tenantNo =
                deviceStorage
                        .getTenantNo();

        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (benefitId <= 0) {

            btnSaveBenefit.setEnabled(
                    true
            );

            showError(
                    "Invalid benefit ID."
            );

            return;
        }


        apiService
                .updateBenefit(
                        tenantNo,
                        deviceToken,
                        benefitId,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        BenefitResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    BenefitResponse>>
                                            response) {

                                btnSaveBenefit.setEnabled(
                                        true
                                );


                                if (!response.isSuccessful()) {

                                    showError(
                                            "HTTP "
                                                    + response.code()
                                                    + ": "
                                                    + extractApiError(
                                                    response,
                                                    "Unable to update benefit."
                                            )
                                    );

                                    return;
                                }


                                if (response.body() == null) {

                                    showError(
                                            "Server returned an empty response."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        BenefitResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to update benefit."
                                            )
                                    );

                                    return;
                                }


                                Toast.makeText(
                                        thisActivity(),
                                        "Benefit updated successfully.",
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
                                                    BenefitResponse>>
                                            call,

                                    Throwable t) {

                                btnSaveBenefit.setEnabled(
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
    // BILLING DISPLAY → API
    // =====================================================

    private String convertBillingTypeToApi(
            String display) {

        if (display == null) {
            return null;
        }


        switch (display.trim()) {

            case "One Time":
                return "ONE_TIME";

            case "Monthly":
                return "MONTHLY";

            case "Membership Period":
                return "MEMBERSHIP_PERIOD";

            default:
                return null;
        }
    }


    // =====================================================
    // BILLING API → DISPLAY
    // =====================================================

    private String convertBillingTypeToDisplay(
            String value) {

        if (value == null) {
            return "One Time";
        }


        switch (
                value.toUpperCase(
                        Locale.ROOT
                )
        ) {

            case "ONE_TIME":
                return "One Time";

            case "MONTHLY":
                return "Monthly";

            case "MEMBERSHIP_PERIOD":
                return "Membership Period";

            default:
                return value;
        }
    }


    // =====================================================
    // BRANCH DISPLAY NAME
    // =====================================================

    private String buildBranchDisplayName(
            BranchResponse branch) {

        if (branch == null) {
            return "Branch";
        }


        String name =
                safeText(
                        branch.getBranchName()
                );


        String no =
                safeText(
                        branch.getBranchNo()
                );


        if (!no.isEmpty()
                && !name.isEmpty()) {

            return no
                    + " - "
                    + name;
        }


        if (!name.isEmpty()) {

            return name;
        }


        if (!no.isEmpty()) {

            return no;
        }


        return "Branch "
                + branch.getBranchId();
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }


    // =====================================================
    // GET TEXT
    // =====================================================

    private String getText(
            android.widget.TextView view) {

        if (view == null
                || view.getText() == null) {

            return "";
        }


        return view.getText()
                .toString()
                .trim();
    }


    // =====================================================
    // FIELD ERROR
    // =====================================================

    private void showFieldError(
            TextInputLayout layout,
            String message) {

        if (layout != null) {

            layout.setError(
                    message
            );
        }
    }


    // =====================================================
    // CLEAR ERRORS
    // =====================================================

    private void clearErrors() {

        if (layoutBenefitCode != null) {
            layoutBenefitCode.setError(null);
        }

        if (layoutBenefitName != null) {
            layoutBenefitName.setError(null);
        }

        if (layoutBenefitDescription != null) {
            layoutBenefitDescription.setError(null);
        }

        if (layoutBenefitBillingType != null) {
            layoutBenefitBillingType.setError(null);
        }

        if (layoutBenefitFee != null) {
            layoutBenefitFee.setError(null);
        }
    }


    // =====================================================
    // ERROR
    // =====================================================

    private void showError(
            String message) {

        if (isFinishing()
                || isDestroyed()) {

            return;
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
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
    // API ERROR
    // =====================================================

    private String extractApiError(
            Response<?> response,
            String fallback) {

        if (response == null) {
            return fallback;
        }


        try {

            if (response.errorBody() != null) {

                String error =
                        response.errorBody()
                                .string();


                if (error != null
                        && !error.trim().isEmpty()) {

                    return error;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }


        return fallback;
    }


    // =====================================================
    // THIS ACTIVITY
    // =====================================================

    private AddBenefitActivity
    thisActivity() {

        return this;
    }


    // =====================================================
    // TITLE
    // =====================================================

    private void setTitle(
            String title) {

        android.widget.TextView
                screenTitle =
                findViewById(
                        R.id.txtScreenTitle
                );


        if (screenTitle != null) {

            screenTitle.setText(
                    title
            );
        }
    }
}