package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class NotificationInquiry {
    @SerializedName("inquiryId")
    private String inquiryId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("solved")
    private boolean solved;

    @SerializedName("createdTime")
    private String createdTime;

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public boolean isSolved() {
        return solved;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}
