package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.ProgramBranchResponse;
import arafath65.minime.gymmanagementui.model.ProgramResponse;


public class ProgramAdapter
        extends RecyclerView.Adapter<
        ProgramAdapter.ProgramViewHolder> {


    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnProgramActionListener {

        void onEditProgram(
                ProgramResponse program
        );


        void onActivateProgram(
                ProgramResponse program
        );


        void onDeactivateProgram(
                ProgramResponse program
        );
    }


    // =====================================================
    // DATA
    // =====================================================

    private final List<ProgramResponse> programList;

    private final OnProgramActionListener listener;


    /*
     * Global selected branch.
     *
     * null = All Branches
     */
    private Integer selectedBranchId;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProgramAdapter(
            List<ProgramResponse> programList,
            OnProgramActionListener listener) {

        this.programList =
                programList;

        this.listener =
                listener;
    }


    // =====================================================
    // SET SELECTED BRANCH
    // =====================================================

    public void setSelectedBranchId(
            Integer branchId) {

        this.selectedBranchId =
                branchId;

        notifyDataSetChanged();
    }


    // =====================================================
    // GET SELECTED BRANCH
    // =====================================================

    public Integer getSelectedBranchId() {

        return selectedBranchId;
    }


    // =====================================================
    // SET PROGRAMS
    // =====================================================

    public void setPrograms(
            List<ProgramResponse> programs) {

        programList.clear();


        if (programs != null) {

            programList.addAll(
                    programs
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // CREATE VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_program,
                                parent,
                                false
                        );


        return new ProgramViewHolder(
                view
        );
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ProgramViewHolder holder,
            int position) {

        ProgramResponse program =
                programList.get(position);


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        holder.txtProgramName.setText(
                safeText(
                        program.getProgramName()
                )
        );


        // -------------------------------------------------
        // CODE
        // -------------------------------------------------

        holder.txtProgramCode.setText(
                safeText(
                        program.getProgramCode()
                )
        );


        // -------------------------------------------------
        // DURATION
        // -------------------------------------------------

        Integer duration =
                program.getDurationDays();


        if (duration == null) {

            duration = 0;
        }


        holder.txtProgramDuration.setText(
                duration
                        + (
                        duration == 1
                                ? " Day"
                                : " Days"
                )
        );


        // -------------------------------------------------
        // FEE
        // -------------------------------------------------

        BigDecimal fee =
                getFeeForSelectedBranch(
                        program
                );


        holder.txtProgramFee.setText(
                formatMoney(
                        fee
                )
        );


        // -------------------------------------------------
// BRANCH
// -------------------------------------------------

        StringBuilder branchText =
                new StringBuilder();

        if (program.getBranchAssignments() != null) {

            for (
                    ProgramBranchResponse branch
                    : program.getBranchAssignments()
            ) {

                if (branch == null) {
                    continue;
                }

                String branchName =
                        branch.getBranchName();

                if (branchName == null
                        || branchName.trim().isEmpty()) {

                    continue;
                }

                if (branchText.length() > 0) {

                    branchText.append(", ");
                }

                branchText.append(
                        branchName.trim()
                );
            }
        }

        if (branchText.length() == 0) {

            holder.txtProgramBranch.setText(
                    "All Branches"
            );

        } else {

            holder.txtProgramBranch.setText(
                    branchText.toString()
            );
        }


        // -------------------------------------------------
        // DESCRIPTION
        // -------------------------------------------------

        String description =
                program.getDescription();


        if (description == null
                || description.trim().isEmpty()) {

            holder.txtProgramDescription
                    .setVisibility(
                            View.GONE
                    );

        } else {

            holder.txtProgramDescription
                    .setVisibility(
                            View.VISIBLE
                    );


            holder.txtProgramDescription.setText(
                    description.trim()
            );
        }


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        boolean active =
                Boolean.TRUE.equals(
                        program.getStatus()
                );


        holder.txtProgramStatus.setText(
                active
                        ? "ACTIVE"
                        : "INACTIVE"
        );


        if (active) {

            holder.txtProgramStatus.setTextColor(
                    holder.itemView
                            .getContext()
                            .getResources()
                            .getColor(
                                    R.color.green_primary,
                                    holder.itemView
                                            .getContext()
                                            .getTheme()
                            )
            );

        } else {

            holder.txtProgramStatus.setTextColor(
                    0xFFE05A5A
            );
        }


        // -------------------------------------------------
        // MORE
        // -------------------------------------------------

        holder.btnProgramMore.setOnClickListener(
                v -> showProgramMenu(
                        program,
                        v
                )
        );
    }


    // =====================================================
    // GET FEE FOR SELECTED BRANCH
    // =====================================================

    private BigDecimal getFeeForSelectedBranch(
            ProgramResponse program) {

        if (program == null) {

            return null;
        }


        List<ProgramBranchResponse>
                assignments =
                program.getBranchAssignments();


        if (assignments == null
                || assignments.isEmpty()) {

            return null;
        }


        // =================================================
        // SPECIFIC GLOBAL BRANCH
        // =================================================

        if (selectedBranchId != null) {

            for (
                    ProgramBranchResponse assignment
                    : assignments
            ) {

                if (assignment == null
                        || assignment.getBranchId() == null) {

                    continue;
                }


                if (selectedBranchId.equals(
                        assignment.getBranchId()
                )) {

                    return assignment.getFee();
                }
            }


            return null;
        }


        // =================================================
        // ALL BRANCHES
        // =================================================

        /*
         * If All Branches is selected, try to determine
         * whether all assignments have the same fee.
         *
         * If they do, show that fee.
         *
         * If they have different fees, return null
         * because one program has multiple prices.
         */

        BigDecimal firstFee = null;


        boolean differentFees = false;


        for (
                ProgramBranchResponse assignment
                : assignments
        ) {

            if (assignment == null) {
                continue;
            }


            BigDecimal fee =
                    assignment.getFee();


            if (fee == null) {
                continue;
            }


            if (firstFee == null) {

                firstFee = fee;

            } else if (
                    firstFee.compareTo(fee) != 0
            ) {

                differentFees = true;

                break;
            }
        }


        if (!differentFees) {

            return firstFee;
        }


        /*
         * Different fees across branches.
         *
         * The card will show "Multiple" instead of
         * incorrectly displaying one branch's price.
         */
        return null;
    }


    // =====================================================
    // BRANCH TEXT
    // =====================================================

    private String getBranchText(
            ProgramResponse program) {

        if (program == null
                || program.getBranchAssignments() == null
                || program.getBranchAssignments().isEmpty()) {

            return "-";
        }


        /*
         * Specific branch selected.
         */
        if (selectedBranchId != null) {

            for (
                    ProgramBranchResponse assignment
                    : program.getBranchAssignments()
            ) {

                if (assignment == null
                        || assignment.getBranchId() == null) {

                    continue;
                }


                if (selectedBranchId.equals(
                        assignment.getBranchId()
                )) {

                    String branchName =
                            safeText(
                                    assignment.getBranchName()
                            );


                    String branchNo =
                            safeText(
                                    assignment.getBranchNo()
                            );


                    if (!"-".equals(branchNo)
                            && !"-".equals(branchName)) {

                        return branchNo
                                + " - "
                                + branchName;
                    }


                    if (!"-".equals(branchName)) {

                        return branchName;
                    }


                    return branchNo;
                }
            }


            return "-";
        }


        // =================================================
        // ALL BRANCHES
        // =================================================

        List<String> names =
                new ArrayList<>();


        for (
                ProgramBranchResponse assignment
                : program.getBranchAssignments()
        ) {

            if (assignment == null) {
                continue;
            }


            String branchName =
                    safeText(
                            assignment.getBranchName()
                    );


            String branchNo =
                    safeText(
                            assignment.getBranchNo()
                    );


            String displayName;


            if (!"-".equals(branchNo)
                    && !"-".equals(branchName)) {

                displayName =
                        branchNo
                                + " - "
                                + branchName;

            } else if (!"-".equals(branchName)) {

                displayName =
                        branchName;

            } else {

                displayName =
                        branchNo;
            }


            if (!names.contains(
                    displayName
            )) {

                names.add(
                        displayName
                );
            }
        }


        if (names.isEmpty()) {

            return "-";
        }


        return android.text.TextUtils.join(
                ", ",
                names
        );
    }


    // =====================================================
    // MENU
    // =====================================================

    private void showProgramMenu(
            ProgramResponse program,
            View anchor) {

        boolean active =
                Boolean.TRUE.equals(
                        program.getStatus()
                );


        String[] options;


        if (active) {

            options =
                    new String[]{
                            "Edit Program",
                            "Deactivate Program"
                    };

        } else {

            options =
                    new String[]{
                            "Edit Program",
                            "Activate Program"
                    };
        }


        new AlertDialog.Builder(
                anchor.getContext()
        )
                .setTitle(
                        safeText(
                                program.getProgramName()
                        )
                )
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                if (listener != null) {

                                    listener.onEditProgram(
                                            program
                                    );
                                }

                            } else {

                                if (active) {

                                    if (listener != null) {

                                        listener.onDeactivateProgram(
                                                program
                                        );
                                    }

                                } else {

                                    if (listener != null) {

                                        listener.onActivateProgram(
                                                program
                                        );
                                    }
                                }
                            }
                        }
                )
                .show();
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return programList == null
                ? 0
                : programList.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ProgramViewHolder
            extends RecyclerView.ViewHolder {


        CardView programCard;

        TextView txtProgramName;

        TextView txtProgramCode;

        TextView txtProgramDuration;

        TextView txtProgramFee;

        TextView txtProgramBranch;

        TextView txtProgramStatus;

        TextView txtProgramDescription;

        ImageButton btnProgramMore;



        ProgramViewHolder(
                @NonNull View itemView) {

            super(itemView);


            programCard =
                    itemView.findViewById(
                            R.id.programCard
                    );


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


            /*
             * Optional branch TextView.
             *
             * This allows the adapter to work even if
             * your current item_program.xml does not yet
             * contain it.
             */
            int branchId =
                    itemView.getResources()
                            .getIdentifier(
                                    "txtProgramBranch",
                                    "id",
                                    itemView.getContext()
                                            .getPackageName()
                            );


            if (branchId != 0) {

                txtProgramBranch =
                        itemView.findViewById(
                                branchId
                        );

            } else {

                txtProgramBranch = null;
            }


            txtProgramStatus =
                    itemView.findViewById(
                            R.id.txtProgramStatus
                    );


            txtProgramDescription =
                    itemView.findViewById(
                            R.id.txtProgramDescription
                    );


            btnProgramMore =
                    itemView.findViewById(
                            R.id.btnProgramMore
                    );
        }
    }


    // =====================================================
    // MONEY
    // =====================================================

    private String formatMoney(
            BigDecimal value) {

        /*
         * No fee.
         */
        if (value == null) {

            return "Multiple";
        }


        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        Locale.US
                );


        formatter.setMinimumFractionDigits(
                2
        );


        formatter.setMaximumFractionDigits(
                2
        );


        return "Rs. "
                + formatter.format(
                value
        );
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }


        return value.trim();
    }
}