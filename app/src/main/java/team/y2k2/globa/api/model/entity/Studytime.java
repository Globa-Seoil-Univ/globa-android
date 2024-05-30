package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Studytime {

    @SerializedName("studyTime")
    private int studyTime;
    @SerializedName("createdTime")
    private String createdTime;

    public int getStudyTime() {
        return studyTime;
    }
    public void setStudyTime(int studyTime) {
        this.studyTime = studyTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
