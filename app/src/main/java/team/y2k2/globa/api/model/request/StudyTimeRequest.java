package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class StudyTimeRequest {
    @SerializedName("studyTime")
    private final String studyTime;

    public StudyTimeRequest(String studyTime) {
        this.studyTime = studyTime;
    }

    public String getStudyTime() {
        return studyTime;
    }
}
