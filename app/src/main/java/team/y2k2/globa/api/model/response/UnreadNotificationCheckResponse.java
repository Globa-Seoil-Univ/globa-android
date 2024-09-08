package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UnreadNotificationCheckResponse {

    @SerializedName("hasUnRead")
    private boolean hasUnRead;

    public boolean isHasUnRead() {
        return hasUnRead;
    }
}
