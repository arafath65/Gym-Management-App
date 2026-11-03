package arafath65.minime.gymmanagementui.MainDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import arafath65.minime.gymmanagementui.MainDashboard.adapter.MembershipPlanAdapter;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;

public class MembershipPlansActivity extends AppCompatActivity {

    // =====================================================
    // VIEWS
    // =====================================================

    private MaterialButton btnAddMembershipPlan;

    private TextInputEditText txtSearchMembershipPlan;

    private AutoCompleteTextView
            txtMembershipPlanStatusFilter;

    private RecyclerView recyclerMembershipPlans;

    private android.widget.TextView
            txtMembershipPlanCount;


    // =====================================================
    // DATA
    // =====================================================

    private final List<MembershipPlanResponse>
            allPlans =
            new ArrayList<>();

    private final List<MembershipPlanResponse>
            filteredPlans =
            new ArrayList<>();


    // =====================================================
    // ADAPTER
    // =====================================================

    private MembershipPlanAdapter
            membershipPlanAdapter;


    // =====================================================
    // LIFECYCLE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_membership_plan
        );


        // =================================================
        // INITIALIZE
        // =================================================

        initializeViews();

        setupStatusFilter();

        setupRecyclerView();

        setupListeners();

        loadMembershipPlans();
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        btnAddMembershipPlan =
                findViewById(
                        R.id.btnAddMembershipPlan
                );

        txtSearchMembershipPlan =
                findViewById(
                        R.id.txtSearchMembershipPlan
                );

        txtMembershipPlanStatusFilter =
                findViewById(
                        R.id.txtMembershipPlanStatusFilter
                );

        txtMembershipPlanCount =
                findViewById(
                        R.id.txtMembershipPlanCount
                );

        recyclerMembershipPlans =
                findViewById(
                        R.id.recyclerMembershipPlans
                );
    }


    // =====================================================
    // STATUS FILTER
    // =====================================================

    private void setupStatusFilter() {

        String[] statusItems = {
                "Active",
                "Inactive",
                "All"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        statusItems
                );

        txtMembershipPlanStatusFilter
                .setAdapter(adapter);

        txtMembershipPlanStatusFilter
                .setText(
                        "Active",
                        false
                );
    }


    // =====================================================
    // RECYCLER VIEW
    // =====================================================

    private void setupRecyclerView() {

        recyclerMembershipPlans
                .setLayoutManager(
                        new LinearLayoutManager(this)
                );

        recyclerMembershipPlans
                .setHasFixedSize(true);

        membershipPlanAdapter =
                new MembershipPlanAdapter(
                        filteredPlans
                );

        recyclerMembershipPlans
                .setAdapter(
                        membershipPlanAdapter
                );
    }


    // =====================================================
    // LISTENERS
    // =====================================================

    private void setupListeners() {

        // -------------------------------------------------
        // ADD PLAN
        // -------------------------------------------------

        btnAddMembershipPlan.setOnClickListener(
                view -> openAddMembershipPlan()
        );


        // -------------------------------------------------
        // SEARCH
        // -------------------------------------------------

        txtSearchMembershipPlan
                .addTextChangedListener(
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

                                applyFilters();
                            }

                            @Override
                            public void afterTextChanged(
                                    Editable s) {
                            }
                        }
                );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        txtMembershipPlanStatusFilter
                .setOnItemClickListener(
                        (parent, view, position, id) ->
                                applyFilters()
                );
    }


    // =====================================================
    // OPEN ADD PLAN
    // =====================================================

    private void openAddMembershipPlan() {

        Intent intent =
                new Intent(
                        MembershipPlansActivity.this,
                        AddMembershipPlanActivity.class
                );

        startActivity(intent);
    }


    // =====================================================
    // LOAD MEMBERSHIP PLANS
    // =====================================================

    private void loadMembershipPlans() {

        /*
         * TODO:
         *
         * Connect this method to your existing ApiService
         * implementation.
         *
         * Do NOT use:
         *
         *     SessionStorage.getDeviceToken()
         *
         * or:
         *
         *     SessionStorage.getTenantNo()
         *
         * because those methods are not available in
         * your current SessionStorage.
         *
         * Use the existing authenticated API mechanism
         * already used elsewhere in your application.
         */


        /*
         * Temporary empty state.
         *
         * The API callback should eventually call:
         *
         *     allPlans.clear();
         *     allPlans.addAll(responsePlans);
         *     applyFilters();
         *
         */

        allPlans.clear();

        applyFilters();
    }


    // =====================================================
    // APPLY FILTERS
    // =====================================================

    private void applyFilters() {

        String searchText = "";

        if (txtSearchMembershipPlan != null
                && txtSearchMembershipPlan
                .getText() != null) {

            searchText =
                    txtSearchMembershipPlan
                            .getText()
                            .toString()
                            .trim()
                            .toLowerCase();
        }


        String selectedStatus =
                "Active";

        if (txtMembershipPlanStatusFilter != null
                && txtMembershipPlanStatusFilter
                .getText() != null
                && !txtMembershipPlanStatusFilter
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

            selectedStatus =
                    txtMembershipPlanStatusFilter
                            .getText()
                            .toString()
                            .trim();
        }


        filteredPlans.clear();


        for (MembershipPlanResponse plan :
                allPlans) {


            // =============================================
            // SEARCH
            // =============================================

            boolean matchesSearch =
                    searchText.isEmpty();


            if (!matchesSearch) {

                String planCode =
                        safeLower(
                                plan.getPlanCode()
                        );

                String planName =
                        safeLower(
                                plan.getPlanName()
                        );

                String description =
                        safeLower(
                                plan.getDescription()
                        );


                matchesSearch =
                        planCode.contains(searchText)
                                || planName.contains(searchText)
                                || description.contains(searchText);
            }


            if (!matchesSearch) {
                continue;
            }


            // =============================================
            // STATUS
            // =============================================

            boolean matchesStatus =
                    true;


            if ("Active".equalsIgnoreCase(
                    selectedStatus)) {

                matchesStatus =
                        Boolean.TRUE.equals(
                                plan.getStatus()
                        );

            } else if ("Inactive".equalsIgnoreCase(
                    selectedStatus)) {

                matchesStatus =
                        Boolean.FALSE.equals(
                                plan.getStatus()
                        );
            }


            if (!matchesStatus) {
                continue;
            }


            // =============================================
            // ADD
            // =============================================

            filteredPlans.add(plan);
        }


        updateCount();

        if (membershipPlanAdapter != null) {

            membershipPlanAdapter.notifyDataSetChanged();
        }
    }


    // =====================================================
    // UPDATE COUNT
    // =====================================================

    private void updateCount() {

        if (txtMembershipPlanCount == null) {
            return;
        }

        txtMembershipPlanCount.setText(
                "Total Plans: "
                        + filteredPlans.size()
        );
    }


    // =====================================================
    // SAFE LOWER CASE
    // =====================================================

    private String safeLower(String value) {

        if (value == null) {
            return "";
        }

        return value.toLowerCase();
    }


    // =====================================================
    // REFRESH
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        /*
         * Refresh after returning from
         * Add/Edit Membership Plan.
         */

        if (recyclerMembershipPlans != null) {
            loadMembershipPlans();
        }
    }
}