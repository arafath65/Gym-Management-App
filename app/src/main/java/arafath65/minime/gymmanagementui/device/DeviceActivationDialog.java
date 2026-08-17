package arafath65.minime.gymmanagementui.device;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.DeviceActivationRequest;
import arafath65.minime.gymmanagementui.model.DeviceActivationResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceActivationDialog {

    public interface ActivationListener {
        void onActivationSuccess(DeviceActivationResponse response);

        void onActivationFailed();
    }

    private final Context context;
    private final ActivationListener listener;

    private Dialog dialog;

    private TextInputEditText txtActivationCode;
    private MaterialButton btnActivate;

    public DeviceActivationDialog(
            Context context,
            ActivationListener listener) {

        this.context = context;
        this.listener = listener;
    }

    public void show() {

        dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(
                R.layout.dialog_device_activation
        );

        dialog.setCancelable(false);

        txtActivationCode =
                dialog.findViewById(
                        R.id.txtActivationCode
                );

        btnActivate =
                dialog.findViewById(
                        R.id.btnActivate
                );

        btnActivate.setOnClickListener(v ->
                activateDevice()
        );

        Window window = dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width =
                    (int) (context.getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.60);

            params.height =
                    WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        dialog.show();

        // Set size again after show()
        Window shownWindow = dialog.getWindow();

        if (shownWindow != null) {

            WindowManager.LayoutParams params =
                    shownWindow.getAttributes();

            params.width =
                    (int) (context.getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.60);

            params.height =
                    WindowManager.LayoutParams.WRAP_CONTENT;

            shownWindow.setAttributes(params);
        }
    }

    private void activateDevice() {

        String activationCode =
                txtActivationCode
                        .getText()
                        .toString()
                        .trim()
                        .toUpperCase();

        if (TextUtils.isEmpty(activationCode)) {

            txtActivationCode.setError(
                    "Enter activation code"
            );

            return;
        }

        btnActivate.setEnabled(false);

        btnActivate.setText("Activating...");

        DeviceManager deviceManager =
                new DeviceManager(context);

        DeviceActivationRequest request =
                new DeviceActivationRequest();

        request.setActivationCode(
                activationCode
        );

        request.setDeviceUuid(
                deviceManager.getDeviceUuid()
        );

        request.setDeviceName(
                Build.MANUFACTURER
                        + " "
                        + Build.MODEL
        );

        request.setModel(
                Build.MODEL
        );

        request.setAndroidVersion(
                Build.VERSION.RELEASE
        );

        request.setAppVersion(
                getAppVersion()
        );

        // Firebase will be added later
        request.setFirebaseToken(null);

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.activateDevice(request)
                .enqueue(
                        new Callback<ApiResponse<DeviceActivationResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<DeviceActivationResponse>> call,
                                    Response<ApiResponse<DeviceActivationResponse>> response) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getData() != null) {

                                    DeviceActivationResponse data =
                                            response.body().getData();

                                    saveDeviceInformation(data);

                                    if (dialog != null
                                            && dialog.isShowing()) {

                                        dialog.dismiss();
                                    }

                                    listener.onActivationSuccess(data);

                                } else {

                                    btnActivate.setEnabled(true);

                                    btnActivate.setText(
                                            "ACTIVATE DEVICE"
                                    );

                                    String message =
                                            "Device activation failed.";

                                    if (response.body() != null
                                            && response.body().getMessage() != null) {

                                        message =
                                                response.body().getMessage();
                                    }

                                    Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<ApiResponse<DeviceActivationResponse>> call,
                                    Throwable t) {

                                btnActivate.setEnabled(true);

                                btnActivate.setText(
                                        "ACTIVATE DEVICE"
                                );

                                Toast.makeText(
                                        context,
                                        "Cannot connect to server.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void saveDeviceInformation(
            DeviceActivationResponse response) {

        DeviceStorage storage =
                new DeviceStorage(context);

        storage.saveDeviceToken(
                response.getDeviceToken()
        );

        storage.saveTenantNo(
                response.getTenantNo()
        );

        storage.saveGymName(
                response.getGymName()
        );

        storage.saveBranchNo(
                response.getBranchNo()
        );

        storage.saveBranchName(
                response.getBranchName()
        );
    }

    private String getAppVersion() {

        try {

            return context
                    .getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(),
                            0
                    )
                    .versionName;

        } catch (Exception e) {

            return "1.0.0";
        }
    }

    public void dismiss() {

        if (dialog != null
                && dialog.isShowing()) {

            dialog.dismiss();
        }
    }
}