package team.y2k2.globa.main.profile;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {
    @SerializedName("profile")
    private String profile;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("publicFolderId")
    private String publicFolderId;

    // 생성자, Getter 및 Setter 메서드 등은 필요에 따라 추가할 수 있습니다.

    // Getter 및 Setter 메서드 예시:
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPublicFolderId() {
        return publicFolderId;
    }

    public void setPublicFolderId(String publicFolderId) {
        this.publicFolderId = publicFolderId;
    }
}
