package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private int userId;

    @SerializedName("snsKind")
    private String snsKind;

    @SerializedName("snsId")
    private String snsId;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("profile")
    private String profile;


    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }
}