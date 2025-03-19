package team.y2k2.globa.notification.docs;

public class DocsFragmentItem {

    private final String notificationId;
    private final String profile;
    private final String title;
    private final String content;
    private final String createdTime;
    private final String type;
    private final boolean isRead;

    public DocsFragmentItem(String notificationId, String profile, String title, String content, String createdTime, String type, boolean isRead) {
        this.notificationId = notificationId;
        this.profile = profile;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }
}
