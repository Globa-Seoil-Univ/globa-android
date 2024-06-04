package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import team.y2k2.globa.api.model.entity.Answer;

public class InquiryDetailResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("createdTime")
    private String createdTime;

    @SerializedName("answer")
    private Answer answer;

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getTitle() {
        return title;
    }

    public Answer getAnswer() {
        return answer;
    }
}
