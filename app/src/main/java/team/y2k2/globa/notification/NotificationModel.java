package team.y2k2.globa.notification;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Notice;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.api.model.response.NotificationResponse;

public class NotificationModel {
    NotificationResponse response;

    List<Notification> notifications;

    public NotificationModel(NotificationResponse response) {
        this.response = response;
        notifications = response.getNotifications();
    }


    public List<Notification> getNotifications() {
        return notifications;
    }

    public NotificationResponse getResponse() {
        return response;
    }
}
