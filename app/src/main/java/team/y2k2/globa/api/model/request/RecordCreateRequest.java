package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class RecordCreateRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("path")
    private final String path;

    @SerializedName("size")
    private final String size;

    public RecordCreateRequest(String title, String path, String size) {
        this.title = title;
        this.path = path;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getSize() {
        return size;
    }
}
