package team.y2k2.globa.main.folder.inside;

import com.google.gson.annotations.SerializedName;

public class FolderInsideRecord {

    @SerializedName("recordId")
    private int recordId;

    @SerializedName("title")
    private String title;

    @SerializedName("path")
    private String path;

    @SerializedName("createdTime")
    private String createdTime;


    public String getTitle() {
        return title;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getPath() {
        return path;
    }

    public int getRecordId() {
        return recordId;
    }
}
