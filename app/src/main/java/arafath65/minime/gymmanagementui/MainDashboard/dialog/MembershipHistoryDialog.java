package arafath65.minime.gymmanagementui.MainDashboard.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import arafath65.minime.gymmanagementui.MainDashboard.adapter.BenefitHistoryAdapter;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.MembershipHistoryAdapter;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.ProgramHistoryAdapter;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramResponse;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipHistoryDialog extends DialogFragment {

    private static final String ARG_MEMBER_ID = "member_id";
    private static final String ARG_MEMBER_NAME = "member_name";

    private Integer memberId;
    private String memberName;
    private DeviceStorage deviceStorage;

    private TextView txtTitle;
    private TextView txtMember;
    private Button btnClose;

    private View tabMemberships;
    private View tabPrograms;
    private View tabBenefits;

    private View containerMemberships;
    private View containerPrograms;
    private View containerBenefits;

    private RecyclerView recyclerMemberships;
    private RecyclerView recyclerPrograms;
    private RecyclerView recyclerBenefits;

    private TextView txtNoMembershipHistory;
    private TextView txtNoProgramHistory;
    private TextView txtNoBenefitHistory;

    private AutoCompleteTextView spinnerStatus;

    private MembershipHistoryAdapter membershipAdapter;
    private ProgramHistoryAdapter programAdapter;
    private BenefitHistoryAdapter benefitAdapter;

    private final List<MemberMembershipResponse> allMemberships = new ArrayList<>();
    private final List<MemberMembershipProgramResponse> allPrograms = new ArrayList<>();
    private final List<MemberMembershipBenefitResponse> allBenefits = new ArrayList<>();

    private Integer currentMembershipId = null;

    private String selectedStatus = "ALL";

    public static MembershipHistoryDialog newInstance(
            Integer memberId,
            String memberName
    ) {

        MembershipHistoryDialog dialog = new MembershipHistoryDialog();

        Bundle args = new Bundle();
        args.putInt(ARG_MEMBER_ID, memberId);

        if (memberName != null) {
            args.putString(ARG_MEMBER_NAME, memberName);
        }

        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.dialog_membership_history,
                container,
                false
        );

        if (getArguments() != null) {

            memberId = getArguments().getInt(ARG_MEMBER_ID);

            memberName = getArguments().getString(
                    ARG_MEMBER_NAME,
                    ""
            );
        }

        deviceStorage = new DeviceStorage(
                requireContext().getApplicationContext()
        );

        initializeViews(view);
        setupTabs();
        setupRecyclerViews();

        txtMember.setText(
                memberName == null || memberName.trim().isEmpty()
                        ? "Membership History"
                        : memberName
        );

        showMembershipTab();

        loadMembershipHistory();

        return view;
    }

    private void initializeViews(View view) {

        txtTitle = view.findViewById(
                R.id.txtMembershipHistoryTitle
        );

        txtMember = view.findViewById(
                R.id.txtMembershipHistoryMember
        );

        btnClose = view.findViewById(
                R.id.btnCloseMembershipHistory
        );

        tabMemberships = view.findViewById(
                R.id.tabHistoryMemberships
        );

        tabPrograms = view.findViewById(
                R.id.tabHistoryPrograms
        );

        tabBenefits = view.findViewById(
                R.id.tabHistoryBenefits
        );

        containerMemberships = view.findViewById(
                R.id.containerHistoryMemberships
        );

        containerPrograms = view.findViewById(
                R.id.containerHistoryPrograms
        );

        containerBenefits = view.findViewById(
                R.id.containerHistoryBenefits
        );

        recyclerMemberships = view.findViewById(
                R.id.recyclerMembershipHistory
        );

        recyclerPrograms = view.findViewById(
                R.id.recyclerProgramHistory
        );

        recyclerBenefits = view.findViewById(
                R.id.recyclerBenefitHistory
        );

        txtNoMembershipHistory = view.findViewById(
                R.id.txtNoMembershipHistory
        );

        txtNoProgramHistory = view.findViewById(
                R.id.txtNoProgramHistory
        );

        txtNoBenefitHistory = view.findViewById(
                R.id.txtNoBenefitHistory
        );

        txtTitle.setText("Membership History");

        btnClose.setOnClickListener(v ->
                dismiss()
        );
    }

    private void setupTabs() {

        tabMemberships.setOnClickListener(v ->
                showMembershipTab()
        );

        tabPrograms.setOnClickListener(v -> {

            showProgramTab();

            if (allPrograms.isEmpty()) {
                loadProgramHistory();
            } else {
                applyProgramFilter();
            }
        });

        tabBenefits.setOnClickListener(v -> {

            showBenefitTab();

            if (allBenefits.isEmpty()) {
                loadBenefitHistory();
            } else {
                applyBenefitFilter();
            }
        });
    }

    private void setupStatusDropdown() {

        String[] statuses = {
                "ALL",
                "ACTIVE",
                "FROZEN",
                "EXPIRED",
                "CANCELLED",
                "INACTIVE"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                statuses
        ) {
            @NonNull
            @Override
            public View getDropDownView(
                    int position,
                    @Nullable View convertView,
                    @NonNull ViewGroup parent) {

                TextView item = (TextView) super.getDropDownView(
                        position, convertView, parent
                );

                item.setTextColor(android.graphics.Color.WHITE);
                item.setBackgroundColor(
                        android.graphics.Color.parseColor("#191F20")
                );
                item.setPadding(20, 16, 20, 16);

                return item;
            }
        };

        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setThreshold(0);
        spinnerStatus.setDropDownBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(
                        android.graphics.Color.parseColor("#191F20")
                )
        );

        spinnerStatus.setText("ALL", false);
        spinnerStatus.setKeyListener(null);
        spinnerStatus.setInputType(0);
        spinnerStatus.setFocusable(false);
        spinnerStatus.setClickable(true);

        spinnerStatus.setOnClickListener(v -> {
            // Clear the displayed filter temporarily so ALL options
            // appear, instead of filtering the list to only "ALL".
            spinnerStatus.setText("", false);
            spinnerStatus.showDropDown();
        });

        spinnerStatus.setOnDismissListener(() -> {
            if (spinnerStatus.getText().toString().trim().isEmpty()) {
                spinnerStatus.setText(selectedStatus, false);
            }
        });

        spinnerStatus.setOnItemClickListener((parent, view, position, id) -> {
            selectedStatus = String.valueOf(
                    parent.getItemAtPosition(position)
            );

            spinnerStatus.setText(selectedStatus, false);
            refreshCurrentTab();
        });
    }

    private void setupRecyclerViews() {

        recyclerMemberships.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerPrograms.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerBenefits.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        membershipAdapter = new MembershipHistoryAdapter();

        programAdapter = new ProgramHistoryAdapter();

        benefitAdapter = new BenefitHistoryAdapter();

        recyclerMemberships.setAdapter(
                membershipAdapter
        );

        recyclerPrograms.setAdapter(
                programAdapter
        );

        recyclerBenefits.setAdapter(
                benefitAdapter
        );
    }

    private void showMembershipTab() {

        containerMemberships.setVisibility(
                View.VISIBLE
        );

        containerPrograms.setVisibility(
                View.GONE
        );

        containerBenefits.setVisibility(
                View.GONE
        );

        updateTabAppearance(
                tabMemberships,
                true
        );

        updateTabAppearance(
                tabPrograms,
                false
        );

        updateTabAppearance(
                tabBenefits,
                false
        );

        applyMembershipFilter();
    }

    private void showProgramTab() {

        containerMemberships.setVisibility(
                View.GONE
        );

        containerPrograms.setVisibility(
                View.VISIBLE
        );

        containerBenefits.setVisibility(
                View.GONE
        );

        selectedStatus = "ALL";

        spinnerStatus.setText(
                "ALL",
                false
        );

        updateTabAppearance(
                tabMemberships,
                false
        );

        updateTabAppearance(
                tabPrograms,
                true
        );

        updateTabAppearance(
                tabBenefits,
                false
        );

        applyProgramFilter();
    }

    private void showBenefitTab() {

        containerMemberships.setVisibility(
                View.GONE
        );

        containerPrograms.setVisibility(
                View.GONE
        );

        containerBenefits.setVisibility(
                View.VISIBLE
        );

        selectedStatus = "ALL";

        spinnerStatus.setText(
                "ALL",
                false
        );

        updateTabAppearance(
                tabMemberships,
                false
        );

        updateTabAppearance(
                tabPrograms,
                false
        );

        updateTabAppearance(
                tabBenefits,
                true
        );

        applyBenefitFilter();
    }

    private void updateTabAppearance(
            View tab,
            boolean selected
    ) {

        if (selected) {
            tab.setAlpha(1.0f);
        } else {
            tab.setAlpha(0.55f);
        }
    }

    private void refreshCurrentTab() {

        if (containerMemberships.getVisibility()
                == View.VISIBLE) {

            applyMembershipFilter();

        } else if (containerPrograms.getVisibility()
                == View.VISIBLE) {

            applyProgramFilter();

        } else if (containerBenefits.getVisibility()
                == View.VISIBLE) {

            applyBenefitFilter();
        }
    }

    // ============================================================
    // MEMBERSHIP HISTORY
    // ============================================================

    private void loadMembershipHistory() {

        android.util.Log.e(
                "MEMBERSHIP_HISTORY",
                "loadMembershipHistory() STARTED"
        );

        if (memberId == null) {
            return;
        }

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        android.util.Log.e(
                "MEMBERSHIP_HISTORY",
                "TenantNo=[" + deviceStorage.getTenantNo()
                        + "], DeviceTokenPresent="
                        + (getDeviceToken() != null
                        && !getDeviceToken().trim().isEmpty())
                        + ", MemberId=" + memberId
        );

        apiService.getMemberMemberships(
                getTenantNo(),
                getDeviceToken(),
                memberId
        ).enqueue(new Callback<ApiResponse<List<MemberMembershipResponse>>>() {

            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<MemberMembershipResponse>>> call,
                    @NonNull Response<ApiResponse<List<MemberMembershipResponse>>> response
            ) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    String errorBody = "";

                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (java.io.IOException e) {
                        errorBody = e.getMessage();
                    }

                    android.util.Log.e(
                            "MEMBERSHIP_HISTORY",
                            "HTTP failure"
                                    + "\nURL: " + call.request().url()
                                    + "\nCode: " + response.code()
                                    + "\nError body: " + errorBody
                    );

                    showMembershipEmpty();
                    return;
                }

                ApiResponse<List<MemberMembershipResponse>> body =
                        response.body();

                if (!body.isSuccess()
                        || body.getData() == null) {

                    showMembershipEmpty();
                    return;
                }

                allMemberships.clear();

                allMemberships.addAll(body.getData());

                android.util.Log.e(
                        "MEMBERSHIP_HISTORY",
                        "API returned " + allMemberships.size()
                                + " memberships for memberId=" + memberId
                );

                for (MemberMembershipResponse membership : allMemberships) {
                    if (membership == null) {
                        continue;
                    }

                    android.util.Log.e(
                            "MEMBERSHIP_HISTORY",
                            "ID=" + membership.getMemberMembershipId()
                                    + ", Number=" + membership.getMembershipNumber()
                                    + ", State=" + membership.getMembershipState()
                    );
                }

                allMemberships.sort((a, b) -> {
                    if (a == null || a.getStartDate() == null) {
                        return 1;
                    }

                    if (b == null || b.getStartDate() == null) {
                        return -1;
                    }

                    return b.getStartDate().compareTo(a.getStartDate());
                });

                findCurrentMembership();

                applyMembershipFilter();
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<List<MemberMembershipResponse>>> call,
                    @NonNull Throwable t
            ) {

                android.util.Log.e(
                        "MEMBERSHIP_HISTORY",
                        "Request failed",
                        t
                );

                showMembershipEmpty();

                Toast.makeText(
                        requireContext(),
                        "Failed to load membership history",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void findCurrentMembership() {

        currentMembershipId = null;

        for (MemberMembershipResponse membership
                : allMemberships) {

            if (membership == null) {
                continue;
            }

            String state =
                    membership.getMembershipState();

            if (state == null) {
                continue;
            }

            if ("ACTIVE".equalsIgnoreCase(state)
                    || "FROZEN".equalsIgnoreCase(state)) {

                currentMembershipId =
                        membership.getMemberMembershipId();

                break;
            }
        }
    }

    private void applyMembershipFilter() {

        List<MemberMembershipResponse> filtered =
                new ArrayList<>();

        for (MemberMembershipResponse membership
                : allMemberships) {

            if (membership == null) {
                continue;
            }

            /*
             * IMPORTANT:
             *
             * Current membership is already displayed
             * on the main Membership screen.
             *
             * Therefore do NOT display it again inside
             * Membership History.
             */
            String state = membership.getMembershipState();

            boolean isCurrentMembership =
                    "ACTIVE".equalsIgnoreCase(state)
                            || "FROZEN".equalsIgnoreCase(state);

            if (isCurrentMembership
                    && currentMembershipId != null
                    && membership.getMemberMembershipId() != null
                    && currentMembershipId.equals(
                    membership.getMemberMembershipId()
            )) {

                continue;
            }

            filtered.add(membership);
        }

        membershipAdapter.setMemberships(filtered);

        if (filtered.isEmpty()) {
            showMembershipEmpty();
        } else {
            recyclerMemberships.setVisibility(
                    View.VISIBLE
            );

            txtNoMembershipHistory.setVisibility(
                    View.GONE
            );
        }
    }

    private boolean matchesMembershipStatus(
            MemberMembershipResponse membership
    ) {

        if ("ALL".equalsIgnoreCase(selectedStatus)) {
            return true;
        }

        String state =
                membership.getMembershipState();

        return state != null
                && state.equalsIgnoreCase(
                selectedStatus
        );
    }

    private void showMembershipEmpty() {

        recyclerMemberships.setVisibility(
                View.GONE
        );

        txtNoMembershipHistory.setVisibility(
                View.VISIBLE
        );
    }

    // ============================================================
    // PROGRAM HISTORY
    // ============================================================

    private void loadProgramHistory() {

        if (memberId == null) {
            return;
        }

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.getMemberProgramHistory(
                getTenantNo(),
                getDeviceToken(),
                memberId,
                getTenantId()
        ).enqueue(new Callback<ApiResponse<List<MemberMembershipProgramResponse>>>() {

            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<MemberMembershipProgramResponse>>> call,
                    @NonNull Response<ApiResponse<List<MemberMembershipProgramResponse>>> response
            ) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    showProgramEmpty();
                    return;
                }

                ApiResponse<List<MemberMembershipProgramResponse>> body =
                        response.body();

                if (!body.isSuccess()
                        || body.getData() == null) {

                    showProgramEmpty();
                    return;
                }

                allPrograms.clear();

                allPrograms.addAll(
                        body.getData()
                );

                applyProgramFilter();
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<List<MemberMembershipProgramResponse>>> call,
                    @NonNull Throwable t
            ) {

                showProgramEmpty();

                Toast.makeText(
                        requireContext(),
                        "Failed to load program history",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void applyProgramFilter() {

        List<MemberMembershipProgramResponse> filtered =
                new ArrayList<>();

        for (MemberMembershipProgramResponse program
                : allPrograms) {

            if (program == null) {
                continue;
            }

            /*
             * Current active program assignments belong to
             * the current membership and are already shown
             * on the main screen.
             */
            if (isCurrentActiveProgram(program)) {
                continue;
            }

            if (!matchesProgramStatus(program)) {
                continue;
            }

            filtered.add(program);
        }

        programAdapter.setPrograms(
                filtered
        );

        if (filtered.isEmpty()) {
            showProgramEmpty();
        } else {

            recyclerPrograms.setVisibility(
                    View.VISIBLE
            );

            txtNoProgramHistory.setVisibility(
                    View.GONE
            );
        }
    }

    private boolean isCurrentActiveProgram(
            MemberMembershipProgramResponse program
    ) {

        if (currentMembershipId == null) {
            return false;
        }

        Integer membershipId =
                program.getMemberMembershipId();

        Boolean status =
                program.getStatus();

        return membershipId != null
                && currentMembershipId.equals(
                membershipId
        )
                && Boolean.TRUE.equals(status);
    }

    private boolean matchesProgramStatus(
            MemberMembershipProgramResponse program
    ) {

        if ("ALL".equalsIgnoreCase(selectedStatus)) {
            return true;
        }

        Boolean status =
                program.getStatus();

        if ("ACTIVE".equalsIgnoreCase(selectedStatus)) {
            return Boolean.TRUE.equals(status);
        }

        if ("INACTIVE".equalsIgnoreCase(selectedStatus)
                || "EXPIRED".equalsIgnoreCase(selectedStatus)) {

            return Boolean.FALSE.equals(status);
        }

        return true;
    }

    private void showProgramEmpty() {

        recyclerPrograms.setVisibility(
                View.GONE
        );

        txtNoProgramHistory.setVisibility(
                View.VISIBLE
        );
    }

    // ============================================================
    // BENEFIT HISTORY
    // ============================================================

    private void loadBenefitHistory() {

        if (memberId == null) {
            return;
        }

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.getMemberBenefitHistory(
                getTenantNo(),
                getDeviceToken(),
                memberId
        ).enqueue(new Callback<ApiResponse<List<MemberMembershipBenefitResponse>>>() {

            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<MemberMembershipBenefitResponse>>> call,
                    @NonNull Response<ApiResponse<List<MemberMembershipBenefitResponse>>> response
            ) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    showBenefitEmpty();
                    return;
                }

                ApiResponse<List<MemberMembershipBenefitResponse>> body =
                        response.body();

                if (!body.isSuccess()
                        || body.getData() == null) {

                    showBenefitEmpty();
                    return;
                }

                allBenefits.clear();

                allBenefits.addAll(
                        body.getData()
                );

                applyBenefitFilter();
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<List<MemberMembershipBenefitResponse>>> call,
                    @NonNull Throwable t
            ) {

                showBenefitEmpty();

                Toast.makeText(
                        requireContext(),
                        "Failed to load benefit history",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void applyBenefitFilter() {

        List<MemberMembershipBenefitResponse> filtered =
                new ArrayList<>();

        for (MemberMembershipBenefitResponse benefit
                : allBenefits) {

            if (benefit == null) {
                continue;
            }

            /*
             * Current active benefits are already shown
             * in the main Membership screen.
             */
            if (isCurrentActiveBenefit(benefit)) {
                continue;
            }

            if (!matchesBenefitStatus(benefit)) {
                continue;
            }

            filtered.add(benefit);
        }

        benefitAdapter.setBenefits(
                filtered
        );

        if (filtered.isEmpty()) {
            showBenefitEmpty();
        } else {

            recyclerBenefits.setVisibility(
                    View.VISIBLE
            );

            txtNoBenefitHistory.setVisibility(
                    View.GONE
            );
        }
    }

    private boolean isCurrentActiveBenefit(
            MemberMembershipBenefitResponse benefit
    ) {

        if (currentMembershipId == null) {
            return false;
        }

        Integer membershipId =
                benefit.getMemberMembershipId();

        Boolean status =
                benefit.getStatus();

        return membershipId != null
                && currentMembershipId.equals(
                membershipId
        )
                && Boolean.TRUE.equals(status);
    }

    private boolean matchesBenefitStatus(
            MemberMembershipBenefitResponse benefit
    ) {

        if ("ALL".equalsIgnoreCase(selectedStatus)) {
            return true;
        }

        Boolean status =
                benefit.getStatus();

        if ("ACTIVE".equalsIgnoreCase(selectedStatus)) {
            return Boolean.TRUE.equals(status);
        }

        if ("INACTIVE".equalsIgnoreCase(selectedStatus)
                || "EXPIRED".equalsIgnoreCase(selectedStatus)) {

            return Boolean.FALSE.equals(status);
        }

        return true;
    }

    private void showBenefitEmpty() {

        recyclerBenefits.setVisibility(
                View.GONE
        );

        txtNoBenefitHistory.setVisibility(
                View.VISIBLE
        );
    }

    // ============================================================
    // CONFIG
    // ============================================================

    // ============================================================
// CONFIG
// ============================================================

    private DeviceStorage getDeviceStorage() {
        return new DeviceStorage(requireContext());
    }

    // ============================================================
// CONFIG
// ============================================================

    private String getTenantNo() {
        String tenantNo = deviceStorage.getTenantNo();
        return tenantNo == null ? "" : tenantNo.trim();
    }

    private String getDeviceToken() {
        String deviceToken = deviceStorage.getDeviceToken();
        return deviceToken == null ? "" : deviceToken.trim();
    }

    private Integer getTenantId() {
        return requireContext()
                .getSharedPreferences(
                        "GymPrefs",
                        android.content.Context.MODE_PRIVATE
                )
                .getInt("tenantId", 0);
    }

    // ============================================================
    // DARK FULL-SCREEN DIALOG
    // ============================================================

    @Override
    public void onStart() {

        super.onStart();

        Dialog dialog = getDialog();

        if (dialog == null) {
            return;
        }

        Window window =
                dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setBackgroundDrawableResource(
                android.R.color.transparent
        );

        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }
}