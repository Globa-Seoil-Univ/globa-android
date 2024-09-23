package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("notificationId")
    private String notificationId;
    @SerializedName("type")
    private String type;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("isRead")
    private boolean isRead;
    @SerializedName("notice")
    private Notice notice;
    @SerializedName("user")
    private User user;
    @SerializedName("share")
    private Share share;
    @SerializedName("folder")
    private Folder folder;
    @SerializedName("record")
    private Record record;
    @SerializedName("comment")
    private Comment comment;
    @SerializedName("inquiry")
    private Inquiry inquiry;

    public String getCreatedTime() {
        return createdTime;
    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public Folder getFolder() {
        return folder;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public Notice getNotice() {
        return notice;
    }

    public Record getRecord() {
        return record;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public Share getShare() {
        return share;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }
}