package team.y2k2.globa.main.docs.list;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Record;

public class DocsListItemModel {
    private final ArrayList<DocsListItem> items;

    public DocsListItemModel() {
        items = new ArrayList<>();
//        DocsListItem item1 = new DocsListItem(R.string.docs_title_sample_1,R.string.docs_title_datetime_1);
//        List<String> keywordList_1 = new ArrayList<>();
//        keywordList_1.add("글로바");
//        keywordList_1.add("테스트");
//        keywordList_1.add("생성형 AI");
//        keywordList_1.add("서일대학교");
//        item1.setKeywordList(keywordList_1);
//        item1.setImage(R.drawable.profile_image_2, R.drawable.profile_image_1, R.drawable.profile_image_3);
    }


    public void addItem(String recordId, String folderId, String title, String datetime, List<Keyword> keywords) {
        Log.d(getClass().getName(), recordId + " | " + folderId + " | " + title + " | " + datetime + " | ");
        DocsListItem item = new DocsListItem(recordId, folderId, title, datetime);
        item.setKeywordList(keywords);
        items.add(item);
    }

    public void addItems(List<Record> records){
        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            DocsListItem item = new DocsListItem(recordId, folderId, title, datetime);
            item.setKeywordList(keywords);

            items.add(item);
        }
    }

    public ArrayList<DocsListItem> getItems() {
        return items;
    }
}
