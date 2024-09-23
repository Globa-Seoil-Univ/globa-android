package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class AlertRequest {

    @SerializedName("uploadNofi")
    private boolean uploadNofi;
    @SerializedName("shareNofi")
    private boolean shareNofi;
    @SerializedName("eventNofi")
    private boolean eventNofi;

    public AlertRequest(boolean uploadNofi, boolean shareNofi, boolean eventNofi) {
        this.uploadNofi = uploadNofi;
        this.shareNofi = shareNofi;
        this.eventNofi = eventNofi;
    }

    public boolean isUploadNofi() {
        return uploadNofi;
    }
    public boolean isShareNofi() {
        return shareNofi;
    }
    public boolean isEventNofi() {
        return eventNofi;
    }
}
