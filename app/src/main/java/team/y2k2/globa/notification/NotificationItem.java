package team.y2k2.globa.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("solved")
    private boolean solved;

    public NotificationItem(String title, String content, boolean solved) {
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
