package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FolderPermissionResponse {

    @SerializedName("users")
    private List<UserRole> users;

    @SerializedName("total")
    private int total;

    public List<UserRole> getUsers() {
        return users;
    }

    public void setUsers(List<UserRole> users) {
        this.users = users;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
