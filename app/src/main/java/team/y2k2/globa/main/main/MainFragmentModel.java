package team.y2k2.globa.main.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

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
    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;
    private NoticeApiClient noticeApiClient;
    private NotificationApiClient notificationApiClient;
    private Context context;
    private MainModel model;
    private RecordResponse recordResponse;
    private FolderResponse folderResponse;

    public void setContext(Context context) {
        this.context = context;
        this.noticeApiClient = new NoticeApiClient() {
            @Override
            public void displayErrorDialog(int errorCode, String errorMessage) {
                // 다이얼로그 표시할 이유 없이, 해당 NoticeFragment에서 직접 처리
            }
        };
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient(){
            @Override
            public void displayErrorDialog(int errorCode, String errorMessage) {
                // 다이얼로그 표시할 이유 없이, 해당 RecordAdapter에서 직접 처리
            }
        };
        this.notificationApiClient = new NotificationApiClient();
    }

    public MutableLiveData<UnreadNotificationCheckResponse> getNotificationCheckLiveData() {
        return notificationCheckLiveData;
    }

    public String[] getPromotionsImage() {
        List<NoticeResponse> noticeResponse = noticeApiClient.requestPromotion(3);
        if (noticeResponse == null) {
            return new String[0];
        }
        String[] images = new String[noticeResponse.size()];

        for (int i = 0; i < noticeResponse.size(); i++) {
            NoticeResponse index = noticeResponse.get(i);
            images[i] = index.getThumbnail();
        }

        return images;
    }

    public ArrayList<DocsListItem> getCurrentlyRecords() {
        recordResponse = recordApiClient.requestGetRecords(100);
        folderResponse = folderApiClient.requestGetFolders(1, 100);

        DocsListItemModel listItems = new DocsListItemModel();
        List<Record> records = recordResponse.getRecords();

        RecordDB recordDB = new RecordDB(context);
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);

            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            recordDB.onInsert(Integer.parseInt(recordId), Integer.parseInt(folderId), title, datetime, keywords);
        }

        model = new MainModel(records);

        listItems.addItems(model.records);
        return listItems.getItems();
    }

    public ArrayList<DocsListItem> getMostViewedRecords() {
        recordResponse = recordApiClient.requestGetRecords(100);
        folderResponse = folderApiClient.requestGetFolders(1, 100);

        DocsListItemModel listItems = new DocsListItemModel();

        List<Record> records = recordResponse.getRecords();
        // Record 클래스의 카운트 값 기반으로 정렬
        records.sort((r1, r2) -> {
            SharedPreferences preferences1 = context.getSharedPreferences("record_" + r1.getRecordId(), Activity.MODE_PRIVATE);
            int count1 = preferences1.getInt("count", 0);

            SharedPreferences preferences2 = context.getSharedPreferences("record_" + r2.getRecordId(), Activity.MODE_PRIVATE);
            int count2 = preferences2.getInt("count", 0);

            // 내림차순 정렬
            return Integer.compare(count2, count1);
        });

        listItems.addItems(records);
        return listItems.getItems();
    }

    public ArrayList<DocsListItem> getSharedRecords() {
        recordResponse = recordApiClient.requestGetRecordsOfSharing(20);

        DocsListItemModel listItems = new DocsListItemModel();
        List<Record> records = recordResponse.getRecords();

        RecordDB recordDB = new RecordDB(context);
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);

            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            recordDB.onInsert(Integer.parseInt(recordId), Integer.parseInt(folderId), title, datetime, keywords);
        }

        model = new MainModel();

        listItems.addItems(model.records);
        return listItems.getItems();
    }

    public ArrayList<DocsListItem> getReceivedRecords() {
        recordResponse = recordApiClient.requestGetRecordsOfReceiving(20);

        DocsListItemModel listItems = new DocsListItemModel();
        List<Record> records = recordResponse.getRecords();

        RecordDB recordDB = new RecordDB(context);
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);

            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            recordDB.onInsert(Integer.parseInt(recordId), Integer.parseInt(folderId), title, datetime, keywords);
        }
        model = new MainModel();

        listItems.addItems(model.records);
        return listItems.getItems();
    }

    public void getUnreadNotificationCheck() {
        UnreadNotificationCheckResponse response = notificationApiClient.getUnreadNotificationCheck();
        notificationCheckLiveData.postValue(response);
    }
}