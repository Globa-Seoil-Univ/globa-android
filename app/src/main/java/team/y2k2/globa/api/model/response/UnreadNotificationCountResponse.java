package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UnreadNotificationCountResponse {

    @SerializedName("all")
    private String all;
    @SerializedName("notice")
    private String notice;
    @SerializedName("share")
    private String share;
    @SerializedName("document")
    private String document;
    @SerializedName("inquiry")
    private String inquiry;

    public String getAll() {
        return all;
    }
    public String getNotice() {
        return notice;
    }
    public String getShare() {
        return share;
    }
    public String getDocument() {
        return document;
    }
    public String getInquiry() {
        return inquiry;
    }

}
