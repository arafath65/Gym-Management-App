package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MembershipPlanBenefitResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanBranchResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;


public class MembershipPlanAdapter
        extends RecyclerView.Adapter<MembershipPlanAdapter.ViewHolder> {


    // =====================================================
    // DATA
    // =====================================================

    private final List<MembershipPlanResponse> plans;


    /*
     * Stores the IDs of plans currently expanded.
     *
     * We use plan ID instead of RecyclerView position so
     * filtering/searching does not cause the wrong card
     * to expand.
     */
    private final Set<Integer> expandedPlanIds =
            new HashSet<>();


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnPlanMoreClickListener {

        void onMoreClick(
                MembershipPlanResponse plan,
                View anchor
        );
    }


    private OnPlanMoreClickListener
            moreClickListener;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembershipPlanAdapter(
            List<MembershipPlanResponse> plans) {

        this.plans = plans;
    }


    // =====================================================
    // MORE LISTENER
    // =====================================================

    public void setOnPlanMoreClickListener(
            OnPlanMoreClickListener listener) {

        this.moreClickListener =
                listener;
    }


    // =====================================================
    // CREATE VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_membership_plan,
                        parent,
                        false
                );


        return new ViewHolder(
                view
        );
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MembershipPlanResponse plan =
                plans.get(position);


        // =================================================
        // PLAN CODE
        // =================================================

        holder.txtPlanCode.setText(
                safeText(
                        plan.getPlanCode()
                )
        );


        // =================================================
        // PLAN NAME
        // =================================================

        holder.txtPlanName.setText(
                safeText(
                        plan.getPlanName()
                )
        );


        // =================================================
        // PLAN DETAILS
        // =================================================

        holder.txtPlanDetails.setText(
                buildPlanDetails(
                        plan
                )
        );


        // =================================================
        // EXPANDED STATE
        // =================================================

        boolean expanded =
                isPlanExpanded(
                        plan
                );


        holder.layoutMembershipPlanBenefits
                .setVisibility(
                        expanded
                                ? View.VISIBLE
                                : View.GONE
                );


        if (expanded) {

            bindBenefits(
                    holder,
                    plan
            );
        }


        // =================================================
        // THREE DOT MENU
        // =================================================

        holder.btnMorePlan.setOnClickListener(
                v -> {

                    if (moreClickListener != null) {

                        moreClickListener.onMoreClick(
                                plan,
                                v
                        );
                    }
                }
        );


        /*
         * Prevent the three-dot button click from
         * triggering the card expand/collapse.
         */
        holder.btnMorePlan.setOnTouchListener(
                (v, event) -> {

                    v.performClick();

                    return true;
                }
        );


        // =================================================
        // CARD CLICK → EXPAND / COLLAPSE
        // =================================================

        holder.cardMembershipPlan.setOnClickListener(
                v -> {

                    togglePlan(
                            plan
                    );


                    int adapterPosition =
                            holder.getBindingAdapterPosition();


                    if (adapterPosition !=
                            RecyclerView.NO_POSITION) {

                        notifyItemChanged(
                                adapterPosition
                        );
                    }
                }
        );
    }


    // =====================================================
    // TOGGLE PLAN
    // =====================================================

    private void togglePlan(
            MembershipPlanResponse plan) {

        if (plan == null
                || plan.getMembershipPlanId() == null) {

            return;
        }


        Integer planId =
                plan.getMembershipPlanId();


        if (expandedPlanIds.contains(
                planId
        )) {

            expandedPlanIds.remove(
                    planId
            );

        } else {

            expandedPlanIds.add(
                    planId
            );
        }
    }


    // =====================================================
    // CHECK EXPANDED
    // =====================================================

    private boolean isPlanExpanded(
            MembershipPlanResponse plan) {

        return plan != null
                && plan.getMembershipPlanId() != null
                && expandedPlanIds.contains(
                plan.getMembershipPlanId()
        );
    }


    // =====================================================
    // BIND BENEFITS
    // =====================================================

    private void bindBenefits(
            ViewHolder holder,
            MembershipPlanResponse plan) {

        holder.layoutPlanBenefitsList
                .removeAllViews();


        holder.txtNoPlanBenefits
                .setVisibility(
                        View.GONE
                );


        List<MembershipPlanBenefitResponse>
                benefits =
                plan.getBenefits();


        if (benefits == null
                || benefits.isEmpty()) {

            holder.txtNoPlanBenefits
                    .setVisibility(
                            View.VISIBLE
                    );

            return;
        }


        boolean hasActiveBenefit =
                false;


        for (
                MembershipPlanBenefitResponse benefit
                : benefits
        ) {

            if (benefit == null) {
                continue;
            }


            /*
             * Only show active benefits.
             */
            if (benefit.getStatus() != null
                    && !Boolean.TRUE.equals(
                    benefit.getStatus()
            )) {

                continue;
            }


            hasActiveBenefit =
                    true;


            holder.layoutPlanBenefitsList
                    .addView(
                            createBenefitView(
                                    holder.itemView,
                                    benefit
                            )
                    );
        }


        if (!hasActiveBenefit) {

            holder.txtNoPlanBenefits
                    .setVisibility(
                            View.VISIBLE
                    );
        }
    }


    // =====================================================
    // CREATE BENEFIT VIEW
    // =====================================================

    private View createBenefitView(
            View itemView,
            MembershipPlanBenefitResponse benefit) {

        LinearLayout row =
                new LinearLayout(
                        itemView.getContext()
                );


        row.setOrientation(
                LinearLayout.HORIZONTAL
        );


        row.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );


        LinearLayout.LayoutParams
                rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        rowParams.bottomMargin =
                dp(
                        itemView,
                        8
                );


        row.setLayoutParams(
                rowParams
        );


        // =================================================
        // CHECK MARK
        // =================================================

        TextView check =
                new TextView(
                        itemView.getContext()
                );


        check.setText(
                "✓"
        );


        check.setTextColor(
                ContextCompat.getColor(
                        itemView.getContext(),
                        R.color.green_light
                )
        );


        check.setTextSize(
                16
        );


        check.setTypeface(
                null,
                Typeface.BOLD
        );


        LinearLayout.LayoutParams
                checkParams =
                new LinearLayout.LayoutParams(
                        dp(itemView, 24),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        check.setLayoutParams(
                checkParams
        );


        row.addView(
                check
        );


        // =================================================
        // BENEFIT INFORMATION
        // =================================================

        LinearLayout info =
                new LinearLayout(
                        itemView.getContext()
                );


        info.setOrientation(
                LinearLayout.VERTICAL
        );


        info.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        // -------------------------------------------------
        // BENEFIT NAME
        // -------------------------------------------------

        TextView name =
                new TextView(
                        itemView.getContext()
                );


        name.setText(
                safeText(
                        benefit.getBenefitName()
                )
        );


        name.setTextColor(
                ContextCompat.getColor(
                        itemView.getContext(),
                        R.color.text_primary
                )
        );


        name.setTextSize(
                12
        );


        name.setTypeface(
                null,
                Typeface.BOLD
        );


        info.addView(
                name
        );


        // -------------------------------------------------
        // BENEFIT CODE
        // -------------------------------------------------

        String code =
                benefit.getBenefitCode();


        if (code != null
                && !code.trim().isEmpty()) {

            TextView codeText =
                    new TextView(
                            itemView.getContext()
                    );


            codeText.setText(
                    code
            );


            codeText.setTextColor(
                    ContextCompat.getColor(
                            itemView.getContext(),
                            R.color.text_secondary
                    )
            );


            codeText.setTextSize(
                    10
            );


            LinearLayout.LayoutParams
                    codeParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            codeParams.topMargin =
                    dp(
                            itemView,
                            2
                    );


            codeText.setLayoutParams(
                    codeParams
            );


            info.addView(
                    codeText
            );
        }


        row.addView(
                info
        );


        return row;
    }


    // =====================================================
    // DP
    // =====================================================

    private int dp(
            View view,
            int value) {

        float density =
                view.getResources()
                        .getDisplayMetrics()
                        .density;


        return Math.round(
                value * density
        );
    }


    // =====================================================
    // BUILD PLAN DETAILS
    // =====================================================

    private String buildPlanDetails(
            MembershipPlanResponse plan) {

        if (plan == null) {
            return "-";
        }


        StringBuilder details =
                new StringBuilder();


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        String duration =
                formatDuration(
                        plan
                );


        if (!duration.equals("-")) {

            details.append(
                    duration
            );
        }


        // -------------------------------------------------
        // PRICE
        // -------------------------------------------------

        String price =
                formatPrice(
                        plan.getBranchAssignments()
                );


        if (!price.equals("Rs. 0.00")
                || details.length() == 0) {

            if (details.length() > 0) {

                details.append(
                        " • "
                );
            }


            details.append(
                    price
            );
        }


        // -------------------------------------------------
        // BILLING
        // -------------------------------------------------

        String billing =
                formatBilling(
                        plan
                );


        if (!billing.equals("-")) {

            if (details.length() > 0) {

                details.append(
                        " • "
                );
            }


            details.append(
                    billing
            );
        }


        // -------------------------------------------------
        // ACCESS
        // -------------------------------------------------

        String access =
                formatAccess(
                        plan.getAccessType()
                );


        if (!access.equals("-")) {

            if (details.length() > 0) {

                details.append(
                        " • "
                );
            }


            details.append(
                    access
            );
        }


        if (details.length() == 0) {

            return "-";
        }


        return details.toString();
    }


    // =====================================================
    // DURATION
    // =====================================================

    private String formatDuration(
            MembershipPlanResponse plan) {

        if (plan == null) {
            return "-";
        }


        String type =
                plan.getDurationType();


        Integer value =
                plan.getDurationValue();


        if (type == null
                || type.trim().isEmpty()) {

            return "-";
        }


        String normalizedType =
                type
                        .replace(
                                "_",
                                " "
                        )
                        .trim();


        // -------------------------------------------------
        // UNLIMITED
        // -------------------------------------------------

        if ("UNLIMITED".equalsIgnoreCase(
                normalizedType
        )) {

            return "Unlimited";
        }


        if (value == null) {

            return normalizedType;
        }


        // -------------------------------------------------
        // DAYS
        // -------------------------------------------------

        if ("DAYS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Day"
                    : value + " Days";
        }


        // -------------------------------------------------
        // MONTHS
        // -------------------------------------------------

        if ("MONTHS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Month"
                    : value + " Months";
        }


        // -------------------------------------------------
        // YEARS
        // -------------------------------------------------

        if ("YEARS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Year"
                    : value + " Years";
        }


        return value
                + " "
                + normalizedType;
    }


    // =====================================================
    // BILLING
    // =====================================================

    private String formatBilling(
            MembershipPlanResponse plan) {

        if (plan == null) {
            return "-";
        }


        String billingType =
                plan.getBillingType();


        if (billingType == null
                || billingType.trim().isEmpty()) {

            return "-";
        }


        return billingType
                .replace(
                        "_",
                        " "
                )
                .toUpperCase(
                        Locale.ENGLISH
                );
    }


    // =====================================================
    // ACCESS
    // =====================================================

    private String formatAccess(
            String accessType) {

        if (accessType == null
                || accessType.trim().isEmpty()) {

            return "-";
        }


        return accessType
                .replace(
                        "_",
                        " "
                )
                .toUpperCase(
                        Locale.ENGLISH
                );
    }


    // =====================================================
    // PRICE
    // =====================================================

    private String formatPrice(
            List<MembershipPlanBranchResponse>
                    assignments) {

        if (assignments == null
                || assignments.isEmpty()) {

            return "Rs. 0.00";
        }


        BigDecimal firstPrice =
                BigDecimal.ZERO;


        boolean firstFound =
                false;


        boolean samePrice =
                true;


        for (
                MembershipPlanBranchResponse assignment
                : assignments
        ) {

            if (assignment == null) {
                continue;
            }


            BigDecimal price =
                    safeAmount(
                            assignment.getPrice()
                    );


            if (!firstFound) {

                firstPrice =
                        price;

                firstFound =
                        true;

            } else {

                if (price.compareTo(
                        firstPrice
                ) != 0) {

                    samePrice =
                            false;

                    break;
                }
            }
        }


        if (!firstFound) {

            return "Rs. 0.00";
        }


        if (samePrice) {

            return formatMoney(
                    firstPrice
            );
        }


        return "Multiple prices";
    }


    // =====================================================
    // MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal amount) {

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
                safeAmount(
                        amount
                )
        );
    }


    // =====================================================
    // SAFE AMOUNT
    // =====================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
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
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return plans == null
                ? 0
                : plans.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        MaterialCardView cardMembershipPlan;

        TextView txtPlanCode;

        TextView txtPlanName;

        TextView txtPlanDetails;

        MaterialButton btnMorePlan;

        LinearLayout layoutMembershipPlanBenefits;

        LinearLayout layoutPlanBenefitsList;

        TextView txtNoPlanBenefits;


        ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            cardMembershipPlan =
                    itemView.findViewById(
                            R.id.cardMembershipPlan
                    );


            txtPlanCode =
                    itemView.findViewById(
                            R.id.txtPlanCode
                    );


            txtPlanName =
                    itemView.findViewById(
                            R.id.txtPlanName
                    );


            txtPlanDetails =
                    itemView.findViewById(
                            R.id.txtPlanDetails
                    );


            btnMorePlan =
                    itemView.findViewById(
                            R.id.btnMorePlan
                    );


            layoutMembershipPlanBenefits =
                    itemView.findViewById(
                            R.id.layoutMembershipPlanBenefits
                    );


            layoutPlanBenefitsList =
                    itemView.findViewById(
                            R.id.layoutPlanBenefitsList
                    );


            txtNoPlanBenefits =
                    itemView.findViewById(
                            R.id.txtNoPlanBenefits
                    );
        }
    }
}