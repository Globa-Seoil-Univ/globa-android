package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Analysis {

    @SerializedName("analysisId")
    private int analysisId;

    @SerializedName("content")
    private String content;

    @SerializedName("highlights")
    private List<Highlight> highlights;

    public int getAnalysisId() {
        return analysisId;
    }

    public String getContent() {
        return content;
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }
}