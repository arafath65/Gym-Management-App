package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.BenefitBranchResponse;
import arafath65.minime.gymmanagementui.model.BenefitResponse;

public class BenefitsAdapter
        extends RecyclerView.Adapter<
        BenefitsAdapter.BenefitViewHolder> {


    // =====================================================
    // DATA
    // =====================================================

    private final List<BenefitResponse> benefits =
            new ArrayList<>();


    // =====================================================
    // LISTENER
    // =====================================================

    private OnBenefitActionListener
            actionListener;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BenefitsAdapter(
            OnBenefitActionListener actionListener) {

        this.actionListener =
                actionListener;
    }


    // =====================================================
    // SET DATA
    // =====================================================

    public void setBenefits(
            List<BenefitResponse> newBenefits) {

        benefits.clear();


        if (newBenefits != null) {

            benefits.addAll(
                    newBenefits
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // GET ITEM
    // =====================================================

    public BenefitResponse getItem(
            int position) {

        if (position < 0
                || position >= benefits.size()) {

            return null;
        }


        return benefits.get(
                position
        );
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return benefits.size();
    }


    // =====================================================
    // CREATE
    // =====================================================

    @NonNull
    @Override
    public BenefitViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_benefit,
                                parent,
                                false
                        );


        return new BenefitViewHolder(
                view
        );
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull BenefitViewHolder holder,
            int position) {

        BenefitResponse benefit =
                benefits.get(
                        position
                );


        holder.bind(
                benefit
        );
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    class BenefitViewHolder
            extends RecyclerView.ViewHolder {


        private final TextView txtBenefitName;

        private final TextView txtBenefitCode;

        private final TextView txtBenefitBillingType;

        private final TextView txtBenefitFee;

        private final TextView txtBenefitStatus;

        private final TextView txtBenefitDescription;

        private final ImageButton btnBenefitMore;


        private final LinearLayout
                contentContainer;


        BenefitViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtBenefitName =
                    itemView.findViewById(
                            R.id.txtBenefitName
                    );


            txtBenefitCode =
                    itemView.findViewById(
                            R.id.txtBenefitCode
                    );


            txtBenefitBillingType =
                    itemView.findViewById(
                            R.id.txtBenefitBillingType
                    );


            txtBenefitFee =
                    itemView.findViewById(
                            R.id.txtBenefitFee
                    );


            txtBenefitStatus =
                    itemView.findViewById(
                            R.id.txtBenefitStatus
                    );


            txtBenefitDescription =
                    itemView.findViewById(
                            R.id.txtBenefitDescription
                    );


            btnBenefitMore =
                    itemView.findViewById(
                            R.id.btnBenefitMore
                    );


            contentContainer =
                    (LinearLayout)
                            txtBenefitDescription
                                    .getParent();
        }


        // =================================================
        // BIND
        // =================================================

        void bind(
                BenefitResponse benefit) {

            // ---------------------------------------------
            // NAME
            // ---------------------------------------------

            txtBenefitName.setText(
                    safeText(
                            benefit.getBenefitName()
                    )
            );


            // ---------------------------------------------
            // CODE
            // ---------------------------------------------

            txtBenefitCode.setText(
                    safeText(
                            benefit.getBenefitCode()
                    )
            );


            // ---------------------------------------------
            // BILLING
            // ---------------------------------------------

            txtBenefitBillingType.setText(
                    formatBillingType(
                            benefit.getBillingType()
                    )
            );


            // ---------------------------------------------
            // FEE
            // ---------------------------------------------

            txtBenefitFee.setText(
                    formatCurrency(
                            benefit.getDefaultFee()
                    )
            );


            // ---------------------------------------------
            // STATUS
            // ---------------------------------------------

            boolean active =
                    Boolean.TRUE.equals(
                            benefit.getStatus()
                    );


            txtBenefitStatus.setText(
                    active
                            ? "ACTIVE"
                            : "INACTIVE"
            );


            txtBenefitStatus.setTextColor(
                    ContextCompat.getColor(
                            itemView.getContext(),
                            active
                                    ? R.color.green_primary
                                    : R.color.red_primary
                    )
            );


            // ---------------------------------------------
            // DESCRIPTION
            // ---------------------------------------------

            String description =
                    benefit.getDescription();


            if (description == null
                    || description.trim().isEmpty()) {

                txtBenefitDescription.setVisibility(
                        View.GONE
                );

            } else {

                txtBenefitDescription.setVisibility(
                        View.VISIBLE
                );


                txtBenefitDescription.setText(
                        description.trim()
                );
            }


            // ---------------------------------------------
            // BRANCHES
            // ---------------------------------------------

            updateBranchDisplay(
                    benefit
            );


            // ---------------------------------------------
            // MORE
            // ---------------------------------------------

            btnBenefitMore.setOnClickListener(
                    view -> {

                        if (actionListener != null) {

                            actionListener
                                    .onBenefitMore(
                                            benefit,
                                            view
                                    );
                        }
                    }
            );


            // ---------------------------------------------
            // CARD CLICK
            // ---------------------------------------------

            itemView.setOnClickListener(
                    view -> {

                        if (actionListener != null) {

                            actionListener
                                    .onBenefitClick(
                                            benefit
                                    );
                        }
                    }
            );
        }


        // =================================================
        // BRANCH DISPLAY
        // =================================================

        private void updateBranchDisplay(
                BenefitResponse benefit) {

            // ---------------------------------------------
            // REMOVE OLD DYNAMIC BRANCH VIEWS
            // ---------------------------------------------

            for (int i =
                 contentContainer.getChildCount() - 1;
                 i >= 0;
                 i--) {

                View child =
                        contentContainer.getChildAt(i);


                if ("benefit_branch_display"
                        .equals(
                                child.getTag()
                        )) {

                    contentContainer.removeView(
                            child
                    );
                }
            }


            // ---------------------------------------------
            // CREATE BRANCH TEXT
            // ---------------------------------------------

            String branchText =
                    buildBranchText(
                            benefit
                    );


            if (branchText.isEmpty()) {
                return;
            }


            TextView branchView =
                    new TextView(
                            itemView.getContext()
                    );


            branchView.setTag(
                    "benefit_branch_display"
            );


            branchView.setText(
                    branchText
            );


            branchView.setTextColor(
                    ContextCompat.getColor(
                            itemView.getContext(),
                            R.color.text_secondary
                    )
            );


            branchView.setTextSize(
                    11
            );


            branchView.setTypeface(
                    null,
                    Typeface.NORMAL
            );


            branchView.setMaxLines(
                    2
            );


            branchView.setEllipsize(
                    android.text.TextUtils.TruncateAt.END
            );


            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            params.setMargins(
                    0,
                    8,
                    0,
                    0
            );


            branchView.setLayoutParams(
                    params
            );


            contentContainer.addView(
                    branchView
            );
        }


        // =================================================
        // BUILD BRANCH TEXT
        // =================================================

        private String buildBranchText(
                BenefitResponse benefit) {

            if (benefit == null) {
                return "";
            }


            // ---------------------------------------------
            // ALL BRANCHES
            // ---------------------------------------------

            if ("ALL_BRANCHES".equalsIgnoreCase(
                    safeText(
                            benefit.getBranchMode()
                    )
            )) {

                return "Branches: All Branches";
            }


            // ---------------------------------------------
            // SELECTED BRANCHES
            // ---------------------------------------------

            List<BenefitBranchResponse>
                    assignments =
                    benefit.getBranchAssignments();


            if (assignments == null
                    || assignments.isEmpty()) {

                return "";
            }


            StringBuilder builder =
                    new StringBuilder(
                            "Branches: "
                    );


            int count = 0;


            for (BenefitBranchResponse assignment :
                    assignments) {

                if (assignment == null) {
                    continue;
                }


                String branchName =
                        safeText(
                                assignment.getBranchName()
                        );


                String branchNo =
                        safeText(
                                assignment.getBranchNo()
                        );


                String display;


                if (!branchNo.isEmpty()
                        && !branchName.isEmpty()) {

                    display =
                            branchNo
                                    + " - "
                                    + branchName;

                } else if (!branchName.isEmpty()) {

                    display =
                            branchName;

                } else if (!branchNo.isEmpty()) {

                    display =
                            branchNo;

                } else {

                    continue;
                }


                if (count > 0) {

                    builder.append(
                            ", "
                    );
                }


                builder.append(
                        display
                );


                count++;
            }


            if (count == 0) {
                return "";
            }


            return builder.toString();
        }
    }


    // =====================================================
    // BILLING FORMAT
    // =====================================================

    private String formatBillingType(
            String billingType) {

        if (billingType == null
                || billingType.trim().isEmpty()) {

            return "-";
        }


        switch (
                billingType
                        .trim()
                        .toUpperCase(
                                Locale.US
                        )
        ) {

            case "ONE_TIME":

                return "One Time";


            case "MONTHLY":

                return "Monthly";


            case "MEMBERSHIP_PERIOD":

                return "Membership Period";


            default:

                return billingType;
        }
    }


    // =====================================================
    // CURRENCY
    // =====================================================

    private String formatCurrency(
            BigDecimal amount) {

        if (amount == null) {

            amount =
                    BigDecimal.ZERO;
        }


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


        return value.trim();
    }


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnBenefitActionListener {

        void onBenefitClick(
                BenefitResponse benefit
        );


        void onBenefitMore(
                BenefitResponse benefit,
                View anchor
        );
    }
}