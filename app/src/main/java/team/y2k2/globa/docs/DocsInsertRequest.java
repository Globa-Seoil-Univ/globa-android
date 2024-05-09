package team.y2k2.globa.docs;

import com.google.gson.annotations.SerializedName;

public class DocsInsertRequest {

    @SerializedName("title")
    String title;

    @SerializedName("path")
    String path;

    @SerializedName("size")
    String size;

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
