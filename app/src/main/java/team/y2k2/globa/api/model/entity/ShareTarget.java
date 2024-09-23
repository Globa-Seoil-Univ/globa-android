package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class ShareTarget {
    @SerializedName("code")
    String code;

    @SerializedName("role")
    String role;

    public ShareTarget(String code, String role) {
        this.code = code;
        this.role = role;
    }

    public String getCode() {
        return code;
    }
    public String getRole() {
        return role;
    }

}