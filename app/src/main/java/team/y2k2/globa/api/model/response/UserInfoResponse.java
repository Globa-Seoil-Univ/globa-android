package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("profile")
    private String profile;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("publicFolderId")
    private String publicFolderId;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getUserId() {
        return userId;
    }

    public String getPublicFolderId() {
        return publicFolderId;
    }

    public void setPublicFolderId(String publicFolderId) {
        this.publicFolderId = publicFolderId;
    }
}
