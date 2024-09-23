package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class NotificationTokenRequest {

    @SerializedName("token")
    private String token;

    public NotificationTokenRequest(String token) {
        this.token = token;
    }

}
