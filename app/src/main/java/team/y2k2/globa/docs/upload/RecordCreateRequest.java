package team.y2k2.globa.docs.upload;

import com.google.gson.annotations.SerializedName;

public class RecordCreateRequest {

    @SerializedName("title")
    String title;

    @SerializedName("path")
    String path;

    @SerializedName("size")
    String size;

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
