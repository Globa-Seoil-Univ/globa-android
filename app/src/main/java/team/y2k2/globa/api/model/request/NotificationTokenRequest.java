package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class NotificationTokenRequest {
    @SerializedName("token")
    private final String token;

    public NotificationTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
