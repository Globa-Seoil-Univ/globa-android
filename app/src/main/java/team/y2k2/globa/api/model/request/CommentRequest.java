package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class CommentRequest {

    @SerializedName("content")
    private final String content;

    public CommentRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
