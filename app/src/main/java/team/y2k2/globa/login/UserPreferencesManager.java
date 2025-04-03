package team.y2k2.globa.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class UserPreferencesManager {
    private static final String PREF_NAME = "account";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_PROFILE = "profile";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PUBLIC_FOLDER_ID = "publicFolderId";
    private static final String KEY_USER_CODE = "userCode";

    private final SharedPreferences preferences;

    public UserPreferencesManager(Activity activity) {
        this.preferences = activity.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    public void saveLoginInfo(LoginRequest request, LoginResponse response) {
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();

        Log.d("엑세스 토큰", "AT : " + accessToken);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_UID, request.getSnsId());
        editor.putString(KEY_NAME, request.getName());
        editor.putString(KEY_PROFILE, request.getProfile());
        editor.apply();
    }

    public void saveUserProfile(UserInfoResponse response) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_NAME, response.getName());
        editor.putString(KEY_USER_ID, response.getUserId());
        editor.putString(KEY_PUBLIC_FOLDER_ID, response.getPublicFolderId());
        editor.putString(KEY_USER_CODE, response.getCode());
        editor.apply();
    }

    public String getAccessToken() {
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUid() {
        return preferences.getString(KEY_UID, null);
    }

    public String getName() {
        return preferences.getString(KEY_NAME, null);
    }

    public String getProfile() {
        return preferences.getString(KEY_PROFILE, null);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, null);
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public String getPublicFolderId() {
        return preferences.getString(KEY_PUBLIC_FOLDER_ID, null);
    }

    public String getUserCode() {
        return preferences.getString(KEY_USER_CODE, null);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
} 