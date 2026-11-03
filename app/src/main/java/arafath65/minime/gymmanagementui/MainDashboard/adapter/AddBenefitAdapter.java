package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.BenefitBranchResponse;
import arafath65.minime.gymmanagementui.model.BenefitResponse;

public class AddBenefitAdapter
        extends RecyclerView.Adapter<
        AddBenefitAdapter.ViewHolder> {


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnAddClickListener {

        void onAddClick(
                BenefitResponse benefit
        );
    }


    // =====================================================
    // DATA
    // =====================================================

    private final List<BenefitResponse>
            benefits =
            new ArrayList<>();


    private final Set<Integer>
            assignedBenefitIds =
            new HashSet<>();


    private final OnAddClickListener listener;


    private Integer branchId;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AddBenefitAdapter(
            OnAddClickListener listener) {

        this.listener =
                listener;
    }


    // =====================================================
    // SET BRANCH ID
    // =====================================================

    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;

        notifyDataSetChanged();
    }


    // =====================================================
    // SET BENEFITS
    // =====================================================

    public void setBenefits(
            List<BenefitResponse> data) {

        benefits.clear();


        if (data != null) {

            benefits.addAll(
                    data
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // SET ASSIGNED IDS
    // =====================================================

    public void setAssignedBenefitIds(
            Set<Integer> ids) {

        assignedBenefitIds.clear();


        if (ids != null) {

            assignedBenefitIds.addAll(
                    ids
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // CREATE
    // =====================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_add_benefit,
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

        BenefitResponse benefit =
                benefits.get(
                        position
                );


        holder.txtBenefitCode.setText(
                safe(
                        benefit.getBenefitCode()
                )
        );


        holder.txtBenefitName.setText(
                safe(
                        benefit.getBenefitName()
                )
        );


        String type =
                benefit.getBillingType();


        BigDecimal fee =
                getEffectiveFee(
                        benefit
                );


        String feeText =
                formatAmount(
                        fee
                );


        if (type == null
                || type.trim().isEmpty()) {

            holder.txtBenefitDetails.setText(
                    feeText
            );

        } else {

            holder.txtBenefitDetails.setText(
                    type
                            + " • "
                            + feeText
            );
        }


        Integer benefitId =
                benefit.getBenefitId();


        boolean alreadyAdded =
                benefitId != null
                        && assignedBenefitIds.contains(
                        benefitId
                );


        holder.btnAddBenefit.setEnabled(
                !alreadyAdded
        );


        holder.btnAddBenefit.setText(
                alreadyAdded
                        ? "ADDED"
                        : "ADD"
        );


        holder.btnAddBenefit.setAlpha(
                alreadyAdded
                        ? 0.55f
                        : 1.0f
        );


        holder.btnAddBenefit.setOnClickListener(
                v -> {

                    if (alreadyAdded) {
                        return;
                    }


                    if (listener != null) {

                        listener.onAddClick(
                                benefit
                        );
                    }
                }
        );
    }


    // =====================================================
    // EFFECTIVE FEE
    // =====================================================

    private BigDecimal getEffectiveFee(
            BenefitResponse benefit) {

        if (benefit == null) {

            return BigDecimal.ZERO;
        }


        /*
         * Prefer the fee configured specifically
         * for the member's current branch.
         */

        if (branchId != null
                && benefit.getBranchAssignments() != null) {

            for (
                    BenefitBranchResponse assignment
                    : benefit.getBranchAssignments()
            ) {

                if (assignment == null
                        || assignment.getBranchId() == null) {

                    continue;
                }


                if (branchId.equals(
                        assignment.getBranchId()
                )) {

                    if (assignment.getFee() != null) {

                        return assignment.getFee();
                    }


                    /*
                     * NULL branch fee means the benefit
                     * uses its default fee.
                     */

                    return safeAmount(
                            benefit.getDefaultFee()
                    );
                }
            }
        }


        /*
         * Fallback to the benefit default fee.
         */

        return safeAmount(
                benefit.getDefaultFee()
        );
    }


    // =====================================================
    // SAFE AMOUNT
    // =====================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        if (amount == null) {

            return BigDecimal.ZERO;
        }


        return amount;
    }


    // =====================================================
    // AMOUNT
    // =====================================================

    private String formatAmount(
            BigDecimal amount) {

        if (amount == null) {

            return "Rs. 0.00";
        }


        return "Rs. "
                + amount.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }


    // =====================================================
    // SAFE
    // =====================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }


        return value;
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return benefits.size();
    }


    // =====================================================
    // HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtBenefitCode;

        TextView txtBenefitName;

        TextView txtBenefitDetails;

        MaterialButton btnAddBenefit;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            txtBenefitCode =
                    itemView.findViewById(
                            R.id.txtBenefitCode
                    );


            txtBenefitName =
                    itemView.findViewById(
                            R.id.txtBenefitName
                    );


            txtBenefitDetails =
                    itemView.findViewById(
                            R.id.txtBenefitDetails
                    );


            btnAddBenefit =
                    itemView.findViewById(
                            R.id.btnAddBenefit
                    );
        }
    }
}