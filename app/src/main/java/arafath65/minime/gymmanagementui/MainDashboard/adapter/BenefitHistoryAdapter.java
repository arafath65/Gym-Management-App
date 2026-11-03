package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;

public class BenefitHistoryAdapter
        extends RecyclerView.Adapter<BenefitHistoryAdapter.ViewHolder> {

    private final List<MemberMembershipBenefitResponse> benefits =
            new ArrayList<>();


    // =====================================================
    // SET DATA
    // =====================================================

    public void setBenefits(List<MemberMembershipBenefitResponse> data) {

        benefits.clear();

        if (data != null) {
            benefits.addAll(data);
        }

        notifyDataSetChanged();
    }


    // =====================================================
    // CREATE VIEW
    // =====================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_benefit_history,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }


    // =====================================================
    // BIND VIEW
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MemberMembershipBenefitResponse benefit =
                benefits.get(position);


        // -------------------------------------------------
        // BENEFIT NAME
        // -------------------------------------------------

        String benefitName =
                safe(benefit.getBenefitName());

        holder.txtBenefitName.setText(
                benefitName.isEmpty()
                        ? "-"
                        : benefitName
        );


        // -------------------------------------------------
        // BENEFIT CODE
        // -------------------------------------------------

        String benefitCode =
                safe(benefit.getBenefitCode());

        holder.txtBenefitCode.setText(
                benefitCode.isEmpty()
                        ? "-"
                        : benefitCode
        );


        // -------------------------------------------------
        // MEMBERSHIP ID
        // -------------------------------------------------

        Integer membershipId =
                benefit.getMemberMembershipId();

        holder.txtMembershipNumber.setText(
                membershipId == null
                        ? "-"
                        : "Membership ID: " + membershipId
        );


        // -------------------------------------------------
        // SOURCE TYPE
        // -------------------------------------------------

        String sourceType =
                safe(benefit.getSourceType());

        holder.txtSource.setText(
                sourceType.isEmpty()
                        ? "-"
                        : sourceType.toUpperCase(Locale.US)
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        String startDate =
                safe(benefit.getStartDate());

        String endDate =
                safe(benefit.getEndDate());

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
        // FEE
        // -------------------------------------------------

        BigDecimal fee =
                benefit.getFee();

        holder.txtFee.setText(
                "Fee: " + formatAmount(fee)
        );


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        Boolean status =
                benefit.getStatus();

        String statusText =
                Boolean.TRUE.equals(status)
                        ? "ACTIVE"
                        : "INACTIVE";

        holder.txtStatus.setText(statusText);
    }


    // =====================================================
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {
        return benefits.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtBenefitName;
        TextView txtBenefitCode;
        TextView txtMembershipNumber;
        TextView txtSource;
        TextView txtDates;
        TextView txtFee;
        TextView txtStatus;


        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtBenefitName =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryName
                    );

            txtBenefitCode =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryCode
                    );

            txtMembershipNumber =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryMembershipNumber
                    );

            txtSource =
                    itemView.findViewById(
                            R.id.txtBenefitHistorySource
                    );

            txtDates =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryDates
                    );

            txtFee =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryFee
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtBenefitHistoryStatus
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
                Locale.US,
                "%,.2f",
                amount
        );
    }
}