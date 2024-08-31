package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.ShareTarget;

public class FolderDeleteRequest {
    @SerializedName("folderId")
    String folderId;

    @SerializedName("shareTarget")
    List<ShareTarget> shareTarget;

    public FolderDeleteRequest(int folderId){
        this.folderId = String.valueOf(folderId);
    }

    public String getFolderId() {
        return folderId;
    }
}


