package team.y2k2.globa.main.profile.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.main.profile.withdraw.WithdrawActivity;

public class MyInfoViewModel extends ViewModel {
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pickImage = new MutableLiveData<>();
    private final MutableLiveData<String> profileImageLiveData = new MutableLiveData<>(); // UI 관찰용
    private final MutableLiveData<List<MyInfoItem>> itemListLiveData = new MutableLiveData<>();

    private UserApiClient apiClient;
    private String currentName;
    private String currentUserCode;
    private String currentUserId;
    private String currentProfileImageUrl; // ViewModel 내부에서 관리하는 현재 프로필 이미지 URL (서버 기준)

    private Context applicationContext;
    private static final String TAG = "MyInfoViewModel"; // 로그 태그 추가

    public MyInfoViewModel() {
        // apiClient는 initialize에서 생성
    }

    public void initialize(Context appContext) {
        this.applicationContext = appContext.getApplicationContext();
        this.apiClient = new UserApiClient(appContext);
        if (itemListLiveData.getValue() == null) {
            itemListLiveData.setValue(new ArrayList<>());
        }
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public LiveData<Boolean> getPickImage() {
        return pickImage;
    }

    public LiveData<String> getProfileImageLiveData() {
        return profileImageLiveData;
    }

    public LiveData<List<MyInfoItem>> getItemListLiveData() {
        return itemListLiveData;
    }

    public void fetchAndSetUserInfo() {
        if (apiClient == null) {
            Log.e(TAG, "apiClient is null in fetchAndSetUserInfo. Ensure initialize() is called.");
            return;
        }
        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        if (userInfoResponse != null) {
            this.currentName = userInfoResponse.getName();
            this.currentUserCode = userInfoResponse.getCode();
            this.currentUserId = userInfoResponse.getUserId();

            String newProfileUrlFromServer = userInfoResponse.getProfile();
            if (newProfileUrlFromServer != null && !newProfileUrlFromServer.isEmpty()) {
                this.currentProfileImageUrl = newProfileUrlFromServer;
                this.profileImageLiveData.setValue(this.currentProfileImageUrl);
            }

            updateUserPreferences(userInfoResponse);
            generateAndPublishItemList();
            Log.d(TAG, "fetchAndSetUserInfo: 사용자 정보 로드 및 설정 완료. 이름: " + this.currentName);
        } else {
            Log.e(TAG, "fetchAndSetUserInfo: UserInfoResponse is null");
        }
    }

    public void setInitialData(String name, String profileUrl, String userCode, String userId) {
        this.currentName = name;
        this.currentProfileImageUrl = profileUrl;
        this.currentUserCode = userCode;
        this.currentUserId = userId;

        this.profileImageLiveData.setValue(profileUrl);

        generateAndPublishItemList();
        Log.d(TAG, "setInitialData: 초기 데이터로 UI 즉시 업데이트 완료. 이름: " + name);
    }

    private void updateUserPreferences(UserInfoResponse response) {
        if (applicationContext == null || response == null) return;
        SharedPreferences preferences = applicationContext.getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", response.getName());
        editor.putString("userId", response.getUserId());
        editor.putString("publicFolderId", response.getPublicFolderId());
        editor.putString("userCode", response.getCode());
        if (response.getProfile() != null) {
            editor.putString("profile", response.getProfile());
        } else {
            editor.remove("profile");
        }
        editor.apply();
        Log.d(TAG, "updateUserPreferences: SharedPreferences 업데이트됨.");
    }

    public void updateNameFromEdit(String newNameFromEdit) {
        this.currentName = newNameFromEdit;
        if (applicationContext != null) {
            SharedPreferences preferences = applicationContext.getSharedPreferences("account", Context.MODE_PRIVATE);
            preferences.edit().putString("userName", this.currentName).apply();
        }
        generateAndPublishItemList();
        Log.d(TAG, "updateNameFromEdit: 이름 업데이트 및 리스트 재생성/게시. 새 이름: " + this.currentName);
    }

    // 사진 선택기에서 로컬 URI를 받았을 때 UI 즉시 업데이트용
    public void updateProfileImageUri(String localProfileImageUri) {
        this.profileImageLiveData.setValue(localProfileImageUri); // UI 미리보기를 위해 LiveData 업데이트
        Log.d(TAG, "updateProfileImageUri: LiveData에 로컬 URI 설정: " + localProfileImageUri);
    }

    public String getUserId() {
        return currentUserId;
    }

    public void uploadImage(MultipartBody.Part multipartBody) {
        if (apiClient == null) {
            Log.e(TAG, "apiClient is null in uploadImage.");
            errorLiveData.setValue("이미지 업로드 준비 중 오류가 발생했습니다.");
            return;
        }
        Response<?> response = apiClient.requestUpdateProfileImage(multipartBody);

        if (response.isSuccessful()) {
            Log.d(TAG, "이미지 업로드 API 성공: " + response.code());
            // 업로드 성공 후, 최신 사용자 정보를 다시 가져와서 프로필 URL 갱신
            UserInfoResponse updatedInfo = apiClient.requestUserInfo();
            if (updatedInfo != null) {
                this.currentProfileImageUrl = updatedInfo.getProfile(); // ViewModel의 내부 상태 업데이트
                this.profileImageLiveData.setValue(this.currentProfileImageUrl); // LiveData 업데이트 (UI 반영)
                Log.d(TAG, "uploadImage: 새 프로필 URL로 LiveData 업데이트: " + this.currentProfileImageUrl);
                if(applicationContext != null) { // SharedPreferences도 최신 URL로 업데이트
                    SharedPreferences preferences = applicationContext.getSharedPreferences("account", Context.MODE_PRIVATE);
                    preferences.edit().putString("profile", this.currentProfileImageUrl).apply();
                }
            } else {
                Log.e(TAG, "uploadImage: 이미지 업로드 성공 후 사용자 정보 재조회 실패");
                errorLiveData.setValue("프로필 정보를 갱신하는데 실패했습니다.");
            }
        } else {
            Log.e(TAG, "이미지 업로드 API 실패: " + response.code() + ", " + response.message());
            errorLiveData.setValue("이미지 업로드에 실패했습니다.");
            // 실패 시, LiveData를 이전 서버 값(currentProfileImageUrl)으로 되돌리거나 사용자에게 알림
            this.profileImageLiveData.setValue(this.currentProfileImageUrl); // 이전 값으로 복원 시도
        }
    }

    public void onBackButtonClick() {
        finishActivity.setValue(true);
    }

    public void onChangePhotoButtonClick() {
        pickImage.setValue(true);
    }

    public void resetPickImage() {
        pickImage.setValue(false);
    }

    private void generateAndPublishItemList() {
        if (applicationContext == null) {
            Log.e(TAG, "generateAndPublishItemList: ApplicationContext가 null입니다.");
            itemListLiveData.setValue(new ArrayList<>());
            return;
        }
        ArrayList<MyInfoItem> newItems = new ArrayList<>();
        newItems.add(new MyInfoItem(applicationContext.getString(R.string.name), this.currentName, R.drawable.arrow_right, new NicknameEditActivity()));
        newItems.add(new MyInfoItem(applicationContext.getString(R.string.profile_account_code), this.currentUserCode, R.drawable.item_docs_frame, null));
        newItems.add(new MyInfoItem(applicationContext.getString(R.string.sign_out), "", R.drawable.arrow_right, null));
        newItems.add(new MyInfoItem(applicationContext.getString(R.string.withdraw), "", R.drawable.arrow_right, new WithdrawActivity()));

        itemListLiveData.setValue(newItems);
        Log.d(TAG, "generateAndPublishItemList: itemListLiveData 업데이트됨. 현재 이름: " + this.currentName);
    }
}