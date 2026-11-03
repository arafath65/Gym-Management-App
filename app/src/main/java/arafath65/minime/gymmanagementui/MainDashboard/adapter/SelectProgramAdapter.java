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

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.ProgramBranchResponse;
import arafath65.minime.gymmanagementui.model.ProgramResponse;


public class SelectProgramAdapter
        extends RecyclerView.Adapter<SelectProgramAdapter.ViewHolder> {


    // =====================================================
    // DATA
    // =====================================================

    private final List<ProgramResponse> programs =
            new ArrayList<>();


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnProgramClickListener {

        void onProgramClick(
                ProgramResponse program
        );
    }


    private final OnProgramClickListener listener;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public SelectProgramAdapter(
            OnProgramClickListener listener) {

        this.listener =
                listener;
    }


    // =====================================================
    // SET PROGRAMS
    // =====================================================

    public void setPrograms(
            List<ProgramResponse> data) {

        programs.clear();

        if (data != null) {

            programs.addAll(
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
                        R.layout.item_select_program,
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

        ProgramResponse program =
                programs.get(
                        position
                );


        holder.txtProgramName.setText(
                safeText(
                        program.getProgramName()
                )
        );


        holder.txtProgramCode.setText(
                safeText(
                        program.getProgramCode()
                )
        );


        Integer duration =
                program.getDurationDays();


        holder.txtDuration.setText(
                duration != null
                        ? duration + " Days"
                        : "-"
        );


        ProgramBranchResponse branch =
                findActiveBranch(
                        program,
                        holder.itemView.getTag() instanceof Integer
                                ? (Integer) holder.itemView.getTag()
                                : null
                );


        /*
         * The branch ID is supplied through setBranchId().
         * If no branch is supplied, we simply display the
         * first active assignment.
         */

        if (branch == null) {

            branch =
                    findFirstActiveBranch(
                            program
                    );
        }


        if (branch != null) {

            holder.txtBranch.setText(
                    safeText(
                            branch.getBranchName()
                    )
            );


            holder.txtFee.setText(
                    "Rs. " +
                            formatMoney(
                                    branch.getFee()
                            )
            );

        } else {

            holder.txtBranch.setText(
                    "-"
            );


            holder.txtFee.setText(
                    "Rs. 0.00"
            );
        }


        holder.itemView.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onProgramClick(
                                program
                        );
                    }
                }
        );
    }


    // =====================================================
    // BRANCH ID
    // =====================================================

    private Integer branchId;


    public void setBranchId(
            Integer branchId) {

        this.branchId =
                branchId;

        notifyDataSetChanged();
    }


    // =====================================================
    // FIND BRANCH
    // =====================================================

    private ProgramBranchResponse findActiveBranch(

            ProgramResponse program,

            Integer ignored) {

        if (program == null ||
                program.getBranchAssignments() == null) {

            return null;
        }


        if (branchId == null) {

            return null;
        }


        for (
                ProgramBranchResponse branch
                : program.getBranchAssignments()) {


            if (branch == null ||
                    branch.getBranchId() == null) {

                continue;
            }


            if (branch.getBranchId().equals(
                    branchId
            )
                    &&
                    Boolean.TRUE.equals(
                            branch.getStatus()
                    )) {

                return branch;
            }
        }


        return null;
    }


    // =====================================================
    // FIRST ACTIVE BRANCH
    // =====================================================

    private ProgramBranchResponse findFirstActiveBranch(

            ProgramResponse program) {

        if (program == null ||
                program.getBranchAssignments() == null) {

            return null;
        }


        for (
                ProgramBranchResponse branch
                : program.getBranchAssignments()) {


            if (branch == null) {
                continue;
            }


            if (Boolean.TRUE.equals(
                    branch.getStatus()
            )) {

                return branch;
            }
        }


        return null;
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
        TextView txtDuration;
        TextView txtFee;
        TextView txtBranch;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            txtProgramName =
                    itemView.findViewById(
                            R.id.txtSelectProgramName
                    );


            txtProgramCode =
                    itemView.findViewById(
                            R.id.txtSelectProgramCode
                    );


            txtDuration =
                    itemView.findViewById(
                            R.id.txtSelectProgramDuration
                    );


            txtFee =
                    itemView.findViewById(
                            R.id.txtSelectProgramFee
                    );


            txtBranch =
                    itemView.findViewById(
                            R.id.txtSelectProgramBranch
                    );
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
                        java.math.RoundingMode.HALF_UP
                )
                .toPlainString();
    }
}