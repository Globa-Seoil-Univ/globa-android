package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Section {
    @SerializedName("sectionId")
    private int sectionId;

    @SerializedName("title")
    private String title;

    @SerializedName("startTime")
    private int startTime;

    @SerializedName("analyses")
    private Analysis analyses;

    @SerializedName("summaries")
    private List<Summary> summaries;

    public int getSectionId() {
        return sectionId;
    }

    public String getTitle() {
        return title;
    }

    public int getStartTime() {
        return startTime;
    }

    public Analysis getAnalyses() {
        return analyses;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }
}
