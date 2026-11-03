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
import java.util.List;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipBenefitResponse;

public class MemberMembershipBenefitAdapter
        extends RecyclerView.Adapter<
        MemberMembershipBenefitAdapter.BenefitViewHolder> {


    // =====================================================
    // DATA
    // =====================================================

    private final List<MemberMembershipBenefitResponse>
            benefits =
            new ArrayList<>();


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnBenefitClickListener {

        void onBenefitClick(
                MemberMembershipBenefitResponse benefit
        );
    }


    private OnBenefitClickListener listener;


    // =====================================================
    // SET LISTENER
    // =====================================================

    public void setOnBenefitClickListener(
            OnBenefitClickListener listener) {

        this.listener = listener;
    }


    // =====================================================
    // SET BENEFITS
    // =====================================================

    public void setBenefits(
            List<MemberMembershipBenefitResponse> data) {

        benefits.clear();

        if (data != null) {
            benefits.addAll(data);
        }

        notifyDataSetChanged();
    }


    // =====================================================
    // CREATE HOLDER
    // =====================================================

    @NonNull
    @Override
    public BenefitViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_member_membership_benefit,
                        parent,
                        false
                );

        return new BenefitViewHolder(view);
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull BenefitViewHolder holder,
            int position) {

        MemberMembershipBenefitResponse benefit =
                benefits.get(position);

        holder.bind(
                benefit
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
    // VIEW HOLDER
    // =====================================================

    class BenefitViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtBenefitName;
        private final TextView txtBenefitCode;
        private final TextView txtBenefitType;
        private final TextView txtBenefitFee;
        private final TextView txtBenefitDates;
        private final TextView txtBenefitStatus;


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


            txtBenefitType =
                    itemView.findViewById(
                            R.id.txtBenefitType
                    );


            txtBenefitFee =
                    itemView.findViewById(
                            R.id.txtBenefitFee
                    );


            txtBenefitDates =
                    itemView.findViewById(
                            R.id.txtBenefitDates
                    );


            txtBenefitStatus =
                    itemView.findViewById(
                            R.id.txtBenefitStatus
                    );
        }


        void bind(
                MemberMembershipBenefitResponse benefit) {

            txtBenefitName.setText(
                    safeText(
                            benefit.getBenefitName()
                    )
            );


            txtBenefitCode.setText(
                    safeText(
                            benefit.getBenefitCode()
                    )
            );


            txtBenefitType.setText(
                    formatType(
                            benefit.getSourceType()
                    )
            );


            txtBenefitFee.setText(
                    "Rs. " +
                            formatMoney(
                                    benefit.getFee()
                            )
            );


            String start =
                    safeText(
                            benefit.getStartDate()
                    );


            String end =
                    safeText(
                            benefit.getEndDate()
                    );


            if ("-".equals(end)) {

                txtBenefitDates.setText(
                        start
                );

            } else {

                txtBenefitDates.setText(
                        start +
                                "  →  " +
                                end
                );
            }


            boolean active =
                    Boolean.TRUE.equals(
                            benefit.getStatus()
                    );


            txtBenefitStatus.setText(
                    active
                            ? "ACTIVE"
                            : "INACTIVE"
            );


            itemView.setOnClickListener(v -> {

                // INCLUDED plan benefits are display-only.
                // They must not be treated as removable manual assignments.
                if ("INCLUDED".equalsIgnoreCase(benefit.getSourceType())
                        && benefit.getMemberMembershipBenefitId() == null) {
                    return;
                }

                if (listener != null) {
                    listener.onBenefitClick(benefit);
                }
            });
        }
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }


    // =====================================================
    // TYPE
    // =====================================================

    private String formatType(
            String sourceType) {

        if (sourceType == null ||
                sourceType.trim().isEmpty()) {

            return "-";
        }

        return sourceType;
    }


    // =====================================================
    // MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal value) {

        if (value == null) {
            return "0.00";
        }

        return value
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }
}