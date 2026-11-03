package arafath65.minime.gymmanagementui.MainDashboard.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

import arafath65.minime.gymmanagementui.Network.ApiClient;
import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.device.DeviceStorage;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.ProgramEnrollmentRequest;
import arafath65.minime.gymmanagementui.model.ProgramEnrollmentResponse;
import arafath65.minime.gymmanagementui.model.ProgramResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberProgramFragment extends Fragment {

    private static final String ARG_MEMBER_ID = "memberId";
    private static final String ARG_MEMBER_NAME = "memberName";
    private static final String ARG_BRANCH_ID = "memberBranchId";

    private int memberId;
    private int branchId;
    private String memberName = "";

    private ApiService apiService;
    private DeviceStorage deviceStorage;
    private LinearLayout layoutStandalonePrograms;
    private View layoutProgramEmptyState;
    private TextView txtNoCurrentProgram;
    private MaterialButton btnAddProgram;
    private MaterialButton btnProgramHistory;
    private View loadingView;
    private boolean loading;

    public MemberProgramFragment() { }

    public static MemberProgramFragment newInstance(int memberId, String memberName, Integer branchId) {
        MemberProgramFragment fragment = new MemberProgramFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MEMBER_ID, memberId);
        args.putString(ARG_MEMBER_NAME, memberName);
        args.putInt(ARG_BRANCH_ID, branchId == null ? 0 : branchId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            memberId = args.getInt(ARG_MEMBER_ID, 0);
            memberName = args.getString(ARG_MEMBER_NAME, "");
            branchId = args.getInt(ARG_BRANCH_ID, 0);
        }
        apiService = ApiClient.getClient().create(ApiService.class);
        deviceStorage = new DeviceStorage(requireContext().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_program, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutStandalonePrograms = view.findViewById(R.id.layoutStandalonePrograms);
        layoutProgramEmptyState = view.findViewById(R.id.layoutProgramEmptyState);
        txtNoCurrentProgram = view.findViewById(R.id.txtNoCurrentProgram);
        btnAddProgram = view.findViewById(R.id.btnAddProgram);
        btnProgramHistory = view.findViewById(R.id.btnProgramHistory);
        btnAddProgram.setText("+ Enroll in Program");
        btnAddProgram.setOnClickListener(v -> loadAvailableProgramsAndShowForm());
        btnProgramHistory.setOnClickListener(v -> loadProgramHistory());
        loadActivePrograms();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && layoutStandalonePrograms != null) loadActivePrograms();
    }

    private boolean credentialsAvailable() {
        if (deviceStorage.getTenantNo() == null || deviceStorage.getTenantNo().trim().isEmpty()
                || deviceStorage.getDeviceToken() == null || deviceStorage.getDeviceToken().trim().isEmpty()) {
            showMessage("Tenant or device information is missing.");
            return false;
        }
        return true;
    }

    private void loadActivePrograms() {
        // onViewCreated() and onResume() can both call this method. Do not
        // issue overlapping requests, otherwise the same enrollment may render twice.
        if (loading || !isAdded() || memberId <= 0 || !credentialsAvailable()) return;
        loading = true;
        layoutStandalonePrograms.removeAllViews();
        txtNoCurrentProgram.setText("Loading current programs...");
        txtNoCurrentProgram.setVisibility(View.VISIBLE);
        layoutProgramEmptyState.setVisibility(View.VISIBLE);
        apiService.getActiveMemberPrograms(deviceStorage.getTenantNo(), deviceStorage.getDeviceToken(), memberId)
                .enqueue(new Callback<ApiResponse<List<ProgramEnrollmentResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<ProgramEnrollmentResponse>>> call,
                                           @NonNull Response<ApiResponse<List<ProgramEnrollmentResponse>>> response) {
                        if (!isAdded()) return;
                        loading = false;
                        ApiResponse<List<ProgramEnrollmentResponse>> body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            txtNoCurrentProgram.setText(body != null && body.getMessage() != null
                                    ? body.getMessage() : "Could not load current programs.");
                            return;
                        }
                        List<ProgramEnrollmentResponse> enrollments = body.getData();
                        if (enrollments == null || enrollments.isEmpty()) {
                            txtNoCurrentProgram.setText("No current program assigned.");
                            txtNoCurrentProgram.setVisibility(View.VISIBLE);
                            layoutProgramEmptyState.setVisibility(View.VISIBLE);
                            return;
                        }
                        layoutProgramEmptyState.setVisibility(View.VISIBLE);
                        txtNoCurrentProgram.setVisibility(View.GONE);
                        // Deduplicate defensively by enrollment ID/number in case the
                        // backend returns duplicate rows.
                        Set<String> seen = new HashSet<>();
                        int added = 0;
                        for (ProgramEnrollmentResponse enrollment : enrollments) {
                            String key = enrollment.getProgramEnrollmentId() != null
                                    ? "ID:" + enrollment.getProgramEnrollmentId()
                                    : "NO:" + valueOr(enrollment.getEnrollmentNumber(), "");
                            if (key.equals("NO:") || seen.add(key)) {
                                layoutStandalonePrograms.addView(createEnrollmentCard(enrollment));
                                added++;
                            }
                        }
                        if (added == 0) {
                            txtNoCurrentProgram.setText("No current program assigned.");
                            txtNoCurrentProgram.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<ProgramEnrollmentResponse>>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        loading = false;
                        txtNoCurrentProgram.setText("Unable to load programs. Check your connection and retry.");
                        showMessage("Program loading failed: " + safeMessage(t));
                    }
                });
    }

    private View createEnrollmentCard(ProgramEnrollmentResponse item) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        android.graphics.drawable.GradientDrawable cardBackground = new android.graphics.drawable.GradientDrawable();
        cardBackground.setColor(getResources().getColor(R.color.login_card, requireContext().getTheme()));
        cardBackground.setCornerRadius(dp(14));
        cardBackground.setStroke(dp(1), getResources().getColor(R.color.border_color, requireContext().getTheme()));
        card.setBackground(cardBackground);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        TextView title = cardText(valueOr(item.getProgramName(), "Program"), 17, true);
        card.addView(title);
        String code = valueOr(item.getProgramCode(), "");
        if (!code.isEmpty()) card.addView(cardText("Code: " + code, 12, false));
        card.addView(cardText("Enrollment: " + valueOr(item.getEnrollmentNumber(), "—"), 12, false));
        card.addView(cardText("Start date: " + valueOr(item.getStartDate(), "—"), 13, false));
        card.addView(cardText("End date: " + valueOr(item.getEndDate(), "—"), 13, false));
        card.addView(cardText("Status: " + valueOr(item.getEnrollmentState(), "Active"), 13, true));
        card.addView(cardText("Total: " + money(item.getTotalAmount()), 13, false));
        card.addView(cardText("Paid: " + money(item.getPaidAmount()) + "    Balance: " + money(item.getBalanceAmount()), 13, false));

        String state = valueOr(item.getEnrollmentState(), "ACTIVE");
        if ("ACTIVE".equalsIgnoreCase(state)) {
            MaterialButton cancelButton = new MaterialButton(requireContext());
            cancelButton.setText("Cancel Program");
            cancelButton.setTextColor(android.graphics.Color.parseColor("#F87171"));
            cancelButton.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#F87171")));
            cancelButton.setStrokeWidth(dp(1));
            cancelButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.TRANSPARENT));
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cancelParams.topMargin = dp(8);
            cancelButton.setLayoutParams(cancelParams);
            cancelButton.setOnClickListener(v -> confirmCancelEnrollment(item, cancelButton));
            card.addView(cancelButton);
        }
        return card;
    }

    private void confirmCancelEnrollment(ProgramEnrollmentResponse item, MaterialButton button) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Program")
                .setMessage("Cancel " + valueOr(item.getProgramName(), "this program")
                        + " for this member? This will change the enrollment status to CANCELLED.")
                .setNegativeButton("Keep Program", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Cancel Program", (dialog, which) -> cancelEnrollment(item, button))
                .show();
    }

    private void cancelEnrollment(ProgramEnrollmentResponse item, MaterialButton button) {
        Integer enrollmentId = item.getProgramEnrollmentId();
        if (enrollmentId == null || enrollmentId <= 0) {
            showMessage("Cannot cancel this program because its enrollment ID is missing.");
            return;
        }
        if (!credentialsAvailable()) return;
        button.setEnabled(false);
        button.setText("Cancelling...");
        apiService.cancelProgramEnrollment(
                deviceStorage.getTenantNo(),
                deviceStorage.getDeviceToken(),
                enrollmentId
        ).enqueue(new Callback<ApiResponse<ProgramEnrollmentResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ProgramEnrollmentResponse>> call,
                                   @NonNull Response<ApiResponse<ProgramEnrollmentResponse>> response) {
                if (!isAdded()) return;
                ApiResponse<ProgramEnrollmentResponse> body = response.body();
                if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                    button.setEnabled(true);
                    button.setText("Cancel Program");
                    showMessage(body != null && body.getMessage() != null
                            ? body.getMessage() : "Could not cancel program. HTTP " + response.code());
                    return;
                }
                showMessage("Program enrollment cancelled successfully.");
                loadActivePrograms();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ProgramEnrollmentResponse>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                button.setEnabled(true);
                button.setText("Cancel Program");
                showMessage("Cancellation failed: " + safeMessage(t));
            }
        });
    }

    private TextView cardText(String text, int size, boolean bold) {
        TextView view = new TextView(requireContext());
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(getResources().getColor(bold ? R.color.text_primary : R.color.text_secondary, requireContext().getTheme()));
        if (bold) view.setTypeface(null, android.graphics.Typeface.BOLD);
        view.setPadding(0, dp(3), 0, dp(3));
        return view;
    }

    private void loadAvailableProgramsAndShowForm() {
        if (branchId <= 0) {
            showMessage("This member does not have a registered branch. Assign a branch to the member before enrolling in a program.");
            return;
        }
        if (!credentialsAvailable()) return;
        btnAddProgram.setEnabled(false);
        apiService.getPrograms(deviceStorage.getTenantNo(), deviceStorage.getDeviceToken(),
                        branchId > 0 ? branchId : null, true)
                .enqueue(new Callback<ApiResponse<List<ProgramResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<ProgramResponse>>> call,
                                           @NonNull Response<ApiResponse<List<ProgramResponse>>> response) {
                        if (!isAdded()) return;
                        btnAddProgram.setEnabled(true);
                        ApiResponse<List<ProgramResponse>> body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            showMessage(
                                    body != null && body.getMessage() != null
                                            ? body.getMessage()
                                            : "Failed to load programs. HTTP "
                                              + response.code()
                            );
                            return;
                        }

                        List<ProgramResponse> programs = body.getData();

                        if (programs == null || programs.isEmpty()) {
                            showMessage(
                                    "The server responded successfully, but returned no programs. "
                                            + "Check whether active programs are assigned to branch ID "
                                            + branchId + "."
                            );
                            return;
                        }

// Open the enrollment dialog.
                        showEnrollmentForm(programs);
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<ProgramResponse>>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnAddProgram.setEnabled(true);
                        showMessage("Could not load programs: " + safeMessage(t));
                    }
                });
    }

    private void showEnrollmentForm(List<ProgramResponse> programs) {
        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        final int darkColor = android.graphics.Color.parseColor("#191F20");
        final int textColor = android.graphics.Color.parseColor("#F5F7F7");
        final int secondaryColor = android.graphics.Color.parseColor("#A7B2B0");
        final int greenColor = android.graphics.Color.parseColor("#4CAF50");
        final int borderColor = android.graphics.Color.parseColor("#35413D");

        form.setBackgroundColor(darkColor);
        form.setPadding(dp(20), dp(8), dp(20), dp(4));

        // The member's registered branch is fixed for this enrollment.
        Spinner branchSpinner = new Spinner(requireContext());
        String branchLabel = "Branch #" + branchId + " (member's registered branch)";
        for (ProgramResponse program : programs) {
            if (program.getBranchAssignments() == null) continue;
            for (arafath65.minime.gymmanagementui.model.ProgramBranchResponse assignment
                    : program.getBranchAssignments()) {
                if (assignment != null && assignment.getBranchId() != null
                        && assignment.getBranchId() == branchId) {
                    String name = valueOr(assignment.getBranchName(), "Branch #" + branchId);
                    String number = valueOr(assignment.getBranchNo(), "");
                    branchLabel = name + (number.isEmpty() ? "" : " (" + number + ")") + " — locked";
                    break;
                }
            }
        }
        branchSpinner.setAdapter(new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, java.util.Collections.singletonList(branchLabel)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTextColor(textColor);
                item.setPadding(dp(8), dp(8), dp(8), dp(8));
                return item;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getDropDownView(position, convertView, parent);
                item.setTextColor(textColor);
                item.setBackgroundColor(darkColor);
                item.setPadding(dp(12), dp(12), dp(12), dp(12));
                return item;
            }
        });
        branchSpinner.setEnabled(false);
        form.addView(label("Branch"));
        form.addView(branchSpinner);

        Spinner programSpinner = new Spinner(requireContext());
        List<String> labels = new ArrayList<>();
        for (ProgramResponse program : programs) {
            labels.add(valueOr(program.getProgramName(), "Program")
                    + (program.getProgramCode() == null ? "" : " (" + program.getProgramCode() + ")"));
        }
        programSpinner.setAdapter(new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, labels) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTextColor(textColor);
                item.setPadding(dp(8), dp(8), dp(8), dp(8));
                return item;
            }

            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getDropDownView(position, convertView, parent);
                item.setTextColor(textColor);
                item.setBackgroundColor(darkColor);
                item.setPadding(dp(12), dp(12), dp(12), dp(12));
                return item;
            }
        });
        form.addView(label("Program"));
        form.addView(programSpinner);

        EditText feeInput = numericInput("Program fee");
        feeInput.setFocusable(false);
        feeInput.setClickable(false);
        feeInput.setLongClickable(false);
        form.addView(label("Program fee (set by the selected program and branch)"));
        form.addView(feeInput);

        EditText startDate = new EditText(requireContext());
        startDate.setSingleLine(true);
        startDate.setHint("YYYY-MM-DD");
        startDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));
        startDate.setTextColor(textColor);
        startDate.setHintTextColor(secondaryColor);
        startDate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        startDate.setFocusable(false);
        startDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (picker, year, month, day) ->
                    startDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        form.addView(label("Start date"));
        form.addView(startDate);

        EditText discount = numericInput("Discount amount (0 if none)");
        discount.setText("0");
        form.addView(label("Discount amount"));
        form.addView(discount);

        TextView totalValue = label("Agreed program price: 0.00");
        totalValue.setTextSize(17);
        totalValue.setTextColor(greenColor);
        totalValue.setTypeface(null, android.graphics.Typeface.BOLD);
        totalValue.setPadding(0, dp(14), 0, dp(8));
        form.addView(totalValue);

        TextView paymentNote = label("No payment is collected during program assignment.");
        paymentNote.setTextSize(12);
        paymentNote.setTextColor(secondaryColor);
        form.addView(paymentNote);

        EditText notes = new EditText(requireContext());
        notes.setHint("Notes (optional)");
        notes.setMinLines(2);
        notes.setGravity(android.view.Gravity.TOP);
        notes.setTextColor(textColor);
        notes.setHintTextColor(secondaryColor);
        notes.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        form.addView(label("Notes"));
        form.addView(notes);

        final Runnable[] updateTotals = new Runnable[1];
        updateTotals[0] = () -> {
            BigDecimal fee = parseAmount(feeInput.getText().toString());
            BigDecimal discountAmount = parseAmount(discount.getText().toString());
            if (fee == null) fee = BigDecimal.ZERO;
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;
            BigDecimal total = fee.subtract(discountAmount);
            if (total.signum() < 0) total = BigDecimal.ZERO;
            totalValue.setText("Agreed program price: " + money(total));
        };

        Runnable updateFee = () -> {
            ProgramResponse selected = programs.get(programSpinner.getSelectedItemPosition());
            BigDecimal selectedFee = null;
            if (selected.getBranchAssignments() != null) {
                for (arafath65.minime.gymmanagementui.model.ProgramBranchResponse assignment
                        : selected.getBranchAssignments()) {
                    if (assignment != null && assignment.getBranchId() != null
                            && assignment.getBranchId() == branchId
                            && (assignment.getStatus() == null || assignment.getStatus())) {
                        selectedFee = assignment.getFee();
                        break;
                    }
                }
            }
            feeInput.setText(selectedFee == null ? "0" : selectedFee.toPlainString());
            updateTotals[0].run();
        };
        programSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateFee.run();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
        android.text.TextWatcher totalsWatcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotals[0].run(); }
            @Override public void afterTextChanged(android.text.Editable s) { }
        };
        feeInput.addTextChangedListener(totalsWatcher);
        discount.addTextChangedListener(totalsWatcher);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(darkColor);
        scrollView.addView(form);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enroll Member in Program")
                .setView(scrollView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Enroll", null)
                .create();

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                android.graphics.drawable.GradientDrawable dialogBackground =
                        new android.graphics.drawable.GradientDrawable();
                dialogBackground.setColor(darkColor);
                dialogBackground.setCornerRadius(dp(20));
                dialogBackground.setStroke(dp(1), borderColor);
                dialog.getWindow().setBackgroundDrawable(dialogBackground);
            }

            int titleId = requireContext().getResources().getIdentifier(
                    "alertTitle", "id", "android");
            TextView dialogTitle = titleId == 0 ? null : dialog.findViewById(titleId);
            if (dialogTitle != null) {
                dialogTitle.setTextColor(textColor);
                dialogTitle.setTextSize(20);
                dialogTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            android.widget.Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            android.widget.Button enrollButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (cancelButton != null) {
                cancelButton.setTextColor(secondaryColor);
            }
            if (enrollButton != null) {
                enrollButton.setTextColor(greenColor);
                enrollButton.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String date = startDate.getText().toString().trim();
                if (date.isEmpty()) { startDate.setError("Select a start date"); return; }
                BigDecimal feeValue = parseAmount(feeInput.getText().toString());
                BigDecimal discountValue = parseAmount(discount.getText().toString());
                if (feeValue == null || discountValue == null
                        || feeValue.signum() < 0 || discountValue.signum() < 0) {
                    showMessage("Enter a valid non-negative program fee and discount amount."); return;
                }
                if (discountValue.compareTo(feeValue) > 0) {
                    showMessage("Discount cannot be greater than the program fee."); return;
                }
                ProgramResponse selected = programs.get(programSpinner.getSelectedItemPosition());
                ProgramEnrollmentRequest request = new ProgramEnrollmentRequest();
                request.setMemberId(memberId);
                // Always use the member's registered branch; the UI branch selector is locked.
                request.setBranchId(branchId > 0 ? branchId : null);
                request.setProgramId(selected.getProgramId());
                request.setStartDate(date);
                // The backend currently requires a paymentType field. INSTALLMENT is used
                // as the enrollment classification; no payment is collected here.
                request.setPaymentType("INSTALLMENT");
                request.setDiscountAmount(discountValue);
                request.setInitialPayment(BigDecimal.ZERO);
                request.setNotes(notes.getText().toString().trim());
                submitEnrollment(request, dialog);
            });
        });
        dialog.show();
        updateFee.run();
    }

    private void submitEnrollment(ProgramEnrollmentRequest request, AlertDialog dialog) {
        if (!credentialsAvailable()) return;
        apiService.createProgramEnrollment(deviceStorage.getTenantNo(), deviceStorage.getDeviceToken(), request)
                .enqueue(new Callback<ApiResponse<ProgramEnrollmentResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<ProgramEnrollmentResponse>> call,
                                           @NonNull Response<ApiResponse<ProgramEnrollmentResponse>> response) {
                        if (!isAdded()) return;
                        ApiResponse<ProgramEnrollmentResponse> body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            showMessage(body != null && body.getMessage() != null ? body.getMessage() : "Enrollment failed.");
                            return;
                        }
                        dialog.dismiss();
                        showMessage("Member enrolled successfully.");
                        loadActivePrograms();
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<ProgramEnrollmentResponse>> call, @NonNull Throwable t) {
                        if (isAdded()) showMessage("Enrollment failed: " + safeMessage(t));
                    }
                });
    }

    private void loadProgramHistory() {
        if (!credentialsAvailable()) return;
        btnProgramHistory.setEnabled(false);
        apiService.getProgramEnrollments(deviceStorage.getTenantNo(), deviceStorage.getDeviceToken(),
                        memberId, null, branchId > 0 ? branchId : null)
                .enqueue(new Callback<ApiResponse<List<ProgramEnrollmentResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<ProgramEnrollmentResponse>>> call,
                                           @NonNull Response<ApiResponse<List<ProgramEnrollmentResponse>>> response) {
                        if (!isAdded()) return;
                        btnProgramHistory.setEnabled(true);
                        ApiResponse<List<ProgramEnrollmentResponse>> body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            showMessage(body != null && body.getMessage() != null ? body.getMessage() : "Could not load program history.");
                            return;
                        }
                        List<ProgramEnrollmentResponse> list = body.getData();
                        if (list == null || list.isEmpty()) {
                            showMessage("No program history found for this member."); return;
                        }
                        String[] rows = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            ProgramEnrollmentResponse e = list.get(i);
                            rows[i] = valueOr(e.getProgramName(), "Program") + "\n"
                                    + "Enrollment: " + valueOr(e.getEnrollmentNumber(), "—") + " | "
                                    + valueOr(e.getStartDate(), "—") + " to " + valueOr(e.getEndDate(), "—") + "\n"
                                    + "Status: " + valueOr(e.getEnrollmentState(), "—") + " | Balance: " + money(e.getBalanceAmount());
                        }
                        new MaterialAlertDialogBuilder(requireContext()).setTitle("Program History")
                                .setItems(rows, (d, which) -> showEnrollmentDetails(list.get(which)))
                                .setPositiveButton("Close", null).show();
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<ProgramEnrollmentResponse>>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnProgramHistory.setEnabled(true);
                        showMessage("History loading failed: " + safeMessage(t));
                    }
                });
    }

    private void showEnrollmentDetails(ProgramEnrollmentResponse e) {
        String details = "Program: " + valueOr(e.getProgramName(), "—")
                + "\nCode: " + valueOr(e.getProgramCode(), "—")
                + "\nEnrollment: " + valueOr(e.getEnrollmentNumber(), "—")
                + "\nStart: " + valueOr(e.getStartDate(), "—")
                + "\nEnd: " + valueOr(e.getEndDate(), "—")
                + "\nState: " + valueOr(e.getEnrollmentState(), "—")
                + "\nTotal: " + money(e.getTotalAmount())
                + "\nPaid: " + money(e.getPaidAmount())
                + "\nBalance: " + money(e.getBalanceAmount())
                + "\nNotes: " + valueOr(e.getNotes(), "—");
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Program Enrollment Details")
                .setMessage(details).setPositiveButton("Close", null).show();
    }

    private TextView label(String text) {
        TextView view = new TextView(requireContext());

        view.setText(text);
        view.setTextSize(13);
        view.setPadding(0, dp(10), 0, dp(3));
        view.setTextColor(
                android.graphics.Color.parseColor("#A7B2B0")
        );

        return view;
    }

    private EditText numericInput(String hint) {
        EditText edit = new EditText(requireContext());

        edit.setHint(hint);
        edit.setSingleLine(true);
        edit.setTextSize(16);

        edit.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        edit.setTextColor(
                android.graphics.Color.parseColor("#F5F7F7")
        );

        edit.setHintTextColor(
                android.graphics.Color.parseColor("#A7B2B0")
        );

        edit.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#4CAF50")
                )
        );

        return edit;
    }

    private BigDecimal parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try { return new BigDecimal(value.trim()); } catch (NumberFormatException ex) { return null; }
    }

    private String money(BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String safeMessage(Throwable t) {
        return t.getMessage() == null ? "network error" : t.getMessage();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showMessage(String message) {
        if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
