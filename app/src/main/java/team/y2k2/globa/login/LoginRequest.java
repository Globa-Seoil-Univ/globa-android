package team.y2k2.globa.login;

public class LoginRequest {

    private String snsKind;
    private String snsId;
    private String name;
    private String profile;
    private boolean notification;

    public LoginRequest(String snsKind, String snsId, String name, String profile, boolean notification) {
        this.snsKind = snsKind;
        this.snsId = snsId;
        this.name = name;
        this.profile = profile;
        this.notification = notification;
    }

    public String getProfile() {
        return profile;
    }

    public String getSnsKind() {
        return snsKind;
    }

    public String getSnsId() {
        return snsId;
    }

    public String getName() {
        return name;
    }

    public boolean isNotification() {
        return notification;
    }
}
