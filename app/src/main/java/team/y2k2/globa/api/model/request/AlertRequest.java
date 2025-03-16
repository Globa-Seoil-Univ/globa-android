package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class AlertRequest {

    @SerializedName("uploadNofi")
    private final boolean uploadNotification;
    @SerializedName("shareNofi")
    private final boolean shareNotification;
    @SerializedName("eventNofi")
    private final boolean eventNotification;

    public AlertRequest(boolean uploadNotification, boolean shareNotification, boolean eventNofi) {
        this.uploadNotification = uploadNotification;
        this.shareNotification = shareNotification;
        this.eventNotification = eventNofi;
    }

    public boolean isUploadNotification() {
        return uploadNotification;
    }
    public boolean isShareNotification() {
        return shareNotification;
    }
    public boolean isEventNofi() {
        return eventNotification;
    }
}
