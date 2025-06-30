package team.y2k2.globa.main.profile.withdraw; // 본인의 패키지 경로로 수정

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.FirebaseMessaging; // Firebase 의존성

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.util.Event;

public class WithdrawViewModel extends ViewModel {

    private final UserApiClient userApiClient;
    private final FirebaseMessaging firebaseMessaging;

    private final List<String> reasonOptions = Arrays.asList(
            "서비스 사용이 불편해요",
            "정확성이 떨어져요",
            "기능이 부족해요",
            "다른 서비스가 더 좋아요"
    );

    public final MutableLiveData<String> selectedReason = new MutableLiveData<>();
    public final MutableLiveData<String> detailReason = new MutableLiveData<>(""); // EditText와 양방향 바인딩
    public final LiveData<Boolean> isWithdrawButtonEnabled; // 탈퇴 버튼 활성화 조건
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Event<Class<?>>> _navigateToActivity = new MutableLiveData<>(); // 내비게이션 이벤트
    private final MutableLiveData<Event<String>> _showToastEvent = new MutableLiveData<>(); // 토스트 메시지 이벤트
    private final MutableLiveData<Event<Void>> _withdrawSuccessEvent = new MutableLiveData<>(); // 탈퇴 성공 이벤트 (SharedPreferences 처리용)
    private final MutableLiveData<Event<String>> _withdrawErrorEvent = new MutableLiveData<>(); // 탈퇴 실패 이벤트 (에러 메시지 포함)
    public final LiveData<Event<Class<?>>> navigateToActivity = _navigateToActivity; // Public 필드 추가
    public final LiveData<Event<String>> showToastEvent = _showToastEvent; // Public 필드 추가
    public final LiveData<Event<Void>> withdrawSuccessEvent = _withdrawSuccessEvent; // Public 필드 추가
    public final LiveData<Event<String>> withdrawErrorEvent = _withdrawErrorEvent; // Public 필드 추가



    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // 생성자 주입 (DI 프레임워크 사용 권장)
    public WithdrawViewModel(UserApiClient apiClient, FirebaseMessaging firebaseMessaging) {
        this.userApiClient = apiClient;
        this.firebaseMessaging = firebaseMessaging;

        // 탈퇴 버튼 활성화 로직: 사유가 선택되었는지 확인
        isWithdrawButtonEnabled = Transformations.map(selectedReason, reason ->
                reason != null && !reason.isEmpty()
        );
    }

    public void onWithdrawClicked() {
        String reason = selectedReason.getValue();
        String detail = detailReason.getValue() != null ? detailReason.getValue() : "";

        if (reason == null || reason.isEmpty()) {
            _showToastEvent.setValue(new Event<>("탈퇴 사유를 선택해주세요.")); // 리소스 사용 권장
            return;
        }

        int surveyType = reasonOptions.indexOf(reason) + 1;
        if (surveyType <= 0) { // 리스트에 없는 값이 선택된 경우 (오류 상황)
            _showToastEvent.setValue(new Event<>("유효하지 않은 사유입니다."));
            Log.e(getClass().getSimpleName(), "Invalid reason selected: " + reason);
            return;
        }

        // 로딩 시작 및 API 호출
        _isLoading.setValue(true);
        withdrawUserInternal(surveyType, detail);
    }

    // 실제 API 호출 (백그라운드 스레드에서 실행)
    private void withdrawUserInternal(int surveyType, String content) {
        // *** 실제 앱에서는 Retrofit의 Call.enqueue 사용 권장 ***
        executorService.execute(() -> {
            try {
                // API 호출
                Response<Void> response = userApiClient.requestWithdrawUser(surveyType, content);

                // UI 스레드에서 LiveData 업데이트
                if (response != null && response.isSuccessful()) {
                    Log.d(getClass().getSimpleName(), "회원 탈퇴 성공");
                    unsubscribeFirebaseTopics();
                    _withdrawSuccessEvent.postValue(new Event<>(null)); // SharedPreferences 처리용
                    _showToastEvent.postValue(new Event<>("회원 탈퇴 성공")); // 토스트 메시지용
                    _navigateToActivity.postValue(new Event<>(IntroActivity.class)); // 화면 이동용
                } else {
                    String errorMsg = "회원 탈퇴 실패";
                    int code = (response != null) ? response.code() : -1;
                    String respMsg = (response != null) ? response.message() : "Response is null";
                    errorMsg += " (Code: " + code + ")";
                    Log.d(getClass().getSimpleName(), "회원 탈퇴 실패 : " + code + " - " + respMsg);
                    _withdrawErrorEvent.postValue(new Event<>(errorMsg + "\n" + respMsg)); // 실패 메시지 전달
                }
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "회원 탈퇴 중 오류 발생: " + e.getMessage(), e);
                _withdrawErrorEvent.postValue(new Event<>("오류가 발생했습니다: " + e.getMessage()));
            } finally {
                _isLoading.postValue(false);
            }
        });
    }

    // Firebase 토픽 구독 해지
    private void unsubscribeFirebaseTopics() {
        try {
            // 어디서 쓰는 지 모름
            firebaseMessaging.unsubscribeFromTopic("notification");

            // 기본 제공 서비스
            firebaseMessaging.unsubscribeFromTopic("notice");
            firebaseMessaging.unsubscribeFromTopic("event");

            Log.d(getClass().getSimpleName(), "FCM 토픽 구독 해제");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error unsubscribing from Firebase topics", e);
            // 구독 해지 실패는 치명적이지 않으므로, 로그만 남기고 계속 진행
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // ViewModel 소멸 시 Executor 종료
    }

    // Activity의 RadioGroup 리스너에서 호출될 메서드
    public void setSelectedReason(String reason) {
        selectedReason.setValue(reason);
    }
}