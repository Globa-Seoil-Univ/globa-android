package team.y2k2.globa.main.profile.inquiry.add;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.clients.InquiryApiClient;

public class InquiryAddViewModel extends ViewModel {

    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private final MutableLiveData<String> showToast = new MutableLiveData<>();
    private String title = "";
    private String description = "";
    private final MutableLiveData<String> titleCount = new MutableLiveData<>("0/32");

    private InquiryApiClient inquiryApiClient;
    private static final String TAG = "InquiryViewModel"; // 로그 태그 추가


    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public LiveData<String> getShowToast() {
        return showToast;
    }

    public LiveData<String> getTitleCount() {
        return titleCount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void onTitleTextChanged(String newTitle) {
        this.title = newTitle;
        updateTitleCount();
    }

    public void onTitleAfterTextChanged(String newTitle) {
        this.title = newTitle;
    }

    public void onDescriptionTextChanged(String newDescription) {
        this.description = newDescription;
    }

    private void updateTitleCount() {
        titleCount.setValue(title.length() + "/32");
    }

    public void setInquiryApiClient(InquiryApiClient inquiryApiClient) {
        this.inquiryApiClient = inquiryApiClient;
        updateTitleCount();
    }

    public void onBackClick() {
        if (!title.isEmpty() && !description.isEmpty()) {
            Log.d(TAG, "onBackClick: 내용이 있으므로 문의 제출 시도.");
            sendInquiry(title, description);
        } else {
            Log.d(TAG, "onBackClick: 내용이 없으므로 Activity만 종료.");
            finishActivity.setValue(true);
        }
    }

    public void onConfirmClick() {
        if (inquiryApiClient == null) {
            Log.e(TAG, "onConfirmClick: InquiryApiClient가 설정되지 않았습니다.");
            showToast.setValue("오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return;
        }
        if (!title.trim().isEmpty() && !description.trim().isEmpty()) {
            Log.d(TAG, "onConfirmClick: 제목과 내용이 모두 있어 문의 제출.");
            sendInquiry(title.trim(), description.trim());
        } else {
            Log.d(TAG, "onConfirmClick: 제목 또는 내용이 비어있음.");
            showToast.setValue("제목과 내용을 모두 입력해주세요.");
        }
    }

    private void sendInquiry(String inquiryTitle, String inquiryContent) {
        if (inquiryApiClient == null) {
            Log.e(TAG, "sendInquiry: InquiryApiClient가 설정되지 않았습니다.");
            showToast.setValue("문의 전송 중 오류가 발생했습니다.");
            return;
        }
        Response<Void> response = inquiryApiClient.requestInsertInquiry(inquiryTitle, inquiryContent);

        if (response.isSuccessful()) {
            showToast.setValue("문의를 보냈습니다.");
            Log.d(TAG, "문의 추가 성공. Code: " + response.code());
            finishActivity.setValue(true);
        } else {
            showToast.setValue("문의 전송에 실패했습니다. 다시 시도해주세요.");
            Log.e(TAG, "문의 추가 실패. Code: " + response.code() + ", Message: " + response.message());
        }
    }

    public void doneShowToast() {
        showToast.setValue(null);
    }
}