package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Answer {

    @SerializedName("answer")
    private String answer;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("createdTime")
    private String createdTime;


    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getAnswer() {
        return answer;
    }
}
