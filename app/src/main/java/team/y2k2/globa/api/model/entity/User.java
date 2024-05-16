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
}