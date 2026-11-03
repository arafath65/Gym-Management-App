package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import arafath65.minime.gymmanagementui.R;

/**
 * Parent screen for configuring Membership Plans, Programs and Benefits.
 * Existing feature fragments are reused so their forms and API behavior stay intact.
 */
public class PlansFragment extends Fragment {

    private static final int TAB_MEMBERSHIP = 0;
    private static final int TAB_PROGRAMS = 1;
    private static final int TAB_BENEFITS = 2;

    private TabLayout tabLayout;
    private int selectedTab = TAB_MEMBERSHIP;

    public PlansFragment() {
        // Required empty public constructor.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabsPlans);
        tabLayout.addTab(tabLayout.newTab().setText("Membership"));
        tabLayout.addTab(tabLayout.newTab().setText("Programs"));
        tabLayout.addTab(tabLayout.newTab().setText("Benefits"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action required.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Keep the currently displayed screen.
            }
        });

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt("selectedPlansTab", TAB_MEMBERSHIP);
        }
        TabLayout.Tab initialTab = tabLayout.getTabAt(selectedTab);
        if (initialTab != null) {
            initialTab.select();
        } else {
            showTab(TAB_MEMBERSHIP);
        }
    }

    /**
     * Called by MainDashboard when the global branch selector changes.
     * Refreshes the currently visible child tab immediately.
     */
    public void refreshCurrentTab() {
        if (!isAdded()) {
            return;
        }

        Fragment current = getChildFragmentManager()
                .findFragmentById(R.id.plansTabContainer);

        if (current instanceof MembershipPlansFragment) {
            ((MembershipPlansFragment) current).refreshMembershipPlans();
        } else if (current instanceof ProgramsFragment) {
            ((ProgramsFragment) current).refreshPrograms();
        } else if (current instanceof BenefitsFragment) {
            ((BenefitsFragment) current).refreshBenefits();
        }
    }

    private void showTab(int position) {
        selectedTab = position;

        Fragment fragment;
        if (position == TAB_PROGRAMS) {
            fragment = new ProgramsFragment();
        } else if (position == TAB_BENEFITS) {
            fragment = new BenefitsFragment();
        } else {
            fragment = new MembershipPlansFragment();
        }

        getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.plansTabContainer, fragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("selectedPlansTab", selectedTab);
        super.onSaveInstanceState(outState);
    }
}
