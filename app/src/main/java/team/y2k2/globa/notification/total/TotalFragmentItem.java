package team.y2k2.globa.notification.total;

public class TotalFragmentItem {

    private String notificationId;
    private String profile;
    private String title;
    private String content;
    private String createdTime;
    private String folderId;
    private String shareId;
    private String inquiryId;
    private String type;
    private boolean isRead;

    public TotalFragmentItem(String notificationId, String profile, String title, String content, String createdTime, String folderId, String shareId, String inquiryId, String type, boolean isRead) {
        this.notificationId = notificationId;
        this.profile = profile;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.folderId = folderId;
        this.shareId = shareId;
        this.inquiryId = inquiryId;
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
    public String getFolderId() {
        return folderId;
    }
    public String getShareId() {
        return shareId;
    }
    public String getInquiryId() {
        return inquiryId;
    }
    public String getType() {
        return type;
    }
    public boolean isRead() {
        return isRead;
    }

}
