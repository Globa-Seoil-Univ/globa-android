package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FolderShareDeniedRequest {
    @SerializedName("notificationId")
    private int notificationId;

    public FolderShareDeniedRequest(int notificationId) {
        this.notificationId = notificationId;
    }
}
