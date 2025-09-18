package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class AlertResponse {
    @SerializedName("uploadNofi")
    private boolean uploadNofi;
    @SerializedName("shareNofi")
    private boolean shareNofi;
    @SerializedName("eventNofi")
    private boolean eventNofi;
    @SerializedName("primaryNofi")
    private boolean primaryNofi;

    public boolean isUploadNofi() {
        return uploadNofi;
    }
    public boolean isShareNofi() {
        return shareNofi;
    }
    public boolean isEventNofi() {
        return eventNofi;
    }

    public boolean isPrimaryNofi() { return primaryNofi; }

}
