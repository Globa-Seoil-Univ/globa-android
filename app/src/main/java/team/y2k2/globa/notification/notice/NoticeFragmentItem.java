package team.y2k2.globa.notification.notice;

public class NoticeFragmentItem {

    private String notificationId;;
    private String profile;
    private String title;
    private String content;
    private String createdTime;
    private boolean isRead;

    public NoticeFragmentItem(String notificationId, String profile, String title, String content, String createdTime, boolean isRead) {
        this.notificationId = notificationId;
        this.profile = profile;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.isRead = isRead;
    }

    public String getNotificationId() {
        return notificationId;
    }
    public String getProfile() {
        return profile;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getCreatedTime() {
        return createdTime;
    }
    public boolean isRead() {
        return isRead;
    }
}
