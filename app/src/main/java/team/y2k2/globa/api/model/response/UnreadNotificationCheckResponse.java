package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UnreadNotificationCheckResponse {

    @SerializedName("all")
    private int all;

    @SerializedName("notice")
    private int notice;

    @SerializedName("share")
    private int share;

    @SerializedName("document")
    private int document;

    @SerializedName("inquiry")
    private int inquiry;

    public int getAll() {
        return all;
    }

    public int getDocument() {
        return document;
    }

    public int getInquiry() {
        return inquiry;
    }

    public int getNotice() {
        return notice;
    }

    public int getShare() {
        return share;
    }
}
