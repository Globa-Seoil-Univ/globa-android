package team.y2k2.globa.main.profile.info;

import android.content.Context;
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
    private final MutableLiveData<String> profileImage = new MutableLiveData<>();
    private UserApiClient apiClient;
    private final List<MyInfoItem> itemList = new ArrayList<>();
    private String name, code, userId;
    private Context context;
    public void setApiClient(Context context) {
        this.apiClient = new UserApiClient();
        this.context = context;
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

    public String getProfileImage() {
        return profileImage.getValue();
    }

    public void setUserInfo(UserInfoResponse userInfoResponse) {
        profileImage.setValue(userInfoResponse.getProfile());
        name = userInfoResponse.getName();
        code = userInfoResponse.getCode();
        userId = userInfoResponse.getUserId();

        loadUserInfoList();
    }

    public List<MyInfoItem> getItemList() {
        return itemList;
    }

    public void updateName(String newName) {
        itemList.get(0).setName(newName);
    }

    public void updateProfileImage(String newProfile) {
        profileImage.setValue(newProfile);
    }

    public String getUserId() {
        return userId;
    }

    public void uploadImage(MultipartBody.Part multipartBody, String userId) {
        Response<Void> response = apiClient.requestUpdateProfileImage(multipartBody, userId);

        if (response.isSuccessful()) {
            Log.d(getClass().getSimpleName(), "이미지 업로드 완료: " + response.code());
            updateProfileImage(apiClient.requestUserInfo().getProfile());
        } else {
            Log.d(getClass().getSimpleName(), "이미지 업로드 실패: " + response.code() + ", " + response.message());
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

    public void loadUserInfoList() {
        itemList.clear();
        itemList.add(new MyInfoItem(context.getString(R.string.name), name, R.drawable.arrow_right, new NicknameEditActivity()));
        itemList.add(new MyInfoItem(context.getString(R.string.profile_account_code), code, R.drawable.item_docs_frame, null));
        itemList.add(new MyInfoItem(context.getString(R.string.sign_out), "", R.drawable.arrow_right, null));
        itemList.add(new MyInfoItem(context.getString(R.string.withdraw), "", R.drawable.arrow_right, new WithdrawActivity()));
    }
}