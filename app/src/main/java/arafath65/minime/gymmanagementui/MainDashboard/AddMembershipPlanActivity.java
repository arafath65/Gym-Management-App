package arafath65.minime.gymmanagementui.MainDashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arafath65.minime.gymmanagementui.MainDashboard.adapter.AddMembershipPlanBenefitAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BenefitResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBenefitRequest;
import arafath65.minime.gymmanagementui.model.MembershipPlanBenefitResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBranchRequest;
import arafath65.minime.gymmanagementui.model.MembershipPlanBranchResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanRequest;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMembershipPlanActivity
        extends AppCompatActivity {


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
    // EDIT MODE
    // =====================================================

    private boolean editMode = false;

    private Integer editMembershipPlanId = null;

    private MembershipPlanResponse editingPlan;


    // =====================================================
    // BRANCH DATA
    // =====================================================

    private final List<BranchResponse> branchList =
            new ArrayList<>();


    private final List<MaterialCheckBox> branchCheckBoxes =
            new ArrayList<>();


    // =====================================================
    // BENEFIT DATA
    // =====================================================

    private final List<BenefitResponse> benefitList =
            new ArrayList<>();


    private final Set<Integer> selectedBenefitIds =
            new HashSet<>();


    // =====================================================
    // BENEFIT VIEWS
    // =====================================================

    private RecyclerView recyclerMembershipPlanBenefits;

    private TextView txtNoMembershipPlanBenefits;


    private AddMembershipPlanBenefitAdapter
            membershipPlanBenefitAdapter;


    // =====================================================
    // VIEWS
    // =====================================================

    private MaterialButton btnSaveMembershipPlan;

    private MaterialButton btnCancelMembershipPlan;


    private MaterialCheckBox checkAllBranches;


    private LinearLayout layoutBranchList;

    private TextView txtNoBranches;


    private LinearLayout layoutDefaultPricing;

    private LinearLayout layoutBranchPricing;

    private LinearLayout layoutBranchPricingList;


    private TextInputEditText txtPlanCode;

    private TextInputEditText txtPlanName;

    private TextInputEditText txtPlanDescription;


    private AutoCompleteTextView txtDurationType;

    private TextInputEditText txtDurationValue;


    private TextInputEditText txtDefaultPrice;

    private TextInputEditText txtDefaultJoiningFee;

    private TextInputEditText txtDefaultRenewalFee;


    private AutoCompleteTextView txtBillingType;

    private AutoCompleteTextView txtBillingFrequency;

    private TextInputEditText txtBillingFrequencyValue;

    private AutoCompleteTextView txtAccessBillingMode;


    private MaterialSwitch switchMonthlyActivation;

    private MaterialSwitch switchDailyCharge;


    private AutoCompleteTextView txtAccessType;


    private MaterialSwitch switchFreezeAllowed;

    private TextInputEditText txtMaxFreezeDays;

    private TextInputEditText txtFreezeFee;


    // =====================================================
    // SELECTED BRANCH PRICING VIEWS
    // =====================================================

    private final List<BranchPricingViews>
            branchPricingViews =
            new ArrayList<>();


    // =====================================================
    // EDIT DATA RESTORATION FLAG
    // =====================================================

    /*
     * Used while loading existing branch assignments.
     *
     * During this process we don't want checkbox
     * listeners to rebuild the pricing cards repeatedly.
     */
    private boolean restoringEditData = false;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(
                R.layout.activity_add_membership_plan
        );


        // =================================================
        // STORAGE
        // =================================================

        sessionStorage =
                new SessionStorage(this);


        deviceStorage =
                new DeviceStorage(this);


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
        // EDIT MODE
        // =================================================

        editMode =
                getIntent()
                        .getBooleanExtra(
                                "editMode",
                                false
                        );


        if (editMode) {

            editMembershipPlanId =
                    getIntent()
                            .getIntExtra(
                                    "membershipPlanId",
                                    0
                            );


            if (editMembershipPlanId == 0) {

                Toast.makeText(
                        this,
                        "Invalid membership plan.",
                        Toast.LENGTH_LONG
                ).show();


                finish();

                return;
            }
        }


        // =================================================
        // VIEWS
        // =================================================

        initializeViews();


        // =================================================
        // BENEFITS
        // =================================================

        initializeBenefitViews();


        // =================================================
        // DEFAULT VALUES
        // =================================================

        setupDefaultValues();


        // =================================================
        // DROPDOWNS
        // =================================================

        setupDropdowns();


        // =================================================
        // BRANCH EVENTS
        // =================================================

        setupBranchSelection();


        // =================================================
        // FREEZE
        // =================================================

        setupFreeze();


        // =================================================
        // BUTTONS
        // =================================================

        btnCancelMembershipPlan
                .setOnClickListener(
                        v -> finish()
                );


        btnSaveMembershipPlan
                .setOnClickListener(
                        v -> saveMembershipPlan()
                );


        // =================================================
        // LOAD BRANCHES
        // =================================================

        loadBranches();


        // =================================================
        // LOAD BENEFITS
        // =================================================

        loadBenefits();
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        btnSaveMembershipPlan =
                findViewById(
                        R.id.btnSaveMembershipPlan
                );


        btnCancelMembershipPlan =
                findViewById(
                        R.id.btnCancelMembershipPlan
                );


        checkAllBranches =
                findViewById(
                        R.id.checkAllBranches
                );


        layoutBranchList =
                findViewById(
                        R.id.layoutBranchList
                );


        txtNoBranches =
                findViewById(
                        R.id.txtNoBranches
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


        txtPlanCode =
                findViewById(
                        R.id.txtPlanCode
                );


        txtPlanName =
                findViewById(
                        R.id.txtPlanName
                );


        txtPlanDescription =
                findViewById(
                        R.id.txtPlanDescription
                );


        txtDurationType =
                findViewById(
                        R.id.txtDurationType
                );


        txtDurationValue =
                findViewById(
                        R.id.txtDurationValue
                );


        txtDefaultPrice =
                findViewById(
                        R.id.txtDefaultPrice
                );


        txtDefaultJoiningFee =
                findViewById(
                        R.id.txtDefaultJoiningFee
                );


        txtDefaultRenewalFee =
                findViewById(
                        R.id.txtDefaultRenewalFee
                );


        txtBillingType =
                findViewById(
                        R.id.txtBillingType
                );


        txtBillingFrequency =
                findViewById(
                        R.id.txtBillingFrequency
                );


        txtBillingFrequencyValue =
                findViewById(
                        R.id.txtBillingFrequencyValue
                );


        txtAccessBillingMode =
                findViewById(
                        R.id.txtAccessBillingMode
                );


        switchMonthlyActivation =
                findViewById(
                        R.id.switchMonthlyActivation
                );


        switchDailyCharge =
                findViewById(
                        R.id.switchDailyCharge
                );


        txtAccessType =
                findViewById(
                        R.id.txtAccessType
                );


        switchFreezeAllowed =
                findViewById(
                        R.id.switchFreezeAllowed
                );


        txtMaxFreezeDays =
                findViewById(
                        R.id.txtMaxFreezeDays
                );


        txtFreezeFee =
                findViewById(
                        R.id.txtFreezeFee
                );


        if (editMode) {

            btnSaveMembershipPlan.setText(
                    "Update Plan"
            );
        }
    }


    // =====================================================
    // INITIALIZE BENEFIT VIEWS
    // =====================================================

    private void initializeBenefitViews() {

        recyclerMembershipPlanBenefits =
                findViewById(
                        R.id.recyclerMembershipPlanBenefits
                );


        txtNoMembershipPlanBenefits =
                findViewById(
                        R.id.txtNoMembershipPlanBenefits
                );


        recyclerMembershipPlanBenefits
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );


        recyclerMembershipPlanBenefits
                .setNestedScrollingEnabled(
                        false
                );


        membershipPlanBenefitAdapter =
                new AddMembershipPlanBenefitAdapter();


        membershipPlanBenefitAdapter
                .setOnBenefitSelectionChangedListener(
                        ids -> {

                            selectedBenefitIds
                                    .clear();

                            if (ids != null) {

                                selectedBenefitIds
                                        .addAll(ids);
                            }
                        }
                );


        recyclerMembershipPlanBenefits
                .setAdapter(
                        membershipPlanBenefitAdapter
                );
    }


    // =====================================================
    // DEFAULT VALUES
    // =====================================================

    private void setupDefaultValues() {

        checkAllBranches.setChecked(
                true
        );


        layoutDefaultPricing.setVisibility(
                View.VISIBLE
        );


        layoutBranchPricing.setVisibility(
                View.GONE
        );


        switchMonthlyActivation.setChecked(
                false
        );


        switchDailyCharge.setChecked(
                false
        );


        switchFreezeAllowed.setChecked(
                false
        );


        txtMaxFreezeDays.setEnabled(
                false
        );


        txtFreezeFee.setEnabled(
                false
        );
    }


    // =====================================================
    // DROPDOWNS
    // =====================================================

    private void setupDropdowns() {

        setDropdownItems(
                txtDurationType,
                new String[]{
                        "DAYS",
                        "MONTHS",
                        "YEARS",
                        "UNLIMITED"
                }
        );


        txtDurationType.setText(
                "MONTHS",
                false
        );


        setDropdownItems(
                txtBillingType,
                new String[]{
                        "ONE_TIME",
                        "RECURRING",
                        "INSTALLMENT"
                }
        );


        txtBillingType.setText(
                "ONE_TIME",
                false
        );


        setDropdownItems(
                txtBillingFrequency,
                new String[]{
                        "WEEKLY",
                        "MONTHLY",
                        "QUARTERLY",
                        "YEARLY",
                        "CUSTOM"
                }
        );


        txtBillingFrequency.setText(
                "MONTHLY",
                false
        );


        setDropdownItems(
                txtAccessBillingMode,
                new String[]{
                        "FIXED_TERM",
                        "MONTHLY_CONTRACT",
                        "MONTHLY_FLEX",
                        "DAILY"
                }
        );


        txtAccessBillingMode.setText(
                "FIXED_TERM",
                false
        );


        setDropdownItems(
                txtAccessType,
                new String[]{
                        "FULL",
                        "OFF_PEAK",
                        "CUSTOM"
                }
        );


        txtAccessType.setText(
                "FULL",
                false
        );
    }


    // =====================================================
    // DROPDOWN ITEMS
    // =====================================================

    private void setDropdownItems(
            AutoCompleteTextView view,
            String[] items) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_dropdown_item_1line,
                        items
                );


        view.setAdapter(
                adapter
        );


        view.setOnClickListener(
                v -> view.showDropDown()
        );
    }


    // =====================================================
    // FREEZE
    // =====================================================

    private void setupFreeze() {

        switchFreezeAllowed
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            txtMaxFreezeDays
                                    .setEnabled(
                                            isChecked
                                    );


                            txtFreezeFee
                                    .setEnabled(
                                            isChecked
                                    );


                            if (!isChecked) {

                                txtMaxFreezeDays
                                        .setText("");


                                txtFreezeFee
                                        .setText("");
                            }
                        }
                );
    }


    // =====================================================
    // BRANCH SELECTION
    // =====================================================

    private void setupBranchSelection() {

        checkAllBranches
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            if (restoringEditData) {
                                return;
                            }


                            if (isChecked) {

                                /*
                                 * Selecting ALL BRANCHES.
                                 *
                                 * The existing branch-specific
                                 * pricing cards are no longer
                                 * needed.
                                 */

                                setAllBranchCheckboxes(
                                        false
                                );


                                layoutDefaultPricing
                                        .setVisibility(
                                                View.VISIBLE
                                        );


                                layoutBranchPricing
                                        .setVisibility(
                                                View.GONE
                                        );

                            } else {

                                updatePricingVisibility();
                            }
                        }
                );
    }


    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        String tenantNo =
                sessionStorage
                        .getTenantNo();


        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showError(
                    "Tenant information is missing."
            );

            return;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showError(
                    "Device information is missing."
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


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                branchList.clear();


                                if (apiResponse.getData() != null) {

                                    branchList.addAll(
                                            apiResponse.getData()
                                    );
                                }


                                setupBranchCheckboxes();


                                // -----------------------------------------
                                // EDIT MODE
                                // -----------------------------------------

                                if (editMode) {

                                    loadMembershipPlan();
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
                                        "Unable to connect to server."
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
                sessionStorage
                        .getTenantNo();


        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showBenefitEmptyState(
                    "Tenant information is missing."
            );

            return;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showBenefitEmptyState(
                    "Device information is missing."
            );

            return;
        }


        apiService
                .getActiveBenefits(
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

                                if (isFinishing()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showBenefitEmptyState(
                                            "Unable to load benefits."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<BenefitResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showBenefitEmptyState(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                benefitList.clear();


                                if (apiResponse.getData() != null) {

                                    benefitList.addAll(
                                            apiResponse.getData()
                                    );
                                }


                                membershipPlanBenefitAdapter
                                        .setBenefits(
                                                benefitList
                                        );


                                updateBenefitEmptyState();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<BenefitResponse>>>
                                            call,

                                    Throwable t) {

                                if (isFinishing()) {
                                    return;
                                }


                                showBenefitEmptyState(
                                        "Unable to connect to server."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // BENEFIT EMPTY STATE
    // =====================================================

    private void updateBenefitEmptyState() {

        if (benefitList.isEmpty()) {

            recyclerMembershipPlanBenefits
                    .setVisibility(
                            View.GONE
                    );


            txtNoMembershipPlanBenefits
                    .setVisibility(
                            View.VISIBLE
                    );

        } else {

            recyclerMembershipPlanBenefits
                    .setVisibility(
                            View.VISIBLE
                    );


            txtNoMembershipPlanBenefits
                    .setVisibility(
                            View.GONE
                    );
        }
    }


    // =====================================================
    // BENEFIT ERROR / EMPTY STATE
    // =====================================================

    private void showBenefitEmptyState(
            String message) {

        recyclerMembershipPlanBenefits
                .setVisibility(
                        View.GONE
                );


        txtNoMembershipPlanBenefits
                .setVisibility(
                        View.VISIBLE
                );


        txtNoMembershipPlanBenefits
                .setText(
                        message == null
                                || message.trim().isEmpty()
                                ? "No benefits available."
                                : message
                );
    }


    // =====================================================
    // SETUP BRANCH CHECKBOXES
    // =====================================================

    private void setupBranchCheckboxes() {

        layoutBranchList.removeAllViews();

        branchCheckBoxes.clear();


        if (branchList.isEmpty()) {

            txtNoBranches.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        txtNoBranches.setVisibility(
                View.GONE
        );


        for (BranchResponse branch :
                branchList) {

            MaterialCheckBox checkBox =
                    new MaterialCheckBox(this);


            String displayName =
                    branch.getBranchName();


            if (branch.getBranchNo() != null
                    && !branch.getBranchNo()
                    .trim()
                    .isEmpty()) {

                displayName =
                        branch.getBranchNo()
                                + " - "
                                + branch.getBranchName();
            }


            checkBox.setText(
                    displayName
            );


            checkBox.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );


            checkBox.setTextSize(
                    14
            );


            checkBox.setPadding(
                    0,
                    6,
                    0,
                    6
            );


            checkBox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {

                        if (restoringEditData) {
                            return;
                        }


                        if (isChecked) {

                            /*
                             * Selecting a specific branch
                             * changes the mode to selected
                             * branches.
                             */

                            checkAllBranches
                                    .setOnCheckedChangeListener(
                                            null
                                    );


                            checkAllBranches
                                    .setChecked(
                                            false
                                    );


                            setupBranchSelection();


                            updatePricingVisibility();

                        } else {

                            updatePricingVisibility();
                        }
                    }
            );


            branchCheckBoxes.add(
                    checkBox
            );


            layoutBranchList.addView(
                    checkBox
            );
        }
    }


    // =====================================================
    // SET ALL BRANCH CHECKBOXES
    // =====================================================

    private void setAllBranchCheckboxes(
            boolean checked) {

        for (MaterialCheckBox checkBox :
                branchCheckBoxes) {

            checkBox.setChecked(
                    checked
            );
        }


        updatePricingVisibility();
    }


    // =====================================================
    // PRICING VISIBILITY
    // =====================================================

    private void updatePricingVisibility() {

        if (checkAllBranches.isChecked()) {

            layoutDefaultPricing
                    .setVisibility(
                            View.VISIBLE
                    );


            layoutBranchPricing
                    .setVisibility(
                            View.GONE
                    );


            return;
        }


        boolean hasSelectedBranch =
                hasSelectedBranch();


        if (hasSelectedBranch) {

            layoutDefaultPricing
                    .setVisibility(
                            View.GONE
                    );


            layoutBranchPricing
                    .setVisibility(
                            View.VISIBLE
                    );


            /*
             * IMPORTANT:
             *
             * This rebuild now preserves the values
             * of branches that are still selected.
             */
            buildBranchPricingViews();

        } else {

            layoutDefaultPricing
                    .setVisibility(
                            View.GONE
                    );


            layoutBranchPricing
                    .setVisibility(
                            View.GONE
                    );
        }
    }


    // =====================================================
    // HAS SELECTED BRANCH
    // =====================================================

    private boolean hasSelectedBranch() {

        for (MaterialCheckBox checkBox :
                branchCheckBoxes) {

            if (checkBox.isChecked()) {

                return true;
            }
        }


        return false;
    }


    // =====================================================
    // BUILD BRANCH PRICING VIEWS
    // =====================================================

    private void buildBranchPricingViews() {

        /*
         * =================================================
         * IMPORTANT
         * =================================================
         *
         * Before removing the existing cards, remember
         * their current values using branchId.
         *
         * Example:
         *
         * Colombo -> 5000 / 1000 / 5000
         * Kandy   -> 6000 / 1000 / 6000
         *
         * If Nugegoda is newly selected, Colombo and Kandy
         * values are restored after rebuilding.
         *
         * If Colombo was unchecked, its card is removed and
         * therefore its values are intentionally discarded.
         *
         * If Colombo is selected again later, no old card
         * exists, so it starts empty.
         */

        Map<Integer, BranchPricingValues>
                currentValues =
                captureCurrentBranchPricingValues();


        // -------------------------------------------------
        // REMOVE OLD CARDS
        // -------------------------------------------------

        layoutBranchPricingList
                .removeAllViews();


        branchPricingViews.clear();


        // -------------------------------------------------
        // CREATE SELECTED BRANCH CARDS
        // -------------------------------------------------

        for (int i = 0;
             i < branchList.size();
             i++) {

            BranchResponse branch =
                    branchList.get(i);


            if (!branchCheckBoxes
                    .get(i)
                    .isChecked()) {

                continue;
            }


            BranchPricingViews views =
                    createBranchPricingCard(
                            branch
                    );


            // -------------------------------------------------
            // RESTORE CURRENT VALUES
            // -------------------------------------------------

            BranchPricingValues values =
                    currentValues.get(
                            branch.getBranchId()
                    );


            if (values != null) {

                views.price.setText(
                        values.price
                );


                views.joiningFee.setText(
                        values.joiningFee
                );


                views.renewalFee.setText(
                        values.renewalFee
                );
            }


            branchPricingViews.add(
                    views
            );


            layoutBranchPricingList
                    .addView(
                            views.card
                    );
        }
    }


    // =====================================================
    // CAPTURE CURRENT BRANCH PRICING VALUES
    // =====================================================

    private Map<Integer, BranchPricingValues>
    captureCurrentBranchPricingValues() {

        Map<Integer, BranchPricingValues>
                valuesMap =
                new HashMap<>();


        for (
                BranchPricingViews views
                : branchPricingViews
        ) {

            if (views == null
                    || views.branchId == null) {

                continue;
            }


            String price =
                    getEditTextValue(
                            views.price
                    );


            String joiningFee =
                    getEditTextValue(
                            views.joiningFee
                    );


            String renewalFee =
                    getEditTextValue(
                            views.renewalFee
                    );


            valuesMap.put(
                    views.branchId,

                    new BranchPricingValues(
                            price,
                            joiningFee,
                            renewalFee
                    )
            );
        }


        return valuesMap;
    }


    // =====================================================
    // GET EDIT TEXT VALUE
    // =====================================================

    private String getEditTextValue(
            TextInputEditText editText) {

        if (editText == null
                || editText.getText() == null) {

            return "";
        }


        return editText
                .getText()
                .toString();
    }


    // =====================================================
    // CREATE BRANCH PRICING CARD
    // =====================================================

    private BranchPricingViews
    createBranchPricingCard(
            BranchResponse branch) {

        MaterialCardView card =
                new MaterialCardView(this);


        card.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );


        card.setRadius(
                14
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


        LinearLayout container =
                new LinearLayout(this);


        container.setOrientation(
                LinearLayout.VERTICAL
        );


        int padding =
                dp(16);


        container.setPadding(
                padding,
                padding,
                padding,
                padding
        );


        String branchName =
                branch.getBranchName();


        if (branch.getBranchNo() != null
                && !branch.getBranchNo()
                .trim()
                .isEmpty()) {

            branchName =
                    branch.getBranchNo()
                            + " - "
                            + branch.getBranchName();
        }


        TextView title =
                new TextView(this);


        title.setText(
                branchName
        );


        title.setTextColor(
                getColor(
                        R.color.text_primary
                )
        );


        title.setTextSize(
                15
        );


        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );


        container.addView(
                title
        );


        // =================================================
        // PRICE
        // =================================================

        TextInputLayout priceLayout =
                createInputLayout(
                        "Price"
                );


        TextInputEditText price =
                createMoneyEditText();


        priceLayout.addView(
                price
        );


        container.addView(
                priceLayout
        );


        // =================================================
        // JOINING FEE
        // =================================================

        TextInputLayout joiningLayout =
                createInputLayout(
                        "Joining Fee"
                );


        TextInputEditText joiningFee =
                createMoneyEditText();


        joiningLayout.addView(
                joiningFee
        );


        container.addView(
                joiningLayout
        );


        // =================================================
        // RENEWAL FEE
        // =================================================

        TextInputLayout renewalLayout =
                createInputLayout(
                        "Renewal Fee"
                );


        TextInputEditText renewalFee =
                createMoneyEditText();


        renewalLayout.addView(
                renewalFee
        );


        container.addView(
                renewalLayout
        );


        card.addView(
                container
        );


        return new BranchPricingViews(
                branch.getBranchId(),
                card,
                price,
                joiningFee,
                renewalFee
        );
    }


    // =====================================================
    // CREATE INPUT LAYOUT
    // =====================================================

    private TextInputLayout
    createInputLayout(
            String hint) {

        TextInputLayout layout =
                new TextInputLayout(
                        this
                );


        layout.setBoxBackgroundMode(
                TextInputLayout
                        .BOX_BACKGROUND_OUTLINE
        );


        layout.setHint(
                hint
        );


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        params.topMargin =
                dp(10);


        layout.setLayoutParams(
                params
        );


        return layout;
    }


    // =====================================================
    // CREATE MONEY EDIT TEXT
    // =====================================================

    private TextInputEditText
    createMoneyEditText() {

        TextInputEditText editText =
                new TextInputEditText(
                        this
                );


        editText.setSingleLine(
                true
        );


        editText.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );


        editText.setTextColor(
                getColor(
                        R.color.text_primary
                )
        );


        return editText;
    }


    // =====================================================
    // LOAD EXISTING PLAN
    // =====================================================

    private void loadMembershipPlan() {

        String tenantNo =
                sessionStorage
                        .getTenantNo();


        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (editMembershipPlanId == null
                || editMembershipPlanId <= 0) {

            showError(
                    "Invalid membership plan."
            );

            return;
        }


        apiService
                .getMembershipPlan(
                        tenantNo,
                        deviceToken,
                        editMembershipPlanId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MembershipPlanResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            response) {

                                if (isFinishing()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to load membership plan."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        MembershipPlanResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                editingPlan =
                                        apiResponse.getData();


                                populateEditData();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            call,

                                    Throwable t) {

                                if (isFinishing()) {
                                    return;
                                }


                                showError(
                                        "Unable to load membership plan."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // POPULATE EDIT DATA
    // =====================================================

    private void populateEditData() {

        if (editingPlan == null) {
            return;
        }


        txtPlanCode.setText(
                safeString(
                        editingPlan.getPlanCode()
                )
        );


        txtPlanName.setText(
                safeString(
                        editingPlan.getPlanName()
                )
        );


        txtPlanDescription.setText(
                safeString(
                        editingPlan.getDescription()
                )
        );


        // =================================================
        // DURATION
        // =================================================

        if (editingPlan.getDurationType() != null) {

            txtDurationType.setText(
                    editingPlan
                            .getDurationType(),
                    false
            );
        }


        setText(
                txtDurationValue,
                editingPlan.getDurationValue()
        );


        // =================================================
        // BILLING
        // =================================================

        if (editingPlan.getBillingType() != null) {

            txtBillingType.setText(
                    editingPlan
                            .getBillingType(),
                    false
            );
        }


        if (editingPlan.getBillingFrequency() != null) {

            txtBillingFrequency.setText(
                    editingPlan
                            .getBillingFrequency(),
                    false
            );
        }


        setText(
                txtBillingFrequencyValue,
                editingPlan
                        .getBillingFrequencyValue()
        );


        // =================================================
        // ACCESS BILLING
        // =================================================

        if (editingPlan.getAccessBillingMode() != null) {

            txtAccessBillingMode.setText(
                    editingPlan
                            .getAccessBillingMode(),
                    false
            );
        }


        switchMonthlyActivation.setChecked(
                Boolean.TRUE.equals(
                        editingPlan
                                .getMonthlyActivationRequired()
                )
        );


        switchDailyCharge.setChecked(
                Boolean.TRUE.equals(
                        editingPlan
                                .getDailyChargeEnabled()
                )
        );


        // =================================================
        // ACCESS TYPE
        // =================================================

        if (editingPlan.getAccessType() != null) {

            txtAccessType.setText(
                    editingPlan
                            .getAccessType(),
                    false
            );
        }


        // =================================================
        // FREEZE
        // =================================================

        boolean freezeAllowed =
                Boolean.TRUE.equals(
                        editingPlan
                                .getFreezeAllowed()
                );


        switchFreezeAllowed.setChecked(
                freezeAllowed
        );


        if (freezeAllowed) {

            txtMaxFreezeDays.setEnabled(
                    true
            );


            txtFreezeFee.setEnabled(
                    true
            );


            setText(
                    txtMaxFreezeDays,
                    editingPlan
                            .getMaxFreezeDays()
            );


            setDecimalText(
                    txtFreezeFee,
                    editingPlan
                            .getFreezeFee()
            );
        }


        // =================================================
        // BENEFITS
        // =================================================

        populateBenefits();


        // =================================================
        // BRANCHES
        // =================================================

        populateBranches();
    }


    // =====================================================
    // POPULATE BENEFITS
    // =====================================================

    private void populateBenefits() {

        selectedBenefitIds.clear();


        List<MembershipPlanBenefitResponse>
                planBenefits =
                editingPlan.getBenefits();


        if (planBenefits != null) {

            for (
                    MembershipPlanBenefitResponse
                            planBenefit
                    : planBenefits
            ) {

                if (planBenefit == null) {
                    continue;
                }


                Integer benefitId =
                        planBenefit.getBenefitId();


                if (benefitId == null) {
                    continue;
                }


                /*
                 * Only active plan-benefit records
                 * should be restored.
                 */
                if (planBenefit.getStatus() == null
                        || Boolean.TRUE.equals(
                        planBenefit.getStatus()
                )) {

                    selectedBenefitIds.add(
                            benefitId
                    );
                }
            }
        }


        membershipPlanBenefitAdapter
                .setSelectedBenefitIds(
                        selectedBenefitIds
                );
    }


    // =====================================================
    // POPULATE BRANCHES
    // =====================================================

    private void populateBranches() {

        List<MembershipPlanBranchResponse>
                assignments =
                editingPlan
                        .getBranchAssignments();


        if (assignments == null
                || assignments.isEmpty()) {

            checkAllBranches.setChecked(
                    true
            );


            layoutDefaultPricing
                    .setVisibility(
                            View.VISIBLE
                    );


            layoutBranchPricing
                    .setVisibility(
                            View.GONE
                    );


            return;
        }


        /*
         * Temporarily disable checkbox-driven rebuilds.
         */

        restoringEditData = true;


        checkAllBranches
                .setOnCheckedChangeListener(
                        null
                );


        checkAllBranches.setChecked(
                false
        );


        for (int i = 0;
             i < branchList.size();
             i++) {

            BranchResponse branch =
                    branchList.get(i);


            MaterialCheckBox checkBox =
                    branchCheckBoxes.get(i);


            boolean selected =
                    findAssignment(
                            assignments,
                            branch.getBranchId()
                    ) != null;


            checkBox.setChecked(
                    selected
            );
        }


        restoringEditData = false;


        /*
         * Restore the normal ALL checkbox listener.
         */

        setupBranchSelection();


        // =================================================
        // SHOW BRANCH PRICING
        // =================================================

        layoutDefaultPricing
                .setVisibility(
                        View.GONE
                );


        layoutBranchPricing
                .setVisibility(
                        View.VISIBLE
                );


        /*
         * Build all selected branch cards once.
         */
        buildBranchPricingViews();


        // =================================================
        // POPULATE BACKEND VALUES
        // =================================================

        for (
                BranchPricingViews views
                : branchPricingViews
        ) {

            MembershipPlanBranchResponse
                    assignment =
                    findAssignment(
                            assignments,
                            views.branchId
                    );


            if (assignment == null) {
                continue;
            }


            setDecimalText(
                    views.price,
                    assignment.getPrice()
            );


            setDecimalText(
                    views.joiningFee,
                    assignment.getJoiningFee()
            );


            setDecimalText(
                    views.renewalFee,
                    assignment.getRenewalFee()
            );
        }
    }


    // =====================================================
    // FIND ASSIGNMENT
    // =====================================================

    private MembershipPlanBranchResponse
    findAssignment(
            List<MembershipPlanBranchResponse>
                    assignments,

            Integer branchId) {

        if (assignments == null
                || branchId == null) {

            return null;
        }


        for (
                MembershipPlanBranchResponse assignment
                : assignments
        ) {

            if (assignment == null) {
                continue;
            }


            if (assignment.getBranchId() != null
                    && assignment.getBranchId()
                    .equals(
                            branchId
                    )) {

                return assignment;
            }
        }


        return null;
    }


    // =====================================================
    // SAVE MEMBERSHIP PLAN
    // =====================================================

    private void saveMembershipPlan() {

        if (!validateForm()) {
            return;
        }


        String tenantNo =
                sessionStorage
                        .getTenantNo();


        String deviceToken =
                deviceStorage
                        .getDeviceToken();


        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showError(
                    "Tenant information is missing."
            );

            return;
        }


        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showError(
                    "Device information is missing."
            );

            return;
        }


        MembershipPlanRequest request =
                buildRequest();


        btnSaveMembershipPlan
                .setEnabled(false);


        // =================================================
        // UPDATE
        // =================================================

        if (editMode) {

            apiService
                    .updateMembershipPlan(
                            tenantNo,
                            deviceToken,
                            editMembershipPlanId,
                            request
                    )
                    .enqueue(
                            new Callback<
                                    ApiResponse<
                                            MembershipPlanResponse>>() {

                                @Override
                                public void onResponse(
                                        Call<
                                                ApiResponse<
                                                        MembershipPlanResponse>>
                                                call,

                                        Response<
                                                ApiResponse<
                                                        MembershipPlanResponse>>
                                                response) {

                                    btnSaveMembershipPlan
                                            .setEnabled(true);


                                    if (isFinishing()) {
                                        return;
                                    }


                                    if (!response.isSuccessful()) {

                                        String errorMessage =
                                                "HTTP Error "
                                                        + response.code();


                                        try {

                                            if (response.errorBody()
                                                    != null) {

                                                String errorBody =
                                                        response
                                                                .errorBody()
                                                                .string();


                                                if (errorBody != null
                                                        && !errorBody
                                                        .trim()
                                                        .isEmpty()) {

                                                    errorMessage +=
                                                            "\n"
                                                                    + errorBody;
                                                }
                                            }

                                        } catch (Exception e) {

                                            errorMessage +=
                                                    "\nUnable to read server error.";
                                        }


                                        showError(
                                                errorMessage
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
                                            MembershipPlanResponse>
                                            apiResponse =
                                            response.body();


                                    if (!apiResponse.isSuccess()) {

                                        showError(
                                                apiResponse.getMessage()
                                        );

                                        return;
                                    }


                                    Toast.makeText(
                                            AddMembershipPlanActivity.this,
                                            "Membership plan updated successfully.",
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
                                                        MembershipPlanResponse>>
                                                call,

                                        Throwable t) {

                                    btnSaveMembershipPlan
                                            .setEnabled(true);


                                    if (isFinishing()) {
                                        return;
                                    }


                                    showError(
                                            "Connection failed: "
                                                    + (
                                                    t.getMessage() == null
                                                            ? "Unknown error."
                                                            : t.getMessage()
                                            )
                                    );
                                }
                            }
                    );


            return;
        }


        // =================================================
        // CREATE
        // =================================================

        apiService
                .createMembershipPlan(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MembershipPlanResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            response) {

                                btnSaveMembershipPlan
                                        .setEnabled(true);


                                if (isFinishing()) {
                                    return;
                                }


                                if (!response.isSuccessful()) {

                                    String errorMessage =
                                            "HTTP Error "
                                                    + response.code();


                                    try {

                                        if (response.errorBody()
                                                != null) {

                                            String errorBody =
                                                    response
                                                            .errorBody()
                                                            .string();


                                            if (errorBody != null
                                                    && !errorBody
                                                    .trim()
                                                    .isEmpty()) {

                                                errorMessage +=
                                                        "\n"
                                                                + errorBody;
                                            }
                                        }

                                    } catch (Exception e) {

                                        errorMessage +=
                                                "\nUnable to read server error.";
                                    }


                                    showError(
                                            errorMessage
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
                                        MembershipPlanResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                Toast.makeText(
                                        AddMembershipPlanActivity.this,
                                        "Membership plan created successfully.",
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
                                                    MembershipPlanResponse>>
                                            call,

                                    Throwable t) {

                                btnSaveMembershipPlan
                                        .setEnabled(true);


                                if (isFinishing()) {
                                    return;
                                }


                                showError(
                                        "Connection failed: "
                                                + (
                                                t.getMessage() == null
                                                        ? "Unknown error."
                                                        : t.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // VALIDATE FORM
    // =====================================================

    private boolean validateForm() {

        if (isEmpty(txtPlanCode)) {

            txtPlanCode.setError(
                    "Plan code is required."
            );


            txtPlanCode.requestFocus();


            return false;
        }


        if (isEmpty(txtPlanName)) {

            txtPlanName.setError(
                    "Plan name is required."
            );


            txtPlanName.requestFocus();


            return false;
        }


        if (isEmpty(txtDurationValue)
                && !"UNLIMITED".equals(
                txtDurationType
                        .getText()
                        .toString()
        )) {

            txtDurationValue.setError(
                    "Duration value is required."
            );


            txtDurationValue.requestFocus();


            return false;
        }


        if (!checkAllBranches.isChecked()
                && !hasSelectedBranch()) {

            showError(
                    "Select at least one branch."
            );


            return false;
        }


        if (checkAllBranches.isChecked()) {

            if (isEmpty(txtDefaultPrice)) {

                txtDefaultPrice.setError(
                        "Price is required."
                );


                txtDefaultPrice.requestFocus();


                return false;
            }

        } else {

            for (
                    BranchPricingViews views
                    : branchPricingViews
            ) {

                if (isEmpty(views.price)) {

                    views.price.setError(
                            "Price is required."
                    );


                    views.price.requestFocus();


                    return false;
                }
            }
        }


        if (switchFreezeAllowed.isChecked()) {

            if (isEmpty(txtMaxFreezeDays)) {

                txtMaxFreezeDays.setError(
                        "Maximum freeze days is required."
                );


                txtMaxFreezeDays.requestFocus();


                return false;
            }
        }


        return true;
    }


    // =====================================================
    // BUILD REQUEST
    // =====================================================

    private MembershipPlanRequest
    buildRequest() {

        MembershipPlanRequest request =
                new MembershipPlanRequest();


        // =================================================
        // BASIC
        // =================================================

        request.setPlanCode(
                text(txtPlanCode)
        );


        request.setPlanName(
                text(txtPlanName)
        );


        request.setDescription(
                text(txtPlanDescription)
        );


        // =================================================
        // DURATION
        // =================================================

        request.setDurationType(
                txtDurationType
                        .getText()
                        .toString()
        );


        request.setDurationValue(
                parseInteger(
                        txtDurationValue
                )
        );


        // =================================================
        // BILLING
        // =================================================

        request.setBillingType(
                txtBillingType
                        .getText()
                        .toString()
        );


        request.setBillingFrequency(
                txtBillingFrequency
                        .getText()
                        .toString()
        );


        request.setBillingFrequencyValue(
                parseInteger(
                        txtBillingFrequencyValue
                )
        );


        // =================================================
        // ACCESS BILLING
        // =================================================

        request.setAccessBillingMode(
                txtAccessBillingMode
                        .getText()
                        .toString()
        );


        request.setMonthlyActivationRequired(
                switchMonthlyActivation
                        .isChecked()
        );


        request.setDailyChargeEnabled(
                switchDailyCharge
                        .isChecked()
        );


        // =================================================
        // ACCESS TYPE
        // =================================================

        request.setAccessType(
                txtAccessType
                        .getText()
                        .toString()
        );


        // =================================================
        // FREEZE
        // =================================================

        request.setFreezeAllowed(
                switchFreezeAllowed
                        .isChecked()
        );


        request.setMaxFreezeDays(
                parseInteger(
                        txtMaxFreezeDays
                )
        );


        request.setFreezeFee(
                parseDecimal(
                        txtFreezeFee
                )
        );


        // =================================================
        // BRANCH MODE
        // =================================================

        if (checkAllBranches.isChecked()) {

            request.setBranchMode(
                    "ALL_BRANCHES"
            );


            request.setDefaultPrice(
                    parseDecimal(
                            txtDefaultPrice
                    )
            );


            request.setDefaultJoiningFee(
                    parseDecimal(
                            txtDefaultJoiningFee
                    )
            );


            request.setDefaultRenewalFee(
                    parseDecimal(
                            txtDefaultRenewalFee
                    )
            );


            request.setBranches(
                    null
            );

        } else {

            request.setBranchMode(
                    "SELECTED_BRANCHES"
            );


            request.setDefaultPrice(
                    null
            );


            request.setDefaultJoiningFee(
                    null
            );


            request.setDefaultRenewalFee(
                    null
            );


            List<MembershipPlanBranchRequest>
                    branches =
                    new ArrayList<>();


            for (
                    BranchPricingViews views
                    : branchPricingViews
            ) {

                MembershipPlanBranchRequest
                        branchRequest =
                        new MembershipPlanBranchRequest();


                branchRequest.setBranchId(
                        views.branchId
                );


                branchRequest.setPrice(
                        parseDecimal(
                                views.price
                        )
                );


                branchRequest.setJoiningFee(
                        parseDecimal(
                                views.joiningFee
                        )
                );


                branchRequest.setRenewalFee(
                        parseDecimal(
                                views.renewalFee
                        )
                );


                branches.add(
                        branchRequest
                );
            }


            request.setBranches(
                    branches
            );
        }


        // =================================================
        // BENEFITS
        // =================================================

        List<MembershipPlanBenefitRequest>
                benefits =
                new ArrayList<>();


        for (
                Integer benefitId
                : selectedBenefitIds
        ) {

            if (benefitId == null) {
                continue;
            }


            MembershipPlanBenefitRequest
                    benefitRequest =
                    new MembershipPlanBenefitRequest();


            benefitRequest.setBenefitId(
                    benefitId
            );


            /*
             * Benefits selected inside the Membership Plan
             * are included benefits.
             */
            benefitRequest.setBenefitType(
                    "INCLUDED"
            );


            /*
             * Included benefits do not have a separate
             * membership-plan charge.
             *
             * The backend also enforces this.
             */
            benefitRequest.setFee(
                    BigDecimal.ZERO
            );


            benefits.add(
                    benefitRequest
            );
        }


        request.setBenefits(
                benefits
        );


        return request;
    }


    // =====================================================
    // HELPERS
    // =====================================================

    private boolean isEmpty(
            TextInputEditText editText) {

        return editText == null
                || editText.getText() == null
                || editText
                .getText()
                .toString()
                .trim()
                .isEmpty();
    }


    private String text(
            TextInputEditText editText) {

        if (editText == null
                || editText.getText() == null) {

            return null;
        }


        String value =
                editText
                        .getText()
                        .toString()
                        .trim();


        return value.isEmpty()
                ? null
                : value;
    }


    private Integer parseInteger(
            TextInputEditText editText) {

        String value =
                text(editText);


        if (value == null) {

            return null;
        }


        try {

            return Integer.parseInt(
                    value
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }


    private BigDecimal parseDecimal(
            TextInputEditText editText) {

        String value =
                text(editText);


        if (value == null) {

            return BigDecimal.ZERO;
        }


        try {

            return new BigDecimal(
                    value
            );

        } catch (NumberFormatException e) {

            return BigDecimal.ZERO;
        }
    }


    private void setText(
            TextInputEditText editText,
            Object value) {

        if (value == null) {

            editText.setText("");

        } else {

            editText.setText(
                    String.valueOf(value)
            );
        }
    }


    private void setDecimalText(
            TextInputEditText editText,
            BigDecimal value) {

        if (value == null) {

            editText.setText("");

        } else {

            editText.setText(
                    value.stripTrailingZeros()
                            .toPlainString()
            );
        }
    }


    private String safeString(
            String value) {

        return value == null
                ? ""
                : value;
    }


    private void showError(
            String message) {

        Toast.makeText(
                this,
                message == null
                        || message.trim().isEmpty()
                        ? "Something went wrong."
                        : message,
                Toast.LENGTH_LONG
        ).show();
    }


    private int dp(
            int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;


        return Math.round(
                value * density
        );
    }


    // =====================================================
    // BRANCH PRICING VIEWS
    // =====================================================

    private static class BranchPricingViews {

        private final Integer branchId;

        private final MaterialCardView card;

        private final TextInputEditText price;

        private final TextInputEditText joiningFee;

        private final TextInputEditText renewalFee;


        private BranchPricingViews(
                Integer branchId,
                MaterialCardView card,
                TextInputEditText price,
                TextInputEditText joiningFee,
                TextInputEditText renewalFee) {

            this.branchId =
                    branchId;

            this.card =
                    card;

            this.price =
                    price;

            this.joiningFee =
                    joiningFee;

            this.renewalFee =
                    renewalFee;
        }
    }


    // =====================================================
    // TEMPORARY BRANCH PRICING VALUES
    // =====================================================

    private static class BranchPricingValues {

        private final String price;

        private final String joiningFee;

        private final String renewalFee;


        private BranchPricingValues(
                String price,
                String joiningFee,
                String renewalFee) {

            this.price =
                    price;

            this.joiningFee =
                    joiningFee;

            this.renewalFee =
                    renewalFee;
        }
    }
}