package arafath65.minime.gymmanagementui.MainDashboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.Utilss.ImageCompressionUtil;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentRequest;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;


public class AddFitnessAssessmentActivity
        extends AppCompatActivity {

    private ImageView imgFront;
    private ImageView imgSide;
    private ImageView imgBack;

    private MaterialButton btnFrontPhoto;
    private MaterialButton btnSidePhoto;
    private MaterialButton btnBackPhoto;

    private Uri frontUri;
    private Uri sideUri;
    private Uri backUri;
    private File frontImageFile;
    private File sideImageFile;
    private File backImageFile;
    private Uri cameraImageUri;

    // =====================================================
    // EDIT IMAGE REPLACEMENT FLAGS
    // =====================================================

    private boolean replaceFront = false;
    private boolean replaceSide = false;
    private boolean replaceBack = false;

    // =====================================================
    // IMAGE LOADING
    // =====================================================

    private final ExecutorService imageExecutor =
            Executors.newFixedThreadPool(3);

    private static final int IMAGE_FRONT = 1;
    private static final int IMAGE_SIDE = 2;
    private static final int IMAGE_BACK = 3;

    private int currentImageType;


    // =====================================================
    // NETWORK
    // =====================================================

    private ApiService apiService;

    private DeviceStorage deviceStorage;


    // =====================================================
    // MEMBER
    // =====================================================

    private int memberId;


    // =====================================================
    // EDIT MODE
    // =====================================================

    private boolean editMode = false;

    private int fitnessId = 0;


    // =====================================================
    // BUTTONS
    // =====================================================

    private ImageButton btnBack;

    private MaterialButton btnCancel;

    private MaterialButton btnSaveFitness;


    // =====================================================
    // INPUTS
    // =====================================================

    private TextInputEditText txtAssessmentDate;

    private TextInputEditText txtWeight;

    private TextInputEditText txtHeight;

    private TextInputEditText txtBodyFat;


    private TextInputEditText txtChest;

    private TextInputEditText txtWaist;

    private TextInputEditText txtHips;

    private TextInputEditText txtArms;

    private TextInputEditText txtThigh;


    private TextInputEditText txtNotes;

    // =====================================================
// GALLERY
// =====================================================

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        handleSelectedImage(uri);
                    }
            );

    // =====================================================
// CAMERA LAUNCHER
// =====================================================

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (!success) {

                            return;
                        }

                        if (cameraImageUri == null) {

                            return;
                        }

                        handleSelectedImage(
                                cameraImageUri
                        );
                    }
            );


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_add_fitness_assessment
        );


        // =================================================
        // GET MEMBER ID
        // =================================================

        memberId =
                getIntent()
                        .getIntExtra(
                                "memberId",
                                0
                        );


        if (memberId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid member.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =================================================
        // GET EDIT MODE
        // =================================================

        editMode =
                getIntent()
                        .getBooleanExtra(
                                "editMode",
                                false
                        );


        // =================================================
        // GET FITNESS ID
        // =================================================

        if (editMode) {

            fitnessId =
                    getIntent()
                            .getIntExtra(
                                    "fitnessId",
                                    0
                            );


            if (fitnessId <= 0) {

                Toast.makeText(
                        this,
                        "Invalid fitness assessment.",
                        Toast.LENGTH_LONG
                ).show();

                finish();

                return;
            }
        }


        // =================================================
        // NETWORK
        // =================================================

        apiService =
                ApiClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        deviceStorage =
                new DeviceStorage(
                        getApplicationContext()
                );


        // =================================================
        // INITIALIZE
        // =================================================

        initializeViews();


        setupButtons();


        setupDatePicker();


        // =================================================
        // MODE
        // =================================================

        if (editMode) {

            setupEditMode();

            loadFitnessAssessment();

        } else {

            setupAddMode();

        }
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );


        btnCancel =
                findViewById(
                        R.id.btnCancel
                );


        btnSaveFitness =
                findViewById(
                        R.id.btnSaveFitness
                );


        txtAssessmentDate =
                findViewById(
                        R.id.txtAssessmentDate
                );


        txtWeight =
                findViewById(
                        R.id.txtWeight
                );


        txtHeight =
                findViewById(
                        R.id.txtHeight
                );


        txtBodyFat =
                findViewById(
                        R.id.txtBodyFat
                );


        txtChest =
                findViewById(
                        R.id.txtChest
                );


        txtWaist =
                findViewById(
                        R.id.txtWaist
                );


        txtHips =
                findViewById(
                        R.id.txtHips
                );


        txtArms =
                findViewById(
                        R.id.txtArms
                );


        txtThigh =
                findViewById(
                        R.id.txtThigh
                );


        txtNotes =
                findViewById(
                        R.id.txtNotes
                );

        imgFront = findViewById(R.id.imgFront);
        imgSide = findViewById(R.id.imgSide);
        imgBack = findViewById(R.id.imgBack);

        btnFrontPhoto = findViewById(R.id.btnFrontPhoto);
        btnSidePhoto = findViewById(R.id.btnSidePhoto);
        btnBackPhoto = findViewById(R.id.btnBackPhoto);
        btnFrontPhoto.setOnClickListener(v -> showImageSourceDialog(IMAGE_FRONT));

        btnSidePhoto.setOnClickListener(v -> showImageSourceDialog(IMAGE_SIDE));

        btnBackPhoto.setOnClickListener(v -> showImageSourceDialog(IMAGE_BACK));
    }


    // =====================================================
    // BUTTONS
    // =====================================================

    private void setupButtons() {

        btnBack.setOnClickListener(
                v -> finish()
        );


        btnCancel.setOnClickListener(
                v -> finish()
        );


        btnSaveFitness.setOnClickListener(
                v -> saveAssessment()
        );
    }


    // =====================================================
    // ADD MODE
    // =====================================================

    private void setupAddMode() {

        btnSaveFitness.setText(
                "Save Assessment"
        );


        setDefaultDate();
    }


    // =====================================================
    // EDIT MODE
    // =====================================================

    private void setupEditMode() {

        btnSaveFitness.setText(
                "Update Assessment"
        );
    }

    // =====================================================
// IMAGE SOURCE DIALOG
// =====================================================

    private void showImageSourceDialog(
            int imageType) {

        currentImageType =
                imageType;


        String title;


        if (imageType == IMAGE_FRONT) {

            title =
                    "Front Photo";

        } else if (imageType == IMAGE_SIDE) {

            title =
                    "Side Photo";

        } else {

            title =
                    "Back Photo";
        }


        new AlertDialog.Builder(this)

                .setTitle(title)

                .setItems(
                        new String[]{
                                "Camera",
                                "Gallery"
                        },

                        (dialog, which) -> {

                            if (which == 0) {

                                openCamera();

                            } else {

                                openGallery();
                            }
                        }
                )

                .show();
    }

    // =====================================================
// OPEN GALLERY
// =====================================================

    private void openGallery() {

        galleryLauncher.launch(
                "image/*"
        );
    }

    // =====================================================
// OPEN CAMERA
// =====================================================

    private void openCamera() {

        try {

            File imageFile =
                    createCameraImageFile();


            cameraImageUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            imageFile
                    );


            cameraLauncher.launch(
                    cameraImageUri
            );


        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Unable to open camera.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
// CREATE CAMERA IMAGE FILE
// =====================================================

    private File createCameraImageFile()
            throws IOException {

        String fileName =
                "FITNESS_"
                        + System.currentTimeMillis();


        File storageDir =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );


        if (storageDir == null) {

            throw new IOException(
                    "Camera storage unavailable."
            );
        }


        if (!storageDir.exists() &&
                !storageDir.mkdirs()) {

            throw new IOException(
                    "Unable to create camera directory."
            );
        }


        return File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );
    }

    // =====================================================
// HANDLE SELECTED IMAGE
// =====================================================

    // =====================================================
// HANDLE SELECTED IMAGE
// =====================================================

    private void handleSelectedImage(
            Uri uri) {

        try {

            File compressedFile =
                    ImageCompressionUtil.compressImage(
                            this,
                            uri
                    );

            if (currentImageType == IMAGE_FRONT) {

                frontImageFile = compressedFile;
                frontUri = Uri.fromFile(compressedFile);
                imgFront.setImageURI(frontUri);

                btnFrontPhoto.setText(
                        editMode ? "Replace Front Photo" : "Change Photo"
                );

                if (editMode) {
                    replaceFront = true;
                }

            } else if (currentImageType == IMAGE_SIDE) {

                sideImageFile = compressedFile;
                sideUri = Uri.fromFile(compressedFile);
                imgSide.setImageURI(sideUri);

                btnSidePhoto.setText(
                        editMode ? "Replace Side Photo" : "Change Photo"
                );

                if (editMode) {
                    replaceSide = true;
                }

            } else if (currentImageType == IMAGE_BACK) {

                backImageFile = compressedFile;
                backUri = Uri.fromFile(compressedFile);
                imgBack.setImageURI(backUri);

                btnBackPhoto.setText(
                        editMode ? "Replace Back Photo" : "Change Photo"
                );

                if (editMode) {
                    replaceBack = true;
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to process image.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =====================================================
    // DATE PICKER
    // =====================================================
    // DATE PICKER
    // =====================================================

    private void setupDatePicker() {

        txtAssessmentDate.setOnClickListener(
                v -> showDatePicker()
        );
    }


    // =====================================================
    // SHOW DATE PICKER
    // =====================================================

    private void showDatePicker() {

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


                            txtAssessmentDate.setText(
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
    // DEFAULT DATE
    // =====================================================

    private void setDefaultDate() {

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


        txtAssessmentDate.setText(
                date
        );
    }


    // =====================================================
    // LOAD EXISTING FITNESS ASSESSMENT
    // =====================================================


    private void loadFitnessAssessment() {

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();


        // =================================================
        // TENANT
        // =================================================

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Tenant information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =================================================
        // DEVICE
        // =================================================

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Device information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =================================================
        // GET SINGLE ASSESSMENT
        // =================================================

        apiService
                .getFitnessAssessmentById(
                        tenantNo,
                        deviceToken,
                        memberId,
                        fitnessId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberFitnessAssessmentResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            response) {


                                // =============================================
                                // HTTP ERROR
                                // =============================================

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            "Unable to load fitness assessment.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    finish();

                                    return;
                                }


                                // =============================================
                                // API RESPONSE
                                // =============================================

                                ApiResponse<
                                        MemberFitnessAssessmentResponse>
                                        apiResponse =
                                        response.body();


                                // =============================================
                                // API ERROR
                                // =============================================

                                if (!apiResponse.isSuccess() ||
                                        apiResponse.getData() == null) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    finish();

                                    return;
                                }


                                // =============================================
                                // POPULATE FORM
                                // =============================================

                                populateAssessment(
                                        apiResponse.getData()
                                );
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            call,

                                    Throwable t) {

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                                finish();
                            }
                        }
                );
    }


    // =====================================================
    // POPULATE EXISTING ASSESSMENT
    // =====================================================

    private void populateAssessment(
            MemberFitnessAssessmentResponse assessment) {

        if (assessment.getAssessmentDate() != null) {
            txtAssessmentDate.setText(
                    assessment.getAssessmentDate()
            );
        }

        txtWeight.setText(
                formatDecimal(
                        assessment.getWeight()
                )
        );

        txtHeight.setText(
                formatDecimal(
                        assessment.getHeight()
                )
        );

        txtBodyFat.setText(
                formatDecimal(
                        assessment.getBodyFat()
                )
        );

        txtChest.setText(
                formatDecimal(
                        assessment.getChest()
                )
        );

        txtWaist.setText(
                formatDecimal(
                        assessment.getWaist()
                )
        );

        txtHips.setText(
                formatDecimal(
                        assessment.getHips()
                )
        );

        txtArms.setText(
                formatDecimal(
                        assessment.getArms()
                )
        );

        txtThigh.setText(
                formatDecimal(
                        assessment.getThigh()
                )
        );

        txtNotes.setText(
                safeString(
                        assessment.getNotes()
                )
        );

        loadExistingImages(
                assessment
        );
    }


    // =====================================================
    // LOAD EXISTING FITNESS IMAGES
    // =====================================================

    private void loadExistingImages(
            MemberFitnessAssessmentResponse assessment) {

        if (assessment.getFrontImage() != null &&
                !assessment.getFrontImage().trim().isEmpty()) {

            btnFrontPhoto.setText(
                    "Replace Front Photo"
            );

            loadFitnessImage(
                    IMAGE_FRONT,
                    imgFront
            );
        }

        if (assessment.getSideImage() != null &&
                !assessment.getSideImage().trim().isEmpty()) {

            btnSidePhoto.setText(
                    "Replace Side Photo"
            );

            loadFitnessImage(
                    IMAGE_SIDE,
                    imgSide
            );
        }

        if (assessment.getBackImage() != null &&
                !assessment.getBackImage().trim().isEmpty()) {

            btnBackPhoto.setText(
                    "Replace Back Photo"
            );

            loadFitnessImage(
                    IMAGE_BACK,
                    imgBack
            );
        }
    }


    // =====================================================
    // LOAD FITNESS IMAGE
    // =====================================================

    private void loadFitnessImage(
            int imageType,
            ImageView imageView) {

        String tenantNo =
                deviceStorage.getTenantNo();

        String deviceToken =
                deviceStorage.getDeviceToken();

        if (tenantNo == null ||
                tenantNo.trim().isEmpty() ||
                deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            return;
        }

        String type;

        if (imageType == IMAGE_FRONT) {
            type = "front";
        } else if (imageType == IMAGE_SIDE) {
            type = "side";
        } else {
            type = "back";
        }

        apiService
                .getFitnessImage(
                        tenantNo,
                        deviceToken,
                        memberId,
                        fitnessId,
                        type
                )
                .enqueue(
                        new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(
                                    Call<ResponseBody> call,
                                    Response<ResponseBody> response) {

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            "Unable to load " + type + " image. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                imageExecutor.execute(() -> {

                                    try (InputStream inputStream =
                                                 response.body().byteStream()) {

                                        Bitmap bitmap =
                                                BitmapFactory.decodeStream(
                                                        inputStream
                                                );

                                        if (bitmap == null) {
                                            return;
                                        }

                                        runOnUiThread(() ->
                                                imageView.setImageBitmap(
                                                        bitmap
                                                )
                                        );

                                    } catch (Exception ignored) {
                                    }
                                });
                            }

                            @Override
                            public void onFailure(
                                    Call<ResponseBody> call,
                                    Throwable t) {

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Image load failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    private void saveAssessment() {

        String assessmentDate =
                getText(
                        txtAssessmentDate
                );

        if (assessmentDate.isEmpty()) {

            txtAssessmentDate.setError(
                    "Assessment date is required."
            );

            txtAssessmentDate.requestFocus();

            return;
        }

        MemberFitnessAssessmentRequest request =
                new MemberFitnessAssessmentRequest();

        request.setAssessmentDate(assessmentDate);
        request.setWeight(getDecimal(txtWeight));
        request.setHeight(getDecimal(txtHeight));
        request.setBodyFat(getDecimal(txtBodyFat));
        request.setChest(getDecimal(txtChest));
        request.setWaist(getDecimal(txtWaist));
        request.setHips(getDecimal(txtHips));
        request.setArms(getDecimal(txtArms));
        request.setThigh(getDecimal(txtThigh));
        request.setNotes(getText(txtNotes));

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

        btnSaveFitness.setEnabled(false);

        btnSaveFitness.setText(
                editMode
                        ? "Updating..."
                        : "Saving..."
        );

        if (editMode) {

            updateFitnessAssessmentWithImages(
                    tenantNo,
                    deviceToken,
                    request
            );

            return;
        }

        Gson gson =
                new Gson();

        RequestBody assessmentBody =
                RequestBody.create(
                        gson.toJson(request),
                        MediaType.parse(
                                "application/json"
                        )
                );

        MultipartBody.Part frontPart =
                createImagePart(
                        "frontImage",
                        frontImageFile
                );

        MultipartBody.Part sidePart =
                createImagePart(
                        "sideImage",
                        sideImageFile
                );

        MultipartBody.Part backPart =
                createImagePart(
                        "backImage",
                        backImageFile
                );

        apiService
                .createFitnessAssessmentWithImages(
                        tenantNo,
                        deviceToken,
                        memberId,
                        assessmentBody,
                        frontPart,
                        sidePart,
                        backPart
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberFitnessAssessmentResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> call,
                                    Response<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> response) {

                                btnSaveFitness.setEnabled(true);
                                btnSaveFitness.setText("Save Assessment");

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            "Unable to save assessment. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                ApiResponse<
                                        MemberFitnessAssessmentResponse>
                                        apiResponse =
                                        response.body();

                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Fitness assessment saved.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> call,
                                    Throwable t) {

                                btnSaveFitness.setEnabled(true);
                                btnSaveFitness.setText("Save Assessment");

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    private MultipartBody.Part createImagePart(
            String partName,
            File file) {

        if (file == null ||
                !file.exists()) {

            return null;
        }


        RequestBody requestBody =
                RequestBody.create(
                        file,
                        MediaType.parse(
                                "image/jpeg"
                        )
                );


        return MultipartBody.Part.createFormData(
                partName,
                file.getName(),
                requestBody
        );
    }


    // =====================================================
    // CREATE FITNESS ASSESSMENT
    // =====================================================

    private void createFitnessAssessment(

            String tenantNo,

            String deviceToken,

            MemberFitnessAssessmentRequest request) {


        apiService
                .createFitnessAssessment(
                        tenantNo,
                        deviceToken,
                        memberId,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberFitnessAssessmentResponse>>() {

                            @Override
                            public void onResponse(

                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            call,

                                    Response<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            response) {


                                btnSaveFitness.setEnabled(
                                        true
                                );


                                btnSaveFitness.setText(
                                        "Save Assessment"
                                );


                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            "Unable to save assessment.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                ApiResponse<
                                        MemberFitnessAssessmentResponse>
                                        apiResponse =
                                        response.body();


                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // =================================
                                // SUCCESS
                                // =================================

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Fitness assessment saved.",
                                        Toast.LENGTH_SHORT
                                ).show();


                                setResult(
                                        RESULT_OK
                                );


                                finish();
                            }


                            @Override
                            public void onFailure(

                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>>
                                            call,

                                    Throwable t) {


                                btnSaveFitness.setEnabled(
                                        true
                                );


                                btnSaveFitness.setText(
                                        "Save Assessment"
                                );


                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Connection failed: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // UPDATE FITNESS ASSESSMENT
    // =====================================================

    private void updateFitnessAssessmentWithImages(

            String tenantNo,

            String deviceToken,

            MemberFitnessAssessmentRequest request) {

        Gson gson =
                new Gson();

        RequestBody assessmentBody =
                RequestBody.create(
                        gson.toJson(request),
                        MediaType.parse(
                                "application/json"
                        )
                );

        MultipartBody.Part frontPart =
                replaceFront
                        ? createImagePart(
                        "frontImage",
                        frontImageFile
                )
                        : null;

        MultipartBody.Part sidePart =
                replaceSide
                        ? createImagePart(
                        "sideImage",
                        sideImageFile
                )
                        : null;

        MultipartBody.Part backPart =
                replaceBack
                        ? createImagePart(
                        "backImage",
                        backImageFile
                )
                        : null;

        RequestBody replaceFrontBody =
                RequestBody.create(
                        String.valueOf(replaceFront),
                        MediaType.parse("text/plain")
                );

        RequestBody replaceSideBody =
                RequestBody.create(
                        String.valueOf(replaceSide),
                        MediaType.parse("text/plain")
                );

        RequestBody replaceBackBody =
                RequestBody.create(
                        String.valueOf(replaceBack),
                        MediaType.parse("text/plain")
                );

        apiService
                .updateFitnessAssessmentWithImages(
                        tenantNo,
                        deviceToken,
                        memberId,
                        fitnessId,
                        assessmentBody,
                        frontPart,
                        sidePart,
                        backPart,
                        replaceFrontBody,
                        replaceSideBody,
                        replaceBackBody
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        MemberFitnessAssessmentResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> call,
                                    Response<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> response) {

                                btnSaveFitness.setEnabled(true);
                                btnSaveFitness.setText(
                                        "Update Assessment"
                                );

                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    String message =
                                            "Update failed. HTTP "
                                                    + response.code();

                                    if (response.errorBody() != null) {
                                        try {
                                            message =
                                                    response.errorBody()
                                                            .string();
                                        } catch (Exception ignored) {
                                        }
                                    }

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                ApiResponse<
                                        MemberFitnessAssessmentResponse>
                                        apiResponse =
                                        response.body();

                                if (!apiResponse.isSuccess()) {

                                    Toast.makeText(
                                            AddFitnessAssessmentActivity.this,
                                            apiResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
                                        "Fitness assessment updated.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    MemberFitnessAssessmentResponse>> call,
                                    Throwable t) {

                                btnSaveFitness.setEnabled(true);
                                btnSaveFitness.setText(
                                        "Update Assessment"
                                );

                                Toast.makeText(
                                        AddFitnessAssessmentActivity.this,
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
    // GET DECIMAL
    // =====================================================

    private BigDecimal getDecimal(
            TextInputEditText editText) {

        String value =
                getText(
                        editText
                );


        if (value.isEmpty()) {

            return null;
        }


        try {

            return new BigDecimal(
                    value
            );

        } catch (NumberFormatException e) {

            editText.setError(
                    "Invalid number."
            );


            return null;
        }
    }


    // =====================================================
    // FORMAT DECIMAL
    // =====================================================

    private String formatDecimal(
            BigDecimal value) {

        if (value == null) {

            return "";
        }


        return value
                .stripTrailingZeros()
                .toPlainString();
    }


    // =====================================================
    // SAFE STRING
    // =====================================================

    private String safeString(
            String value) {

        if (value == null) {

            return "";
        }


        return value;
    }


    // =====================================================
    // FINISH ANIMATION
    // =====================================================

    @Override
    protected void onDestroy() {

        imageExecutor.shutdownNow();

        super.onDestroy();
    }


    @Override
    public void finish() {

        super.finish();


        overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_slide_out_right
        );
    }
}
