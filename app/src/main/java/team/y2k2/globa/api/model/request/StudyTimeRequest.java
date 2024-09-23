package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class StudyTimeRequest {

    @SerializedName("studyTime")
    private String studyTime;


    public StudyTimeRequest(String studyTime) {
        this.studyTime = studyTime;
    }

}
