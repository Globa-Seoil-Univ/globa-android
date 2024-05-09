package team.y2k2.globa.main.folder;

import com.google.gson.annotations.SerializedName;

class User {
    @SerializedName("userId")
    private int userId;

    @SerializedName("snsKind")
    private String snsKind;

    @SerializedName("snsId")
    private String snsId;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    // 다른 필드들도 필요에 따라 추가합니다.

    // Getter 메서드도 필요에 따라 추가합니다.
}

public class FolderResponse {
    @SerializedName("folderId")
    private int folderId;

    @SerializedName("user")
    private User user;

    @SerializedName("title")
    private String title;

    @SerializedName("createdTime")
    private String createdTime;



    // Getter 메서드도 필요에 따라 추가합니다.


    public String getCreatedTime() {
        return createdTime;
    }

    public String getTitle() {
        return title;
    }

    public int getFolderId() {
        return folderId;
    }

    public User getUser() {
        return user;
    }
}