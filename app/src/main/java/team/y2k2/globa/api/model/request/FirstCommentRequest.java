package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FirstCommentRequest {
    @SerializedName("startIdx")
    private final String startIdx;

    @SerializedName("endIdx")
    private final String endIdx;

    @SerializedName("content")
    private final String content;

    public FirstCommentRequest(String startIdx, String endIdx, String content) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getEndIdx() {
        return endIdx;
    }

    public String getStartIdx() {
        return startIdx;
    }
}
