package team.y2k2.globa.main.search;

import android.util.Log;

public class SearchDocsItem {
    private final String title;
    private final String datetime;
    private final String recordId;
    private final String folderId;

    public SearchDocsItem(String folderId, String recordId, String title, String datetime) {
        this.folderId = folderId;
        this.recordId = recordId;
        this.title = title;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getRecordId() {
        return recordId;
    }
}