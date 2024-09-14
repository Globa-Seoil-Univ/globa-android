package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class StudyTimeRequest {

    @SerializedName("studyTime")
    private String studyTime;
    @SerializedName("createdTime")
    private String createdTime;

    public StudyTimeRequest(String studyTime, String createdTime) {
        this.studyTime = studyTime;
        this.createdTime = createdTime;
    }

}
