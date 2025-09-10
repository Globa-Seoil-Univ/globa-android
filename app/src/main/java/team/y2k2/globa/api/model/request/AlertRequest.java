package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class AlertRequest {

    @SerializedName("uploadNofi")
    private final boolean uploadNotification;
    @SerializedName("shareNofi")
    private final boolean shareNotification;
    @SerializedName("eventNofi")
    private final boolean eventNotification;
    @SerializedName("primaryNofi")
    private final boolean primaryNotification;

    public AlertRequest(boolean uploadNotification, boolean shareNotification, boolean eventNotification, boolean primaryNotification) {
        this.uploadNotification = uploadNotification;
        this.shareNotification = shareNotification;
        this.eventNotification = eventNotification;
        this.primaryNotification = primaryNotification;
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
    public boolean isPrimaryNotification() { return primaryNotification; }
}
