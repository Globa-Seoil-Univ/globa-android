package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.Keyword;

public class Record {
    @SerializedName("recordId")
    private String recordId;

    @SerializedName("folderId")
    private String folderId;

    @SerializedName("title")
    private String title;

    @SerializedName("keywords")
    private List<Keyword> keywords;

    @SerializedName("createdTime")
    private String createdTime;
    public String getCreatedTime() {
        return createdTime;
    }

    public String getTitle() {
        return title;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public String getFolderId() {
        return folderId;
    }
    public String getRecordId() {
        return recordId;
    }
}
