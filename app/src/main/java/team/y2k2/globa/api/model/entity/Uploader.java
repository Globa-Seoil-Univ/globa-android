package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Uploader {
    @SerializedName("folderId")
    private String userId;

    @SerializedName("title")
    private String profile;

    @SerializedName("createdTime")
    private String name;

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public String getUserId() {
        return userId;
    }
}
