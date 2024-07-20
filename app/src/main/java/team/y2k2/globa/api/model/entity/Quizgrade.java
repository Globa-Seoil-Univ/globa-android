package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Quizgrade {

    @SerializedName("quizGrade")
    private double score;
    @SerializedName("createdTime")
    private String createdTime;

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
