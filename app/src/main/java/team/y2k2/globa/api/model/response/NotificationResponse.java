package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Notification;

public class NotificationResponse {
    @SerializedName("notifications")
    private List<Notification> notifications;

    @SerializedName("total")
    private int total;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public int getTotal() {
        return total;
    }
}
