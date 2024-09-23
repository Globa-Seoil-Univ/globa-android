package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class SubComment {

    @SerializedName("commentId")
    private String commentId;

    @SerializedName("content")
    private String content;

    @SerializedName("deleted")
    private boolean deleted;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("user")
    private User user;


    public String getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public User getUser() {
        return user;
    }

    public boolean isDeleted() {
        return deleted;
    }

}
