package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Inquiry {

    @SerializedName("inquiryId")
    private String inquiryId;

    @SerializedName("title")
    private String title;


    public String getInquiryId() {
        return inquiryId;
    }

    public String getTitle() {
        return title;
    }
}
