package team.y2k2.globa.notification.inquiry;

public class InquiryFragmentItem {

    private String notificationId;
    private String inquiryId;
    private String title;
    private String content;
    private String createdTime;
    private boolean isRead;

    public InquiryFragmentItem(String notificationId, String inquiryId, String title, String content, String createdTime, boolean isRead) {
        this.notificationId = notificationId;
        this.inquiryId = inquiryId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.isRead = isRead;
    }

    public String getNotificationId() {
        return notificationId;
    }
    public String getInquiryId() {
        return inquiryId;
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
