package team.y2k2.globa.main.profile.inquiry;

import com.google.gson.annotations.SerializedName;

public class InquiryRequest {
    @SerializedName("title")
    String title;

    @SerializedName("content")
    String content;

    public InquiryRequest(String title, String content){
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
