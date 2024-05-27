package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Keyword {
    @SerializedName("word")
    String word;

    @SerializedName("importance")
    float importance;

    public String getWord() {
        return word;
    }

    public float getImportance() {
        return importance;
    }
}
