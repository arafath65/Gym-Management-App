package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberMembershipProgramResponse;

public class ProgramHistoryAdapter
        extends RecyclerView.Adapter<ProgramHistoryAdapter.ViewHolder> {

    private final List<MemberMembershipProgramResponse> programs =
            new ArrayList<>();


    // =====================================================
    // SET DATA
    // =====================================================

    public void setPrograms(List<MemberMembershipProgramResponse> data) {

        programs.clear();

        if (data != null) {
            programs.addAll(data);
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
                        R.layout.item_program_history,
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

        MemberMembershipProgramResponse program =
                programs.get(position);


        // -------------------------------------------------
        // PROGRAM NAME
        // -------------------------------------------------

        String programName =
                safe(program.getProgramName());

        holder.txtProgramName.setText(
                programName.isEmpty()
                        ? "-"
                        : programName
        );


        // -------------------------------------------------
        // PROGRAM CODE
        // -------------------------------------------------

        String programCode =
                safe(program.getProgramCode());

        holder.txtProgramCode.setText(
                programCode.isEmpty()
                        ? "-"
                        : programCode
        );


        // -------------------------------------------------
        // MEMBERSHIP ID
        // -------------------------------------------------

        Integer membershipId =
                program.getMemberMembershipId();

        holder.txtMembershipNumber.setText(
                membershipId == null
                        ? "-"
                        : "Membership ID: " + membershipId
        );


        // -------------------------------------------------
        // DATES
        // -------------------------------------------------

        String startDate =
                safe(program.getStartDate());

        String endDate =
                safe(program.getEndDate());

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

        Boolean status =
                program.getStatus();

        String statusText =
                Boolean.TRUE.equals(status)
                        ? "ACTIVE"
                        : "INACTIVE";

        holder.txtStatus.setText(statusText);


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        String branchName =
                safe(program.getBranchName());

        if (branchName.isEmpty()) {

            holder.txtBranch.setVisibility(View.GONE);

        } else {

            holder.txtBranch.setVisibility(View.VISIBLE);
            holder.txtBranch.setText(branchName);
        }
    }


    // =====================================================
    // ITEM COUNT
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

        TextView txtProgramName;
        TextView txtProgramCode;
        TextView txtMembershipNumber;
        TextView txtDates;
        TextView txtStatus;
        TextView txtBranch;


        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtProgramName =
                    itemView.findViewById(
                            R.id.txtProgramHistoryName
                    );

            txtProgramCode =
                    itemView.findViewById(
                            R.id.txtProgramHistoryCode
                    );

            txtMembershipNumber =
                    itemView.findViewById(
                            R.id.txtProgramHistoryMembershipNumber
                    );

            txtDates =
                    itemView.findViewById(
                            R.id.txtProgramHistoryDates
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtProgramHistoryStatus
                    );

            txtBranch =
                    itemView.findViewById(
                            R.id.txtProgramHistoryBranch
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
}