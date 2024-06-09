package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class FolderPermissionChangeRequest {

    @SerializedName("role")
    private String role;

    public FolderPermissionChangeRequest(String role) {
        this.role = role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
