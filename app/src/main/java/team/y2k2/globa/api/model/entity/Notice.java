package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Notice {
    @SerializedName("noticeId")
    private String noticeId;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;

    public String getNoticeId() {
        return noticeId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
