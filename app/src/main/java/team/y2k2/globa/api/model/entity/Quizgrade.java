package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Quizgrade {

    @SerializedName("score")
    private int score;
    @SerializedName("createdTime")
    private String createdTime;

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
