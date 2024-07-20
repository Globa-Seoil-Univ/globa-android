package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Share {
    @SerializedName("shareId")
    private String shareId;

    public String getShareId() {
        return shareId;
    }
}
