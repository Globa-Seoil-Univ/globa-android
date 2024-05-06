package team.y2k2.globa.main.folder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class Folder {
    @SerializedName("folderId")
    private int folderId;

    @SerializedName("title")
    private String title;

    @SerializedName("isShared")
    private boolean isShared;

    @SerializedName("createdTime")
    private String createdTime;


    public String getTitle() {
        return title;
    }

    public int getFolderId() {
        return folderId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public boolean isShared() {
        return isShared;
    }
}

public class FolderResponse {
    private List<Folder> folders;
    private int total;

    public int getTotal() {
        return total;
    }

    public List<Folder> getFolders() {
        return folders;
    }
}