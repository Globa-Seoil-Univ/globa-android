package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("commentId")
    private String commentId;

    @SerializedName("content")
    private String content;

    public String getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
