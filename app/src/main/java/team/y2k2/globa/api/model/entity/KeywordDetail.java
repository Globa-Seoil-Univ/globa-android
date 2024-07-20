package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class KeywordDetail {

    @SerializedName("word")
    private String word;
    @SerializedName("engword")
    private String engword;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("pronunciation")
    private String pronunciation;

    public String getWord() {
        return word;
    }
    public String getEngword() {
        return engword;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }
    public String getPronunciation() {
        return pronunciation;
    }
}
