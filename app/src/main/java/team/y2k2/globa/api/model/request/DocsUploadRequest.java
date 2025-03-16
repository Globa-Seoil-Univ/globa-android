package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class DocsUploadRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("path")
    private final String path;

    public DocsUploadRequest(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}
