package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import team.y2k2.globa.api.model.entity.User;

public class FolderResponse {
    @SerializedName("folderId")
    private int folderId;

    @SerializedName("user")
    private User user;

    @SerializedName("title")
    private String title;

    @SerializedName("createdTime")
    private String createdTime;

    public String getCreatedTime() {
        return createdTime;
    }

    public String getTitle() {
        return title;
    }

    public int getFolderId() {
        return folderId;
    }

    public User getUser() {
        return user;
    }
}