package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipResponse;

public class MembershipHistoryAdapter
        extends RecyclerView.Adapter<MembershipHistoryAdapter.ViewHolder> {

    private final List<MemberMembershipResponse> memberships = new ArrayList<>();
    private final Set<Integer> expandedMembershipIds = new HashSet<>();


    // =====================================================
    // SET DATA
    // =====================================================

    public void setMemberships(List<MemberMembershipResponse> data) {

        memberships.clear();

        if (data != null) {
            memberships.addAll(data);
        }

        expandedMembershipIds.clear();

        notifyDataSetChanged();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_membership_history,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MemberMembershipResponse membership =
                memberships.get(position);

        Integer membershipId =
                membership.getMemberMembershipId();

        // -------------------------------------------------
        // PLAN NAME
        // -------------------------------------------------

        String planName = safe(membership.getPlanName());

        holder.txtPlanName.setText(
                planName.isEmpty() ? "-" : planName
        );


        // -------------------------------------------------
        // PLAN CODE
        // -------------------------------------------------

        String planCode = safe(membership.getPlanCode());

        holder.txtPlanCode.setText(
                planCode.isEmpty() ? "-" : planCode
        );


        // -------------------------------------------------
        // MEMBERSHIP NUMBER
        // -------------------------------------------------

        String membershipNumber =
                safe(membership.getMembershipNumber());

        holder.txtMembershipNumber.setText(
                membershipNumber.isEmpty()
                        ? "-"
                        : membershipNumber
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        String startDate =
                safe(membership.getStartDate());

        String endDate =
                safe(membership.getEndDate());

        String dateText;

        if (!startDate.isEmpty() && !endDate.isEmpty()) {

            dateText =
                    startDate
                            + " → "
                            + endDate;

        } else if (!startDate.isEmpty()) {

            dateText = startDate;

        } else if (!endDate.isEmpty()) {

            dateText = endDate;

        } else {

            dateText = "-";
        }

        holder.txtDates.setText(dateText);


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        String state = safe(membership.getMembershipState());

        holder.txtStatus.setText(
                state.isEmpty() ? "-" : state.toUpperCase()
        );

        if ("CANCELLED".equalsIgnoreCase(state)) {
            holder.txtStatus.setTextColor(
                    android.graphics.Color.WHITE
            );
            holder.txtStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#D32F2F")
                    )
            );
        } else if ("EXPIRED".equalsIgnoreCase(state)) {
            holder.txtStatus.setTextColor(
                    android.graphics.Color.WHITE
            );
            holder.txtStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#757575")
                    )
            );
        } else {
            holder.txtStatus.setTextColor(
                    android.graphics.Color.parseColor("#4CAF50")
            );
        }


        // -------------------------------------------------
        // FINANCIAL VALUES
        // -------------------------------------------------

        holder.txtTotal.setText(
                formatAmount(membership.getTotalAmount())
        );

        holder.txtPaid.setText(
                formatAmount(membership.getPaidAmount())
        );

        holder.txtBalance.setText(
                formatAmount(membership.getBalanceAmount())
        );


        // -------------------------------------------------
        // EXPANDED STATE
        // -------------------------------------------------

        boolean expanded =
                membershipId != null
                        && expandedMembershipIds.contains(membershipId);

        holder.layoutFinancials.setVisibility(
                expanded
                        ? View.VISIBLE
                        : View.GONE
        );

        holder.txtExpand.setText(
                expanded
                        ? "Tap to hide payment details"
                        : "Tap to view payment details"
        );


        // -------------------------------------------------
        // CLICK
        // -------------------------------------------------

        holder.itemView.setOnClickListener(v -> {

            if (membershipId == null) {
                return;
            }

            if (expandedMembershipIds.contains(membershipId)) {

                expandedMembershipIds.remove(membershipId);

            } else {

                expandedMembershipIds.add(membershipId);
            }

            notifyItemChanged(holder.getBindingAdapterPosition());
        });
    }


    // =====================================================
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {
        return memberships.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPlanName;
        TextView txtPlanCode;
        TextView txtMembershipNumber;
        TextView txtDates;
        TextView txtStatus;
        TextView txtExpand;

        TextView txtTotal;
        TextView txtPaid;
        TextView txtBalance;

        LinearLayout layoutFinancials;


        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtPlanName =
                    itemView.findViewById(
                            R.id.txtHistoryPlanName
                    );

            txtPlanCode =
                    itemView.findViewById(
                            R.id.txtHistoryPlanCode
                    );

            txtMembershipNumber =
                    itemView.findViewById(
                            R.id.txtHistoryMembershipNumber
                    );

            txtDates =
                    itemView.findViewById(
                            R.id.txtHistoryDates
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtHistoryStatus
                    );

            txtExpand =
                    itemView.findViewById(
                            R.id.txtHistoryExpand
                    );

            layoutFinancials =
                    itemView.findViewById(
                            R.id.layoutHistoryFinancials
                    );

            txtTotal =
                    itemView.findViewById(
                            R.id.txtHistoryTotal
                    );

            txtPaid =
                    itemView.findViewById(
                            R.id.txtHistoryPaid
                    );

            txtBalance =
                    itemView.findViewById(
                            R.id.txtHistoryBalance
                    );
        }
    }


    // =====================================================
    // SAFE STRING
    // =====================================================

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }


    // =====================================================
    // FORMAT AMOUNT
    // =====================================================

    private String formatAmount(BigDecimal amount) {

        if (amount == null) {
            return "Rs. 0.00";
        }

        return "Rs. "
                + String.format(
                java.util.Locale.US,
                "%,.2f",
                amount
        );
    }
}