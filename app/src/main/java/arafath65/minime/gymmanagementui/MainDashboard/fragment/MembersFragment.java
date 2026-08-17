package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import arafath65.minime.gymmanagementui.MainDashboard.AddMemberActivity;
import arafath65.minime.gymmanagementui.MainDashboard.activity.MemberProfileActivity;
import arafath65.minime.gymmanagementui.MainDashboard.adapter.MemberAdapter;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.dialog.MemberQrDialog;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.Member;
import arafath65.minime.gymmanagementui.model.MemberResponse;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MembersFragment
        extends Fragment
        implements MemberAdapter.OnMemberActionListener {


    // =====================================================
    // STORAGE
    // =====================================================

    private SessionStorage sessionStorage;

    private DeviceStorage deviceStorage;


    // =====================================================
    // VIEWS
    // =====================================================

    private RecyclerView recyclerMembers;

    private Button btnAddMember;

    private TextView txtTotalMembers;
    private TextInputEditText txtSearch;

    private ApiService apiService;

    private String tenantNo;

    private String deviceToken;


    // =====================================================
    // ADAPTER
    // =====================================================

    private MemberAdapter memberAdapter;


    // =====================================================
    // DATA
    // =====================================================

    private final List<Member> memberList =
            new ArrayList<>();

    private final List<Member> allMemberList =
            new ArrayList<>();


    // =====================================================
    // ADD / EDIT ACTIVITY RESULT
    // =====================================================

    private final ActivityResultLauncher<Intent>
            memberActivityLauncher =

            registerForActivityResult(
                    new ActivityResultContracts
                            .StartActivityForResult(),

                    result -> {

                        if (!isAdded()) {
                            return;
                        }


                        if (result.getResultCode()
                                == requireActivity()
                                .RESULT_OK) {

                            loadMembers();
                        }
                    }
            );


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MembersFragment() {

        // Required empty constructor
    }


    // =====================================================
    // ON ATTACH
    // =====================================================

    @Override
    public void onAttach(
            @NonNull android.content.Context context) {

        super.onAttach(
                context
        );


        sessionStorage =
                new SessionStorage(
                        context
                );


        deviceStorage =
                new DeviceStorage(
                        context
                );
    }


    // =====================================================
    // ON CREATE VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(

            @NonNull LayoutInflater inflater,

            @Nullable ViewGroup container,

            @Nullable Bundle savedInstanceState) {


        View view =
                inflater.inflate(
                        R.layout.fragment_members,
                        container,
                        false
                );


        // =================================================
        // ADD MEMBER BUTTON
        // =================================================

        btnAddMember =
                view.findViewById(
                        R.id.btnAddMember
                );


        txtTotalMembers =
                view.findViewById(
                        R.id.txtMemberCount
                );

        // SEARCH

        txtSearch =
                view.findViewById(
                        R.id.txtSearch
                );

        setupMemberSearch();


        if (btnAddMember != null) {

            btnAddMember.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        requireContext(),
                                        AddMemberActivity.class
                                );


                        memberActivityLauncher.launch(
                                intent
                        );


                        requireActivity()
                                .overridePendingTransition(
                                        R.anim.activity_slide_in_right,
                                        R.anim.activity_fade_out
                                );
                    }
            );
        }


        // =================================================
        // RECYCLER VIEW
        // =================================================

        recyclerMembers =
                view.findViewById(
                        R.id.recyclerMembers
                );


        recyclerMembers.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        2
                )
        );


        // =================================================
        // ADAPTER
        // =================================================

        // ADAPTER

        apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );

        tenantNo =
                deviceStorage.getTenantNo();

        deviceToken =
                deviceStorage.getDeviceToken();

        memberAdapter =
                new MemberAdapter(
                        memberList,
                        this,
                        apiService,
                        tenantNo,
                        deviceToken
                );

        recyclerMembers.setAdapter(
                memberAdapter
        );


        // =================================================
        // LOAD MEMBERS
        // =================================================

        loadMembers();


        return view;
    }

    // SEARCH SETUP

    private void setupMemberSearch() {

        if (txtSearch == null) {
            return;
        }

        txtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterMembers(
                                s == null
                                        ? ""
                                        : s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // FILTER MEMBERS

    private void filterMembers(
            String searchText) {

        String query =
                searchText
                        .trim()
                        .toLowerCase();

        memberList.clear();

        if (query.isEmpty()) {

            memberList.addAll(
                    allMemberList
            );

        } else {

            for (Member member :
                    allMemberList) {

                String memberNo =
                        safeSearchText(
                                member.getMemberNo()
                        );

                String fullName =
                        safeSearchText(
                                member.getFullName()
                        );

                String mobile =
                        safeSearchText(
                                member.getMobile()
                        );

                String branchName =
                        safeSearchText(
                                member.getBranchName()
                        );

                if (memberNo.contains(query) ||
                        fullName.contains(query) ||
                        mobile.contains(query) ||
                        branchName.contains(query)) {

                    memberList.add(
                            member
                    );
                }
            }
        }

        memberAdapter.notifyDataSetChanged();

        if (txtTotalMembers != null) {

            txtTotalMembers.setText(
                    "Total Members: "
                            + memberList.size()
            );
        }
    }

    // SAFE SEARCH TEXT

    private String safeSearchText(
            String value) {

        if (value == null) {
            return "";
        }

        return value.toLowerCase();
    }


    // =====================================================
    // LOAD MEMBERS
    // =====================================================

    private void loadMembers() {


        // =================================================
        // SAFETY CHECK
        // =================================================

        if (sessionStorage == null) {

            sessionStorage =
                    new SessionStorage(
                            requireContext()
                    );
        }


        if (deviceStorage == null) {

            deviceStorage =
                    new DeviceStorage(
                            requireContext()
                    );
        }


        // =================================================
        // SESSION
        // =================================================

        // SESSION

        tenantNo =
                sessionStorage.getTenantNo();

        deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // SELECTED BRANCH
        // =================================================

        Integer branchId = null;


        if (!sessionStorage.isAllBranches()) {

            branchId =
                    sessionStorage
                            .getSelectedBranchId();
        }


        // =================================================
        // VALIDATION
        // =================================================

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Tenant information is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Device information is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =================================================
        // API
        // =================================================

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .getMembers(
                        tenantNo,
                        deviceToken,
                        branchId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<MemberResponse>>>() {


                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    List<MemberResponse>>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    List<MemberResponse>>>
                                            response) {


                                // =================================
                                // FRAGMENT CHECK
                                // =================================

                                if (!isAdded()) {

                                    return;
                                }


                                // =================================
                                // HTTP ERROR
                                // =================================

                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to load members.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                // =================================
                                // RESPONSE
                                // =================================

                                ApiResponse<
                                        List<MemberResponse>>
                                        apiResponse =
                                        response.body();


                                if (apiResponse == null) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Empty response from server.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                // =================================
                                // SERVER ERROR
                                // =================================

                                if (!apiResponse.isSuccess()) {

                                    String message =
                                            apiResponse.getMessage();


                                    if (message == null ||
                                            message.trim().isEmpty()) {

                                        message =
                                                "Unable to load members.";
                                    }


                                    Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                // =================================
                                // DATA
                                // =================================

                                List<MemberResponse>
                                        responses =
                                        apiResponse.getData();


                                if (responses == null) {

                                    Toast.makeText(
                                            requireContext(),
                                            "No member data received.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                // UPDATE MEMBER LIST

                                allMemberList.clear();

                                memberList.clear();

                                for (
                                        MemberResponse memberResponse :
                                        responses) {

                                    Member member =
                                            new Member(
                                                    memberResponse
                                            );

                                    allMemberList.add(
                                            member
                                    );

                                    memberList.add(
                                            member
                                    );
                                }

                                memberAdapter.notifyDataSetChanged();

                                if (txtTotalMembers != null) {

                                    txtTotalMembers.setText(
                                            "Total Members: "
                                                    + memberList.size()
                                    );
                                }


                                // =================================
                                // COUNT
                                // =================================

                                if (txtTotalMembers != null) {

                                    txtTotalMembers.setText(
                                            "Total Members: "
                                                    + memberList.size()
                                    );
                                }


                                memberAdapter
                                        .notifyDataSetChanged();


                                // =================================
                                // EMPTY
                                // =================================

                                if (responses.isEmpty()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "No active members found.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            // =====================================
                            // FAILURE
                            // =====================================

                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    List<MemberResponse>>>
                                            call,

                                    Throwable t) {


                                if (!isAdded()) {

                                    return;
                                }


                                Toast.makeText(
                                        requireContext(),
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // VIEW MEMBER
    // =====================================================

    @Override
    public void onMemberClick(
            Member member) {


        Intent intent =
                new Intent(
                        requireContext(),
                        MemberProfileActivity.class
                );


        intent.putExtra(
                "memberId",
                member.getMemberId()
        );


        startActivity(
                intent
        );


        requireActivity()
                .overridePendingTransition(
                        R.anim.activity_enter,
                        R.anim.activity_exit
                );
    }


    // =====================================================
    // EDIT MEMBER
    // =====================================================

    @Override
    public void onEditMember(
            Member member) {


        if (member == null ||
                member.getMemberId() <= 0) {

            Toast.makeText(
                    requireContext(),
                    "Invalid member.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Intent intent =
                new Intent(
                        requireContext(),
                        AddMemberActivity.class
                );


        // -------------------------------------------------
        // MEMBER ID
        // -------------------------------------------------

        intent.putExtra(
                "memberId",
                member.getMemberId()
        );


        // -------------------------------------------------
        // EDIT MODE
        // -------------------------------------------------

        intent.putExtra(
                "editMode",
                true
        );


        // -------------------------------------------------
        // OPEN EDIT SCREEN
        // -------------------------------------------------

        memberActivityLauncher.launch(
                intent
        );


        requireActivity()
                .overridePendingTransition(
                        R.anim.activity_slide_in_right,
                        R.anim.activity_fade_out
                );
    }


    // GENERATE MEMBER QR

    @Override
    public void onGenerateQr(
            Member member) {

        if (member == null ||
                member.getMemberId() <= 0) {

            Toast.makeText(
                    requireContext(),
                    "Invalid member.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        MemberQrDialog.show(
                requireContext(),
                member.getMemberId(),
                safeText(
                        member.getFullName()
                ),
                safeText(
                        member.getMemberNo()
                ),
                ""
        );
    }
    // SAFE TEXT

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "";
        }

        return value;
    }

    // DEACTIVATE MEMBER
    @Override
    public void onDeactivateMember(
            Member member) {

        if (member == null ||
                member.getMemberId() <= 0) {

            Toast.makeText(
                    requireContext(),
                    "Invalid member.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(
                requireContext()
        )

                .setTitle(
                        "Deactivate Member"
                )

                .setMessage(
                        "Are you sure you want to deactivate\n\n"
                                + member.getFullName()
                                + "?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Deactivate",
                        (dialog, which) -> {

                            deactivateMember(
                                    member
                            );
                        }
                )

                .show();
    }

    // CALL DEACTIVATE API
    private void deactivateMember(
            Member member) {

        String tenantNo =
                sessionStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Tenant information is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Device information is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        apiService
                .deactivateMember(
                        tenantNo,
                        deviceToken,
                        member.getMemberId()
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Void>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<Void>>
                                            call,

                                    Response<
                                            ApiResponse<Void>>
                                            response) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to deactivate member.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                ApiResponse<Void>
                                        apiResponse =
                                        response.body();

                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            requireContext(),
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // SUCCESS

                                Toast.makeText(
                                        requireContext(),
                                        "Member deactivated successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadMembers();
                            }

                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<Void>>
                                            call,

                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }

                                Toast.makeText(
                                        requireContext(),
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // REFRESH MEMBERS
    // =====================================================

    public void refreshMembers() {

        if (sessionStorage == null) {

            sessionStorage =
                    new SessionStorage(
                            requireContext()
                    );
        }


        if (deviceStorage == null) {

            deviceStorage =
                    new DeviceStorage(
                            requireContext()
                    );
        }


        loadMembers();
    }


    // =====================================================
    // ON DETACH
    // =====================================================

    @Override
    public void onDetach() {

        super.onDetach();

        sessionStorage = null;

        deviceStorage = null;
    }
}