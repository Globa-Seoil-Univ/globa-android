package team.y2k2.globa.api.model.response;

public class UserSearchResponse {

    private int userId;
    private String profile;
    private String name;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userid) {
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
}
