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
import java.util.List;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MembershipPlanBranchResponse;
import arafath65.minime.gymmanagementui.model.MembershipPlanResponse;

public class AddMembershipPlanAdapter
        extends RecyclerView.Adapter<
        AddMembershipPlanAdapter.ViewHolder> {

    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnAddClickListener {

        void onAddClick(
                MembershipPlanResponse plan
        );
    }


    // =====================================================
    // DATA
    // =====================================================

    private final List<MembershipPlanResponse> plans =
            new ArrayList<>();


    private final OnAddClickListener listener;


    // =====================================================
    // CURRENT MEMBERSHIP PLAN
    // =====================================================

    private Integer currentMembershipPlanId;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AddMembershipPlanAdapter(
            OnAddClickListener listener) {

        this.listener =
                listener;
    }


    // =====================================================
    // SET PLANS
    // =====================================================

    public void setPlans(
            List<MembershipPlanResponse> data) {

        plans.clear();


        if (data != null) {

            plans.addAll(
                    data
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // SET CURRENT PLAN
    // =====================================================

    public void setCurrentMembershipPlanId(
            Integer membershipPlanId) {

        this.currentMembershipPlanId =
                membershipPlanId;


        notifyDataSetChanged();
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
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_add_membership_plan,
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
                plans.get(
                        position
                );


        holder.txtPlanName.setText(
                safe(
                        plan.getPlanName()
                )
        );


        holder.txtPlanCode.setText(
                safe(
                        plan.getPlanCode()
                )
        );


        holder.txtPlanDuration.setText(
                buildDuration(
                        plan
                )
        );


        holder.txtPlanPrice.setText(
                formatPlanPrice(
                        plan
                )
        );


        // =================================================
        // CURRENT PLAN CHECK
        // =================================================

        boolean alreadyAdded =
                currentMembershipPlanId != null
                        && plan.getMembershipPlanId() != null
                        && currentMembershipPlanId.equals(
                        plan.getMembershipPlanId()
                );


        holder.btnAddPlan.setEnabled(
                !alreadyAdded
        );


        holder.btnAddPlan.setText(
                alreadyAdded
                        ? "ADDED"
                        : "ADD"
        );


        holder.btnAddPlan.setAlpha(
                alreadyAdded
                        ? 0.55f
                        : 1.0f
        );


        holder.btnAddPlan.setOnClickListener(
                v -> {

                    if (alreadyAdded) {
                        return;
                    }


                    if (listener != null) {

                        listener.onAddClick(
                                plan
                        );
                    }
                }
        );
    }


    // =====================================================
    // DURATION
    // =====================================================

    private String buildDuration(
            MembershipPlanResponse plan) {

        if (plan == null) {
            return "-";
        }


        Integer value =
                plan.getDurationValue();


        String type =
                plan.getDurationType();


        if (type == null
                || type.trim().isEmpty()) {

            return "-";
        }


        if ("UNLIMITED".equalsIgnoreCase(
                type
        )) {

            return "Unlimited";
        }


        if (value == null) {

            return type;
        }


        if ("DAYS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Day"
                    : value + " Days";
        }


        if ("MONTHS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Month"
                    : value + " Months";
        }


        if ("YEARS".equalsIgnoreCase(
                type
        )) {

            return value == 1
                    ? "1 Year"
                    : value + " Years";
        }


        return value
                + " "
                + type.replace(
                "_",
                " "
        );
    }


    // =====================================================
    // PLAN PRICE
    // =====================================================

    private String formatPlanPrice(
            MembershipPlanResponse plan) {

        if (plan == null
                || plan.getBranchAssignments() == null
                || plan.getBranchAssignments().isEmpty()) {

            return "Rs. 0.00";
        }


        BigDecimal firstPrice =
                null;


        boolean multiplePrices =
                false;


        for (
                MembershipPlanBranchResponse assignment
                : plan.getBranchAssignments()
        ) {

            if (assignment == null
                    || assignment.getPrice() == null) {

                continue;
            }


            if (firstPrice == null) {

                firstPrice =
                        assignment.getPrice();

            } else if (
                    firstPrice.compareTo(
                            assignment.getPrice()
                    ) != 0
            ) {

                multiplePrices =
                        true;

                break;
            }
        }


        if (multiplePrices) {

            return "Multiple prices";
        }


        if (firstPrice == null) {

            return "Rs. 0.00";
        }


        return "Rs. "
                + firstPrice
                .setScale(
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

        return plans.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtPlanName;

        TextView txtPlanCode;

        TextView txtPlanDuration;

        TextView txtPlanPrice;

        MaterialButton btnAddPlan;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            txtPlanName =
                    itemView.findViewById(
                            R.id.txtPlanName
                    );


            txtPlanCode =
                    itemView.findViewById(
                            R.id.txtPlanCode
                    );


            txtPlanDuration =
                    itemView.findViewById(
                            R.id.txtPlanDuration
                    );


            txtPlanPrice =
                    itemView.findViewById(
                            R.id.txtPlanPrice
                    );


            btnAddPlan =
                    itemView.findViewById(
                            R.id.btnAddPlan
                    );
        }
    }
}