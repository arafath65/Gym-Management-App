package arafath65.minime.gymmanagementui.device;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceStorage {

    private static final String PREF_NAME =
            "fitfloor_device";

    private static final String KEY_DEVICE_TOKEN =
            "device_token";

    private static final String KEY_TENANT_NO =
            "tenant_no";

    private static final String KEY_GYM_NAME =
            "gym_name";

    private static final String KEY_BRANCH_NO =
            "branch_no";

    private static final String KEY_BRANCH_NAME =
            "branch_name";

    private final SharedPreferences preferences;

    public DeviceStorage(Context context) {

        preferences = context.getApplicationContext()
                .getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );
    }

    public void saveDeviceToken(String token) {

        preferences.edit()
                .putString(KEY_DEVICE_TOKEN, token)
                .apply();
    }

    public String getDeviceToken() {

        return preferences.getString(
                KEY_DEVICE_TOKEN,
                null
        );
    }

    public void saveTenantNo(String tenantNo) {

        preferences.edit()
                .putString(KEY_TENANT_NO, tenantNo)
                .apply();
    }

    public String getTenantNo() {

        return preferences.getString(
                KEY_TENANT_NO,
                null
        );
    }

    public void saveGymName(String gymName) {

        preferences.edit()
                .putString(KEY_GYM_NAME, gymName)
                .apply();
    }

    public String getGymName() {

        return preferences.getString(
                KEY_GYM_NAME,
                null
        );
    }

    public void saveBranchNo(String branchNo) {

        preferences.edit()
                .putString(KEY_BRANCH_NO, branchNo)
                .apply();
    }

    public String getBranchNo() {

        return preferences.getString(
                KEY_BRANCH_NO,
                null
        );
    }

    public void saveBranchName(String branchName) {

        preferences.edit()
                .putString(KEY_BRANCH_NAME, branchName)
                .apply();
    }

    public String getBranchName() {

        return preferences.getString(
                KEY_BRANCH_NAME,
                null
        );
    }

    public boolean isRegistered() {

        return getDeviceToken() != null &&
                !getDeviceToken().isEmpty();
    }

    public void clear() {

        preferences.edit()
                .clear()
                .apply();
    }


}