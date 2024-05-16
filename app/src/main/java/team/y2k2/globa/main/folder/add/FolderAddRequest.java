package team.y2k2.globa.main.folder.add;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

class ShareTarget {
    @SerializedName("code")
    String code;

    @SerializedName("role")
    String role;

    ShareTarget(String code, String role) {
        this.code = code;
        this.role = role;
    }
}
