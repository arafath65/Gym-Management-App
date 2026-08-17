package arafath65.minime.gymmanagementui.LoginForm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import arafath65.minime.gymmanagementui.MainActivity;
import arafath65.minime.gymmanagementui.MainDashboard.MainDashboard;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.Utilss.AppToast;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.LoginRequest;
import arafath65.minime.gymmanagementui.model.LoginResponse;
import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;

import arafath65.minime.gymmanagementui.session.SessionStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginForm extends AppCompatActivity {

    private TextInputEditText txtTenant;
    private TextInputEditText txtUsername;
    private TextInputEditText txtPassword;

    private Button btnLogin;

    private DeviceStorage deviceStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login_form);

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


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        txtTenant = findViewById(R.id.txtTenant);

        txtUsername = findViewById(R.id.txtUsername);

        txtPassword = findViewById(R.id.txtPassword);

        txtUsername.setText("owner01");
        txtPassword.setText("123456");

        btnLogin = findViewById(R.id.btnLogin);

        deviceStorage =
                new DeviceStorage(this);

        loadTenant();

        btnLogin.setOnClickListener(v -> {

            login();

        });
    }


    // =========================================================
    // LOAD TENANT
    // =========================================================

    private void loadTenant() {

        String tenantNo =
                deviceStorage.getTenantNo();

        if (tenantNo == null ||
                tenantNo.trim().isEmpty()) {

            AppToast.warning(
                    this,
                    "Tenant information not available."
            );

            return;
        }

        txtTenant.setText(tenantNo);
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private void login() {

        String tenantNo =
                txtTenant.getText()
                        .toString()
                        .trim();

        String username =
                txtUsername.getText()
                        .toString()
                        .trim();

        String password =
                txtPassword.getText()
                        .toString();


        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (tenantNo.isEmpty()) {

            AppToast.warning(
                    this,
                    "Tenant information is missing."
            );

            return;
        }


        if (username.isEmpty()) {

            txtUsername.setError(
                    "Username is required"
            );

            txtUsername.requestFocus();

            return;
        }


        if (password.isEmpty()) {

            txtPassword.setError(
                    "Password is required"
            );

            txtPassword.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // DEVICE TOKEN
        // -----------------------------------------------------

        String deviceToken =
                deviceStorage.getDeviceToken();

        if (deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            AppToast.error(
                    this,
                    "This device is not registered. Please activate the device first."
            );

            return;
        }


        // -----------------------------------------------------
        // CREATE LOGIN REQUEST
        // -----------------------------------------------------

        LoginRequest request =
                new LoginRequest(
                        tenantNo,
                        username,
                        password,
                        deviceToken
                );


        // -----------------------------------------------------
        // DISABLE BUTTON
        // -----------------------------------------------------

        btnLogin.setEnabled(false);


        // -----------------------------------------------------
        // CALL SERVER
        // -----------------------------------------------------

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.login(request)
                .enqueue(new Callback<ApiResponse<LoginResponse>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<LoginResponse>> call,
                            Response<ApiResponse<LoginResponse>> response) {

                        btnLogin.setEnabled(true);


                        // -------------------------------------------------
                        // HTTP ERROR
                        // -------------------------------------------------

                        if (!response.isSuccessful()) {

                            String message =
                                    getErrorMessage(response);

                            AppToast.error(
                                    LoginForm.this,
                                    message
                            );

                            return;
                        }


                        ApiResponse<LoginResponse> apiResponse =
                                response.body();


                        if (apiResponse == null) {

                            AppToast.error(
                                    LoginForm.this,
                                    "No response received from the server."
                            );

                            return;
                        }


                        // -------------------------------------------------
                        // LOGIN FAILED
                        // -------------------------------------------------

                        if (!apiResponse.isSuccess()) {

                            AppToast.error(
                                    LoginForm.this,
                                    apiResponse.getMessage()
                            );

                            return;
                        }


                        // -------------------------------------------------
                        // LOGIN SUCCESS
                        // -------------------------------------------------

                        LoginResponse loginResponse =
                                apiResponse.getData();


                        if (loginResponse == null) {

                            AppToast.error(
                                    LoginForm.this,
                                    "Unable to process the login response."
                            );

                            return;
                        }

                        // =====================================================
// SAVE LOGIN SESSION
// =====================================================

                        SessionStorage sessionStorage =
                                new SessionStorage(LoginForm.this);

                        sessionStorage.saveSession(

                                loginResponse.getUserId(),

                                loginResponse.getTenantId(),

                                loginResponse.getTenantNo(),

                                loginResponse.getGymName(),

                                loginResponse.getBranchId(),

                                loginResponse.getBranchNo(),

                                loginResponse.getBranchName(),

                                loginResponse.getRoleId(),

                                loginResponse.getRoleName(),

                                loginResponse.getUsername(),

                                loginResponse.getFullName(),

                                loginResponse.getProfileImage(),

                                loginResponse.getMobile(),

                                loginResponse.getEmail()
                        );


                        AppToast.success(
                                LoginForm.this,
                                "Welcome " + loginResponse.getFullName()
                        );


                        // -------------------------------------------------
                        // OPEN MAIN ACTIVITY
                        // -------------------------------------------------

                        Intent intent =
                                new Intent(
                                        LoginForm.this,
                                        MainDashboard.class
                                );

                        startActivity(intent);

                        overridePendingTransition(
                                R.anim.activity_enter,
                                R.anim.activity_exit
                        );

                        finish();

                        overridePendingTransition(
                                R.anim.activity_pop_enter,
                                R.anim.activity_pop_exit
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<ApiResponse<LoginResponse>> call,
                            Throwable t) {

                        btnLogin.setEnabled(true);

                        AppToast.error(
                                LoginForm.this,
                                "Cannot connect to server. Please try again."
                        );
                    }
                });
    }

    private String getErrorMessage(
            Response<?> response) {

        try {

            if (response.errorBody() != null) {

                String errorJson =
                        response.errorBody()
                                .string();

                com.google.gson.JsonObject jsonObject =
                        com.google.gson.JsonParser
                                .parseString(errorJson)
                                .getAsJsonObject();

                if (jsonObject.has("message")) {

                    return jsonObject
                            .get("message")
                            .getAsString();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "Unable to login. Please try again.";
    }
}