package arafath65.minime.gymmanagementui.MainDashboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import arafath65.minime.gymmanagementui.MainDashboard.fragment.BenefitsFragment;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.DashboardFragment;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.MembersFragment;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.MembershipPlansFragment;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.PlansFragment;
import arafath65.minime.gymmanagementui.MainDashboard.fragment.ProgramsFragment;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainDashboard extends AppCompatActivity {

    // =====================================================
    // SESSION
    // =====================================================

    private SessionStorage sessionStorage;


    // =====================================================
    // BRANCH SELECTOR
    // =====================================================

    private AutoCompleteTextView txtBranchSelector;

    private List<BranchResponse> branchList =
            new ArrayList<>();


    // =====================================================
    // NAVIGATION VIEWS
    // =====================================================

    private View navDashboard;
    private View navMembers;
    private View navMembership;
    private View navPrograms;
    private View navBenefits;


    // =====================================================
    // NAVIGATION ICONS
    // =====================================================

    private ImageView iconDashboard;
    private ImageView iconMembers;
    private ImageView iconMembership;
    private ImageView iconPrograms;
    private ImageView iconBenefits;


    // =====================================================
    // NAVIGATION TEXT
    // =====================================================

    private TextView textDashboard;
    private TextView textMembers;
    private TextView textMembership;
    private TextView textPrograms;
    private TextView textBenefits;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_main_dashboard
        );


        // =================================================
        // INITIALIZE BRANCH SELECTOR
        // =================================================

        txtBranchSelector =
                findViewById(
                        R.id.txtBranchSelector
                );


        // =================================================
        // INITIALIZE NAVIGATION
        // =================================================

        navDashboard =
                findViewById(
                        R.id.navDashboard
                );


        navMembers =
                findViewById(
                        R.id.navMembers
                );


        navMembership =
                findViewById(
                        R.id.navMembership
                );


        navPrograms =
                findViewById(
                        R.id.navPrograms
                );


        navBenefits =
                findViewById(
                        R.id.navBenefits
                );


        // =================================================
        // INITIALIZE ICONS
        // =================================================

        iconDashboard =
                findViewById(
                        R.id.iconDashboard
                );


        iconMembers =
                findViewById(
                        R.id.iconMembers
                );


        iconMembership =
                findViewById(
                        R.id.iconMembership
                );


        iconPrograms =
                findViewById(
                        R.id.iconPrograms
                );


        iconBenefits =
                findViewById(
                        R.id.iconBenefits
                );


        // =================================================
        // INITIALIZE TEXT
        // =================================================

        textDashboard =
                findViewById(
                        R.id.textDashboard
                );


        textMembers =
                findViewById(
                        R.id.textMembers
                );


        textMembership =
                findViewById(
                        R.id.textMembership
                );


        textPrograms =
                findViewById(
                        R.id.textPrograms
                );


        textBenefits =
                findViewById(
                        R.id.textBenefits
                );


        // Plans combines Membership, Programs and Benefits in one screen.
        // Hide the old standalone navigation rows to avoid duplicate menu items.
        navPrograms.setVisibility(View.GONE);
        navBenefits.setVisibility(View.GONE);
        textMembership.setText("Plans");


        // =================================================
        // WINDOW INSETS
        // =================================================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );


        // =================================================
        // SESSION
        // =================================================

        sessionStorage =
                new SessionStorage(this);


        if (!sessionStorage.isLoggedIn()) {

            Toast.makeText(
                    this,
                    "Session not found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =================================================
        // DEFAULT DASHBOARD
        // =================================================

        if (savedInstanceState == null) {

            setActiveNavigation(
                    navDashboard
            );

            openFragment(
                    new DashboardFragment()
            );
        }

        // =================================================
        // DASHBOARD NAVIGATION
        // =================================================

        navDashboard.setOnClickListener(v -> {

            setActiveNavigation(
                    navDashboard
            );

            openFragment(
                    new DashboardFragment()
            );

        });

        // =================================================
        // MEMBERS NAVIGATION
        // =================================================

        navMembers.setOnClickListener(v -> {

            setActiveNavigation(
                    navMembers
            );

            openFragment(
                    new MembersFragment()
            );

        });


        // =================================================
        // MEMBERSHIP NAVIGATION
        // =================================================

        navMembership.setOnClickListener(v -> {

            setActiveNavigation(
                    navMembership
            );

            openFragment(
                    new PlansFragment()
            );

        });


        // =================================================
        // PROGRAMS NAVIGATION
        // =================================================

        navPrograms.setOnClickListener(v -> {

            setActiveNavigation(
                    navPrograms
            );

            openFragment(
                    new ProgramsFragment()
            );

        });


        // =================================================
        // BENEFITS NAVIGATION
        // =================================================

        navBenefits.setOnClickListener(v -> {

            setActiveNavigation(
                    navBenefits
            );

            openFragment(
                    new BenefitsFragment()
            );

        });


        // =================================================
        // BRANCH SELECTOR
        // =================================================

        setupBranchSelectorVisibility();
    }


    // =====================================================
    // NAVIGATION SELECTION
    // =====================================================

    private void setActiveNavigation(
            View selectedView) {


        // =================================================
        // RESET DASHBOARD
        // =================================================

        navDashboard.setBackground(null);

        iconDashboard.setColorFilter(
                getColor(
                        R.color.text_secondary
                )
        );

        textDashboard.setTextColor(
                getColor(
                        R.color.text_secondary
                )
        );

        textDashboard.setTypeface(
                null,
                Typeface.NORMAL
        );


        // =================================================
        // RESET MEMBERS
        // =================================================

        navMembers.setBackground(null);

        iconMembers.setColorFilter(
                getColor(
                        R.color.text_secondary
                )
        );

        textMembers.setTextColor(
                getColor(
                        R.color.text_secondary
                )
        );

        textMembers.setTypeface(
                null,
                Typeface.NORMAL
        );


        // =================================================
        // RESET MEMBERSHIP
        // =================================================

        navMembership.setBackground(null);

        iconMembership.setColorFilter(
                getColor(
                        R.color.text_secondary
                )
        );

        textMembership.setTextColor(
                getColor(
                        R.color.text_secondary
                )
        );

        textMembership.setTypeface(
                null,
                Typeface.NORMAL
        );


        // =================================================
        // RESET PROGRAMS
        // =================================================

        navPrograms.setBackground(null);

        iconPrograms.setColorFilter(
                getColor(
                        R.color.text_secondary
                )
        );

        textPrograms.setTextColor(
                getColor(
                        R.color.text_secondary
                )
        );

        textPrograms.setTypeface(
                null,
                Typeface.NORMAL
        );


        // =================================================
        // RESET BENEFITS
        // =================================================

        navBenefits.setBackground(null);

        iconBenefits.setColorFilter(
                getColor(
                        R.color.text_secondary
                )
        );

        textBenefits.setTextColor(
                getColor(
                        R.color.text_secondary
                )
        );

        textBenefits.setTypeface(
                null,
                Typeface.NORMAL
        );


        // =================================================
        // DASHBOARD SELECTED
        // =================================================

        if (selectedView == navDashboard) {

            navDashboard.setBackgroundResource(
                    R.drawable.bg_nav_selected
            );

            iconDashboard.setColorFilter(
                    getColor(
                            R.color.green_primary
                    )
            );

            textDashboard.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );

            textDashboard.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }


        // =================================================
        // MEMBERS SELECTED
        // =================================================

        else if (selectedView == navMembers) {

            navMembers.setBackgroundResource(
                    R.drawable.bg_nav_selected
            );

            iconMembers.setColorFilter(
                    getColor(
                            R.color.green_primary
                    )
            );

            textMembers.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );

            textMembers.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }


        // =================================================
        // MEMBERSHIP SELECTED
        // =================================================

        else if (selectedView == navMembership) {

            navMembership.setBackgroundResource(
                    R.drawable.bg_nav_selected
            );

            iconMembership.setColorFilter(
                    getColor(
                            R.color.green_primary
                    )
            );

            textMembership.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );

            textMembership.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }


        // =================================================
        // PROGRAMS SELECTED
        // =================================================

        else if (selectedView == navPrograms) {

            navPrograms.setBackgroundResource(
                    R.drawable.bg_nav_selected
            );

            iconPrograms.setColorFilter(
                    getColor(
                            R.color.green_primary
                    )
            );

            textPrograms.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );

            textPrograms.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }


        // =================================================
        // BENEFITS SELECTED
        // =================================================

        else if (selectedView == navBenefits) {

            navBenefits.setBackgroundResource(
                    R.drawable.bg_nav_selected
            );

            iconBenefits.setColorFilter(
                    getColor(
                            R.color.green_primary
                    )
            );

            textBenefits.setTextColor(
                    getColor(
                            R.color.text_primary
                    )
            );

            textBenefits.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }
    }


    // =====================================================
    // BRANCH SELECTOR VISIBILITY
    // =====================================================

    private void setupBranchSelectorVisibility() {

        if (canSwitchBranch()) {

            txtBranchSelector.setVisibility(
                    View.VISIBLE
            );

            loadBranches();

        } else {

            txtBranchSelector.setVisibility(
                    View.GONE
            );
        }
    }


    // =====================================================
    // ROLE CHECK
    // =====================================================

    private boolean canSwitchBranch() {

        String roleName =
                sessionStorage.getRoleName();


        if (roleName == null) {

            return false;
        }


        return roleName.equalsIgnoreCase(
                "SUPER_ADMIN"
        )
                ||
                roleName.equalsIgnoreCase(
                        "GYM_OWNER"
                );
    }


    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        String tenantNo =
                sessionStorage.getTenantNo();


        DeviceStorage deviceStorage =
                new DeviceStorage(this);


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            showBranchError(
                    "Tenant information is missing."
            );

            return;
        }


        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            showBranchError(
                    "Device is not registered."
            );

            return;
        }


        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService.getBranches(
                tenantNo,
                deviceToken
        ).enqueue(
                new Callback<
                        ApiResponse<
                                List<BranchResponse>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<
                                    List<BranchResponse>>> call,

                            Response<ApiResponse<
                                    List<BranchResponse>>> response) {


                        if (isFinishing() ||
                                isDestroyed()) {

                            return;
                        }


                        handleBranchesResponse(
                                response
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<ApiResponse<
                                    List<BranchResponse>>> call,

                            Throwable t) {


                        if (isFinishing() ||
                                isDestroyed()) {

                            return;
                        }


                        showBranchError(
                                "Unable to load branches."
                        );
                    }
                }
        );
    }


    // =====================================================
    // HANDLE BRANCH RESPONSE
    // =====================================================

    private void handleBranchesResponse(
            Response<ApiResponse<
                    List<BranchResponse>>> response) {


        if (!response.isSuccessful()) {

            showBranchError(
                    "Unable to load branches."
            );

            return;
        }


        ApiResponse<List<BranchResponse>>
                apiResponse =
                response.body();


        if (apiResponse == null) {

            showBranchError(
                    "Empty response from server."
            );

            return;
        }


        if (!apiResponse.isSuccess()) {

            showBranchError(
                    apiResponse.getMessage()
            );

            return;
        }


        List<BranchResponse> branches =
                apiResponse.getData();


        if (branches == null) {

            branches =
                    new ArrayList<>();
        }


        branchList.clear();

        branchList.addAll(
                branches
        );


        setupBranchDropdown();
    }


    // =====================================================
    // SETUP BRANCH DROPDOWN
    // =====================================================

    private void setupBranchDropdown() {

        List<String> branchNames =
                new ArrayList<>();


        // =================================================
        // ALL BRANCHES
        // =================================================

        branchNames.add(
                "All Branches"
        );


        // =================================================
        // BRANCHES
        // =================================================

        for (BranchResponse branch :
                branchList) {


            String displayName =
                    branch.getBranchName();


            if (branch.getBranchNo() != null &&
                    !branch.getBranchNo()
                            .trim()
                            .isEmpty()) {

                displayName =
                        branch.getBranchNo()
                                + " - "
                                + branch.getBranchName();
            }


            branchNames.add(
                    displayName
            );
        }


        // =================================================
        // ADAPTER
        // =================================================

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item_branch_dropdown,
                        branchNames
                );


        txtBranchSelector.setAdapter(
                adapter
        );


        // =================================================
        // CURRENT SELECTION
        // =================================================

        updateBranchSelectorText();


        // =================================================
        // ITEM CLICK
        // =================================================

        txtBranchSelector.setOnItemClickListener(
                (parent, view, position, id) -> {


                    if (position == 0) {

                        // =================================
                        // ALL BRANCHES
                        // =================================

                        sessionStorage
                                .saveSelectedBranch(
                                        null,
                                        null,
                                        "All Branches"
                                );

                    } else {

                        // =================================
                        // SELECTED BRANCH
                        // =================================

                        BranchResponse branch =
                                branchList.get(
                                        position - 1
                                );


                        sessionStorage
                                .saveSelectedBranch(
                                        branch.getBranchId(),
                                        branch.getBranchNo(),
                                        branch.getBranchName()
                                );
                    }


                    updateBranchSelectorText();


                    // =====================================
                    // REFRESH CURRENT FRAGMENT
                    // =====================================

                    refreshCurrentFragment();
                }
        );
    }


    // =====================================================
    // UPDATE BRANCH SELECTOR TEXT
    // =====================================================

    private void updateBranchSelectorText() {

        if (sessionStorage.isAllBranches()) {

            txtBranchSelector.setText(
                    "All Branches",
                    false
            );

            return;
        }


        String branchNo =
                sessionStorage
                        .getSelectedBranchNo();


        String branchName =
                sessionStorage
                        .getSelectedBranchName();


        String displayName =
                branchName;


        if (branchNo != null &&
                !branchNo.trim().isEmpty()) {

            displayName =
                    branchNo
                            + " - "
                            + branchName;
        }


        txtBranchSelector.setText(
                displayName,
                false
        );
    }


    // =====================================================
    // REFRESH CURRENT FRAGMENT
    // =====================================================

    private void refreshCurrentFragment() {

        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.dashboardContainer
                        );


        if (fragment instanceof MembersFragment) {

            ((MembersFragment) fragment)
                    .refreshMembers();
        }


        if (fragment instanceof arafath65.minime.gymmanagementui.MainDashboard.fragment.PlansFragment) {
            ((arafath65.minime.gymmanagementui.MainDashboard.fragment.PlansFragment) fragment)
                    .refreshCurrentTab();
        }


        if (fragment instanceof MembershipPlansFragment) {

            ((MembershipPlansFragment) fragment)
                    .refreshMembershipPlans();
        }


        if (fragment instanceof ProgramsFragment) {

            ((ProgramsFragment) fragment)
                    .refreshPrograms();
        }


        if (fragment instanceof BenefitsFragment) {

            ((BenefitsFragment) fragment)
                    .refreshBenefits();
        }
    }


    // =====================================================
    // OPEN FRAGMENT
    // =====================================================

    private void openFragment(
            Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.activity_enter,
                        R.anim.activity_exit
                )
                .replace(
                        R.id.dashboardContainer,
                        fragment
                )
                .commit();
    }


    // =====================================================
    // BRANCH ERROR
    // =====================================================

    private void showBranchError(
            String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}