package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.MemberResponse;

public class MemberOverviewFragment extends Fragment {

    private MemberResponse member;


    // =====================================================
    // CREATE
    // =====================================================

    public static MemberOverviewFragment newInstance(
            MemberResponse member) {

        MemberOverviewFragment fragment =
                new MemberOverviewFragment();

        fragment.member = member;

        return fragment;
    }


    // =====================================================
    // VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_member_overview,
                container,
                false
        );
    }


    // =====================================================
    // AFTER VIEW CREATED
    // =====================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        if (member == null) {
            return;
        }


        initializeViews(
                view
        );


        displayMember();
    }


    // =====================================================
    // VIEWS
    // =====================================================

    private TextView txtOverviewFullName;
    private TextView txtOverviewGender;
    private TextView txtOverviewDob;
    private TextView txtOverviewNic;

    private TextView txtOverviewMobile;
    private TextView txtOverviewEmail;
    private TextView txtOverviewAddress;
    private TextView txtOverviewCity;

    private TextView txtOverviewEmergencyName;
    private TextView txtOverviewEmergencyMobile;
    private TextView txtOverviewEmergencyRelationship;

    private TextView txtOverviewOccupation;
    private TextView txtOverviewBloodGroup;
    private TextView txtOverviewMedical;
    private TextView txtOverviewGoal;

    private TextView txtOverviewBranch;
    private TextView txtOverviewJoinDate;

    private TextView txtOverviewNotes;


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews(
            View view) {

        txtOverviewFullName =
                view.findViewById(
                        R.id.txtOverviewFullName
                );

        txtOverviewGender =
                view.findViewById(
                        R.id.txtOverviewGender
                );

        txtOverviewDob =
                view.findViewById(
                        R.id.txtOverviewDob
                );

        txtOverviewNic =
                view.findViewById(
                        R.id.txtOverviewNic
                );


        txtOverviewMobile =
                view.findViewById(
                        R.id.txtOverviewMobile
                );

        txtOverviewEmail =
                view.findViewById(
                        R.id.txtOverviewEmail
                );

        txtOverviewAddress =
                view.findViewById(
                        R.id.txtOverviewAddress
                );

        txtOverviewCity =
                view.findViewById(
                        R.id.txtOverviewCity
                );


        txtOverviewEmergencyName =
                view.findViewById(
                        R.id.txtOverviewEmergencyName
                );

        txtOverviewEmergencyMobile =
                view.findViewById(
                        R.id.txtOverviewEmergencyMobile
                );

        txtOverviewEmergencyRelationship =
                view.findViewById(
                        R.id.txtOverviewEmergencyRelationship
                );


        txtOverviewOccupation =
                view.findViewById(
                        R.id.txtOverviewOccupation
                );

        txtOverviewBloodGroup =
                view.findViewById(
                        R.id.txtOverviewBloodGroup
                );

        txtOverviewMedical =
                view.findViewById(
                        R.id.txtOverviewMedical
                );

        txtOverviewGoal =
                view.findViewById(
                        R.id.txtOverviewGoal
                );


        txtOverviewBranch =
                view.findViewById(
                        R.id.txtOverviewBranch
                );

        txtOverviewJoinDate =
                view.findViewById(
                        R.id.txtOverviewJoinDate
                );


        txtOverviewNotes =
                view.findViewById(
                        R.id.txtOverviewNotes
                );
    }


    // =====================================================
    // DISPLAY MEMBER
    // =====================================================

    private void displayMember() {

        txtOverviewFullName.setText(
                value(
                        member.getFullName()
                )
        );


        txtOverviewGender.setText(
                "Gender: " +
                        value(
                                member.getGender()
                        )
        );


        txtOverviewDob.setText(
                "Date of Birth: " +
                        value(
                                member.getDateOfBirth()
                        )
        );


        txtOverviewNic.setText(
                "NIC: " +
                        value(
                                member.getNic()
                        )
        );


        // =================================================
        // CONTACT
        // =================================================

        txtOverviewMobile.setText(
                "Mobile: " +
                        value(
                                member.getMobile()
                        )
        );


        txtOverviewEmail.setText(
                "Email: " +
                        value(
                                member.getEmail()
                        )
        );


        txtOverviewAddress.setText(
                "Address: " +
                        value(
                                member.getAddress()
                        )
        );


        txtOverviewCity.setText(
                "City: " +
                        value(
                                member.getCity()
                        )
        );


        // =================================================
        // EMERGENCY
        // =================================================

        txtOverviewEmergencyName.setText(
                "Name: " +
                        value(
                                member.getEmergencyContactName()
                        )
        );


        txtOverviewEmergencyMobile.setText(
                "Mobile: " +
                        value(
                                member.getEmergencyContactMobile()
                        )
        );


        txtOverviewEmergencyRelationship.setText(
                "Relationship: " +
                        value(
                                member.getEmergencyContactRelationship()
                        )
        );


        // =================================================
        // HEALTH
        // =================================================

        txtOverviewOccupation.setText(
                "Occupation: " +
                        value(
                                member.getOccupation()
                        )
        );


        txtOverviewBloodGroup.setText(
                "Blood Group: " +
                        value(
                                member.getBloodGroup()
                        )
        );


        txtOverviewMedical.setText(
                "Medical Conditions: " +
                        value(
                                member.getMedicalConditions()
                        )
        );


        txtOverviewGoal.setText(
                "Goal: " +
                        value(
                                member.getGoal()
                        )
        );


        // =================================================
        // GYM
        // =================================================

        txtOverviewBranch.setText(
                "Branch: " +
                        value(
                                member.getBranchName()
                        )
        );


        txtOverviewJoinDate.setText(
                "Join Date: " +
                        value(
                                member.getJoinDate()
                        )
        );


        // =================================================
        // NOTES
        // =================================================

        txtOverviewNotes.setText(
                value(
                        member.getNotes()
                )
        );
    }


    // =====================================================
    // SAFE VALUE
    // =====================================================

    private String value(
            Object value) {

        if (value == null) {

            return "-";
        }

        String text =
                value.toString()
                        .trim();

        if (text.isEmpty()) {

            return "-";
        }

        return text;
    }
}