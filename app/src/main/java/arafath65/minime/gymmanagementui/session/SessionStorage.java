package arafath65.minime.gymmanagementui.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionStorage {

    private static final String PREF_NAME =
            "fitfloor_session";

    private static final String KEY_LOGGED_IN =
            "logged_in";

    private static final String KEY_USER_ID =
            "user_id";

    private static final String KEY_TENANT_ID =
            "tenant_id";

    private static final String KEY_TENANT_NO =
            "tenant_no";

    private static final String KEY_GYM_NAME =
            "gym_name";

    // =====================================================
    // LOGIN BRANCH
    // =====================================================

    private static final String KEY_BRANCH_ID =
            "branch_id";

    private static final String KEY_BRANCH_NO =
            "branch_no";

    private static final String KEY_BRANCH_NAME =
            "branch_name";


    // =====================================================
    // SELECTED BRANCH CONTEXT
    // =====================================================

    private static final String KEY_SELECTED_BRANCH_ID =
            "selected_branch_id";

    private static final String KEY_SELECTED_BRANCH_NO =
            "selected_branch_no";

    private static final String KEY_SELECTED_BRANCH_NAME =
            "selected_branch_name";

    private static final String KEY_ALL_BRANCHES =
            "all_branches";


    // =====================================================
    // ROLE
    // =====================================================

    private static final String KEY_ROLE_ID =
            "role_id";

    private static final String KEY_ROLE_NAME =
            "role_name";


    // =====================================================
    // USER
    // =====================================================

    private static final String KEY_USERNAME =
            "username";

    private static final String KEY_FULL_NAME =
            "full_name";

    private static final String KEY_PROFILE_IMAGE =
            "profile_image";

    private static final String KEY_MOBILE =
            "mobile";

    private static final String KEY_EMAIL =
            "email";


    private final SharedPreferences preferences;


    public SessionStorage(Context context) {

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }


    // =====================================================
    // SAVE SESSION
    // =====================================================

    public void saveSession(
            int userId,
            int tenantId,
            String tenantNo,
            String gymName,
            Integer branchId,
            String branchNo,
            String branchName,
            Integer roleId,
            String roleName,
            String username,
            String fullName,
            String profileImage,
            String mobile,
            String email) {

        SharedPreferences.Editor editor =
                preferences.edit();

        editor.putBoolean(
                KEY_LOGGED_IN,
                true
        );

        editor.putInt(
                KEY_USER_ID,
                userId
        );

        editor.putInt(
                KEY_TENANT_ID,
                tenantId
        );

        editor.putString(
                KEY_TENANT_NO,
                tenantNo
        );

        editor.putString(
                KEY_GYM_NAME,
                gymName
        );


        // -------------------------------------------------
        // LOGIN BRANCH
        // -------------------------------------------------

        if (branchId != null) {

            editor.putInt(
                    KEY_BRANCH_ID,
                    branchId
            );

        } else {

            editor.remove(
                    KEY_BRANCH_ID
            );
        }

        editor.putString(
                KEY_BRANCH_NO,
                branchNo
        );

        editor.putString(
                KEY_BRANCH_NAME,
                branchName
        );


        // -------------------------------------------------
        // ROLE
        // -------------------------------------------------

        if (roleId != null) {

            editor.putInt(
                    KEY_ROLE_ID,
                    roleId
            );

        } else {

            editor.remove(
                    KEY_ROLE_ID
            );
        }

        editor.putString(
                KEY_ROLE_NAME,
                roleName
        );


        // -------------------------------------------------
        // USER
        // -------------------------------------------------

        editor.putString(
                KEY_USERNAME,
                username
        );

        editor.putString(
                KEY_FULL_NAME,
                fullName
        );

        editor.putString(
                KEY_PROFILE_IMAGE,
                profileImage
        );

        editor.putString(
                KEY_MOBILE,
                mobile
        );

        editor.putString(
                KEY_EMAIL,
                email
        );


        editor.apply();


        // -------------------------------------------------
        // INITIAL BRANCH CONTEXT
        // -------------------------------------------------

        saveInitialBranchContext(
                branchId,
                branchNo,
                branchName
        );
    }


    // =====================================================
    // INITIAL BRANCH CONTEXT
    // =====================================================

    private void saveInitialBranchContext(
            Integer branchId,
            String branchNo,
            String branchName) {

        SharedPreferences.Editor editor =
                preferences.edit();


        /*
         * If the login response has no branch,
         * the user starts with ALL BRANCHES.
         *
         * Example:
         *
         * GYM OWNER
         * branchId = null
         *
         * Therefore:
         * selectedBranchId = null
         * allBranches = true
         */

        if (branchId == null) {

            editor.remove(
                    KEY_SELECTED_BRANCH_ID
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NO,
                    null
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NAME,
                    "All Branches"
            );

            editor.putBoolean(
                    KEY_ALL_BRANCHES,
                    true
            );

        } else {

            editor.putInt(
                    KEY_SELECTED_BRANCH_ID,
                    branchId
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NO,
                    branchNo
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NAME,
                    branchName
            );

            editor.putBoolean(
                    KEY_ALL_BRANCHES,
                    false
            );
        }


        editor.apply();
    }


    // =====================================================
    // CHANGE SELECTED BRANCH
    // =====================================================

    public void saveSelectedBranch(
            Integer branchId,
            String branchNo,
            String branchName) {

        SharedPreferences.Editor editor =
                preferences.edit();


        if (branchId == null) {

            editor.remove(
                    KEY_SELECTED_BRANCH_ID
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NO,
                    null
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NAME,
                    "All Branches"
            );

            editor.putBoolean(
                    KEY_ALL_BRANCHES,
                    true
            );

        } else {

            editor.putInt(
                    KEY_SELECTED_BRANCH_ID,
                    branchId
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NO,
                    branchNo
            );

            editor.putString(
                    KEY_SELECTED_BRANCH_NAME,
                    branchName
            );

            editor.putBoolean(
                    KEY_ALL_BRANCHES,
                    false
            );
        }


        editor.apply();
    }


    // =====================================================
    // LOGIN STATUS
    // =====================================================

    public boolean isLoggedIn() {

        return preferences.getBoolean(
                KEY_LOGGED_IN,
                false
        );
    }


    // =====================================================
    // USER
    // =====================================================

    public int getUserId() {

        return preferences.getInt(
                KEY_USER_ID,
                0
        );
    }


    public String getUsername() {

        return preferences.getString(
                KEY_USERNAME,
                null
        );
    }


    public String getFullName() {

        return preferences.getString(
                KEY_FULL_NAME,
                null
        );
    }


    public String getProfileImage() {

        return preferences.getString(
                KEY_PROFILE_IMAGE,
                null
        );
    }


    public String getMobile() {

        return preferences.getString(
                KEY_MOBILE,
                null
        );
    }


    public String getEmail() {

        return preferences.getString(
                KEY_EMAIL,
                null
        );
    }


    // =====================================================
    // TENANT
    // =====================================================

    public int getTenantId() {

        return preferences.getInt(
                KEY_TENANT_ID,
                0
        );
    }


    public String getTenantNo() {

        return preferences.getString(
                KEY_TENANT_NO,
                null
        );
    }


    public String getGymName() {

        return preferences.getString(
                KEY_GYM_NAME,
                null
        );
    }


    // =====================================================
    // LOGIN BRANCH
    // =====================================================

    public Integer getBranchId() {

        if (!preferences.contains(
                KEY_BRANCH_ID)) {

            return null;
        }

        return preferences.getInt(
                KEY_BRANCH_ID,
                0
        );
    }


    public String getBranchNo() {

        return preferences.getString(
                KEY_BRANCH_NO,
                null
        );
    }


    public String getBranchName() {

        return preferences.getString(
                KEY_BRANCH_NAME,
                null
        );
    }


    // =====================================================
    // SELECTED BRANCH
    // =====================================================

    public Integer getSelectedBranchId() {

        if (!preferences.contains(
                KEY_SELECTED_BRANCH_ID)) {

            return null;
        }

        return preferences.getInt(
                KEY_SELECTED_BRANCH_ID,
                0
        );
    }


    public String getSelectedBranchNo() {

        return preferences.getString(
                KEY_SELECTED_BRANCH_NO,
                null
        );
    }


    public String getSelectedBranchName() {

        return preferences.getString(
                KEY_SELECTED_BRANCH_NAME,
                "All Branches"
        );
    }


    public boolean isAllBranches() {

        return preferences.getBoolean(
                KEY_ALL_BRANCHES,
                true
        );
    }


    // =====================================================
    // ROLE
    // =====================================================

    public Integer getRoleId() {

        if (!preferences.contains(
                KEY_ROLE_ID)) {

            return null;
        }

        return preferences.getInt(
                KEY_ROLE_ID,
                0
        );
    }


    public String getRoleName() {

        return preferences.getString(
                KEY_ROLE_NAME,
                null
        );
    }


    // =====================================================
    // CLEAR SESSION / LOGOUT
    // =====================================================

    public void clearSession() {

        preferences.edit()
                .clear()
                .apply();
    }
}