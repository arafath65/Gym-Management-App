package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.BenefitResponse;


public class AddMembershipPlanBenefitAdapter
        extends RecyclerView.Adapter<
        AddMembershipPlanBenefitAdapter.ViewHolder> {


    // =====================================================
    // DATA
    // =====================================================

    private final List<BenefitResponse> benefits =
            new ArrayList<>();


    // =====================================================
    // SELECTED BENEFIT IDS
    // =====================================================

    private final Set<Integer> selectedBenefitIds =
            new HashSet<>();


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnBenefitSelectionChangedListener {

        void onSelectionChanged(
                Set<Integer> selectedBenefitIds
        );
    }


    private OnBenefitSelectionChangedListener
            selectionChangedListener;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AddMembershipPlanBenefitAdapter() {
    }


    // =====================================================
    // SET LISTENER
    // =====================================================

    public void setOnBenefitSelectionChangedListener(
            OnBenefitSelectionChangedListener listener) {

        this.selectionChangedListener =
                listener;
    }


    // =====================================================
    // SET BENEFITS
    // =====================================================

    public void setBenefits(
            List<BenefitResponse> list) {

        benefits.clear();

        if (list != null) {

            benefits.addAll(
                    list
            );
        }

        notifyDataSetChanged();
    }


    // =====================================================
    // SET SELECTED BENEFITS
    // =====================================================

    public void setSelectedBenefitIds(
            Set<Integer> ids) {

        selectedBenefitIds.clear();

        if (ids != null) {

            selectedBenefitIds.addAll(
                    ids
            );
        }

        notifyDataSetChanged();
    }


    // =====================================================
    // GET SELECTED BENEFITS
    // =====================================================

    public Set<Integer> getSelectedBenefitIds() {

        return new HashSet<>(
                selectedBenefitIds
        );
    }


    // =====================================================
    // CLEAR SELECTION
    // =====================================================

    public void clearSelection() {

        selectedBenefitIds.clear();

        notifyDataSetChanged();

        notifySelectionChanged();
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
                        R.layout.item_membership_plan_benefit,
                        parent,
                        false
                );


        return new ViewHolder(
                view
        );
    }


    // =====================================================
    // BIND VIEW HOLDER
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        BenefitResponse benefit =
                benefits.get(
                        position
                );


        // -------------------------------------------------
        // RESET CHECK LISTENER
        // -------------------------------------------------

        holder.checkBenefit
                .setOnCheckedChangeListener(
                        null
                );


        // -------------------------------------------------
        // BENEFIT NAME
        // -------------------------------------------------

        holder.txtName.setText(
                safeText(
                        benefit.getBenefitName(),
                        "Unnamed Benefit"
                )
        );


        // -------------------------------------------------
        // BENEFIT CODE
        // -------------------------------------------------

        holder.txtCode.setText(
                safeText(
                        benefit.getBenefitCode(),
                        ""
                )
        );


        // -------------------------------------------------
        // BILLING TYPE
        // -------------------------------------------------

        holder.txtDetails.setText(
                getBillingTypeText(
                        benefit
                )
        );


        // -------------------------------------------------
        // BENEFIT ID
        // -------------------------------------------------

        Integer benefitId =
                benefit.getBenefitId();


        // -------------------------------------------------
        // CURRENT SELECTION
        // -------------------------------------------------

        boolean selected =
                benefitId != null
                        && selectedBenefitIds.contains(
                        benefitId
                );


        holder.checkBenefit.setChecked(
                selected
        );


        // -------------------------------------------------
        // CHECKBOX LISTENER
        // -------------------------------------------------

        holder.checkBenefit
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            if (benefitId == null) {

                                return;
                            }


                            if (isChecked) {

                                selectedBenefitIds.add(
                                        benefitId
                                );

                            } else {

                                selectedBenefitIds.remove(
                                        benefitId
                                );
                            }


                            notifySelectionChanged();
                        }
                );


        // -------------------------------------------------
        // WHOLE ITEM CLICK
        // -------------------------------------------------

        holder.itemView.setOnClickListener(
                v -> {

                    if (benefitId == null) {

                        return;
                    }


                    holder.checkBenefit.setChecked(
                            !holder.checkBenefit.isChecked()
                    );
                }
        );
    }


    // =====================================================
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return benefits.size();
    }


    // =====================================================
    // NOTIFY SELECTION
    // =====================================================

    private void notifySelectionChanged() {

        if (selectionChangedListener != null) {

            selectionChangedListener
                    .onSelectionChanged(
                            new HashSet<>(
                                    selectedBenefitIds
                            )
                    );
        }
    }


    // =====================================================
    // BILLING TYPE TEXT
    // =====================================================

    private String getBillingTypeText(
            BenefitResponse benefit) {

        if (benefit.getBillingType() == null) {

            return "";
        }


        String billingType =
                String.valueOf(
                        benefit.getBillingType()
                );


        switch (
                billingType
                        .trim()
                        .toUpperCase()
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
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value,
            String fallback) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }


        return value;
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {


        CheckBox checkBenefit;

        TextView txtName;

        TextView txtCode;

        TextView txtDetails;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            checkBenefit =
                    itemView.findViewById(
                            R.id.checkMembershipPlanBenefit
                    );


            txtName =
                    itemView.findViewById(
                            R.id.txtMembershipPlanBenefitName
                    );


            txtCode =
                    itemView.findViewById(
                            R.id.txtMembershipPlanBenefitCode
                    );


            txtDetails =
                    itemView.findViewById(
                            R.id.txtMembershipPlanBenefitDetails
                    );
        }
    }
}