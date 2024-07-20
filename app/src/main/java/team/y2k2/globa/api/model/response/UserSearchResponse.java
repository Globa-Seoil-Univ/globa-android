package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UserSearchResponse {
    @SerializedName("userId")
    private int userId;

    @SerializedName("profile")
    private String profile;

    @SerializedName("name")
    private String name;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userid) {
        this.userId = userId;
    }

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
}
