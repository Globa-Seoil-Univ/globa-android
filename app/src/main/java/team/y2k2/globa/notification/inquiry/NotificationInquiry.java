package team.y2k2.globa.notification.inquiry;

import com.google.gson.annotations.SerializedName;

public class NotificationInquiry {
    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("solved")
    private boolean solved;


    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSolved() {
        return solved;
    }
}
