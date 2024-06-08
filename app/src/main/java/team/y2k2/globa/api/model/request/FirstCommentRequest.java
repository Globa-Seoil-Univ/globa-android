package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FirstCommentRequest {
    @SerializedName("startIdx")
    String startIdx;

    @SerializedName("endIdx")
    String endIdx;

    @SerializedName("content")
    String content;

    public FirstCommentRequest(String startIdx, String endIdx, String content) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.content = content;
    }


}
