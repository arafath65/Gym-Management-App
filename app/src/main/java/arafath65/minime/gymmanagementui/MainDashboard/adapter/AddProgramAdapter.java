package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.ProgramResponse;

public class AddProgramAdapter
        extends RecyclerView.Adapter<
        AddProgramAdapter.ViewHolder> {

    // =====================================================
    // LISTENER
    // =====================================================

    public interface OnAddClickListener {

        void onAddClick(
                ProgramResponse program
        );
    }


    // =====================================================
    // DATA
    // =====================================================

    private final List<ProgramResponse> programs =
            new ArrayList<>();


    private final Set<Integer>
            assignedProgramIds =
            new HashSet<>();


    private final OnAddClickListener listener;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AddProgramAdapter(
            OnAddClickListener listener) {

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
    // SET ASSIGNED PROGRAM IDS
    // =====================================================

    public void setAssignedProgramIds(
            Set<Integer> ids) {

        assignedProgramIds.clear();


        if (ids != null) {

            assignedProgramIds.addAll(
                    ids
            );
        }


        notifyDataSetChanged();
    }


    // =====================================================
    // CREATE
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
                                R.layout.item_add_program,
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


        holder.txtProgramCode.setText(
                safe(
                        program.getProgramCode()
                )
        );


        holder.txtProgramName.setText(
                safe(
                        program.getProgramName()
                )
        );


        Integer days =
                program.getDurationDays();


        holder.txtProgramDetails.setText(
                days == null
                        ? "-"
                        : days + " Days"
        );


        Integer programId =
                program.getProgramId();


        boolean alreadyAdded =
                programId != null
                        && assignedProgramIds.contains(
                        programId
                );


        holder.btnAddProgram.setEnabled(
                !alreadyAdded
        );


        holder.btnAddProgram.setText(
                alreadyAdded
                        ? "ADDED"
                        : "ADD"
        );


        holder.btnAddProgram.setAlpha(
                alreadyAdded
                        ? 0.55f
                        : 1.0f
        );


        holder.btnAddProgram.setOnClickListener(
                v -> {

                    if (alreadyAdded) {
                        return;
                    }


                    if (listener != null) {

                        listener.onAddClick(
                                program
                        );
                    }
                }
        );
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

        return programs.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtProgramCode;

        TextView txtProgramName;

        TextView txtProgramDetails;

        MaterialButton btnAddProgram;


        ViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


            txtProgramCode =
                    itemView.findViewById(
                            R.id.txtProgramCode
                    );


            txtProgramName =
                    itemView.findViewById(
                            R.id.txtProgramName
                    );


            txtProgramDetails =
                    itemView.findViewById(
                            R.id.txtProgramDetails
                    );


            btnAddProgram =
                    itemView.findViewById(
                            R.id.btnAddProgram
                    );
        }
    }
}