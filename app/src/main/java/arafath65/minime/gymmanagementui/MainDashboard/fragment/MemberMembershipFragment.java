package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.MainDashboard.CreateMembershipActivity;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberMembershipFragment
        extends Fragment {


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

    private TextView txtMembershipNumber;
    private TextView txtMembershipState;

    private TextView txtPlanName;
    private TextView txtPlanCode;
    private TextView txtBranchName;

    private TextView txtStartDate;
    private TextView txtEndDate;

    private TextView txtPlanPrice;
    private TextView txtJoiningFee;
    private TextView txtDiscountAmount;
    private TextView txtTotalAmount;
    private TextView txtPaidAmount;
    private TextView txtBalanceAmount;

    private TextView txtFreezeDays;
    private TextView txtRenewalCount;
    private TextView txtMembershipNotes;

    private LinearLayout layoutNoMembership;

    private View btnPayment;
    private View btnFreeze;
    private View btnRenew;
    private View btnCreateMembership;


    // =====================================================
    // CURRENT MEMBERSHIP
    // =====================================================

    private MemberMembershipResponse currentMembership;


    // =====================================================
    // CREATE
    // =====================================================

    public static MemberMembershipFragment newInstance(
            int memberId,
            String memberName) {

        MemberMembershipFragment fragment =
                new MemberMembershipFragment();

        Bundle args =
                new Bundle();

        args.putInt(
                "memberId",
                memberId
        );

        args.putString(
                "memberName",
                memberName
        );

        fragment.setArguments(
                args
        );

        return fragment;
    }


    // =====================================================
    // BACKWARD COMPATIBILITY
    // =====================================================

    public static MemberMembershipFragment newInstance(
            int memberId) {

        return newInstance(
                memberId,
                null
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
                R.layout.fragment_member_membership,
                container,
                false
        );
    }


    // =====================================================
    // AFTER VIEW CREATED
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
        // ARGUMENTS
        // -------------------------------------------------

        if (getArguments() == null) {
            return;
        }


        memberId =
                getArguments().getInt(
                        "memberId",
                        0
                );


        memberName =
                getArguments().getString(
                        "memberName"
                );


        if (memberId <= 0) {
            return;
        }


        // -------------------------------------------------
        // API
        // -------------------------------------------------

        apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);


        deviceStorage =
                new DeviceStorage(
                        requireContext()
                );


        // -------------------------------------------------
        // VIEWS
        // -------------------------------------------------

        initializeViews(
                view
        );


        // -------------------------------------------------
        // BUTTONS
        // -------------------------------------------------

        setupButtons();


        // -------------------------------------------------
        // LOAD
        // -------------------------------------------------

        loadMembership();
    }


    // =====================================================
    // ON RESUME
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();


        if (memberId > 0
                && apiService != null
                && getView() != null) {

            loadMembership();
        }
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews(
            View view) {

        txtMembershipNumber =
                view.findViewById(
                        R.id.txtMembershipNumber
                );


        txtMembershipState =
                view.findViewById(
                        R.id.txtMembershipState
                );


        txtPlanName =
                view.findViewById(
                        R.id.txtPlanName
                );


        txtPlanCode =
                view.findViewById(
                        R.id.txtPlanCode
                );


        txtBranchName =
                view.findViewById(
                        R.id.txtBranchName
                );


        txtStartDate =
                view.findViewById(
                        R.id.txtStartDate
                );


        txtEndDate =
                view.findViewById(
                        R.id.txtEndDate
                );


        txtPlanPrice =
                view.findViewById(
                        R.id.txtPlanPrice
                );


        txtJoiningFee =
                view.findViewById(
                        R.id.txtJoiningFee
                );


        txtDiscountAmount =
                view.findViewById(
                        R.id.txtDiscountAmount
                );


        txtTotalAmount =
                view.findViewById(
                        R.id.txtTotalAmount
                );


        txtPaidAmount =
                view.findViewById(
                        R.id.txtPaidAmount
                );


        txtBalanceAmount =
                view.findViewById(
                        R.id.txtBalanceAmount
                );


        txtFreezeDays =
                view.findViewById(
                        R.id.txtFreezeDays
                );


        txtRenewalCount =
                view.findViewById(
                        R.id.txtRenewalCount
                );


        txtMembershipNotes =
                view.findViewById(
                        R.id.txtMembershipNotes
                );


        layoutNoMembership =
                view.findViewById(
                        R.id.layoutNoMembership
                );
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnPayment =
                requireView().findViewById(
                        R.id.btnPayment
                );


        btnFreeze =
                requireView().findViewById(
                        R.id.btnFreeze
                );


        btnRenew =
                requireView().findViewById(
                        R.id.btnRenew
                );


        btnCreateMembership =
                requireView().findViewById(
                        R.id.btnCreateMembership
                );


        // -------------------------------------------------
        // CREATE MEMBERSHIP
        // -------------------------------------------------

        if (btnCreateMembership != null) {

            btnCreateMembership.setOnClickListener(
                    v -> openCreateMembership()
            );
        }


        // -------------------------------------------------
        // PAYMENT
        // -------------------------------------------------

        btnPayment.setOnClickListener(
                v -> {

                    if (currentMembership == null) {

                        showMessage(
                                "No membership available."
                        );

                        return;
                    }


                    showMessage(
                            "Payment screen will be added next."
                    );
                }
        );


        // -------------------------------------------------
        // FREEZE
        // -------------------------------------------------

        btnFreeze.setOnClickListener(
                v -> {

                    if (currentMembership == null) {

                        showMessage(
                                "No membership available."
                        );

                        return;
                    }


                    String state =
                            safeText(
                                    currentMembership
                                            .getMembershipState()
                            );


                    if (!"ACTIVE".equalsIgnoreCase(
                            state
                    )) {

                        showMessage(
                                "Only an active membership can be frozen."
                        );

                        return;
                    }


                    showMessage(
                            "Freeze screen will be added next."
                    );
                }
        );


        // -------------------------------------------------
        // RENEW
        // -------------------------------------------------

        btnRenew.setOnClickListener(
                v -> {

                    if (currentMembership == null) {

                        showMessage(
                                "No membership available."
                        );

                        return;
                    }


                    String state =
                            safeText(
                                    currentMembership
                                            .getMembershipState()
                            );


                    if ("CANCELLED".equalsIgnoreCase(
                            state
                    )) {

                        showMessage(
                                "Cancelled membership cannot be renewed."
                        );

                        return;
                    }


                    showMessage(
                            "Renewal screen will be added next."
                    );
                }
        );
    }


    // =====================================================
    // OPEN CREATE MEMBERSHIP
    // =====================================================

    private void openCreateMembership() {

        Intent intent =
                new Intent(
                        requireContext(),
                        CreateMembershipActivity.class
                );


        intent.putExtra(
                "memberId",
                memberId
        );


        intent.putExtra(
                "memberName",
                memberName
        );


        startActivity(
                intent
        );
    }


    // =====================================================
    // LOAD MEMBERSHIP
    // =====================================================

    private void loadMembership() {

        if (!isAdded()
                || getView() == null
                || deviceStorage == null
                || apiService == null) {

            return;
        }


        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        // -------------------------------------------------
        // TENANT
        // -------------------------------------------------

        if (tenantNo == null
                || tenantNo.trim().isEmpty()) {

            showNoMembership(
                    "Tenant information is missing."
            );

            return;
        }


        // -------------------------------------------------
        // DEVICE
        // -------------------------------------------------

        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            showNoMembership(
                    "Device information is missing."
            );

            return;
        }


        // -------------------------------------------------
        // API
        // -------------------------------------------------

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
                                                MemberMembershipResponse
                                                >
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse
                                                            >
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse
                                                            >
                                                    >
                                            > response) {


                                if (!isAdded()
                                        || getView() == null) {

                                    return;
                                }


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showNoMembership(
                                            "Unable to load membership."
                                    );

                                    return;
                                }


                                ApiResponse<
                                        List<
                                                MemberMembershipResponse
                                                >
                                        > apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()
                                        || apiResponse.getData() == null
                                        || apiResponse.getData().isEmpty()) {

                                    showNoMembership(
                                            "No membership found."
                                    );

                                    return;
                                }


                                currentMembership =
                                        findCurrentMembership(
                                                apiResponse.getData()
                                        );


                                if (currentMembership == null) {

                                    showNoMembership(
                                            "No membership found."
                                    );

                                    return;
                                }


                                displayMembership(
                                        currentMembership
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<
                                                            MemberMembershipResponse
                                                            >
                                                    >
                                            > call,

                                    Throwable t) {

                                if (!isAdded()
                                        || getView() == null) {

                                    return;
                                }


                                showNoMembership(
                                        "Connection failed."
                                );
                            }
                        }
                );
    }


    // =====================================================
    // FIND CURRENT MEMBERSHIP
    // =====================================================

    private MemberMembershipResponse findCurrentMembership(
            List<MemberMembershipResponse> memberships) {


        // -------------------------------------------------
        // ACTIVE / FROZEN FIRST
        // -------------------------------------------------

        for (MemberMembershipResponse membership
                : memberships) {

            String state =
                    safeText(
                            membership.getMembershipState()
                    );


            if ("ACTIVE".equalsIgnoreCase(state)
                    || "FROZEN".equalsIgnoreCase(state)) {

                return membership;
            }
        }


        // -------------------------------------------------
        // OTHERWISE FIRST RECORD
        // -------------------------------------------------

        if (!memberships.isEmpty()) {

            return memberships.get(0);
        }


        return null;
    }


    // =====================================================
    // DISPLAY MEMBERSHIP
    // =====================================================

    private void displayMembership(
            MemberMembershipResponse membership) {

        layoutNoMembership.setVisibility(
                View.GONE
        );


        // -------------------------------------------------
        // HEADER
        // -------------------------------------------------

        txtMembershipNumber.setText(
                safeText(
                        membership.getMembershipNumber()
                )
        );


        String state =
                safeText(
                        membership.getMembershipState()
                );


        txtMembershipState.setText(
                state
        );


        applyStateAppearance(
                state
        );


        // -------------------------------------------------
        // PLAN
        // -------------------------------------------------

        txtPlanName.setText(
                safeText(
                        membership.getPlanName()
                )
        );


        txtPlanCode.setText(
                safeText(
                        membership.getPlanCode()
                )
        );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        txtBranchName.setText(
                safeText(
                        membership.getBranchName()
                )
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        txtStartDate.setText(
                formatDate(
                        membership.getStartDate()
                )
        );


        txtEndDate.setText(
                formatDate(
                        membership.getEndDate()
                )
        );


        // -------------------------------------------------
        // PAYMENT SUMMARY
        // -------------------------------------------------

        txtPlanPrice.setText(
                formatMoney(
                        membership.getPlanPrice()
                )
        );


        txtJoiningFee.setText(
                formatMoney(
                        membership.getJoiningFee()
                )
        );


        txtDiscountAmount.setText(
                formatMoney(
                        membership.getDiscountAmount()
                )
        );


        txtTotalAmount.setText(
                formatMoney(
                        membership.getTotalAmount()
                )
        );


        txtPaidAmount.setText(
                formatMoney(
                        membership.getPaidAmount()
                )
        );


        txtBalanceAmount.setText(
                formatMoney(
                        membership.getBalanceAmount()
                )
        );


        // -------------------------------------------------
        // FREEZE
        // -------------------------------------------------

        int freezeDays =
                membership.getTotalFreezeDays() == null
                        ? 0
                        : membership.getTotalFreezeDays();


        txtFreezeDays.setText(
                freezeDays
                        + " days used"
        );


        // -------------------------------------------------
        // RENEWALS
        // -------------------------------------------------

        int renewalCount =
                membership.getRenewalCount() == null
                        ? 0
                        : membership.getRenewalCount();


        txtRenewalCount.setText(
                renewalCount
                        + (
                        renewalCount == 1
                                ? " renewal"
                                : " renewals"
                )
        );


        // -------------------------------------------------
        // NOTES
        // -------------------------------------------------

        txtMembershipNotes.setText(
                safeText(
                        membership.getNotes()
                )
        );


        // -------------------------------------------------
        // ACTION VISIBILITY
        // -------------------------------------------------

        updateActionVisibility(
                state
        );
    }


    // =====================================================
    // ACTION VISIBILITY
    // =====================================================

    private void updateActionVisibility(
            String state) {

        if (btnPayment == null
                || btnFreeze == null
                || btnRenew == null) {

            return;
        }


        btnPayment.setVisibility(
                View.VISIBLE
        );


        if ("FROZEN".equalsIgnoreCase(state)) {

            btnFreeze.setVisibility(
                    View.GONE
            );


            btnRenew.setVisibility(
                    View.VISIBLE
            );


            return;
        }


        if ("CANCELLED".equalsIgnoreCase(state)) {

            btnFreeze.setVisibility(
                    View.GONE
            );


            btnRenew.setVisibility(
                    View.GONE
            );


            return;
        }


        if ("EXPIRED".equalsIgnoreCase(state)) {

            btnFreeze.setVisibility(
                    View.GONE
            );


            btnRenew.setVisibility(
                    View.VISIBLE
            );


            return;
        }


        btnFreeze.setVisibility(
                View.VISIBLE
        );


        btnRenew.setVisibility(
                View.VISIBLE
        );
    }


    // =====================================================
    // STATE APPEARANCE
    // =====================================================

    private void applyStateAppearance(
            String state) {

        int color;


        if ("ACTIVE".equalsIgnoreCase(state)) {

            color =
                    getResources().getColor(
                            R.color.green_primary,
                            requireContext().getTheme()
                    );

        } else if ("FROZEN".equalsIgnoreCase(state)) {

            color =
                    0xFFE5A23C;

        } else if ("EXPIRED".equalsIgnoreCase(state)) {

            color =
                    0xFFE05A5A;

        } else if ("CANCELLED".equalsIgnoreCase(state)) {

            color =
                    0xFF8A8F98;

        } else {

            color =
                    getResources().getColor(
                            R.color.text_secondary,
                            requireContext().getTheme()
                    );
        }


        txtMembershipState.setTextColor(
                color
        );
    }


    // =====================================================
    // NO MEMBERSHIP
    // =====================================================

    private void showNoMembership(
            String message) {

        if (!isAdded()
                || getView() == null) {

            return;
        }


        currentMembership =
                null;


        layoutNoMembership.setVisibility(
                View.VISIBLE
        );


        txtMembershipNotes.setText(
                message
        );


        txtMembershipNumber.setText(
                "-"
        );


        txtMembershipState.setText(
                "-"
        );


        txtPlanName.setText(
                "-"
        );


        txtPlanCode.setText(
                "-"
        );


        txtBranchName.setText(
                "-"
        );


        txtStartDate.setText(
                "-"
        );


        txtEndDate.setText(
                "-"
        );


        txtPlanPrice.setText(
                "Rs. 0.00"
        );


        txtJoiningFee.setText(
                "Rs. 0.00"
        );


        txtDiscountAmount.setText(
                "Rs. 0.00"
        );


        txtTotalAmount.setText(
                "Rs. 0.00"
        );


        txtPaidAmount.setText(
                "Rs. 0.00"
        );


        txtBalanceAmount.setText(
                "Rs. 0.00"
        );


        txtFreezeDays.setText(
                "0 days used"
        );


        txtRenewalCount.setText(
                "0 renewals"
        );


        if (btnCreateMembership != null) {

            btnCreateMembership.setVisibility(
                    View.VISIBLE
            );
        }


        if (btnPayment != null) {

            btnPayment.setVisibility(
                    View.GONE
            );
        }


        if (btnFreeze != null) {

            btnFreeze.setVisibility(
                    View.GONE
            );
        }


        if (btnRenew != null) {

            btnRenew.setVisibility(
                    View.GONE
            );
        }
    }


    // =====================================================
    // FORMAT MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal value) {

        BigDecimal amount =
                value == null
                        ? BigDecimal.ZERO
                        : value;


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
    // FORMAT DATE
    // =====================================================

    private String formatDate(
            String date) {

        if (date == null
                || date.trim().isEmpty()) {

            return "-";
        }


        return date;
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
}