package arafath65.minime.gymmanagementui.MainDashboard;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipRequest;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMembershipActivity
        extends AppCompatActivity {

    // =====================================================
    // MEMBER
    // =====================================================

    private int memberId;

    private String memberName;


    // =====================================================
    // API
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;


    // =====================================================
    // VIEWS
    // =====================================================

    private ImageButton btnBack;

    private TextView txtMemberName;

    private TextInputLayout layoutBranch;
    private TextInputLayout layoutPlan;
    private TextInputLayout layoutStartDate;
    private TextInputLayout layoutDiscount;

    private AutoCompleteTextView txtBranch;
    private AutoCompleteTextView txtPlan;

    private TextInputEditText txtStartDate;
    private TextInputEditText txtDiscount;
    private TextInputEditText txtNotes;

    private TextView txtPlanPrice;
    private TextView txtJoiningFee;
    private TextView txtTotalAmount;

    private MaterialButton btnCreateMembership;


    // =====================================================
    // DATA
    // =====================================================

    private final List<BranchResponse> branches =
            new ArrayList<>();

    private final List<MembershipPlanResponse> plans =
            new ArrayList<>();


    private BranchResponse selectedBranch;

    private MembershipPlanResponse selectedPlan;


    // =====================================================
    // FORMATTERS
    // =====================================================

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
            );


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_create_membership
        );


        apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);


        deviceStorage =
                new DeviceStorage(
                        getApplicationContext()
                );


        // -------------------------------------------------
        // INTENT DATA
        // -------------------------------------------------

        memberId =
                getIntent().getIntExtra(
                        "memberId",
                        0
                );


        memberName =
                getIntent().getStringExtra(
                        "memberName"
                );


        if (memberId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid member.",
                    Toast.LENGTH_LONG
            ).show();


            finish();

            return;
        }


        initializeViews();

        setupButtons();

        setupDatePicker();

        setupDiscountWatcher();

        setDefaultStartDate();

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


        txtMemberName =
                findViewById(
                        R.id.txtMemberName
                );


        layoutBranch =
                findViewById(
                        R.id.layoutBranch
                );


        layoutPlan =
                findViewById(
                        R.id.layoutPlan
                );


        layoutStartDate =
                findViewById(
                        R.id.layoutStartDate
                );


        layoutDiscount =
                findViewById(
                        R.id.layoutDiscount
                );


        txtBranch =
                findViewById(
                        R.id.txtBranch
                );


        txtPlan =
                findViewById(
                        R.id.txtPlan
                );


        txtStartDate =
                findViewById(
                        R.id.txtStartDate
                );


        txtDiscount =
                findViewById(
                        R.id.txtDiscount
                );


        txtNotes =
                findViewById(
                        R.id.txtNotes
                );


        txtPlanPrice =
                findViewById(
                        R.id.txtPlanPrice
                );


        txtJoiningFee =
                findViewById(
                        R.id.txtJoiningFee
                );


        txtTotalAmount =
                findViewById(
                        R.id.txtTotalAmount
                );


        btnCreateMembership =
                findViewById(
                        R.id.btnCreateMembership
                );


        txtMemberName.setText(
                safeText(
                        memberName
                )
        );
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnBack.setOnClickListener(
                v -> finish()
        );


        btnCreateMembership.setOnClickListener(
                v -> createMembership()
        );
    }


    // =====================================================
    // DATE PICKER
    // =====================================================

    private void setupDatePicker() {

        txtStartDate.setOnClickListener(
                v -> {

                    Calendar calendar =
                            Calendar.getInstance();


                    int year =
                            calendar.get(
                                    Calendar.YEAR
                            );


                    int month =
                            calendar.get(
                                    Calendar.MONTH
                            );


                    int day =
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            );


                    DatePickerDialog dialog =
                            new DatePickerDialog(
                                    this,
                                    (view,
                                     selectedYear,
                                     selectedMonth,
                                     selectedDay) -> {

                                        LocalDate date =
                                                LocalDate.of(
                                                        selectedYear,
                                                        selectedMonth + 1,
                                                        selectedDay
                                                );


                                        txtStartDate.setText(
                                                date.format(
                                                        dateFormatter
                                                )
                                        );
                                    },
                                    year,
                                    month,
                                    day
                            );


                    dialog.show();
                }
        );
    }


    // =====================================================
    // DEFAULT START DATE
    // =====================================================

    private void setDefaultStartDate() {

        txtStartDate.setText(
                LocalDate.now()
                        .format(
                                dateFormatter
                        )
        );
    }


    // =====================================================
    // DISCOUNT WATCHER
    // =====================================================

    private void setupDiscountWatcher() {

        txtDiscount.addTextChangedListener(
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

                        calculateTotal();
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
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
                                        List<
                                                BranchResponse
                                                >
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            BranchResponse
                                                            >
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            BranchResponse
                                                            >
                                                    >
                                            > response) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to load branches."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<
                                                BranchResponse
                                                >
                                        > apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                branches.clear();

                                branches.addAll(
                                        apiResponse.getData()
                                );


                                setupBranchDropdown();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            BranchResponse
                                                            >
                                                    >
                                            > call,

                                    Throwable t) {

                                showError(
                                        "Connection failed while loading branches."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // BRANCH DROPDOWN
    // =====================================================

    private void setupBranchDropdown() {

        List<String> names =
                new ArrayList<>();


        for (BranchResponse branch
                : branches) {

            names.add(
                    safeText(
                            branch.getBranchName()
                    )
            );
        }


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                );


        txtBranch.setAdapter(
                adapter
        );


        txtBranch.setOnItemClickListener(
                (parent,
                 view,
                 position,
                 id) -> {

                    selectedBranch =
                            branches.get(
                                    position
                            );


                    txtBranch.setError(
                            null
                    );


                    loadPlans();
                }
        );


        if (branches.size() == 1) {

            selectedBranch =
                    branches.get(0);


            txtBranch.setText(
                    safeText(
                            selectedBranch
                                    .getBranchName()
                    ),
                    false
            );


            loadPlans();
        }
    }


    // =====================================================
    // LOAD PLANS
    // =====================================================

    private void loadPlans() {

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


        txtPlan.setText(
                "",
                false
        );


        selectedPlan =
                null;


        txtPlanPrice.setText(
                "Rs. 0.00"
        );


        txtJoiningFee.setText(
                "Rs. 0.00"
        );


        txtTotalAmount.setText(
                "Rs. 0.00"
        );


        apiService
                .getMembershipPlans(
                        tenantNo,
                        deviceToken
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                MembershipPlanResponse
                                                >
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MembershipPlanResponse
                                                            >
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MembershipPlanResponse
                                                            >
                                                    >
                                            > response) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to load membership plans."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<
                                                MembershipPlanResponse
                                                >
                                        > apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                plans.clear();


                                for (MembershipPlanResponse plan
                                        : apiResponse.getData()) {

                                    if (!Boolean.TRUE.equals(
                                            plan.getStatus()
                                    )) {

                                        continue;
                                    }


                                    if (selectedBranch != null
                                            && plan.getBranchId() != null
                                            && !selectedBranch
                                            .getBranchId()
                                            .equals(
                                                    plan.getBranchId()
                                            )) {

                                        continue;
                                    }


                                    plans.add(
                                            plan
                                    );
                                }


                                setupPlanDropdown();
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MembershipPlanResponse
                                                            >
                                                    >
                                            > call,

                                    Throwable t) {

                                showError(
                                        "Connection failed while loading plans."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // PLAN DROPDOWN
    // =====================================================

    private void setupPlanDropdown() {

        List<String> names =
                new ArrayList<>();


        for (MembershipPlanResponse plan
                : plans) {

            String text =
                    safeText(
                            plan.getPlanName()
                    );


            if (plan.getPlanCode() != null
                    && !plan.getPlanCode()
                    .trim()
                    .isEmpty()) {

                text +=
                        " (" +
                                plan.getPlanCode() +
                                ")";
            }


            names.add(
                    text
            );
        }


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                );


        txtPlan.setAdapter(
                adapter
        );


        txtPlan.setOnItemClickListener(
                (parent,
                 view,
                 position,
                 id) -> {

                    selectedPlan =
                            plans.get(
                                    position
                            );


                    txtPlan.setError(
                            null
                    );


                    updatePlanSummary();
                }
        );


        if (plans.size() == 1) {

            selectedPlan =
                    plans.get(0);


            String planName =
                    safeText(
                            selectedPlan.getPlanName()
                    );


            if (selectedPlan.getPlanCode() != null
                    && !selectedPlan.getPlanCode()
                    .trim()
                    .isEmpty()) {

                planName +=
                        " (" +
                                selectedPlan.getPlanCode() +
                                ")";
            }


            txtPlan.setText(
                    planName,
                    false
            );


            updatePlanSummary();
        }
    }


    // =====================================================
    // UPDATE PLAN SUMMARY
    // =====================================================

    private void updatePlanSummary() {

        if (selectedPlan == null) {

            txtPlanPrice.setText(
                    "Rs. 0.00"
            );


            txtJoiningFee.setText(
                    "Rs. 0.00"
            );


            txtTotalAmount.setText(
                    "Rs. 0.00"
            );


            return;
        }


        txtPlanPrice.setText(
                formatMoney(
                        selectedPlan.getPrice()
                )
        );


        txtJoiningFee.setText(
                formatMoney(
                        selectedPlan.getJoiningFee()
                )
        );


        calculateTotal();
    }


    // =====================================================
    // CALCULATE TOTAL
    // =====================================================

    private void calculateTotal() {

        if (selectedPlan == null) {

            txtTotalAmount.setText(
                    "Rs. 0.00"
            );

            return;
        }


        BigDecimal price =
                safeDecimal(
                        selectedPlan.getPrice()
                );


        BigDecimal joiningFee =
                safeDecimal(
                        selectedPlan.getJoiningFee()
                );


        BigDecimal discount =
                parseDiscount();


        BigDecimal total =
                price
                        .add(joiningFee)
                        .subtract(discount);


        if (total.signum() < 0) {

            total =
                    BigDecimal.ZERO;
        }


        txtTotalAmount.setText(
                formatMoney(
                        total
                )
        );
    }


    // =====================================================
    // CREATE MEMBERSHIP
    // =====================================================

    private void createMembership() {

        clearErrors();


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        if (selectedBranch == null) {

            layoutBranch.setError(
                    "Please select a branch."
            );

            return;
        }


        // -------------------------------------------------
        // PLAN
        // -------------------------------------------------

        if (selectedPlan == null) {

            layoutPlan.setError(
                    "Please select a membership plan."
            );

            return;
        }


        // -------------------------------------------------
        // START DATE
        // -------------------------------------------------

        String startDate =
                txtStartDate.getText()
                        .toString()
                        .trim();


        if (startDate.isEmpty()) {

            layoutStartDate.setError(
                    "Please select a start date."
            );

            return;
        }


        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        BigDecimal discount =
                parseDiscount();


        BigDecimal price =
                safeDecimal(
                        selectedPlan.getPrice()
                );


        BigDecimal joiningFee =
                safeDecimal(
                        selectedPlan.getJoiningFee()
                );


        BigDecimal maximumDiscount =
                price.add(
                        joiningFee
                );


        if (discount.compareTo(
                maximumDiscount
        ) > 0) {

            layoutDiscount.setError(
                    "Discount cannot exceed "
                            + formatMoney(
                            maximumDiscount
                    )
            );

            return;
        }


        // -------------------------------------------------
        // TENANT / DEVICE
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
        // REQUEST
        // -------------------------------------------------

        MemberMembershipRequest request =
                new MemberMembershipRequest();


        request.setMemberId(
                memberId
        );


        request.setBranchId(
                selectedBranch.getBranchId()
        );


        request.setMembershipPlanId(
                selectedPlan.getMembershipPlanId()
        );


        request.setStartDate(
                startDate
        );


        request.setDiscountAmount(
                discount
        );


        request.setNotes(
                txtNotes.getText()
                        .toString()
                        .trim()
        );


        // -------------------------------------------------
        // DISABLE BUTTON
        // -------------------------------------------------

        btnCreateMembership.setEnabled(
                false
        );


        // -------------------------------------------------
        // API
        // -------------------------------------------------

        apiService
                .createMembership(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberMembershipResponse
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    MemberMembershipResponse
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    MemberMembershipResponse
                                                    >
                                            > response) {

                                btnCreateMembership.setEnabled(
                                        true
                                );


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to create membership. HTTP "
                                                    + response.code()
                                    );

                                    return;
                                }


                                ApiResponse<
                                        MemberMembershipResponse
                                        > apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                MemberMembershipResponse membership =
                                        apiResponse.getData();


                                Toast.makeText(
                                        CreateMembershipActivity.this,
                                        "Membership "
                                                + safeText(
                                                membership
                                                        .getMembershipNumber()
                                        )
                                                + " created successfully.",
                                        Toast.LENGTH_LONG
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
                                                    MemberMembershipResponse
                                                    >
                                            > call,

                                    Throwable t) {

                                btnCreateMembership.setEnabled(
                                        true
                                );


                                showError(
                                        "Connection failed: "
                                                + t.getMessage()
                                );
                            }
                        }
                );
    }


    // =====================================================
    // CLEAR ERRORS
    // =====================================================

    private void clearErrors() {

        layoutBranch.setError(
                null
        );


        layoutPlan.setError(
                null
        );


        layoutStartDate.setError(
                null
        );


        layoutDiscount.setError(
                null
        );
    }


    // =====================================================
    // DEVICE VALIDATION
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
    // PARSE DISCOUNT
    // =====================================================

    private BigDecimal parseDiscount() {

        String value =
                txtDiscount.getText()
                        .toString()
                        .trim();


        if (value.isEmpty()) {

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


    // =====================================================
    // SAFE DECIMAL
    // =====================================================

    private BigDecimal safeDecimal(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }


    // =====================================================
    // FORMAT MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal value) {

        BigDecimal amount =
                safeDecimal(
                        value
                );


        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        Locale.US
                );


        formatter.setMinimumFractionDigits(
                2
        );


        formatter.setMaximumFractionDigits(
                2
        );


        return "Rs. "
                + formatter.format(
                amount
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
    // ERROR
    // =====================================================

    private void showError(
            String message) {

        Toast.makeText(
                this,
                message == null
                        ? "Something went wrong."
                        : message,
                Toast.LENGTH_LONG
        ).show();
    }
}