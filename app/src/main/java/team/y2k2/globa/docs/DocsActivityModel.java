package team.y2k2.globa.docs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.docs.detail.DocsDetailItem;
import team.y2k2.globa.docs.summary.DocsSummaryItem; // DocsSummaryItem 클래스가 있다고 가정
import team.y2k2.globa.docs.summary.DocsSummaryModel;

public class DocsActivityModel extends ViewModel {
    // UI 상태를 나타내는 LiveData
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> folderTitle = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsDetailItem>> detailItems = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsSummaryItem>> summaryItems = new MutableLiveData<>();
    private final MutableLiveData<String> audioUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // 외부에서 데이터를 관찰하기 위한 Getter
    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getFolderTitle() { return folderTitle; }
    public LiveData<ArrayList<DocsDetailItem>> getDetailItems() { return detailItems; }
    public LiveData<ArrayList<DocsSummaryItem>> getSummaryItems() { return summaryItems; }
    public LiveData<String> getAudioUrl() { return audioUrl; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Activity에서 변경된 제목을 ViewModel에 반영하기 위한 Setter
    public void setTitle(String newTitle) {
        this.title.setValue(newTitle);
    }

    // 데이터 로딩 메소드
    public void fetchData(Context context, String folderId, String recordId) {
        isLoading.setValue(true);

        // 네트워크 통신은 반드시 백그라운드 스레드에서 실행
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            RecordApiClient apiClient = new RecordApiClient(context);
            DocsDetailResponse response = apiClient.requestGetDocumentDetail(folderId, recordId);

            // 결과는 메인 스레드에서 LiveData에 반영
            handler.post(() -> {
                isLoading.setValue(false);
                if (response != null) {
                    title.setValue(response.getTitle());
                    if (response.getFolder() != null) {
                        folderTitle.setValue(response.getFolder().getTitle());
                    }
                    audioUrl.setValue(response.getPath());

                    // 데이터를 가공하여 LiveData에 저장
                    DocsModel docsModel = new DocsModel(response.getSections());
                    DocsSummaryModel summaryModel = new DocsSummaryModel(response.getSections()); // DocsSummaryModel이 있다고 가정
                    detailItems.setValue(docsModel.getDetailItems());
                    summaryItems.setValue(summaryModel.getItems());

                } else {
                    errorMessage.setValue("문서 정보를 불러오는 데 실패했습니다.");
                }
            });
        });
    }
}