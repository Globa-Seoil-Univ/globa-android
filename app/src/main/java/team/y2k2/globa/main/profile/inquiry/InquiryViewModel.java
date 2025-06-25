package team.y2k2.globa.main.profile.inquiry;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.clients.InquiryApiClient;

public class InquiryViewModel extends ViewModel {

    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private final MutableLiveData<String> showToast = new MutableLiveData<>();
    private String title = ""; // 입력된 제목을 저장하는 내부 변수
    private String description = ""; // 입력된 내용을 저장하는 내부 변수
    // titleCount LiveData를 "0/32"로 초기화
    private final MutableLiveData<String> titleCount = new MutableLiveData<>("0/32");

    private InquiryApiClient inquiryApiClient;
    private static final String TAG = "InquiryViewModel"; // 로그 태그 추가

    public InquiryViewModel() {
        // API 클라이언트는 setInquiryApiClient를 통해 외부에서 주입받음
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public LiveData<String> getShowToast() {
        return showToast;
    }

    public LiveData<String> getTitleCount() {
        return titleCount;
    }

    // XML에서 양방향 데이터 바인딩을 사용하지 않는다면, EditText의 값을 직접 가져오기 위한 getter는 불필요할 수 있음
    // 현재는 Activity의 TextWatcher에서 값을 받아와 ViewModel의 title, description을 업데이트하는 방식
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Activity의 TextWatcher에서 호출됨
    public void onTitleTextChanged(String newTitle) {
        this.title = newTitle;
        updateTitleCount();
    }

    // Activity의 TextWatcher에서 호출됨 (afterTextChanged)
    public void onTitleAfterTextChanged(String newTitle) {
        // 이 메서드는 현재 onTitleTextChanged와 동일한 작업을 수행.
        // 특별한 추가 로직이 없다면 onTitleTextChanged만 사용해도 무방.
        // 여기서는 일관성을 위해 남겨두지만, 필요에 따라 로직 통합 가능.
        this.title = newTitle;
        // updateTitleCount(); // onTitleTextChanged에서 이미 호출되므로 중복 호출 방지 가능
    }

    // Activity의 TextWatcher에서 호출됨
    public void onDescriptionTextChanged(String newDescription) {
        this.description = newDescription;
    }

    private void updateTitleCount() {
        titleCount.setValue(title.length() + "/32");
    }

    public void setInquiryApiClient(InquiryApiClient inquiryApiClient) {
        this.inquiryApiClient = inquiryApiClient;
        // API 클라이언트가 설정될 때 (Activity onCreate 시점) 초기 제목 길이에 따라 titleCount 업데이트
        updateTitleCount();
    }

    public void onBackClick() {
        // ViewModel의 현재 title과 description 상태를 기반으로 문의를 보낼지 결정
        if (!title.isEmpty() && !description.isEmpty()) {
            Log.d(TAG, "onBackClick: 내용이 있으므로 문의 제출 시도.");
            sendInquiry(title, description);
        } else {
            Log.d(TAG, "onBackClick: 내용이 없으므로 Activity만 종료.");
            finishActivity.setValue(true); // 내용 없으면 그냥 종료
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
        // API 클라이언트가 null인지 여기서도 한번 더 확인하는 것이 안전
        if (inquiryApiClient == null) {
            Log.e(TAG, "sendInquiry: InquiryApiClient가 설정되지 않았습니다.");
            showToast.setValue("문의 전송 중 오류가 발생했습니다.");
            return;
        }
        Response<Void> response = inquiryApiClient.requestInsertInquiry(inquiryTitle, inquiryContent);

        if (response.isSuccessful()) {
            showToast.setValue("문의를 보냈습니다.");
            Log.d(TAG, "문의 추가 성공. Code: " + response.code());
            finishActivity.setValue(true); // 성공 시 Activity 종료
        } else {
            showToast.setValue("문의 전송에 실패했습니다. 다시 시도해주세요.");
            Log.e(TAG, "문의 추가 실패. Code: " + response.code() + ", Message: " + response.message());
        }
    }

    public void doneShowToast() {
        showToast.setValue(null); // Toast가 표시된 후 LiveData 값을 null로 변경하여 중복 표시 방지
    }
}