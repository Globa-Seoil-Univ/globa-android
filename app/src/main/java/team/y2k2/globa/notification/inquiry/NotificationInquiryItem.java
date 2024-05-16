package team.y2k2.globa.notification.inquiry;

import com.google.gson.annotations.SerializedName;

public class NotificationInquiryItem {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("solved")
    private boolean solved;

    public NotificationInquiryItem(String title, String content, boolean solved) {
        this.title = title;
        this.content = content;
        this.solved = solved;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isSolved() {
        return solved;
    }
}
