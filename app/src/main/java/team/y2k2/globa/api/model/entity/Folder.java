package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Folder {
    @SerializedName("folderId")
    private String folderId;

    @SerializedName("title")
    private String title;

    @SerializedName("createdTime")
    private String createdTime;

    // Getter and Setter methods


    public String getFolderId() {
        return folderId;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}
