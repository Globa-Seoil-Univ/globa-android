package team.y2k2.globa.main.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.main.docs.list.DocsListItem;
import team.y2k2.globa.main.docs.list.DocsListItemModel;
import team.y2k2.globa.sql.RecordDB;

public class MainFragmentViewModel {
    ApiClient apiClient;
    Context context;

    MainFragmentModel model;

    RecordResponse recordResponse;
    List<FolderResponse> folderResponse;

    public MainFragmentViewModel(Context context) {
        this.context = context;
        apiClient = new ApiClient(context);
    }

    public String[] getPromotionsImage() {
        List<NoticeResponse> noticeResponse = apiClient.requestPromotion(3);
        String[] images = new String[noticeResponse.size()];

        for(int i = 0; i < noticeResponse.size(); i++) {
            NoticeResponse index = noticeResponse.get(i);
            images[i] = index.getThumbnail();
        }

        return images;
    }

    public ArrayList<DocsListItem> getCurrentlyRecords() {
        recordResponse = apiClient.requestGetRecords(20);
        folderResponse = apiClient.requestGetFolders(1, 20);

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

            recordDB.onInsert(Integer.valueOf(recordId), Integer.valueOf(folderId), title, datetime, keywords);
        }


        model = new MainFragmentModel(records);

        listItems.addItems(model.records);
        return listItems.getItems();
    }

    public ArrayList<DocsListItem> getMostViewedRecords() {
        recordResponse = apiClient.requestGetRecords(20);
        folderResponse = apiClient.requestGetFolders(1, 20);

        DocsListItemModel listItems = new DocsListItemModel();

        List<Record> records = recordResponse.getRecords();
        // Record 클래스의 카운트 값 기반으로 정렬
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                SharedPreferences preferences1 = context.getSharedPreferences("record_" + r1.getRecordId(), Activity.MODE_PRIVATE);
                int count1 = preferences1.getInt("count", 0);

                SharedPreferences preferences2 = context.getSharedPreferences("record_" + r2.getRecordId(), Activity.MODE_PRIVATE);
                int count2 = preferences2.getInt("count", 0);

                // 내림차순 정렬
                return Integer.compare(count2, count1);
            }
        });

        listItems.addItems(records);
        return listItems.getItems();
    }

    public ArrayList<DocsListItem> getSharedRecords() {
        recordResponse = apiClient.requestGetRecords(20);
        folderResponse = apiClient.requestGetFolders(1, 20);

        DocsListItemModel docsList = new DocsListItemModel();
        List<Record> records = recordResponse.getRecords();

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            for(FolderResponse folder : folderResponse) {
                int myFolderId = folder.getFolderId();
                int targetFolderId = Integer.parseInt(folderId);

                if(myFolderId == targetFolderId) {
                    docsList.addItem(recordId, folderId, title, datetime, keywords);
                }
            }
        }
        return docsList.getItems();
    }

    public ArrayList<DocsListItem> getReceivedRecords() {
        recordResponse = apiClient.requestGetRecords(20);
        folderResponse = apiClient.requestGetFolders(1, 20);

        DocsListItemModel docsList = new DocsListItemModel();
        List<Record> records = recordResponse.getRecords();

        ArrayList<Integer> folderIds = new ArrayList<>();

        for(FolderResponse folder : folderResponse) {
            folderIds.add(folder.getFolderId());
        }

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            if(! folderIds.contains(Integer.parseInt(folderId))) {
                docsList.addItem(recordId, folderId, title, datetime, keywords);
            }
        }

        return docsList.getItems();
    }
}