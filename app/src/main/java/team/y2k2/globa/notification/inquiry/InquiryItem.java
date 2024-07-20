package team.y2k2.globa.notification.inquiry;

import com.google.gson.annotations.SerializedName;

public class InquiryItem {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("solved")
    private boolean solved;

    @SerializedName("inquiryId")
    private String inquiryId;

    @SerializedName("createdTime")
    private String createdTime;

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