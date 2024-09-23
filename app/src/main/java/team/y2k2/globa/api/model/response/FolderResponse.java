package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.entity.User;

public class FolderResponse {

    @SerializedName("folders")
    private List<Folder> folders;
    @SerializedName("total")
    private int total;

    public List<Folder> getFolders() {
        return folders;
    }
    public int getTotal() {
        return total;
    }
}