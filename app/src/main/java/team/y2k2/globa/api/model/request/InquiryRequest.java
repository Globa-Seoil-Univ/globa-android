package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class InquiryRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("content")
    private final String content;

    public InquiryRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
