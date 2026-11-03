package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.MainDashboard.CreateMembershipActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.MemberMembershipBenefitAdapter;
import arafath65.minime.gymmanagementui.MainDashboard.dialog.AddMembershipItemsDialog;
import arafath65.minime.gymmanagementui.MainDashboard.dialog.MembershipHistoryDialog;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;
import arafath65.minime.gymmanagementui.model.MembershipRenewalRequest;
import arafath65.minime.gymmanagementui.model.MembershipRenewalResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBenefitResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberMembershipFragment
        extends Fragment {


    // =====================================================
    // ARGUMENTS
    // =====================================================

    private static final String ARG_MEMBER_ID =
            "memberId";

    private static final String ARG_MEMBER_NAME =
            "memberName";

    private static final String ARG_MEMBER_BRANCH_ID =
            "memberBranchId";


    // =====================================================
    // MEMBER
    // =====================================================

    private int memberId;

    private String memberName;

    private int memberBranchId;


    // =====================================================
    // API
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;


    // =====================================================
    // CURRENT MEMBERSHIP VIEWS
    // =====================================================

    private View cardCurrentMembership;

    private TextView txtMembershipNumber;

    private TextView txtPlanName;

    private TextView txtPlanCode;

    private TextView txtStartDate;

    private TextView txtEndDate;

    private TextView txtMembershipState;

    private TextView txtTotalLabel;

    private TextView txtTotalAmount;


    private TextView txtBalanceAmount;

    private LinearLayout layoutBalanceColumn;

    private TextView txtNoMembership;


    // =====================================================
    // MEMBERSHIP BUTTONS
    // =====================================================

    private MaterialButton btnCreateMembership;

    private MaterialButton btnAddToMembership;

    private MaterialButton btnRenewMembership;

    private MaterialButton btnCancelMembership;

    private MaterialButton btnMembershipHistory;



    // =====================================================
    // CURRENT PROGRAM / BENEFIT SECTIONS
    // =====================================================


    private View layoutBenefitsSection;


    // =====================================================
    // PROGRAM VIEWS
    // =====================================================




    // =====================================================
    // BENEFIT VIEWS
    // =====================================================

    private TextView txtNoBenefits;

    private RecyclerView recyclerMembershipBenefits;


    // =====================================================
    // ADAPTERS
    // =====================================================


    private MemberMembershipBenefitAdapter
            benefitAdapter;


    // =====================================================
    // MEMBERSHIPS
    // =====================================================

    private MemberMembershipResponse
            currentMembership;


    private final List<MemberMembershipResponse>
            allMemberships =
            new ArrayList<>();


    // =====================================================
    // DATE FORMAT
    // =====================================================

    private final DateTimeFormatter
            dateFormatter =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
            );


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MemberMembershipFragment() {
    }


    // =====================================================
    // NEW INSTANCE
    // =====================================================

    public static MemberMembershipFragment newInstance(
            int memberId,
            String memberName,
            Integer memberBranchId) {
        MemberMembershipFragment fragment = new MemberMembershipFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MEMBER_ID, memberId);
        args.putString(ARG_MEMBER_NAME, memberName);
        args.putInt(ARG_MEMBER_BRANCH_ID, memberBranchId == null ? 0 : memberBranchId);
        fragment.setArguments(args);
        return fragment;
    }


    // =====================================================
    // BACKWARD COMPATIBILITY
    // =====================================================

    public static MemberMembershipFragment newInstance(
            int memberId) {

        return newInstance(memberId, null, null);
    }


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


        if (getArguments() != null) {

            memberId =
                    getArguments()
                            .getInt(
                                    ARG_MEMBER_ID,
                                    0
                            );

            memberName =
                    getArguments()
                            .getString(
                                    ARG_MEMBER_NAME,
                                    ""
                            );

            memberBranchId = getArguments().getInt(
                    ARG_MEMBER_BRANCH_ID,
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
    // CREATE VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(

            @NonNull LayoutInflater inflater,

            @Nullable ViewGroup container,

            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_member_membership,
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


        initializeViews(view);
        setupButtons();
        loadMemberships();
    }


    // =====================================================
    // RESUME
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();


        if (memberId > 0
                && apiService != null
                && getView() != null) {

            loadMemberships();
        }
    }


    // =====================================================
    // INITIALIZE
    // =====================================================

    private void initializeViews(
            View view) {


        cardCurrentMembership =
                view.findViewById(
                        R.id.cardCurrentMembership
                );


        txtMembershipNumber =
                view.findViewById(
                        R.id.txtMembershipNumber
                );


        txtPlanName =
                view.findViewById(
                        R.id.txtPlanName
                );


        txtPlanCode =
                view.findViewById(
                        R.id.txtPlanCode
                );


        txtStartDate =
                view.findViewById(
                        R.id.txtStartDate
                );


        txtEndDate =
                view.findViewById(
                        R.id.txtEndDate
                );


        txtMembershipState =
                view.findViewById(
                        R.id.txtMembershipState
                );


        txtTotalLabel =
                view.findViewById(
                        R.id.txtTotalLabel
                );


        txtTotalAmount =
                view.findViewById(
                        R.id.txtTotalAmount
                );


        txtBalanceAmount =
                view.findViewById(
                        R.id.txtBalanceAmount
                );

        layoutBalanceColumn =
                view.findViewById(
                        R.id.layoutBalanceColumn
                );


        txtNoMembership =
                view.findViewById(
                        R.id.txtNoMembership
                );


        btnCreateMembership =
                view.findViewById(
                        R.id.btnCreateMembership
                );


        btnAddToMembership =
                view.findViewById(
                        R.id.btnAddToMembership
                );


        btnRenewMembership =
                view.findViewById(
                        R.id.btnRenewMembership
                );


        btnCancelMembership =
                view.findViewById(
                        R.id.btnCancelMembership
                );


        btnMembershipHistory =
                view.findViewById(
                        R.id.btnMembershipHistory
                );

        layoutBenefitsSection =
                view.findViewById(
                        R.id.layoutBenefitsSection
                );


        recyclerMembershipBenefits =
                view.findViewById(
                        R.id.recyclerMembershipBenefits
                );


        txtNoBenefits =
                view.findViewById(
                        R.id.txtNoBenefits
                );


        // =================================================
        // BENEFIT ADAPTER
        // =================================================

        benefitAdapter =
                new MemberMembershipBenefitAdapter();


        recyclerMembershipBenefits.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        recyclerMembershipBenefits.setAdapter(
                benefitAdapter
        );


        if (layoutBenefitsSection != null) {
            layoutBenefitsSection.setVisibility(View.GONE);
        }
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnCreateMembership.setOnClickListener(
                v -> openCreateMembership()
        );


        // Both the empty-membership action and Change Membership Plan
        // must open the same membership-plan selection screen.
        btnAddToMembership.setOnClickListener(
                v -> openCreateMembership()
        );


        btnRenewMembership.setOnClickListener(
                v -> openRenewMembershipDialog()
        );


        btnCancelMembership.setOnClickListener(
                v -> confirmCancelMembership()
        );



        btnMembershipHistory.setOnClickListener(
                v -> openMembershipHistory()
        );

    }

    // =====================================================
    // OPEN MEMBERSHIP HISTORY
    // =====================================================

    private void openMembershipHistory() {

        if (!isAdded()) {
            return;
        }


        if (memberId <= 0) {

            showError(
                    "Invalid member."
            );

            return;
        }


        String displayName =
                memberName;


        if (displayName == null
                || displayName.trim().isEmpty()) {

            displayName =
                    "Member";
        }


        MembershipHistoryDialog dialog =
                MembershipHistoryDialog.newInstance(
                        memberId,
                        displayName
                );


        dialog.show(
                getChildFragmentManager(),
                "MembershipHistoryDialog"
        );
    }


    // =====================================================
    // CREATE MEMBERSHIP
    // =====================================================

    private void openCreateMembership() {

        if (memberId <= 0) {
            showError("Invalid member.");
            return;
        }

        if (memberBranchId <= 0) {
            showError(
                    "Member branch information is missing. "
                            + "Please check the member record."
            );
            return;
        }

        Intent intent = new Intent(
                requireContext(),
                CreateMembershipActivity.class
        );

        intent.putExtra("memberId", memberId);
        intent.putExtra("memberName", memberName);
        intent.putExtra("memberBranchId", memberBranchId);

        startActivity(intent);
    }


    // =====================================================
    // OPEN ADD ITEMS
    // =====================================================

    private void openAddMembershipDialog() {

        if (currentMembership == null) {

            return;
        }


        String state =
                safeText(
                        currentMembership
                                .getMembershipState()
                );


        if (!"ACTIVE".equalsIgnoreCase(state)
                && !"FROZEN".equalsIgnoreCase(state)) {

            return;
        }


        Integer memberMembershipId =
                currentMembership
                        .getMemberMembershipId();


        if (memberMembershipId == null
                || memberMembershipId <= 0) {

            showError(
                    "Membership information is unavailable."
            );

            return;
        }


        Integer branchId =
                currentMembership.getBranchId();


        Integer membershipPlanId =
                currentMembership
                        .getMembershipPlanId();


        String membershipNumber =
                currentMembership
                        .getMembershipNumber();


        String displayName =
                memberName;


        if (displayName == null
                || displayName.trim().isEmpty()) {

            displayName =
                    "Member";
        }


        AddMembershipItemsDialog dialog =
                AddMembershipItemsDialog.newInstance(
                        memberId,
                        memberMembershipId,
                        displayName,
                        membershipNumber,
                        branchId,
                        membershipPlanId
                );


        dialog.setOnItemAddedListener(
                new AddMembershipItemsDialog
                        .OnItemAddedListener() {

                    @Override
                    public void onItemAdded() {

                        loadMemberships();
                    }
                }
        );


        dialog.show(
                getChildFragmentManager(),
                "AddMembershipItemsDialog"
        );
    }


    // =====================================================
    // LOAD MEMBERSHIPS
    // =====================================================

    private void loadMemberships() {

        if (!isAdded()
                || memberId <= 0) {

            return;
        }


        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


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
                .getMemberMemberships(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<
                                                MemberMembershipResponse>>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse>>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showNoMembership();

                                    return;
                                }


                                ApiResponse<
                                        List<
                                                MemberMembershipResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null
                                        || apiResponse.getData().isEmpty()) {

                                    showNoMembership();

                                    return;
                                }


                                allMemberships.clear();

                                allMemberships.addAll(
                                        apiResponse.getData()
                                );


                                /*
                                 * Only ACTIVE or FROZEN are
                                 * considered current.
                                 *
                                 * EXPIRED and CANCELLED remain
                                 * historical records and are
                                 * displayed through Membership
                                 * History.
                                 */

                                currentMembership =
                                        findCurrentMembership(
                                                allMemberships
                                        );


                                if (currentMembership == null) {

                                    showNoActiveMembership();

                                } else {

                                    displayMembership(
                                            currentMembership
                                    );
                                }
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse>>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                showNoMembership();
                            }
                        }
                );
    }


    // =====================================================
    // FIND CURRENT MEMBERSHIP
    // =====================================================

    private MemberMembershipResponse
    findCurrentMembership(
            List<MemberMembershipResponse>
                    memberships) {


        if (memberships == null
                || memberships.isEmpty()) {

            return null;
        }


        /*
         * Prefer ACTIVE/FROZEN. If neither exists, the most recent
         * EXPIRED membership is returned so it can be renewed. CANCELLED
         * memberships remain history-only.
         */

        // Prefer a membership that is currently active or frozen.
        for (MemberMembershipResponse membership : memberships) {
            if (membership == null) {
                continue;
            }

            String state = safeText(membership.getMembershipState());

            if ("ACTIVE".equalsIgnoreCase(state)
                    || "FROZEN".equalsIgnoreCase(state)) {
                return membership;
            }
        }

        // If there is no active/frozen membership, show the most recent
        // expired membership so the user can renew it from this screen.
        MemberMembershipResponse latestExpired = null;
        LocalDate latestExpiredEndDate = null;

        for (MemberMembershipResponse membership : memberships) {
            if (membership == null) {
                continue;
            }

            String state = safeText(membership.getMembershipState());
            if (!"EXPIRED".equalsIgnoreCase(state)) {
                continue;
            }

            LocalDate endDate = null;
            try {
                if (membership.getEndDate() != null
                        && !membership.getEndDate().trim().isEmpty()) {
                    endDate = LocalDate.parse(
                            membership.getEndDate(),
                            dateFormatter
                    );
                }
            } catch (Exception ignored) {
                // If the date is invalid, still allow an EXPIRED record
                // to be considered as a fallback.
            }

            if (latestExpired == null
                    || (endDate != null
                    && (latestExpiredEndDate == null
                    || endDate.isAfter(latestExpiredEndDate)))) {
                latestExpired = membership;
                latestExpiredEndDate = endDate;
            }
        }

        return latestExpired;
    }


    // =====================================================
    // DISPLAY CURRENT MEMBERSHIP
    // =====================================================

    private void displayMembership(
            MemberMembershipResponse membership) {

        if (!isAdded()
                || membership == null) {

            return;
        }


        cardCurrentMembership.setVisibility(
                View.VISIBLE
        );


        txtNoMembership.setVisibility(
                View.GONE
        );


        btnCreateMembership.setVisibility(
                View.GONE
        );


        txtMembershipNumber.setText(
                safeText(
                        membership
                                .getMembershipNumber()
                )
        );


        txtPlanName.setText(
                safeText(
                        membership
                                .getPlanName()
                )
        );


        txtPlanCode.setText(
                safeText(
                        membership
                                .getPlanCode()
                )
        );


        txtStartDate.setText(
                safeText(
                        membership
                                .getStartDate()
                )
        );


        txtEndDate.setText(
                safeText(
                        membership
                                .getEndDate()
                )
        );


        String state =
                safeText(
                        membership
                                .getMembershipState()
                );


        txtMembershipState.setText(
                state
        );


        // Default to fixed-duration presentation until the plan details load.
        txtTotalLabel.setText("TOTAL");
        txtTotalAmount.setText(formatMoney(membership.getTotalAmount()));
        txtTotalAmount.setVisibility(View.VISIBLE);
        txtBalanceAmount.setText(formatMoney(membership.getBalanceAmount()));
        txtBalanceAmount.setVisibility(View.VISIBLE);


        loadMembershipBillingPresentation(membership);


        setupMembershipActions(
                state
        );

        // Apply the date-based enabled/disabled state after button visibility
        // and default state have been configured.
        updateRenewButtonState(membership);

        if (layoutBenefitsSection != null) {
            layoutBenefitsSection.setVisibility(View.VISIBLE);
        }

        Integer membershipId =
                membership
                        .getMemberMembershipId();


        if (membershipId == null || membershipId <= 0) {
            showBenefitEmpty();
            return;
        }

        loadMembershipBenefits(membershipId);
    }

    private void updateRenewButtonState(
            MemberMembershipResponse membership) {

        if (btnRenewMembership == null || membership == null) {
            return;
        }

        String state = safeText(membership.getMembershipState());
        boolean expired = "EXPIRED".equalsIgnoreCase(state);

        try {
            String endDate = membership.getEndDate();

            // The end date itself is still valid. Renewal becomes available
            // from the following day, or whenever the backend marks it EXPIRED.
            expired = expired || (endDate != null
                    && !endDate.trim().isEmpty()
                    && LocalDate.parse(endDate, dateFormatter)
                    .isBefore(LocalDate.now()));

        } catch (Exception ignored) {
            // Keep the EXPIRED state as the fallback if the date is invalid.
        }

        btnRenewMembership.setEnabled(expired);
        btnRenewMembership.setAlpha(expired ? 1.0f : 0.45f);
    }


    // =====================================================
    // MONTHLY / FIXED PLAN PRESENTATION
    // =====================================================

    /**
     * Loads the plan metadata because MemberMembershipResponse intentionally
     * contains the financial snapshot but not billingType/billingFrequency.
     * Monthly plans display Start Date, next monthly date and Monthly Fee;
     * fixed-duration plans display Start Date, End Date, Total and Balance.
     */
    private void loadMembershipBillingPresentation(
            MemberMembershipResponse membership) {

        if (!isAdded() || membership == null
                || membership.getMembershipPlanId() == null) {
            return;
        }

        String tenantNo = deviceStorage.getTenantNo();
        String deviceToken = deviceStorage.getDeviceToken();

        if (tenantNo == null || tenantNo.trim().isEmpty()
                || deviceToken == null || deviceToken.trim().isEmpty()) {
            return;
        }

        final Integer displayedMembershipId = membership.getMemberMembershipId();

        apiService.getMembershipPlan(
                tenantNo,
                deviceToken,
                membership.getMembershipPlanId()
        ).enqueue(new Callback<ApiResponse<MembershipPlanResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<MembershipPlanResponse>> call,
                    Response<ApiResponse<MembershipPlanResponse>> response) {

                if (!isAdded() || currentMembership == null
                        || !java.util.Objects.equals(
                        currentMembership.getMemberMembershipId(),
                        displayedMembershipId)) {
                    return;
                }

                if (!response.isSuccessful() || response.body() == null
                        || !response.body().isSuccess()
                        || response.body().getData() == null) {
                    // Keep the fixed-duration default if plan metadata is unavailable.
                    return;
                }

                MembershipPlanResponse plan = response.body().getData();
                String billingType = plan.getBillingType() == null
                        ? "" : plan.getBillingType().trim().toUpperCase(Locale.US);
                String billingFrequency = plan.getBillingFrequency() == null
                        ? "" : plan.getBillingFrequency().trim().toUpperCase(Locale.US);
                String durationType = plan.getDurationType() == null
                        ? "" : plan.getDurationType().trim().toUpperCase(Locale.US);

                boolean monthlyPlan = billingFrequency.startsWith("MONTH")
                        || billingType.contains("MONTHLY")
                        || ("RECURRING".equals(billingType)
                        && ("MONTH".equals(durationType)
                        || "MONTHS".equals(durationType)));

                if (monthlyPlan) {
                    txtTotalLabel.setText("MONTHLY FEE");
                    BigDecimal monthlyFee = membership.getPlanPrice();
                    if (monthlyFee == null) {
                        monthlyFee = getBranchPlanFee(plan, membership.getBranchId());
                    }
                    txtTotalAmount.setText(formatMoney(monthlyFee));
                    txtTotalAmount.setVisibility(View.VISIBLE);
                    layoutBalanceColumn.setVisibility(View.GONE);

                    // Monthly memberships show the next monthly date, based on start date.
                    try {
                        LocalDate start = LocalDate.parse(
                                membership.getStartDate(), dateFormatter);
                        txtEndDate.setText(start.plusMonths(1).format(dateFormatter));
                    } catch (Exception ignored) {
                        // Preserve the API end date if the start date is not parseable.
                    }
                } else {
                    txtTotalLabel.setText("TOTAL");
                    txtTotalAmount.setText(formatMoney(membership.getTotalAmount()));
                    txtTotalAmount.setVisibility(View.VISIBLE);
                    txtBalanceAmount.setText(formatMoney(membership.getBalanceAmount()));
                    layoutBalanceColumn.setVisibility(View.VISIBLE);
                    txtBalanceAmount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<MembershipPlanResponse>> call,
                    Throwable t) {
                // Preserve the default presentation if plan metadata cannot be fetched.
            }
        });
    }

    private BigDecimal getBranchPlanFee(
            MembershipPlanResponse plan,
            Integer branchId) {

        if (plan == null || plan.getBranchAssignments() == null) {
            return plan == null ? null : BigDecimal.ZERO;
        }

        for (arafath65.minime.gymmanagementui.model.MembershipPlanBranchResponse assignment
                : plan.getBranchAssignments()) {
            if (assignment != null && Boolean.TRUE.equals(assignment.getStatus())
                    && (branchId == null || branchId.equals(assignment.getBranchId()))) {
                return assignment.getPrice();
            }
        }

        return plan.getBranchAssignments().isEmpty()
                ? BigDecimal.ZERO : plan.getBranchAssignments().get(0).getPrice();
    }


    // =====================================================
    // MEMBERSHIP ACTIONS
    // =====================================================

    private void setupMembershipActions(
            String state) {


        btnAddToMembership.setVisibility(
                View.GONE
        );


        btnRenewMembership.setVisibility(
                View.GONE
        );


        btnCancelMembership.setVisibility(
                View.GONE
        );


        btnCreateMembership.setVisibility(
                View.GONE
        );


        btnAddToMembership.setEnabled(
                false
        );


        btnRenewMembership.setEnabled(
                false
        );


        btnCancelMembership.setEnabled(
                false
        );


        btnCreateMembership.setEnabled(
                false
        );


        /*
         * ACTIVE
         */

        if ("ACTIVE".equalsIgnoreCase(state)) {

            btnAddToMembership.setVisibility(
                    View.VISIBLE
            );


            btnRenewMembership.setVisibility(
                    View.VISIBLE
            );


            btnCancelMembership.setVisibility(
                    View.VISIBLE
            );


            btnAddToMembership.setEnabled(
                    true
            );


            btnRenewMembership.setEnabled(
                    true
            );


            btnCancelMembership.setEnabled(
                    true
            );


            return;
        }


        /*
         * FROZEN
         */

        if ("FROZEN".equalsIgnoreCase(state)) {

            btnAddToMembership.setVisibility(View.VISIBLE);
            btnRenewMembership.setVisibility(View.VISIBLE);
            btnCancelMembership.setVisibility(View.VISIBLE);

            btnAddToMembership.setEnabled(true);
            btnRenewMembership.setEnabled(false);
            btnCancelMembership.setEnabled(true);
            return;
        }

        // Expired memberships are displayed as a fallback when no active
        // membership exists. Only Renew is available for an expired record.
        if ("EXPIRED".equalsIgnoreCase(state)) {
            btnRenewMembership.setVisibility(View.VISIBLE);
            btnRenewMembership.setEnabled(true);
            btnRenewMembership.setAlpha(1.0f);
        }
    }


    // =====================================================
    // NO ACTIVE MEMBERSHIP
    // =====================================================

    private void showNoActiveMembership() {

        currentMembership =
                null;


        cardCurrentMembership.setVisibility(
                View.GONE
        );


        txtNoMembership.setVisibility(
                View.VISIBLE
        );


        txtNoMembership.setText(
                "This member currently has no active membership."
        );


        btnCreateMembership.setVisibility(
                View.VISIBLE
        );


        btnCreateMembership.setEnabled(
                true
        );

        if (btnMembershipHistory != null) {
            btnMembershipHistory.setVisibility(
                    View.VISIBLE
            );
        }

        btnAddToMembership.setVisibility(
                View.GONE
        );


        btnRenewMembership.setVisibility(
                View.GONE
        );


        btnCancelMembership.setVisibility(
                View.GONE
        );


        btnAddToMembership.setEnabled(
                false
        );


        btnRenewMembership.setEnabled(
                false
        );


        btnCancelMembership.setEnabled(
                false
        );


        if (layoutBenefitsSection != null) {
            layoutBenefitsSection.setVisibility(View.GONE);
        }
        showBenefitEmpty();
    }


    // =====================================================
    // NO MEMBERSHIP AT ALL
    // =====================================================

    private void showNoMembership() {

        allMemberships.clear();

        currentMembership =
                null;


        cardCurrentMembership.setVisibility(
                View.GONE
        );


        txtNoMembership.setVisibility(
                View.VISIBLE
        );


        txtNoMembership.setText(
                "This member currently has no active membership."
        );


        btnCreateMembership.setVisibility(
                View.VISIBLE
        );


        btnCreateMembership.setEnabled(
                true
        );

        if (btnMembershipHistory != null) {
            btnMembershipHistory.setVisibility(
                    View.VISIBLE
            );
        }

        btnAddToMembership.setVisibility(
                View.GONE
        );


        btnRenewMembership.setVisibility(
                View.GONE
        );


        btnCancelMembership.setVisibility(
                View.GONE
        );


        btnAddToMembership.setEnabled(
                false
        );


        btnRenewMembership.setEnabled(
                false
        );


        btnCancelMembership.setEnabled(
                false
        );


        if (layoutBenefitsSection != null) {
            layoutBenefitsSection.setVisibility(View.GONE);
        }
        showBenefitEmpty();
    }


    // =====================================================
    // RENEW MEMBERSHIP
    // =====================================================

    private void openRenewMembershipDialog() {

        if (currentMembership == null) {

            return;
        }


        String state =
                safeText(
                        currentMembership
                                .getMembershipState()
                );


        if (!"ACTIVE".equalsIgnoreCase(state)
                && !"FROZEN".equalsIgnoreCase(state)
                && !"EXPIRED".equalsIgnoreCase(state)) {

            return;
        }


        Integer membershipId =
                currentMembership
                        .getMemberMembershipId();


        if (membershipId == null
                || membershipId <= 0) {

            showError(
                    "Membership information is unavailable."
            );

            return;
        }


        loadPlanForRenewal(
                membershipId
        );
    }


    // =====================================================
    // LOAD PLAN FOR RENEWAL
    // =====================================================

    private void loadPlanForRenewal(
            Integer membershipId) {

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        Integer planId =
                currentMembership
                        .getMembershipPlanId();


        if (planId == null) {

            showError(
                    "Membership plan information is unavailable."
            );

            return;
        }


        apiService
                .getMembershipPlan(
                        tenantNo,
                        deviceToken,
                        planId
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

                                if (!isAdded()) {
                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().isSuccess()
                                        || response.body().getData() == null) {

                                    showError(
                                            "Unable to load membership plan."
                                    );

                                    return;
                                }


                                MembershipPlanResponse plan =
                                        response.body()
                                                .getData();


                                showRenewalDialog(
                                        membershipId,
                                        plan
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MembershipPlanResponse>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
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
    // RENEWAL DIALOG
    // =====================================================

    private void showRenewalDialog(

            Integer membershipId,

            MembershipPlanResponse plan) {


        LinearLayout root =
                new LinearLayout(
                        requireContext()
                );


        root.setOrientation(
                LinearLayout.VERTICAL
        );


        int padding =
                dp(20);


        root.setPadding(
                padding,
                padding,
                padding,
                dp(8)
        );


        root.addView(
                createDialogInfo(
                        "Membership",
                        safeText(
                                currentMembership
                                        .getMembershipNumber()
                        )
                )
        );


        root.addView(
                createDialogInfo(
                        "Plan",
                        safeText(
                                currentMembership
                                        .getPlanName()
                        )
                )
        );


        root.addView(
                createDialogInfo(
                        "Current End Date",
                        safeText(
                                currentMembership
                                        .getEndDate()
                        )
                )
        );


        TextInputLayout durationLayout =
                new TextInputLayout(
                        requireContext()
                );


        durationLayout.setHint(
                "Duration"
        );


        durationLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        AutoCompleteTextView durationType =
                new AutoCompleteTextView(
                        requireContext()
                );


        String[] durationTypes = {
                "DAYS",
                "MONTHS",
                "YEARS"
        };


        durationType.setAdapter(
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        durationTypes
                )
        );


        String defaultDuration =
                safeText(
                        plan.getDurationType()
                );


        if ("-".equals(defaultDuration)
                || "UNLIMITED".equalsIgnoreCase(
                defaultDuration)) {

            defaultDuration =
                    "MONTHS";
        }


        durationType.setText(
                defaultDuration,
                false
        );


        durationLayout.addView(
                durationType
        );


        LinearLayout.LayoutParams durationParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        durationParams.topMargin =
                dp(12);


        root.addView(
                durationLayout,
                durationParams
        );


        TextInputLayout valueLayout =
                new TextInputLayout(
                        requireContext()
                );


        valueLayout.setHint(
                "Duration Value"
        );


        valueLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        TextInputEditText durationValue =
                new TextInputEditText(
                        requireContext()
                );


        durationValue.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );


        Integer planDuration =
                plan.getDurationValue();


        durationValue.setText(
                planDuration == null
                        ? "1"
                        : String.valueOf(
                        planDuration
                )
        );


        valueLayout.addView(
                durationValue
        );


        LinearLayout.LayoutParams valueParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        valueParams.topMargin =
                dp(12);


        root.addView(
                valueLayout,
                valueParams
        );


        TextInputLayout discountLayout =
                new TextInputLayout(
                        requireContext()
                );


        discountLayout.setHint(
                "Discount"
        );


        discountLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        TextInputEditText discount =
                new TextInputEditText(
                        requireContext()
                );


        discount.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );


        discount.setText(
                "0.00"
        );


        discountLayout.addView(
                discount
        );


        LinearLayout.LayoutParams discountParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        discountParams.topMargin =
                dp(12);


        root.addView(
                discountLayout,
                discountParams
        );


        TextInputLayout dateLayout =
                new TextInputLayout(
                        requireContext()
                );


        dateLayout.setHint(
                "Renewal Date"
        );


        dateLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        TextInputEditText renewalDate =
                new TextInputEditText(
                        requireContext()
                );


        renewalDate.setFocusable(
                false
        );


        renewalDate.setClickable(
                true
        );


        renewalDate.setText(
                LocalDate.now()
                        .format(
                                dateFormatter
                        )
        );


        dateLayout.addView(
                renewalDate
        );


        renewalDate.setOnClickListener(
                v -> showDatePicker(
                        renewalDate
                )
        );


        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        dateParams.topMargin =
                dp(12);


        root.addView(
                dateLayout,
                dateParams
        );


        TextInputLayout notesLayout =
                new TextInputLayout(
                        requireContext()
                );


        notesLayout.setHint(
                "Notes"
        );


        notesLayout.setBoxBackgroundMode(
                TextInputLayout.BOX_BACKGROUND_OUTLINE
        );


        TextInputEditText notes =
                new TextInputEditText(
                        requireContext()
                );


        notes.setMinLines(
                2
        );


        notes.setGravity(
                android.view.Gravity.TOP
        );


        notesLayout.addView(
                notes
        );


        LinearLayout.LayoutParams notesParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        notesParams.topMargin =
                dp(12);


        root.addView(
                notesLayout,
                notesParams
        );


        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(
                        requireContext()
                );


        builder.setTitle(
                "Renew Membership"
        );


        builder.setView(
                root
        );


        builder.setNegativeButton(
                "Cancel",
                null
        );


        builder.setPositiveButton(
                "Renew Membership",
                null
        );


        androidx.appcompat.app.AlertDialog dialog =
                builder.create();


        dialog.setOnShowListener(
                d -> {

                    android.widget.Button positiveButton =
                            dialog.getButton(
                                    android.content.DialogInterface
                                            .BUTTON_POSITIVE
                            );


                    positiveButton.setOnClickListener(
                            v -> {

                                String type =
                                        durationType
                                                .getText()
                                                .toString()
                                                .trim()
                                                .toUpperCase(
                                                        Locale.US
                                                );


                                String valueText =
                                        durationValue
                                                .getText()
                                                .toString()
                                                .trim();


                                if (valueText.isEmpty()) {

                                    valueLayout.setError(
                                            "Duration value is required."
                                    );

                                    return;
                                }


                                int value;


                                try {

                                    value =
                                            Integer.parseInt(
                                                    valueText
                                            );

                                } catch (Exception e) {

                                    valueLayout.setError(
                                            "Invalid duration value."
                                    );

                                    return;
                                }


                                if (value <= 0) {

                                    valueLayout.setError(
                                            "Duration must be greater than zero."
                                    );

                                    return;
                                }


                                BigDecimal discountAmount;


                                try {

                                    String discountText =
                                            discount
                                                    .getText()
                                                    .toString()
                                                    .trim();


                                    if (discountText.isEmpty()) {

                                        discountAmount =
                                                BigDecimal.ZERO;

                                    } else {

                                        discountAmount =
                                                new BigDecimal(
                                                        discountText
                                                );
                                    }

                                } catch (Exception e) {

                                    discountLayout.setError(
                                            "Invalid discount."
                                    );

                                    return;
                                }


                                if (discountAmount.signum()
                                        < 0) {

                                    discountLayout.setError(
                                            "Discount cannot be negative."
                                    );

                                    return;
                                }


                                String selectedDate =
                                        renewalDate
                                                .getText()
                                                .toString()
                                                .trim();


                                MembershipRenewalRequest
                                        request =
                                        new MembershipRenewalRequest();


                                request.setMemberMembershipId(
                                        membershipId
                                );


                                request.setRenewalDate(
                                        selectedDate
                                );


                                request.setDurationType(
                                        type
                                );


                                request.setDurationValue(
                                        value
                                );


                                request.setDiscountAmount(
                                        discountAmount
                                );


                                request.setNotes(
                                        notes
                                                .getText()
                                                .toString()
                                                .trim()
                                );


                                positiveButton.setEnabled(
                                        false
                                );


                                createRenewal(
                                        request,
                                        dialog,
                                        positiveButton
                                );
                            }
                    );
                }
        );


        dialog.show();
    }


    // =====================================================
    // CREATE RENEWAL
    // =====================================================

    private void createRenewal(

            MembershipRenewalRequest request,

            androidx.appcompat.app.AlertDialog dialog,

            android.widget.Button positiveButton) {


        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        apiService
                .createMembershipRenewal(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MembershipRenewalResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MembershipRenewalResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MembershipRenewalResponse>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }


                                positiveButton.setEnabled(
                                        true
                                );


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showError(
                                            "Unable to renew membership. HTTP "
                                                    + response.code()
                                    );

                                    return;
                                }


                                ApiResponse<
                                        MembershipRenewalResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to renew membership."
                                            )
                                    );

                                    return;
                                }


                                MembershipRenewalResponse renewal =
                                        apiResponse.getData();


                                dialog.dismiss();


                                Toast.makeText(
                                        requireContext(),
                                        "Membership renewed successfully.\n"
                                                + "Renewal: "
                                                + safeText(
                                                renewal
                                                        .getRenewalNumber()
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();


                                loadMemberships();
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MembershipRenewalResponse>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                positiveButton.setEnabled(
                                        true
                                );


                                showError(
                                        "Connection failed: "
                                                + safeMessage(
                                                t == null
                                                        ? null
                                                        : t.getMessage(),
                                                "Unknown error."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // CANCEL CONFIRMATION
    // =====================================================

    private void confirmCancelMembership() {

        if (currentMembership == null) {

            return;
        }


        String state =
                safeText(
                        currentMembership
                                .getMembershipState()
                );


        if (!"ACTIVE".equalsIgnoreCase(state)
                && !"FROZEN".equalsIgnoreCase(state)) {

            return;
        }


        String membershipNumber =
                safeText(
                        currentMembership
                                .getMembershipNumber()
                );


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle(
                        "Cancel Membership"
                )
                .setMessage(
                        "Are you sure you want to cancel membership "
                                + membershipNumber
                                + "?"
                                + "\n\n"
                                + "This action will mark the membership as CANCELLED."
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .setPositiveButton(
                        "Yes, Cancel",
                        (dialog, which) ->
                                cancelMembership()
                )
                .show();
    }


    // =====================================================
    // CANCEL MEMBERSHIP
    // =====================================================

    private void cancelMembership() {

        if (currentMembership == null) {

            return;
        }


        Integer membershipId =
                currentMembership
                        .getMemberMembershipId();


        if (membershipId == null
                || membershipId <= 0) {

            showError(
                    "Membership information is unavailable."
            );

            return;
        }


        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        btnCancelMembership.setEnabled(
                false
        );


        apiService
                .cancelMembership(
                        tenantNo,
                        deviceToken,
                        membershipId
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
                                        || response.body() == null) {

                                    showError(
                                            "Unable to cancel membership. HTTP "
                                                    + response.code()
                                    );

                                    btnCancelMembership.setEnabled(
                                            true
                                    );

                                    return;
                                }


                                ApiResponse<
                                        MemberMembershipResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    showError(
                                            safeMessage(
                                                    apiResponse.getMessage(),
                                                    "Unable to cancel membership."
                                            )
                                    );

                                    btnCancelMembership.setEnabled(
                                            true
                                    );

                                    return;
                                }


                                Toast.makeText(
                                        requireContext(),
                                        "Membership cancelled successfully.",
                                        Toast.LENGTH_LONG
                                ).show();


                                loadMemberships();
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


                                btnCancelMembership.setEnabled(
                                        true
                                );


                                showError(
                                        "Connection failed: "
                                                + safeMessage(
                                                t == null
                                                        ? null
                                                        : t.getMessage(),
                                                "Unknown error."
                                        )
                                );
                            }
                        }
                );
    }


    // =====================================================
    // LOAD MEMBERSHIP PROGRAMS
// LOAD MEMBERSHIP BENEFITS
// =====================================================

    private void loadMembershipBenefits(Integer memberMembershipId) {

        String tenantNo = deviceStorage.getTenantNo();
        String deviceToken = deviceStorage.getDeviceToken();

        if (tenantNo == null || tenantNo.trim().isEmpty()
                || deviceToken == null || deviceToken.trim().isEmpty()
                || memberMembershipId == null || memberMembershipId <= 0) {

            showBenefitEmpty();
            return;
        }

        apiService.getMembershipBenefits(
                tenantNo,
                deviceToken,
                memberMembershipId
        ).enqueue(new Callback<
                ApiResponse<List<MemberMembershipBenefitResponse>>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<List<MemberMembershipBenefitResponse>>> call,
                    Response<ApiResponse<List<MemberMembershipBenefitResponse>>> response) {

                if (!isAdded()) {
                    return;
                }

                List<MemberMembershipBenefitResponse> assignedBenefits =
                        new ArrayList<>();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    assignedBenefits.addAll(response.body().getData());
                }

                loadIncludedPlanBenefits(
                        tenantNo,
                        deviceToken,
                        assignedBenefits
                );
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<List<MemberMembershipBenefitResponse>>> call,
                    Throwable t) {

                if (!isAdded()) {
                    return;
                }

                // Still attempt to display plan INCLUDED benefits.
                loadIncludedPlanBenefits(
                        tenantNo,
                        deviceToken,
                        new ArrayList<>()
                );
            }
        });
    }


// =====================================================
// LOAD INCLUDED BENEFITS FROM MEMBERSHIP PLAN
// =====================================================

    private void loadIncludedPlanBenefits(
            String tenantNo,
            String deviceToken,
            List<MemberMembershipBenefitResponse> assignedBenefits) {

        if (!isAdded()) {
            return;
        }

        if (currentMembership == null
                || currentMembership.getMembershipPlanId() == null
                || currentMembership.getMembershipPlanId() <= 0) {

            displayCombinedBenefits(assignedBenefits);
            return;
        }

        Integer membershipPlanId =
                currentMembership.getMembershipPlanId();

        apiService.getMembershipPlanBenefits(
                tenantNo,
                deviceToken,
                membershipPlanId
        ).enqueue(new Callback<
                ApiResponse<List<MembershipPlanBenefitResponse>>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<List<MembershipPlanBenefitResponse>>> call,
                    Response<ApiResponse<List<MembershipPlanBenefitResponse>>> response) {

                if (!isAdded()) {
                    return;
                }

                List<MemberMembershipBenefitResponse> combined =
                        new ArrayList<>(assignedBenefits);

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    for (MembershipPlanBenefitResponse planBenefit
                            : response.body().getData()) {

                        if (planBenefit == null
                                || planBenefit.getBenefitId() == null
                                || !Boolean.TRUE.equals(planBenefit.getStatus())
                                || !"INCLUDED".equalsIgnoreCase(
                                planBenefit.getBenefitType())) {

                            continue;
                        }

                        boolean alreadyDisplayed = false;

                        for (MemberMembershipBenefitResponse assigned
                                : combined) {

                            if (assigned != null
                                    && planBenefit.getBenefitId().equals(
                                    assigned.getBenefitId())) {

                                alreadyDisplayed = true;
                                break;
                            }
                        }

                        if (alreadyDisplayed) {
                            continue;
                        }

                        MemberMembershipBenefitResponse displayBenefit =
                                new MemberMembershipBenefitResponse();

                        // No database assignment ID: this is a display-only
                        // benefit inherited from the membership plan.
                        displayBenefit.setMemberMembershipBenefitId(null);
                        displayBenefit.setMemberMembershipId(
                                currentMembership.getMemberMembershipId()
                        );
                        displayBenefit.setBenefitId(planBenefit.getBenefitId());
                        displayBenefit.setBenefitCode(planBenefit.getBenefitCode());
                        displayBenefit.setBenefitName(planBenefit.getBenefitName());
                        displayBenefit.setSourceType("INCLUDED");
                        displayBenefit.setFee(planBenefit.getFee());
                        displayBenefit.setStartDate(
                                currentMembership.getStartDate()
                        );
                        displayBenefit.setEndDate(
                                currentMembership.getEndDate()
                        );
                        displayBenefit.setStatus(true);

                        combined.add(displayBenefit);
                    }
                }

                displayCombinedBenefits(combined);
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<List<MembershipPlanBenefitResponse>>> call,
                    Throwable t) {

                if (!isAdded()) {
                    return;
                }

                // Preserve assigned benefits even if plan-benefit loading fails.
                displayCombinedBenefits(assignedBenefits);
            }
        });
    }


// =====================================================
// DISPLAY ASSIGNED + INCLUDED BENEFITS
// =====================================================

    private void displayCombinedBenefits(
            List<MemberMembershipBenefitResponse> benefits) {

        if (!isAdded()) {
            return;
        }

        if (benefits == null || benefits.isEmpty()) {
            showBenefitEmpty();
            return;
        }

        benefitAdapter.setBenefits(benefits);

        txtNoBenefits.setVisibility(View.GONE);

        recyclerMembershipBenefits.setVisibility(View.VISIBLE);
    }


    // =====================================================
    // BENEFIT EMPTY
    // =====================================================

    private void showBenefitEmpty() {

        if (benefitAdapter != null) {

            benefitAdapter.setBenefits(
                    null
            );
        }


        if (recyclerMembershipBenefits != null) {

            recyclerMembershipBenefits.setVisibility(
                    View.GONE
            );
        }


        if (txtNoBenefits != null) {

            txtNoBenefits.setVisibility(
                    View.VISIBLE
            );


            txtNoBenefits.setText(
                    "No benefits assigned."
            );
        }
    }


    // =====================================================
    // DATE PICKER
    // =====================================================

    private void showDatePicker(
            TextInputEditText target) {

        LocalDate currentDate;


        try {

            currentDate =
                    LocalDate.parse(
                            target.getText()
                                    .toString(),
                            dateFormatter
                    );

        } catch (Exception e) {

            currentDate =
                    LocalDate.now();
        }


        DatePickerDialog picker =
                new DatePickerDialog(
                        requireContext(),
                        (view, year, month, day) -> {

                            LocalDate selected =
                                    LocalDate.of(
                                            year,
                                            month + 1,
                                            day
                                    );


                            target.setText(
                                    selected.format(
                                            dateFormatter
                                    )
                            );
                        },
                        currentDate.getYear(),
                        currentDate.getMonthValue() - 1,
                        currentDate.getDayOfMonth()
                );


        picker.show();
    }


    // =====================================================
    // DIALOG INFO
    // =====================================================

    private TextView createDialogInfo(
            String title,
            String value) {

        TextView textView =
                new TextView(
                        requireContext()
                );


        textView.setText(
                title
                        + "\n"
                        + value
        );


        textView.setTextSize(
                13
        );


        textView.setTextColor(
                requireContext()
                        .getColor(
                                R.color.text_primary
                        )
        );


        textView.setPadding(
                0,
                dp(6),
                0,
                dp(6)
        );


        return textView;
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
                safeMessage(
                        message,
                        "Something went wrong."
                ),
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
    // MONEY
    // =====================================================

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }

        java.text.DecimalFormat formatter =
                new java.text.DecimalFormat(
                        "#,##0.00",
                        new java.text.DecimalFormatSymbols(
                                java.util.Locale.US
                        )
                );

        return "Rs. " + formatter.format(value);
    }


    // =====================================================
    // DP
    // =====================================================

    private int dp(int value) {

        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }


    // =====================================================
    // REFRESH
    // =====================================================

    public void refreshMembership() {

        if (!isAdded()) {
            return;
        }


        loadMemberships();
    }
}