package team.y2k2.globa.login;

public class LoginRequest {

    private int snsKind;
    private String snsId;
    private String name;
    private String profile;
    private boolean notification;

    public LoginRequest(int snsKind, String snsId, String name, String profile, boolean notification) {
        this.snsKind = snsKind;
        this.snsId = snsId;
        this.name = name;
        this.profile = profile;
        this.notification = notification;
    }

    public String getProfile() {
        return profile;
    }

    public int getSnsKind() {
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
