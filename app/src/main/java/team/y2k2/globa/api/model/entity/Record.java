package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
