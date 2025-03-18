package team.y2k2.globa.notification.inquiry;

import com.google.gson.annotations.SerializedName;

public class InquiryItem {

    @SerializedName("title")
    private final String title;

    @SerializedName("content")
    private final String content;

    @SerializedName("solved")
    private final boolean solved;

    @SerializedName("inquiryId")
    private final String inquiryId;

    @SerializedName("createdTime")
    private final String createdTime;

    public InquiryItem(String inquiryId, String title, String content, String createdTime,boolean solved) {
        this.inquiryId = inquiryId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
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

    public String getInquiryId() {
        return inquiryId;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}