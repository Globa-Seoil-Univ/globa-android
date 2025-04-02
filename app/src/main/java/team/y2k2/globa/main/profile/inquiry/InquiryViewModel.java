package team.y2k2.globa.main.profile.inquiry;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.clients.InquiryApiClient;

public class InquiryViewModel extends ViewModel {

    private MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private MutableLiveData<String> showToast = new MutableLiveData<>();
    private String title = "";
    private String description = "";
    private MutableLiveData<String> titleCount = new MutableLiveData<>();

    private InquiryApiClient inquiryApiClient;

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
        title = newTitle;
        updateTitleCount();
    }

    public void onTitleAfterTextChanged(String newTitle) {
        title = newTitle;
        updateTitleCount();
    }

    public void onDescriptionTextChanged(String newDescription) {
        description = newDescription;
    }

    private void updateTitleCount() {
        titleCount.setValue(title.length() + "/32");
    }

    public void setInquiryApiClient(InquiryApiClient inquiryApiClient) {
        this.inquiryApiClient = inquiryApiClient;
        updateTitleCount();
    }

    public void onBackClick() {
        finishActivity.setValue(true);
    }

    public void onConfirmClick() {
        if (!title.isEmpty() && !description.isEmpty()) {
            sendInquiry(title, description);
        } else {
            showToast.setValue("제목과 내용을 모두 입력해주세요.");
        }
    }

    private void sendInquiry(String title, String content) {
        Response<Void> response = inquiryApiClient.requestInsertInquiry(title, content);

        if (response.isSuccessful()) {
            showToast.setValue("문의를 보냈습니다.");
            Log.d("INQUIRY_RESULT", "문의 추가 성공");
            finishActivity.setValue(true);
        } else {
            showToast.setValue("문의 전송에 실패했습니다. 다시 시도해주세요.");
            Log.d("INQUIRY_RESULT", "문의 추가 실패" + response.code());
        }
    }

    public void doneShowToast() {
        showToast.setValue(null);
    }
}