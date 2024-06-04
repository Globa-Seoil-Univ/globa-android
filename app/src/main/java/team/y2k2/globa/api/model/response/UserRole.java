package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UserRole {
    @SerializedName("shareId")
    private int shareId;

    @SerializedName("roleId")
    private String roleId;

    @SerializedName("user")
    private UserData user;

    public int getShareId() {
        return shareId;
    }
    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public UserData getUser() {
        return user;
    }
    public void setUser(UserData user) {
        this.user = user;
    }

}
