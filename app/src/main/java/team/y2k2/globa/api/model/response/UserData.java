package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("userId")
    private int userId;

    @SerializedName("profile")
    private String profile;

    @SerializedName("name")
    private String name;

    @SerializedName("invitationStatus")
    private String invitationStatus;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

}
