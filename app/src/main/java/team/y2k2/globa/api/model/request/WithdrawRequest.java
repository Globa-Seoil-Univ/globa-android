package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class WithdrawRequest {
    @SerializedName("surveyType")
    private int[] surveyType;
    @SerializedName("content")
    private String content;

    public WithdrawRequest(int[] surveyType, String content) {
        this.surveyType = surveyType;
        this.content = content;
    }

    public int[] getSurveyType() {
        return surveyType;
    }
    public void setSurveyType(int[] surveyType) {
        this.surveyType = surveyType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
