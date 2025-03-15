package team.y2k2.globa.main;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.upload.DocsUploadActivity;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.main.MainFragment;
import team.y2k2.globa.main.profile.ProfileFragment;
import team.y2k2.globa.main.statistics.StatisticsFragment;

public class MainActivityModel extends ViewModel implements MainModel.MainModelCallback {
    public static final int REQUEST_CODE_PICK_RECORD = 101;
    public static final int REQUEST_CODE_UPLOAD_RECORD = 102;

    public static final String TYPE_AUDIO = "audio/*";

    public static final String PRF_RECORD_NAME = "recordName";
    public static final String PRF_RECORD_PATH = "recordPath";

    private MainActivity activity;
    private MainModel model;
    private MainFragment mainFragment;
    private StatisticsFragment statisticsFragment;
    private ProfileFragment profileFragment;
    private FolderFragment folderFragment;

    private MutableLiveData<Fragment> selectedFragment = new MutableLiveData<>();
    private MutableLiveData<Boolean> showBottomSheetDialog = new MutableLiveData<>();
    private MutableLiveData<Intent> uploadRecordIntent = new MutableLiveData<>();

    public MutableLiveData<Fragment> getSelectedFragment() {
        return selectedFragment;
    }

    public MutableLiveData<Boolean> getShowBottomSheetDialog() {
        return showBottomSheetDialog;
    }

    public MutableLiveData<Intent> getUploadRecordIntent() {
        return uploadRecordIntent;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
        this.model = new MainModel(activity);
    }

    public MainActivityModel() {
        mainFragment = new MainFragment();
        statisticsFragment = new StatisticsFragment();
        profileFragment = new ProfileFragment();
        folderFragment = new FolderFragment();
    }

    public void refreshMainFragmentRecords() {
        if (mainFragment != null) {
            mainFragment.showRecords(0);
        }
    }

    public void viewFragment(int index) {
        if (index == R.id.item_main_main)
            selectedFragment.setValue(mainFragment);
        else if (index == R.id.item_main_statistics)
            selectedFragment.setValue(statisticsFragment);
        else if (index == R.id.item_main_upload)
            showBottomSheetDialog.setValue(true);
        else if (index == R.id.item_main_profile)
            selectedFragment.setValue(profileFragment);
        else if (index == R.id.item_main_folder)
            selectedFragment.setValue(folderFragment);
    }

    public void handleUserFcmToken() {
        model.handleUserFcmToken(this);
    }

    public String getUserAccessToken() {
        return model.getUserAccessToken();
    }

    public void uploadRecord(Intent data) {
        model.uploadRecord(data, this);
    }

    @Override
    public void onTokenUpdateSuccess() {
        Log.d("알림 토큰", "알림 토큰 업데이트 완료");
    }

    @Override
    public void onTokenUpdateFailure(int code, String message) {
        Log.d("알림 토큰", "알림 토큰 업데이트 실패 : " + code + ", errorMessage: " + message);
    }

    @Override
    public void onTokenUpdateFailure(Throwable t) {
        Log.d("알림 토큰", "알림 토큰 업데이트 요청 실패 : " + t.getMessage());
    }

    @Override
    public void onTokenFailure(Exception e) {
        Log.e("알림 토큰", "FCM 토큰 가져오기 실패: " + e.getMessage());
    }

    @Override
    public void onRecordUploadReady(String audioPath, String audioName) {
        Intent intent = new Intent(activity, DocsUploadActivity.class);
        intent.putExtra(PRF_RECORD_PATH, audioPath);
        intent.putExtra(PRF_RECORD_NAME, audioName);
        uploadRecordIntent.setValue(intent);
    }
}