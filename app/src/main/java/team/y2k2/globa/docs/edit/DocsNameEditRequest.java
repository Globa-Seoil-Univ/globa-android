package team.y2k2.globa.docs.edit;

import com.google.gson.annotations.SerializedName;

public class DocsNameEditRequest {
    @SerializedName("title")
    private String title;

    public DocsNameEditRequest(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
