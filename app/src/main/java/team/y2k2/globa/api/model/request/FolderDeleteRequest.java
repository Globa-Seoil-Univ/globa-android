package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.ShareTarget;

public class FolderDeleteRequest {
    @SerializedName("folderId")
    private final String folderId;

    @SerializedName("shareTarget")
    private List<ShareTarget> shareTarget;

    public FolderDeleteRequest(int folderId){
        this.folderId = String.valueOf(folderId);
    }

    public String getFolderId() {
        return folderId;
    }
}


