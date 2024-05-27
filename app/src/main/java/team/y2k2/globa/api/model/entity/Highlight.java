package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Highlight {
    @SerializedName("highlightId")
    private int highlightId;

    @SerializedName("type")
    private String type;

    @SerializedName("startIndex")
    private int startIndex;

    @SerializedName("endIndex")
    private int endIndex;

    public int getHighlightId() {
        return highlightId;
    }

    public String getType() {
        return type;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}