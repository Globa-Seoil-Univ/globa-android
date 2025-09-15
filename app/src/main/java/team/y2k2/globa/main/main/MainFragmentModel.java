package team.y2k2.globa.main.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.NoticeApiClient;
import team.y2k2.globa.api.clients.NotificationApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCheckResponse;
import team.y2k2.globa.main.docs.list.DocsListItem;
import team.y2k2.globa.main.docs.list.DocsListItemModel;
import team.y2k2.globa.sql.RecordDB;

public class MainFragmentModel extends ViewModel {
    private final MutableLiveData<UnreadNotificationCheckResponse> notificationCheckLiveData = new MutableLiveData<>();
    private final MutableLiveData<String[]> promotionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsListItem>> currentlyRecordsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsListItem>> mostViewedRecordsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsListItem>> sharedRecordsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DocsListItem>> receivedRecordsLiveData = new MutableLiveData<>();

    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;
    private NoticeApiClient noticeApiClient;
    private NotificationApiClient notificationApiClient;
    private Context context;
    private MainModel model;

    public void setContext(Context context) {
        this.context = context;
        // API 클라이언트 초기화 시 익명 클래스 구현을 제거하여 코드를 간결하게 만듭니다.
        // 에러 다이얼로그 표시는 ApiClient의 책임이므로 ViewModel에서 재정의할 필요가 없습니다.
        this.noticeApiClient = new NoticeApiClient();
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient(context);
        this.notificationApiClient = new NotificationApiClient();
    }

    // 각 데이터에 대한 LiveData Getter를 제공하여 UI가 관찰할 수 있도록 합니다.
    public MutableLiveData<UnreadNotificationCheckResponse> getNotificationCheckLiveData() {
        return notificationCheckLiveData;
    }
    public MutableLiveData<String[]> getPromotionsLiveData() {
        return promotionsLiveData;
    }
    public MutableLiveData<ArrayList<DocsListItem>> getCurrentlyRecordsLiveData() {
        return currentlyRecordsLiveData;
    }
    public MutableLiveData<ArrayList<DocsListItem>> getMostViewedRecordsLiveData() {
        return mostViewedRecordsLiveData;
    }
    public MutableLiveData<ArrayList<DocsListItem>> getSharedRecordsLiveData() {
        return sharedRecordsLiveData;
    }
    public MutableLiveData<ArrayList<DocsListItem>> getReceivedRecordsLiveData() {
        return receivedRecordsLiveData;
    }


    public void loadPromotionsImage() {
        CompletableFuture.runAsync(() -> {
            List<NoticeResponse> noticeResponse = noticeApiClient.requestPromotion(3);
            if (noticeResponse == null) {
                Log.e(getClass().getName(), "프로모션 이미지 조회 API가 null을 반환했습니다.");
                promotionsLiveData.postValue(new String[0]);
                return;
            }
            String[] images = new String[noticeResponse.size()];
            for (int i = 0; i < noticeResponse.size(); i++) {
                images[i] = noticeResponse.get(i).getThumbnail();
            }
            promotionsLiveData.postValue(images);
        });
    }

    public void loadCurrentlyRecords() {
        CompletableFuture.runAsync(() -> {
            RecordResponse recordResponse = recordApiClient.requestGetRecords(100);
            FolderResponse folderResponse = folderApiClient.requestGetFolders(1, 100);

            if (recordResponse == null || folderResponse == null) {
                Log.e(getClass().getName(), "최신 기록 또는 폴더 조회 API가 null을 반환했습니다.");
                currentlyRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            DocsListItemModel listItems = new DocsListItemModel();
            List<Record> records = recordResponse.getRecords();

            if (records == null) {
                Log.e(getClass().getName(), "최신 기록 목록(records)이 null입니다.");
                currentlyRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            RecordDB recordDB = new RecordDB(context);
            for (Record record : records) {
                if(record != null) {
                    recordDB.onInsert(Integer.parseInt(record.getRecordId()), Integer.parseInt(record.getFolderId()), record.getTitle(), record.getCreatedTime(), record.getKeywords());
                }
            }

            model = new MainModel(records);
            listItems.addItems(model.records);
            currentlyRecordsLiveData.postValue(listItems.getItems());
        });
    }

    public void loadMostViewedRecords() {
        CompletableFuture.runAsync(() -> {
            RecordResponse recordResponse = recordApiClient.requestGetRecords(100);

            if (recordResponse == null) {
                Log.e(getClass().getName(), "많이 본 기록 조회 API가 null을 반환했습니다.");
                mostViewedRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            List<Record> records = recordResponse.getRecords();
            if (records == null) {
                Log.e(getClass().getName(), "많이 본 기록 목록(records)이 null입니다.");
                mostViewedRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            records.sort((r1, r2) -> {
                SharedPreferences prefs1 = context.getSharedPreferences("record_" + r1.getRecordId(), Activity.MODE_PRIVATE);
                int count1 = prefs1.getInt("count", 0);
                SharedPreferences prefs2 = context.getSharedPreferences("record_" + r2.getRecordId(), Activity.MODE_PRIVATE);
                int count2 = prefs2.getInt("count", 0);
                return Integer.compare(count2, count1);
            });

            DocsListItemModel listItems = new DocsListItemModel();
            listItems.addItems(records);
            mostViewedRecordsLiveData.postValue(listItems.getItems());
        });
    }

    public void loadSharedRecords() {
        CompletableFuture.runAsync(() -> {
            RecordResponse recordResponse = recordApiClient.requestGetRecordsOfSharing(20);

            if (recordResponse == null || recordResponse.getRecords() == null) {
                Log.e(getClass().getName(), "공유한 기록 조회 API 결과가 null입니다.");
                sharedRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            DocsListItemModel listItems = new DocsListItemModel();
            listItems.addItems(recordResponse.getRecords());
            sharedRecordsLiveData.postValue(listItems.getItems());
        });
    }

    public void loadReceivedRecords() {
        CompletableFuture.runAsync(() -> {
            RecordResponse recordResponse = recordApiClient.requestGetRecordsOfReceiving(20);

            if (recordResponse == null || recordResponse.getRecords() == null) {
                Log.e(getClass().getName(), "공유받은 기록 조회 API 결과가 null입니다.");
                receivedRecordsLiveData.postValue(new ArrayList<>());
                return;
            }

            DocsListItemModel listItems = new DocsListItemModel();
            listItems.addItems(recordResponse.getRecords());
            receivedRecordsLiveData.postValue(listItems.getItems());
        });
    }

    public void loadUnreadNotificationCheck() {
        CompletableFuture.runAsync(() -> {
            UnreadNotificationCheckResponse response = notificationApiClient.getUnreadNotificationCheck();
            if (response != null) {
                notificationCheckLiveData.postValue(response);
            } else {
                Log.e(getClass().getName(), "읽지 않은 알림 확인 API가 null을 반환했습니다.");
            }
        });
    }
}

