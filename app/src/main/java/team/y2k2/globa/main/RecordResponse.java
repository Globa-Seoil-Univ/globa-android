package team.y2k2.globa.main;

import com.google.gson.annotations.SerializedName;

import java.util.List;


class Record {
    @SerializedName("recordId")
    private String recordId;

    @SerializedName("folderId")
    private String folderId;

    @SerializedName("title")
    private String title;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("createdTime")
    private String createdTime;
    public String getCreatedTime() {
        return createdTime;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getRecordId() {
        return recordId;
    }
}

public class RecordResponse {

    @SerializedName("records")
    private List<Record> records;

    @SerializedName("total")
    private int total;


    public int getTotal() {
        return total;
    }

    public List<Record> getRecords() {
        return records;
    }
}
