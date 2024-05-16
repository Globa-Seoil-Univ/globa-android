package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.ShareTarget;

public class FolderAddRequest {
    @SerializedName("title")
    String title;

    @SerializedName("shareTarget")
    List<ShareTarget> shareTarget;

    public FolderAddRequest(String title, List<ShareTarget> shareTarget){
        this.title = title;
        this.shareTarget = shareTarget;
    }

    public FolderAddRequest(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<ShareTarget> getShareTarget() {
        return shareTarget;
    }
}


