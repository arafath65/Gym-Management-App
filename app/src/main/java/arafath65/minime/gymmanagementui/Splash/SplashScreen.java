package arafath65.minime.gymmanagementui.Splash;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import arafath65.minime.gymmanagementui.LoginForm.LoginForm;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.Network.HealthResponse;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceActivationDialog;
import arafath65.minime.gymmanagementui.device.DeviceManager;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationResponse;
import arafath65.minime.gymmanagementui.model.DeviceVerifyRequest;
import arafath65.minime.gymmanagementui.model.DeviceVerifyResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtLoading;
    private TextView txtPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_splash_screen);

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

        progressBar =
                findViewById(R.id.progressBar);

        txtLoading =
                findViewById(R.id.txtLoading);

        txtPercent =
                findViewById(R.id.txtPercent);


        // Loading card animation
        MaterialCardView cardLoading =
                findViewById(R.id.cardLoading);

        cardLoading.setTranslationY(100f);
        cardLoading.setAlpha(0f);

        cardLoading.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(900)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();


        initializeApp();
    }


    // =========================================================
    // INITIAL APPLICATION LOADING
    // =========================================================

    private void initializeApp() {

        // Reset progress when retrying

        runOnUiThread(() -> {

            progressBar.setProgress(0);
            txtPercent.setText("0%");
            txtLoading.setText("Loading Application...");

        });


        new Thread(() -> {

            updateProgress(
                    10,
                    "Loading Application..."
            );

            sleep(500);


            updateProgress(
                    25,
                    "Checking Internet..."
            );

            sleep(500);


            if (!isInternetAvailable()) {

                runOnUiThread(
                        this::showNoInternetDialog
                );

                return;
            }


            updateProgress(
                    35,
                    "Internet Connected..."
            );

            sleep(500);


            updateProgress(
                    45,
                    "Connecting Server..."
            );


            // IMPORTANT:
            // Do not continue after this.
            // checkServer() controls the next step.

            runOnUiThread(
                    this::checkServer
            );

        }).start();
    }


    // =========================================================
    // SLEEP
    // =========================================================

    private void sleep(long time) {

        try {

            Thread.sleep(time);

        } catch (Exception ignored) {
        }
    }


    // =========================================================
    // PROGRESS
    // =========================================================

    private void updateProgress(
            int progress,
            String text) {

        runOnUiThread(() -> {

            ObjectAnimator animator =
                    ObjectAnimator.ofInt(
                            progressBar,
                            "progress",
                            progressBar.getProgress(),
                            progress
                    );

            animator.setDuration(500);

            animator.setInterpolator(
                    new DecelerateInterpolator()
            );

            animator.start();

            txtLoading.setText(text);

            txtPercent.setText(
                    progress + "%"
            );
        });
    }


    // =========================================================
    // INTERNET CHECK
    // =========================================================

    private boolean isInternetAvailable() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                        getSystemService(
                                Context.CONNECTIVITY_SERVICE
                        );

        if (connectivityManager == null) {

            return false;
        }


        Network network =
                connectivityManager.getActiveNetwork();

        if (network == null) {

            return false;
        }


        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(
                        network
                );

        return capabilities != null
                && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
        );
    }


    // =========================================================
    // NO INTERNET
    // =========================================================

    private void showNoInternetDialog() {

        new MaterialAlertDialogBuilder(this)

                .setTitle("No Internet")

                .setMessage(
                        "Please check your internet connection "
                                + "and try again."
                )

                .setCancelable(false)

                .setPositiveButton(
                        "Retry",
                        (dialog, which) -> {

                            initializeApp();

                        }
                )

                .setNegativeButton(
                        "Exit",
                        (dialog, which) -> {

                            finishAffinity();

                        }
                )

                .show();
    }


    // =========================================================
    // SERVER CHECK
    // =========================================================

    private void checkServer() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);


        apiService.health().enqueue(
                new Callback<HealthResponse>() {

                    @Override
                    public void onResponse(
                            Call<HealthResponse> call,
                            Response<HealthResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Log.d(
                                    "SERVER",
                                    response.body().getStatus()
                            );


                            updateProgress(
                                    60,
                                    "Server Connected"
                            );




                            // IMPORTANT
                            // Continue to device checking

                            checkDevice();

                        } else {

                            showServerErrorDialog();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<HealthResponse> call,
                            Throwable t) {

                        Log.e(
                                "API_ERROR",
                                "Connection failed",
                                t
                        );

                        showServerErrorDialog();
                    }
                }
        );
    }


    // =========================================================
    // DEVICE CHECK
    // =========================================================

    private void checkDevice() {

        updateProgress(
                65,
                "Checking Device..."
        );


        DeviceStorage storage =
                new DeviceStorage(this);


        String deviceToken =
                storage.getDeviceToken();


        // =====================================================
        // FIRST INSTALLATION
        // =====================================================

        if (deviceToken == null
                || deviceToken.trim().isEmpty()) {

            updateProgress(
                    70,
                    "Device Not Registered"
            );


            showActivationDialog();

            return;
        }


        // =====================================================
        // EXISTING DEVICE
        // =====================================================

        updateProgress(
                70,
                "Verifying Device..."
        );


        DeviceManager deviceManager =
                new DeviceManager(this);


        String deviceUuid =
                deviceManager.getDeviceUuid();


        DeviceVerifyRequest request =
                new DeviceVerifyRequest();


        request.setDeviceUuid(
                deviceUuid
        );


        request.setDeviceToken(
                deviceToken
        );


        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);


        apiService.verifyDevice(request)
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        DeviceVerifyResponse
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    DeviceVerifyResponse
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    DeviceVerifyResponse
                                                    >
                                            > response) {


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getData() != null) {


                                    DeviceVerifyResponse data =
                                            response.body().getData();


                                    if (data.isRegistered()) {

                                        updateProgress(
                                                80,
                                                "Device Verified"
                                        );


                                        continueToLogin();

                                    } else {

                                        // Server says device is not valid

                                        storage.clear();

                                        showActivationDialog();
                                    }


                                } else {

                                    // Server rejected the device

                                    storage.clear();

                                    showActivationDialog();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    DeviceVerifyResponse
                                                    >
                                            > call,

                                    Throwable t) {


                                Log.e(
                                        "DEVICE_VERIFY",
                                        "Device verification failed",
                                        t
                                );


                                // Do NOT clear token here.
                                //
                                // Server may simply be temporarily
                                // unavailable.

                                showDeviceVerificationError();
                            }
                        }
                );
    }


    // =========================================================
    // ACTIVATION DIALOG
    // =========================================================

    private void showActivationDialog() {

        DeviceActivationDialog activationDialog =
                new DeviceActivationDialog(
                        SplashScreen.this,

                        new DeviceActivationDialog.ActivationListener() {

                            @Override
                            public void onActivationSuccess(
                                    DeviceActivationResponse response) {


                                updateProgress(
                                        80,
                                        "Device Activated"
                                );


                                continueToLogin();
                            }


                            @Override
                            public void onActivationFailed() {

                                // Dialog handles the error.

                            }
                        }
                );


        activationDialog.show();
    }


    // =========================================================
    // CONTINUE TO LOGIN
    // =========================================================

    private void continueToLogin() {

        new Thread(() -> {

            updateProgress(
                    85,
                    "Checking Login..."
            );

            sleep(500);


            updateProgress(
                    95,
                    "Loading Settings..."
            );

            sleep(500);


            updateProgress(
                    100,
                    "Ready"
            );

            sleep(400);


            runOnUiThread(() -> {

                startActivity(
                        new Intent(
                                SplashScreen.this,
                                LoginForm.class
                        )
                );

                finish();
            });

        }).start();
    }


    // =========================================================
    // DEVICE VERIFICATION ERROR
    // =========================================================

    private void showDeviceVerificationError() {

        new MaterialAlertDialogBuilder(this)

                .setTitle(
                        "Device Verification Failed"
                )

                .setMessage(
                        "The device could not be verified "
                                + "with the server."
                                + "\n\n"
                                + "Please check your network "
                                + "connection."
                )

                .setCancelable(false)

                .setPositiveButton(
                        "Retry",
                        (dialog, which) -> {

                            checkDevice();

                        }
                )

                .setNegativeButton(
                        "Exit",
                        (dialog, which) -> {

                            finishAffinity();

                        }
                )

                .show();
    }


    // =========================================================
    // SERVER ERROR
    // =========================================================

    private void showServerErrorDialog() {

        new MaterialAlertDialogBuilder(this)

                .setTitle(
                        "Server Unreachable"
                )

                .setMessage(
                        "Cannot connect to the server."
                                + "\n\n"
                                + "Please check:"
                                + "\n\n"
                                + "• Server is running"
                                + "\n"
                                + "• Tablet and PC are on "
                                + "the same network"
                                + "\n"
                                + "• API address is correct"
                )

                .setCancelable(false)

                .setPositiveButton(
                        "Retry",
                        (dialog, which) -> {

                            initializeApp();

                        }
                )

                .setNegativeButton(
                        "Exit",
                        (dialog, which) -> {

                            finishAffinity();

                        }
                )

                .show();
    }

}