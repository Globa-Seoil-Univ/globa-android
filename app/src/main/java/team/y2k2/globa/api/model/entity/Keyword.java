package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Keyword {
    @SerializedName("word")
    private String word;
    @SerializedName("importance")
    private float importance;

    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

    public float getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
