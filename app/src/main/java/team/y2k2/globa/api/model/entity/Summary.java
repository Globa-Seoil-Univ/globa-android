package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Summary {
    @SerializedName("content")
    private String content;

    public String getContent() {
        return content;
    }

}