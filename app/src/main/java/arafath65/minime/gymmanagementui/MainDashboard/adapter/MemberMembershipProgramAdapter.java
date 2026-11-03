package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MemberMembershipProgramAdapter
        extends RecyclerView.Adapter<MemberMembershipProgramAdapter.ViewHolder> {

    private final List<MemberMembershipProgramResponse> programs =
            new ArrayList<>();


    // =====================================================
    // SET DATA
    // =====================================================

    public void setPrograms(
            List<MemberMembershipProgramResponse> data) {

        programs.clear();

        if (data != null) {
            programs.addAll(data);
        }

        notifyDataSetChanged();
    }


    // =====================================================
    // GET ITEM
    // =====================================================

    public MemberMembershipProgramResponse getItem(
            int position) {

        return programs.get(position);
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
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_member_membership_program,
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

        MemberMembershipProgramResponse program =
                programs.get(position);

        holder.bind(program);
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return programs.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtProgramName;
        private final TextView txtProgramCode;
        private final TextView txtProgramDuration;
        private final TextView txtProgramFee;
        private final TextView txtProgramDates;
        private final TextView txtProgramStatus;
        private final TextView txtProgramDescription;


        ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtProgramName =
                    itemView.findViewById(
                            R.id.txtProgramName
                    );

            txtProgramCode =
                    itemView.findViewById(
                            R.id.txtProgramCode
                    );

            txtProgramDuration =
                    itemView.findViewById(
                            R.id.txtProgramDuration
                    );

            txtProgramFee =
                    itemView.findViewById(
                            R.id.txtProgramFee
                    );

            txtProgramDates =
                    itemView.findViewById(
                            R.id.txtProgramDates
                    );

            txtProgramStatus =
                    itemView.findViewById(
                            R.id.txtProgramStatus
                    );

            txtProgramDescription =
                    itemView.findViewById(
                            R.id.txtProgramDescription
                    );
        }


        void bind(
                MemberMembershipProgramResponse program) {

            txtProgramName.setText(
                    safe(
                            program.getProgramName()
                    )
            );


            txtProgramCode.setText(
                    safe(
                            program.getProgramCode()
                    )
            );


            Integer duration =
                    program.getDurationDays();


            if (duration != null) {

                txtProgramDuration.setText(
                        duration + " Days"
                );

            } else {

                txtProgramDuration.setText(
                        "-"
                );
            }


            txtProgramFee.setText(
                    "Rs. " +
                            formatMoney(
                                    program.getFee()
                            )
            );


            String start =
                    safe(
                            program.getStartDate()
                    );

            String end =
                    safe(
                            program.getEndDate()
                    );


            txtProgramDates.setText(
                    start + "  →  " + end
            );


            boolean active =
                    Boolean.TRUE.equals(
                            program.getStatus()
                    );


            txtProgramStatus.setText(
                    active
                            ? "ACTIVE"
                            : "INACTIVE"
            );


            txtProgramStatus.setTextColor(
                    itemView.getContext()
                            .getColor(
                                    active
                                            ? R.color.green_primary
                                            : R.color.text_secondary
                            )
            );


            txtProgramDescription.setText(
                    safe(
                            program.getDescription()
                    )
            );
        }


        private String safe(
                String value) {

            if (value == null ||
                    value.trim().isEmpty()) {

                return "-";
            }

            return value;
        }


        private String formatMoney(
                BigDecimal value) {

            if (value == null) {
                return "0.00";
            }

            return value
                    .setScale(
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
                    .toPlainString();
        }
    }
}