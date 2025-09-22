package team.y2k2.globa.docs.edit; // 본인의 패키지 경로로 수정하세요

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.util.Event;

public class DocsNameEditViewModel extends ViewModel {

    private static final int MAX_TITLE_LENGTH = 32;

    // 전달받는 데이터 및 의존성
    private final String initialTitle;
    private final String recordId;
    private final String folderId;
    private final RecordApiClient recordApiClient;

    // UI 상태를 나타내는 LiveData
    public final MutableLiveData<String> currentTitle = new MutableLiveData<>();
    public final LiveData<String> charCountText;
    public final LiveData<Boolean> isConfirmEnabled; // 변경 버튼 활성화 여부 (입력값 비어있는지)
    public final LiveData<Boolean> hasTextChanged; // 초기값 대비 변경 여부
    public final LiveData<Boolean> isCancelVisible; // 취소 버튼 표시 여부
    private final MutableLiveData<Event<String>> _editSuccessEvent = new MutableLiveData<>();
    public final LiveData<Event<String>> editSuccessEvent = _editSuccessEvent;

    private final MutableLiveData<Event<Void>> _navigateBackEvent = new MutableLiveData<>();
    public final LiveData<Event<Void>> navigateBackEvent = _navigateBackEvent;

    private final MutableLiveData<Event<String>> _showToastEvent = new MutableLiveData<>();
    public final LiveData<Event<String>> showToastEvent = _showToastEvent;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public DocsNameEditViewModel(String initialTitle, String recordId, String folderId, RecordApiClient recordApiClient) {
        this.initialTitle = initialTitle;
        this.recordId = recordId;
        this.folderId = folderId;
        this.recordApiClient = recordApiClient;

        // LiveData 초기화
        currentTitle.setValue(initialTitle);

        isConfirmEnabled = Transformations.map(currentTitle, title -> title != null && !title.isEmpty());
        hasTextChanged = Transformations.map(currentTitle, current -> current != null && !current.equals(this.initialTitle));
        isCancelVisible = Transformations.map(currentTitle, title -> title != null && !title.isEmpty());

        charCountText = Transformations.map(currentTitle, title -> {
            int length = (title != null) ? title.length() : 0;
            int displayLength = Math.min(length, MAX_TITLE_LENGTH);
            return displayLength + "/" + MAX_TITLE_LENGTH;
        });
    }

    public void onConfirmClicked() {
        // 1. 입력값이 유효한지  확인
        Boolean isEnabled = isConfirmEnabled.getValue();
        if (isEnabled == null || !isEnabled) {
            _showToastEvent.setValue(new Event<>("제목을 입력해 주세요."));
            return; // 비활성화 상태면 더 이상 진행하지 않음
        }

        // 2. 변경 사항이 있는지 확인
        Boolean hasChanged = hasTextChanged.getValue();
        if (hasChanged == null || !hasChanged) {
            _showToastEvent.setValue(new Event<>("변경 사항이 없습니다."));
            return; // 변경 사항 없으면 API 호출 등 다음 단계로 진행하지 않음
        }

        // 3. 활성화 상태이고 변경 사항도 있을 경우, 이름 업데이트 진행
        String newTitle = currentTitle.getValue();
        // isEnabled가 true이므로 newTitle이 null이거나 비어있지 않음
        updateDocsName(newTitle);
    }

    public void onCancelClicked() {
        currentTitle.setValue("");
    }

    public void onBackClicked() {
        _navigateBackEvent.setValue(new Event<>(null));
    }

    private void updateDocsName(final String title) {
        executorService.execute(() -> {
            try {
                Response<?> response = recordApiClient.requestUpdateRecordName(folderId, recordId, title);

                if (response != null && response.isSuccessful()) {
                    Log.d(getClass().getSimpleName(), "Update successful: folderId = " + folderId + ", recordId = " + recordId + ", title =" + title);
                    _editSuccessEvent.postValue(new Event<>(title));
                } else {
                    String errorMsg = "이름 변경 실패";
                    if (response != null) {
                        errorMsg += ": " + response.code() + " " + response.message();
                        Log.e(getClass().getSimpleName(), "API Error: " + response.code() + " - " + response.message());
                    } else {
                        Log.e(getClass().getSimpleName(), "API Error: Response was null");
                    }
                    _showToastEvent.postValue(new Event<>(errorMsg));
                }
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Exception during update: " + e.getMessage(), e);
                _showToastEvent.postValue(new Event<>("이름 변경 중 오류 발생: " + e.getMessage()));
            }
        });
    }

    public LiveData<Event<String>> getEditSuccessEvent() {
        return editSuccessEvent;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Executor 종료
        executorService.shutdown();
    }
}