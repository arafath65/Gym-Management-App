package arafath65.minime.gymmanagementui.MainDashboard;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.dialog.MemberQrDialog;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.BranchResponse;
import arafath65.minime.gymmanagementui.model.MemberRequest;
import arafath65.minime.gymmanagementui.model.MemberResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberActivity extends AppCompatActivity {

    private ApiService apiService;

    private DeviceStorage deviceStorage;

    private ImageView imgMemberPhoto;

    private Uri selectedPhotoUri;

    private ImageButton btnBack;

    private MaterialButton btnCamera;
    private MaterialButton btnGallery;
    private MaterialButton btnCancel;
    private MaterialButton btnSaveMember;


    private TextInputEditText txtFirstName;
    private TextInputEditText txtLastName;
    private TextInputEditText txtFullName;

    private TextInputEditText txtGender;
    private TextInputEditText txtDateOfBirth;

    private TextInputEditText txtNic;
    private TextInputEditText txtMobile;
    private TextInputEditText txtEmail;

    private TextInputEditText txtAddress;
    private TextInputEditText txtCity;


    // =====================================================
    // EMERGENCY
    // =====================================================

    private TextInputEditText txtEmergencyContactName;
    private TextInputEditText txtEmergencyContactMobile;
    private TextInputEditText txtEmergencyContactRelationship;


    // =====================================================
    // HEALTH
    // =====================================================

    private TextInputEditText txtOccupation;
    private TextInputEditText txtBloodGroup;
    private TextInputEditText txtMedicalConditions;
    private TextInputEditText txtGoal;
    private TextInputEditText txtNotes;


    // =====================================================
    // GYM
    // =====================================================

    private TextInputEditText txtBranch;
    private TextInputEditText txtJoinDate;


    // =====================================================
    // SELECTED BRANCH
    // =====================================================

    private Integer selectedBranchId;

    private boolean editMode = false;

    private int memberId = 0;
    private String existingPhotoPath = null;
    private java.io.File selectedPhotoFile = null;

    // OPEN QR AFTER CREATE
    private boolean openQrAfterCreate = false;


    // =====================================================
    // GALLERY
    // =====================================================

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedPhotoUri = uri;

                            imgMemberPhoto.setImageURI(
                                    selectedPhotoUri
                            );
                        }
                    }
            );


    private Uri cameraImageUri;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success && cameraImageUri != null) {

                            selectedPhotoUri =
                                    cameraImageUri;

                            imgMemberPhoto.setImageURI(
                                    selectedPhotoUri
                            );

                        } else {

                            cameraImageUri = null;
                        }
                    }
            );

    private Uri createCameraImageUri() {

        java.io.File imageFile =
                new java.io.File(
                        getCacheDir(),
                        "member_" +
                                System.currentTimeMillis() +
                                ".jpg"
                );

        return androidx.core.content.FileProvider
                .getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        imageFile
                );
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_add_member
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        apiService = ApiClient.getClient().create(ApiService.class);
        deviceStorage = new DeviceStorage(getApplicationContext());

        initializeViews();

        applyInputColors();

        editMode =
                getIntent()
                        .getBooleanExtra(
                                "editMode",
                                false
                        );


        memberId =
                getIntent()
                        .getIntExtra(
                                "memberId",
                                0
                        );


        setupButtons();
        setupSelectors();
        loadBranches();

        if (editMode) {

            btnSaveMember.setText(
                    "Update Member"
            );

            loadMemberForEdit();

        } else {

            setDefaultJoinDate();
        }


    }

    private void initializeViews() {

        imgMemberPhoto =
                findViewById(
                        R.id.imgMemberPhoto
                );


        btnBack =
                findViewById(
                        R.id.btnBack
                );

        btnCamera =
                findViewById(
                        R.id.btnCamera
                );

        btnGallery =
                findViewById(
                        R.id.btnGallery
                );

        btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        btnSaveMember =
                findViewById(
                        R.id.btnSaveMember
                );


        // -------------------------------------------------
        // PERSONAL
        // -------------------------------------------------

        txtFirstName =
                findViewById(
                        R.id.txtFirstName
                );

        txtLastName =
                findViewById(
                        R.id.txtLastName
                );

        txtFullName =
                findViewById(
                        R.id.txtFullName
                );

        txtGender =
                findViewById(
                        R.id.txtGender
                );

        txtDateOfBirth =
                findViewById(
                        R.id.txtDateOfBirth
                );

        txtNic =
                findViewById(
                        R.id.txtNic
                );

        txtMobile =
                findViewById(
                        R.id.txtMobile
                );

        txtEmail =
                findViewById(
                        R.id.txtEmail
                );

        txtAddress =
                findViewById(
                        R.id.txtAddress
                );

        txtCity =
                findViewById(
                        R.id.txtCity
                );


        // -------------------------------------------------
        // EMERGENCY
        // -------------------------------------------------

        txtEmergencyContactName =
                findViewById(
                        R.id.txtEmergencyContactName
                );

        txtEmergencyContactMobile =
                findViewById(
                        R.id.txtEmergencyContactMobile
                );

        txtEmergencyContactRelationship =
                findViewById(
                        R.id.txtEmergencyContactRelationship
                );


        // -------------------------------------------------
        // HEALTH
        // -------------------------------------------------

        txtOccupation =
                findViewById(
                        R.id.txtOccupation
                );

        txtBloodGroup =
                findViewById(
                        R.id.txtBloodGroup
                );

        txtMedicalConditions =
                findViewById(
                        R.id.txtMedicalConditions
                );

        txtGoal =
                findViewById(
                        R.id.txtGoal
                );

        txtNotes =
                findViewById(
                        R.id.txtNotes
                );


        // -------------------------------------------------
        // GYM
        // -------------------------------------------------

        txtBranch =
                findViewById(
                        R.id.txtBranch
                );

        txtJoinDate =
                findViewById(
                        R.id.txtJoinDate
                );
    }

    private void setupButtons() {

        // =================================================
        // BACK
        // =================================================

        btnBack.setOnClickListener(
                v -> finish()
        );


        // =================================================
        // CANCEL
        // =================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );


        // =================================================
        // GALLERY
        // =================================================

        btnGallery.setOnClickListener(
                v -> {

                    galleryLauncher.launch(
                            "image/*"
                    );
                }
        );


        // =================================================
        // CAMERA
        // =================================================

        btnCamera.setOnClickListener(
                v -> {

                    try {

                        cameraImageUri =
                                createCameraImageUri();


                        if (cameraImageUri == null) {

                            Toast.makeText(
                                    this,
                                    "Could not create camera image.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        cameraLauncher.launch(
                                cameraImageUri
                        );

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(
                                this,
                                "Camera error: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );


        // =================================================
        // SAVE
        // =================================================

        btnSaveMember.setOnClickListener(
                v -> saveMember()
        );
    }

    // =====================================================
// LOAD MEMBER FOR EDIT
// =====================================================

    private void loadMemberForEdit() {

        if (memberId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid member.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        apiService
                .getMemberById(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<MemberResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Response<
                                            ApiResponse<MemberResponse>>
                                            response) {


                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Unable to load member.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                ApiResponse<MemberResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess() ||
                                        apiResponse.getData() == null) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                populateMember(
                                        apiResponse.getData()
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Throwable t) {

                                Toast.makeText(
                                        AddMemberActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =====================================================
// POPULATE MEMBER
// =====================================================

    private void populateMember(
            MemberResponse member) {


        txtFirstName.setText(
                safeText(
                        member.getFirstName()
                )
        );


        txtLastName.setText(
                safeText(
                        member.getLastName()
                )
        );


        txtFullName.setText(
                safeText(
                        member.getFullName()
                )
        );


        txtGender.setText(
                safeText(
                        member.getGender()
                )
        );


        txtDateOfBirth.setText(
                safeText(
                        member.getDateOfBirth()
                )
        );


        txtNic.setText(
                safeText(
                        member.getNic()
                )
        );


        txtMobile.setText(
                safeText(
                        member.getMobile()
                )
        );


        txtEmail.setText(
                safeText(
                        member.getEmail()
                )
        );


        txtAddress.setText(
                safeText(
                        member.getAddress()
                )
        );


        txtCity.setText(
                safeText(
                        member.getCity()
                )
        );


        // =================================================
        // EMERGENCY
        // =================================================

        txtEmergencyContactName.setText(
                safeText(
                        member.getEmergencyContactName()
                )
        );


        txtEmergencyContactMobile.setText(
                safeText(
                        member.getEmergencyContactMobile()
                )
        );


        txtEmergencyContactRelationship.setText(
                safeText(
                        member.getEmergencyContactRelationship()
                )
        );


        // =================================================
        // HEALTH
        // =================================================

        txtOccupation.setText(
                safeText(
                        member.getOccupation()
                )
        );


        txtBloodGroup.setText(
                safeText(
                        member.getBloodGroup()
                )
        );


        txtMedicalConditions.setText(
                safeText(
                        member.getMedicalConditions()
                )
        );


        txtGoal.setText(
                safeText(
                        member.getGoal()
                )
        );


        txtNotes.setText(
                safeText(
                        member.getNotes()
                )
        );


        // =================================================
        // BRANCH
        // =================================================

        selectedBranchId =
                member.getBranchId();


        txtBranch.setText(
                safeText(
                        member.getBranchName()
                )
        );


        // =================================================
        // JOIN DATE
        // =================================================

        txtJoinDate.setText(
                safeText(
                        member.getJoinDate()
                )
        );


        // =================================================
        // EXISTING PHOTO
        // =================================================

        existingPhotoPath =
                member.getProfileImage();


        loadExistingMemberPhoto();
    }

    private void setupSelectors() {

        // -------------------------------------------------
        // GENDER
        // -------------------------------------------------

        txtGender.setOnClickListener(
                v -> showGenderDialog()
        );


        // -------------------------------------------------
        // BLOOD GROUP
        // -------------------------------------------------

        txtBloodGroup.setOnClickListener(
                v -> showBloodGroupDialog()
        );


        // -------------------------------------------------
        // DATE OF BIRTH
        // -------------------------------------------------

        txtDateOfBirth.setOnClickListener(
                v -> showDatePicker(
                        txtDateOfBirth
                )
        );


        // -------------------------------------------------
        // JOIN DATE
        // -------------------------------------------------

        txtJoinDate.setOnClickListener(
                v -> showDatePicker(
                        txtJoinDate
                )
        );


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        txtBranch.setOnClickListener(
                v -> {

                    if (branchList == null ||
                            branchList.isEmpty()) {

                        loadBranches();

                    } else {

                        showBranchDialog();
                    }
                }
        );
    }

    private void showGenderDialog() {

        String[] genders = {
                "Male",
                "Female"
        };


        new AlertDialog.Builder(this)
                .setTitle("Select Gender")
                .setItems(
                        genders,
                        (dialog, which) -> {

                            txtGender.setText(
                                    genders[which]
                            );
                        }
                )
                .show();
    }

    private void showBloodGroupDialog() {

        String[] bloodGroups = {
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        };


        new AlertDialog.Builder(this)
                .setTitle("Select Blood Group")
                .setItems(
                        bloodGroups,
                        (dialog, which) -> {

                            txtBloodGroup.setText(
                                    bloodGroups[which]
                            );
                        }
                )
                .show();
    }


    // =====================================================
    // DATE PICKER
    // =====================================================

    private void showDatePicker(
            TextInputEditText target) {

        Calendar calendar =
                Calendar.getInstance();


        int year =
                calendar.get(
                        Calendar.YEAR
                );

        int month =
                calendar.get(
                        Calendar.MONTH
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_MONTH
                );


        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view,
                         selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String date =
                                    String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d",
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDay
                                    );

                            target.setText(
                                    date
                            );
                        },
                        year,
                        month,
                        day
                );


        dialog.show();
    }


    // =====================================================
    // DEFAULT JOIN DATE
    // =====================================================

    private void setDefaultJoinDate() {

        if (txtJoinDate.getText() != null &&
                txtJoinDate.getText()
                        .toString()
                        .trim()
                        .isEmpty()) {

            Calendar calendar =
                    Calendar.getInstance();


            String date =
                    String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            calendar.get(
                                    Calendar.YEAR
                            ),
                            calendar.get(
                                    Calendar.MONTH
                            ) + 1,
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            )
                    );


            txtJoinDate.setText(
                    date
            );
        }
    }


    // =====================================================
    // BRANCH LIST
    // =====================================================

    private List<BranchResponse> branchList;


    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        apiService
                .getBranches(
                        tenantNo,
                        deviceToken
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<BranchResponse>>>()
                        {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<
                                            List<BranchResponse>>> call,
                                    Response<ApiResponse<
                                            List<BranchResponse>>> response) {

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Unable to load branches.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                ApiResponse<
                                        List<BranchResponse>>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess() ||
                                        apiResponse.getData() == null) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                branchList =
                                        apiResponse.getData();


                                /*
                                 * If there is only one branch,
                                 * automatically select it.
                                 */

                                if (branchList.size() == 1) {

                                    BranchResponse branch =
                                            branchList.get(0);

                                    selectedBranchId =
                                            branch.getBranchId();

                                    txtBranch.setText(
                                            branch.getBranchName()
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<
                                            List<BranchResponse>>> call,
                                    Throwable t) {

                                Toast.makeText(
                                        AddMemberActivity.this,
                                        "Branch loading failed.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // SHOW BRANCHES
    // =====================================================

    private void showBranchDialog() {

        if (branchList == null ||
                branchList.isEmpty()) {

            return;
        }


        String[] names =
                new String[
                        branchList.size()
                        ];


        for (int i = 0;
             i < branchList.size();
             i++) {

            BranchResponse branch =
                    branchList.get(i);

            names[i] =
                    branch.getBranchName();
        }


        new AlertDialog.Builder(this)
                .setTitle("Select Branch")
                .setItems(
                        names,
                        (dialog, which) -> {

                            BranchResponse branch =
                                    branchList.get(which);


                            selectedBranchId =
                                    branch.getBranchId();


                            txtBranch.setText(
                                    branch.getBranchName()
                            );
                        }
                )
                .show();
    }

    // =====================================================
// SAVE / UPDATE MEMBER
// =====================================================

    private void saveMember() {

        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        String firstName =
                getText(txtFirstName);

        String lastName =
                getText(txtLastName);

        String fullName =
                getText(txtFullName);


        if (fullName.isEmpty()) {

            txtFullName.setError(
                    "Full name is required."
            );

            txtFullName.requestFocus();

            return;
        }


        if (selectedBranchId == null ||
                selectedBranchId <= 0) {

            Toast.makeText(
                    this,
                    "Please select a branch.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -------------------------------------------------
        // CREATE REQUEST
        // -------------------------------------------------

        MemberRequest request =
                new MemberRequest();


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        request.setBranchId(
                selectedBranchId
        );


        // -------------------------------------------------
        // PERSONAL
        // -------------------------------------------------

        request.setFirstName(
                firstName
        );


        request.setLastName(
                lastName
        );


        request.setFullName(
                fullName
        );


        request.setGender(
                getText(txtGender)
        );


        request.setDateOfBirth(
                getText(txtDateOfBirth)
        );


        request.setNic(
                getText(txtNic)
        );


        request.setMobile(
                getText(txtMobile)
        );


        request.setEmail(
                getText(txtEmail)
        );


        request.setAddress(
                getText(txtAddress)
        );


        request.setCity(
                getText(txtCity)
        );


        // -------------------------------------------------
        // EMERGENCY
        // -------------------------------------------------

        request.setEmergencyContactName(
                getText(
                        txtEmergencyContactName
                )
        );


        request.setEmergencyContactMobile(
                getText(
                        txtEmergencyContactMobile
                )
        );


        request.setEmergencyContactRelationship(
                getText(
                        txtEmergencyContactRelationship
                )
        );


        // -------------------------------------------------
        // HEALTH
        // -------------------------------------------------

        request.setOccupation(
                getText(txtOccupation)
        );


        request.setBloodGroup(
                getText(txtBloodGroup)
        );


        request.setMedicalConditions(
                getText(txtMedicalConditions)
        );


        request.setGoal(
                getText(txtGoal)
        );


        request.setNotes(
                getText(txtNotes)
        );

        request.setProfileImage(
                null
        );


        // -------------------------------------------------
        // JOIN DATE
        // -------------------------------------------------

        request.setJoinDate(
                getText(txtJoinDate)
        );


        // -------------------------------------------------
        // DEVICE DATA
        // -------------------------------------------------

        String tenantNo =
                deviceStorage.getTenantNo();


        String deviceToken =
                deviceStorage.getDeviceToken();


        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // -------------------------------------------------
        // DISABLE BUTTON
        // -------------------------------------------------

        btnSaveMember.setEnabled(
                false
        );


        if (editMode) {

            btnSaveMember.setText(
                    "Updating..."
            );

        } else {

            btnSaveMember.setText(
                    "Saving..."
            );
        }


        // =================================================
        // EDIT MODE
        // =================================================

        if (editMode) {

            apiService
                    .updateMember(
                            tenantNo,
                            deviceToken,
                            memberId,
                            request
                    )
                    .enqueue(
                            new Callback<
                                    ApiResponse<MemberResponse>>() {

                                @Override
                                public void onResponse(

                                        Call<
                                                ApiResponse<MemberResponse>>
                                                call,

                                        Response<
                                                ApiResponse<MemberResponse>>
                                                response) {


                                    // ---------------------------------
                                    // ENABLE BUTTON
                                    // ---------------------------------

                                    btnSaveMember.setEnabled(
                                            true
                                    );


                                    btnSaveMember.setText(
                                            "Update Member"
                                    );


                                    // ---------------------------------
                                    // HTTP ERROR
                                    // ---------------------------------

                                    if (!response.isSuccessful() ||
                                            response.body() == null) {

                                        String message =
                                                "Member update failed. HTTP "
                                                        + response.code();


                                        if (response.errorBody() != null) {

                                            try {

                                                String error =
                                                        response.errorBody()
                                                                .string();


                                                if (error != null &&
                                                        !error.trim().isEmpty()) {

                                                    message =
                                                            error;
                                                }

                                            } catch (Exception ignored) {
                                            }
                                        }


                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                message,
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    // ---------------------------------
                                    // API RESPONSE
                                    // ---------------------------------

                                    ApiResponse<MemberResponse>
                                            apiResponse =
                                            response.body();


                                    if (!apiResponse.isSuccess()) {

                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                apiResponse.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    // ---------------------------------
                                    // SUCCESS
                                    // ---------------------------------

                                    // SUCCESS
                                    if (selectedPhotoUri != null) {

                                        uploadMemberImage(
                                                tenantNo,
                                                deviceToken,
                                                memberId
                                        );

                                    } else {

                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                "Member updated successfully.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        setResult(
                                                RESULT_OK
                                        );

                                        finish();
                                    }
                                }


                                @Override
                                public void onFailure(

                                        Call<
                                                ApiResponse<MemberResponse>>
                                                call,

                                        Throwable t) {


                                    btnSaveMember.setEnabled(
                                            true
                                    );


                                    btnSaveMember.setText(
                                            "Update Member"
                                    );


                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Connection failed: "
                                                    + t.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );


            return;
        }


        // =================================================
        // CREATE MODE
        // =================================================

        apiService
                .createMember(
                        tenantNo,
                        deviceToken,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<MemberResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Response<
                                            ApiResponse<MemberResponse>>
                                            response) {


                                // ---------------------------------
                                // ENABLE BUTTON
                                // ---------------------------------

                                btnSaveMember.setEnabled(
                                        true
                                );


                                btnSaveMember.setText(
                                        "Save Member"
                                );


                                // ---------------------------------
                                // HTTP ERROR
                                // ---------------------------------

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    String message =
                                            "Member creation failed. HTTP "
                                                    + response.code();


                                    if (response.errorBody() != null) {

                                        try {

                                            String error =
                                                    response.errorBody()
                                                            .string();


                                            if (error != null &&
                                                    !error.trim().isEmpty()) {

                                                message =
                                                        error;
                                            }

                                        } catch (Exception ignored) {
                                        }
                                    }


                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // ---------------------------------
                                // API RESPONSE
                                // ---------------------------------

                                ApiResponse<MemberResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // ---------------------------------
                                // SUCCESS
                                // ---------------------------------

                                // SUCCESS
                                MemberResponse createdMember =
                                        apiResponse.getData();

                                if (createdMember == null ||
                                        createdMember.getMemberId() <= 0) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Member created, but member ID was not returned.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    setResult(
                                            RESULT_OK
                                    );

                                    finish();

                                    return;
                                }

                                // SUCCESS

                                openQrAfterCreate = true;

                                if (selectedPhotoUri != null) {

                                    uploadMemberImage(
                                            tenantNo,
                                            deviceToken,
                                            createdMember.getMemberId()
                                    );

                                } else {

                                    openMemberQr(
                                            createdMember
                                    );
                                }
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Throwable t) {


                                btnSaveMember.setEnabled(
                                        true
                                );


                                btnSaveMember.setText(
                                        "Save Member"
                                );


                                Toast.makeText(
                                        AddMemberActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // GET TEXT
    // =====================================================

    private String getText(
            TextInputEditText editText) {

        if (editText == null ||
                editText.getText() == null) {

            return "";
        }


        return editText.getText()
                .toString()
                .trim();
    }

    // =====================================================
// SAFE TEXT
// =====================================================

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "";
        }

        return value;
    }

    // =====================================================
// LOAD EXISTING MEMBER PHOTO
// =====================================================

    private void loadExistingMemberPhoto() {

        if (!editMode) {
            return;
        }

        if (existingPhotoPath == null ||
                existingPhotoPath.trim().isEmpty()) {

            return;
        }

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            return;
        }

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            return;
        }

        apiService
                .getMemberImage(
                        tenantNo,
                        deviceToken,
                        memberId
                )
                .enqueue(
                        new Callback<okhttp3.ResponseBody>() {

                            @Override
                            public void onResponse(
                                    Call<okhttp3.ResponseBody> call,
                                    Response<okhttp3.ResponseBody> response) {

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    return;
                                }

                                try {

                                    java.io.InputStream inputStream =
                                            response.body()
                                                    .byteStream();

                                    android.graphics.Bitmap bitmap =
                                            android.graphics.BitmapFactory
                                                    .decodeStream(
                                                            inputStream
                                                    );

                                    inputStream.close();

                                    if (bitmap != null) {

                                        imgMemberPhoto.setImageBitmap(
                                                bitmap
                                        );
                                    }

                                } catch (Exception ignored) {
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<okhttp3.ResponseBody> call,
                                    Throwable t) {

                                // Keep default member image.
                            }
                        }
                );
    }

    // COMPRESS MEMBER IMAGE
    private File prepareCompressedImage(
            Uri imageUri) throws Exception {

        Bitmap bitmap;

        try (InputStream inputStream =
                     getContentResolver()
                             .openInputStream(imageUri)) {

            if (inputStream == null) {

                throw new Exception(
                        "Unable to open image."
                );
            }

            bitmap =
                    BitmapFactory
                            .decodeStream(
                                    inputStream
                            );
        }

        if (bitmap == null) {

            throw new Exception(
                    "Unable to decode image."
            );
        }

        int maxWidth = 1200;

        if (bitmap.getWidth() > maxWidth) {

            int newHeight =
                    (int) (
                            bitmap.getHeight()
                                    * (
                                    maxWidth /
                                            (float)
                                                    bitmap.getWidth()
                            )
                    );

            bitmap =
                    Bitmap.createScaledBitmap(
                            bitmap,
                            maxWidth,
                            newHeight,
                            true
                    );
        }

        File file =
                new File(
                        getCacheDir(),
                        "member_compressed_"
                                + System.currentTimeMillis()
                                + ".jpg"
                );

        try (FileOutputStream outputStream =
                     new FileOutputStream(file)) {

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    80,
                    outputStream
            );

            outputStream.flush();
        }

        bitmap.recycle();

        return file;
    }

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_slide_out_right
        );
    }

    private void applyInputColors() {

        int inputColor =
                getResources().getColor(
                        R.color.text_primary,
                        getTheme()
                );

        int hintColor =
                getResources().getColor(
                        R.color.text_secondary,
                        getTheme()
                );


        TextInputEditText[] fields = {

                txtFirstName,
                txtLastName,
                txtFullName,

                txtGender,
                txtDateOfBirth,

                txtNic,
                txtMobile,
                txtEmail,

                txtAddress,
                txtCity,

                txtEmergencyContactName,
                txtEmergencyContactMobile,
                txtEmergencyContactRelationship,

                txtOccupation,
                txtBloodGroup,
                txtMedicalConditions,
                txtGoal,
                txtNotes,

                txtBranch,
                txtJoinDate
        };


        for (TextInputEditText field : fields) {

            if (field == null) {
                continue;
            }

            field.setTextColor(
                    inputColor
            );

            field.setHintTextColor(
                    hintColor
            );
        }
    }

    // UPLOAD MEMBER IMAGE
    private void uploadMemberImage(
            String tenantNo,
            String deviceToken,
            int targetMemberId) {

        if (selectedPhotoUri == null) {

            if (openQrAfterCreate) {

                openMemberQrFromMemberId(
                        targetMemberId
                );

            } else {

                setResult(
                        RESULT_OK
                );

                finish();
            }

            return;
        }

        try {

            selectedPhotoFile =
                    prepareCompressedImage(
                            selectedPhotoUri
                    );

            RequestBody requestBody =
                    RequestBody.create(
                            selectedPhotoFile,
                            MediaType.parse(
                                    "image/jpeg"
                            )
                    );

            MultipartBody.Part imagePart =
                    MultipartBody.Part.createFormData(
                            "image",
                            selectedPhotoFile.getName(),
                            requestBody
                    );

            apiService
                    .updateMemberImage(
                            tenantNo,
                            deviceToken,
                            targetMemberId,
                            imagePart
                    )
                    .enqueue(
                            new Callback<
                                    ApiResponse<MemberResponse>>() {

                                @Override
                                public void onResponse(

                                        Call<
                                                ApiResponse<MemberResponse>>
                                                call,

                                        Response<
                                                ApiResponse<MemberResponse>>
                                                response) {

                                    if (!response.isSuccessful() ||
                                            response.body() == null) {

                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                "Member saved, but image upload failed.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        setResult(
                                                RESULT_OK
                                        );

                                        finish();

                                        return;
                                    }

                                    ApiResponse<MemberResponse>
                                            apiResponse =
                                            response.body();

                                    if (!apiResponse.isSuccess()) {

                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                "Member saved, but image upload failed: "
                                                        + apiResponse.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        setResult(
                                                RESULT_OK
                                        );

                                        finish();

                                        return;
                                    }

                                    // SUCCESS

                                    MemberResponse updatedMember =
                                            apiResponse.getData();

                                    if (openQrAfterCreate) {

                                        if (updatedMember != null &&
                                                updatedMember.getMemberId() > 0) {

                                            openMemberQr(
                                                    updatedMember
                                            );

                                        } else {

                                            openMemberQrFromMemberId(
                                                    targetMemberId
                                            );
                                        }

                                    } else {

                                        Toast.makeText(
                                                AddMemberActivity.this,
                                                "Member updated successfully.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        setResult(
                                                RESULT_OK
                                        );

                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(

                                        Call<
                                                ApiResponse<MemberResponse>>
                                                call,

                                        Throwable t) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Member saved, but image upload failed.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    setResult(
                                            RESULT_OK
                                    );

                                    finish();
                                }
                            }
                    );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to prepare image: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            btnSaveMember.setEnabled(
                    true
            );

            btnSaveMember.setText(
                    editMode
                            ? "Update Member"
                            : "Save Member"
            );
        }
    }

    // LOAD MEMBER FOR QR
    private void openMemberQrFromMemberId(
            int targetMemberId) {

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();

        if (tenantNo == null ||
                tenantNo.trim().isEmpty() ||
                deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to load member for QR.",
                    Toast.LENGTH_LONG
            ).show();

            setResult(
                    RESULT_OK
            );

            finish();

            return;
        }

        apiService
                .getMemberById(
                        tenantNo,
                        deviceToken,
                        targetMemberId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<MemberResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Response<
                                            ApiResponse<MemberResponse>>
                                            response) {

                                if (!response.isSuccessful() ||
                                        response.body() == null ||
                                        !response.body().isSuccess() ||
                                        response.body().getData() == null) {

                                    Toast.makeText(
                                            AddMemberActivity.this,
                                            "Member saved, but QR information could not be loaded.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    setResult(
                                            RESULT_OK
                                    );

                                    finish();

                                    return;
                                }

                                openMemberQr(
                                        response.body().getData()
                                );
                            }

                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<MemberResponse>>
                                            call,

                                    Throwable t) {

                                Toast.makeText(
                                        AddMemberActivity.this,
                                        "Member saved, but QR information could not be loaded.",
                                        Toast.LENGTH_LONG
                                ).show();

                                setResult(
                                        RESULT_OK
                                );

                                finish();
                            }
                        }
                );
    }

    // OPEN MEMBER QR
    private void openMemberQr(
            MemberResponse member) {

        if (member == null ||
                member.getMemberId() <= 0) {

            Toast.makeText(
                    this,
                    "Member information is unavailable.",
                    Toast.LENGTH_LONG
            ).show();

            setResult(
                    RESULT_OK
            );

            finish();

            return;
        }

        MemberQrDialog.show(
                this,
                member.getMemberId(),
                safeText(
                        member.getFullName()
                ),
                safeText(
                        member.getMemberNo()
                ),
                safeText(
                        member.getJoinDate()
                )
        );

        setResult(
                RESULT_OK
        );
    }



}