    package team.y2k2.globa.main.profile.edit;

    import android.util.Log;

    import androidx.lifecycle.LiveData;
    import androidx.lifecycle.MutableLiveData;
    import androidx.lifecycle.ViewModel;

    import retrofit2.Response;
    import team.y2k2.globa.api.clients.UserApiClient;

    public class NicknameEditViewModel extends ViewModel {
        private final UserApiClient apiClient;
        private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
        private final MutableLiveData<String> showToast = new MutableLiveData<>();
        private final MutableLiveData<String> nickname = new MutableLiveData<>();
        private final MutableLiveData<Boolean> isChangeButtonEnabled = new MutableLiveData<>();
        private final MutableLiveData<Boolean> isNicknameEmpty = new MutableLiveData<>();
        private final MutableLiveData<String> nicknameLength = new MutableLiveData<>();
        private String newNickname = "";
        private String userId;

        public NicknameEditViewModel() {
            apiClient = new UserApiClient();
        }

        public LiveData<Boolean> getFinishActivity() {
            return finishActivity;
        }

        public LiveData<String> getShowToast() {
            return showToast;
        }

        public LiveData<String> getNickname() {
            return nickname;
        }

        public LiveData<Boolean> getIsChangeButtonEnabled() {
            return isChangeButtonEnabled;
        }

        public LiveData<Boolean> getIsNicknameEmpty() {
            return isNicknameEmpty;
        }

        public LiveData<String> getNicknameLength() {
            return nicknameLength;
        }

        public void setNickname(String nickname) {
            this.nickname.setValue(nickname);
        }

        public void initialize(String userId, String currentName) {
            this.userId = userId;
            newNickname = currentName;
            nickname.setValue(currentName);
            isChangeButtonEnabled.setValue(!currentName.isEmpty());
            isNicknameEmpty.setValue(currentName.isEmpty());
            nicknameLength.setValue(currentName.length() + "/32");
        }

        public String getNewNickname() {
            return newNickname;
        }

        public void onBackClick() {
            finishActivity.setValue(true);
        }

        public void onChangeClick() {
            if (isChangeButtonEnabled.getValue()) {
                updateNickname();
            } else {
                showToast.setValue("닉네임 변경 실패");
            }
        }

        public void onCancelClick() {
            nickname.setValue("");
        }

        public void onNicknameChanged(String newNickname) {
            nickname.setValue(newNickname);
            isChangeButtonEnabled.setValue(!newNickname.isEmpty());
            isNicknameEmpty.setValue(newNickname.isEmpty());
            nicknameLength.setValue(newNickname.length() + "/32");
        }

        public void onNicknameAfterTextChanged(String newNickname) {
            nickname.setValue(newNickname);
            nicknameLength.setValue(newNickname.length() + "/32");
        }

        private void updateNickname() {
            Response<Void> response = apiClient.requestUpdateProfileName(userId, nickname.getValue());

            if (response.isSuccessful()) {
                Log.d(getClass().getName(), "닉네임 업데이트 성공");
                finishActivity.setValue(true);
            } else {
                Log.d(getClass().getName(), "닉네임 업데이트 실패 : " + response.code());
                showToast.setValue("닉네임 변경 실패");
            }
        }

        public void doneShowToast() {
            showToast.setValue(null);
        }
    }