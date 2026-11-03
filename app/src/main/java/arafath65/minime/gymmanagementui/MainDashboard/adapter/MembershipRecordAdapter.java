package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;


public class MembershipRecordAdapter
        extends RecyclerView.Adapter<
        MembershipRecordAdapter.ViewHolder> {


    private final List<MemberMembershipResponse>
            memberships =
            new ArrayList<>();


    private final Set<Integer>
            expandedMembershipIds =
            new HashSet<>();


    // =====================================================
    // SET DATA
    // =====================================================

    public void setMemberships(
            List<MemberMembershipResponse> data) {

        memberships.clear();

        expandedMembershipIds.clear();


        if (data != null) {

            memberships.addAll(
                    data
            );
        }


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
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_membership_record,
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

        MemberMembershipResponse membership =
                memberships.get(
                        position
                );


        Integer membershipId =
                membership.getMemberMembershipId();


        holder.txtRecordPlanName.setText(
                safe(
                        membership.getPlanName()
                )
        );


        holder.txtRecordPlanCode.setText(
                safe(
                        membership.getPlanCode()
                )
        );


        holder.txtRecordMembershipNumber.setText(
                safe(
                        membership.getMembershipNumber()
                )
        );


        holder.txtRecordStartDate.setText(
                safe(
                        membership.getStartDate()
                )
        );


        holder.txtRecordEndDate.setText(
                safe(
                        membership.getEndDate()
                )
        );


        String state =
                safe(
                        membership.getMembershipState()
                );


        holder.txtRecordState.setText(
                state
        );


        holder.txtRecordTotalAmount.setText(
                formatMoney(
                        membership.getTotalAmount()
                )
        );


        holder.txtRecordPaidAmount.setText(
                formatMoney(
                        membership.getPaidAmount()
                )
        );


        holder.txtRecordBalanceAmount.setText(
                formatMoney(
                        membership.getBalanceAmount()
                )
        );


        boolean expanded =
                membershipId != null
                        && expandedMembershipIds.contains(
                        membershipId
                );


        holder.layoutRecordFinancial.setVisibility(
                expanded
                        ? View.VISIBLE
                        : View.GONE
        );


        holder.txtRecordExpand.setText(
                expanded
                        ? "▲"
                        : "▼"
        );


        holder.itemView.setOnClickListener(
                v -> {

                    if (membershipId == null) {
                        return;
                    }


                    if (expandedMembershipIds.contains(
                            membershipId
                    )) {

                        expandedMembershipIds.remove(
                                membershipId
                        );

                    } else {

                        expandedMembershipIds.add(
                                membershipId
                        );
                    }


                    int adapterPosition =
                            holder.getBindingAdapterPosition();


                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        notifyItemChanged(
                                adapterPosition
                        );
                    }
                }
        );
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return memberships.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {


        TextView txtRecordPlanName;

        TextView txtRecordPlanCode;

        TextView txtRecordMembershipNumber;

        TextView txtRecordStartDate;

        TextView txtRecordEndDate;

        TextView txtRecordState;

        TextView txtRecordExpand;

        View layoutRecordFinancial;

        TextView txtRecordTotalAmount;

        TextView txtRecordPaidAmount;

        TextView txtRecordBalanceAmount;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            txtRecordPlanName =
                    itemView.findViewById(
                            R.id.txtRecordPlanName
                    );


            txtRecordPlanCode =
                    itemView.findViewById(
                            R.id.txtRecordPlanCode
                    );


            txtRecordMembershipNumber =
                    itemView.findViewById(
                            R.id.txtRecordMembershipNumber
                    );


            txtRecordStartDate =
                    itemView.findViewById(
                            R.id.txtRecordStartDate
                    );


            txtRecordEndDate =
                    itemView.findViewById(
                            R.id.txtRecordEndDate
                    );


            txtRecordState =
                    itemView.findViewById(
                            R.id.txtRecordState
                    );


            txtRecordExpand =
                    itemView.findViewById(
                            R.id.txtRecordExpand
                    );


            layoutRecordFinancial =
                    itemView.findViewById(
                            R.id.layoutRecordFinancial
                    );


            txtRecordTotalAmount =
                    itemView.findViewById(
                            R.id.txtRecordTotalAmount
                    );


            txtRecordPaidAmount =
                    itemView.findViewById(
                            R.id.txtRecordPaidAmount
                    );


            txtRecordBalanceAmount =
                    itemView.findViewById(
                            R.id.txtRecordBalanceAmount
                    );
        }
    }


    // =====================================================
    // SAFE TEXT
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
    // MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal value) {

        if (value == null) {

            return "Rs. 0.00";
        }


        return "Rs. "
                + value.setScale(
                2,
                RoundingMode.HALF_UP
        ).toPlainString();
    }
}