package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class SubCommentRequest {

    @SerializedName("content")
    private String content;

    public SubCommentRequest(String content) {
        this.content = content;
    }

}
