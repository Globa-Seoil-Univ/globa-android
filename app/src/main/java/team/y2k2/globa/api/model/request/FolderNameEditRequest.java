package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FolderNameEditRequest {

    @SerializedName("title")
    private String title;

    public FolderNameEditRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
