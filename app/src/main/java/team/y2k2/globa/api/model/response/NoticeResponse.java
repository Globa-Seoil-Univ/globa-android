package team.y2k2.globa.api.model.response;


import com.google.gson.annotations.SerializedName;

public class NoticeResponse {
    @SerializedName("noticeId")
    int noticeId;

    @SerializedName("thumbnail")
    String thumbnail;

    @SerializedName("bgColor")
    String bgColor;

    public int getNoticeId() {
        return noticeId;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
