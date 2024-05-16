package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class FolderInsideRecord {
    @SerializedName("recordId")
    private String recordId;

    @SerializedName("folderId")
    private String folderId;

    @SerializedName("title")
    private String title;

    @SerializedName("path")
    private String path;

    @SerializedName("createdTime")
    private String createdTime;


    public String getTitle() {
        return title;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getPath() {
        return path;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getFolderId() {
        return folderId;
    }
}
