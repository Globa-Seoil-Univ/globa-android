package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class RecordCreateRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("path")
    private final String path;

    @SerializedName("lang")
    private final String lang;

    public RecordCreateRequest(String title, String path, String lang) {
        this.title = title;
        this.path = path;
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getLang() {
        return lang;
    }
}
