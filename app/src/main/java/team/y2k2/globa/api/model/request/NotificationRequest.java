package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class NotificationRequest {

    @SerializedName("notificationId")
    private String notificationId;

    public NotificationRequest(String notificationId) {
        this.notificationId = notificationId;
    }
}
