package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("snsKind")
    private int snsKind;

    @SerializedName("snsId")
    private String snsId;

    @SerializedName("name")
    private String name;

    @SerializedName("profile")

    private String profile;

    @SerializedName("notification")
    private boolean notification;

    public LoginRequest(int snsKind, String snsId, String name, String profile, boolean notification) {
        this.snsKind = snsKind;
        this.snsId = snsId;
        this.name = name;
        this.profile = profile;
        this.notification = notification;
    }

    public String getProfile() {
        return profile;
    }

    public int getSnsKind() {
        return snsKind;
    }

    public String getSnsId() {
        return snsId;
    }

    public String getName() {
        return name;
    }

    public boolean isNotification() {
        return notification;
    }
}
