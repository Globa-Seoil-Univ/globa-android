package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FolderShareDeniedRequest {
    @SerializedName("notificationId")
    private final int notificationId;

    public FolderShareDeniedRequest(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
