package team.y2k2.globa.notification.total;

public class TotalFragmentItem {

    private final String notificationId;
    private final String profile;
    private final String title;
    private final String content;
    private final String createdTime;
    private final String folderId;
    private final String shareId;
    private final String inquiryId;
    private final String recordId;
    private final String type;
    private final boolean isRead;

    public TotalFragmentItem(String notificationId, String profile, String title, String content, String createdTime, String folderId, String shareId, String inquiryId, String recordId, String type, boolean isRead) {
        this.notificationId = notificationId;
        this.profile = profile;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.folderId = folderId;
        this.shareId = shareId;
        this.inquiryId = inquiryId;
        this.recordId = recordId;
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
    public String getRecordId() {
        return recordId;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

}

