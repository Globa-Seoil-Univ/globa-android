package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.entity.Section;

public class DocsDetailResponse {

    @SerializedName("recordId")
    private int recordId;

    @SerializedName("title")
    private String title;

    @SerializedName("path")
    private String path;

    @SerializedName("folder")
    private Folder folder;

    @SerializedName("section")
    private List<Section> sections;

    public int getRecordId() {
        return recordId;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public Folder getFolder() {
        return folder;
    }

    public List<Section> getSections() {
        return sections;
    }
}
