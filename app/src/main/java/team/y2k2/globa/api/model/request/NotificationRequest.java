package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class NotificationRequest {
    @SerializedName("notificationId")
    private final String notificationId;

    public NotificationRequest(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return notificationId;
    }
}
