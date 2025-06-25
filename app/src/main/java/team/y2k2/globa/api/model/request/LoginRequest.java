package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

import team.y2k2.globa.login.LoginModel;

public class LoginRequest {
    @SerializedName("snsKind")
    private final String snsKind;

    @SerializedName("snsId")
    private final String snsId;

    @SerializedName("name")
    private final String name;

    @SerializedName("token")
    private final String token;

    @SerializedName("profile")
    private final String profile;

    @SerializedName("notification")
    private final boolean notification;

    @SerializedName("eventNotification")
    private final boolean eventNotification;

    public LoginRequest(LoginModel model, boolean notification, String token) {
        this.snsKind = model.getSnsKind();
        this.snsId = model.getUid();
        this.name = model.getName();
        this.profile = model.getProfileImageUrl();
        this.notification = notification;
        this.token = token;
        this.eventNotification = true;
    }

    public String getToken() {
        return token;
    }

    public String getProfile() {
        return profile;
    }
    public String getSnsKind() {
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

    public boolean isEventNotification() {
        return eventNotification;
    }
}
