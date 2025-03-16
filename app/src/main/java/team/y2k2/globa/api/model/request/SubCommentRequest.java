package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class SubCommentRequest {
    @SerializedName("content")
    private final String content;

    public SubCommentRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
