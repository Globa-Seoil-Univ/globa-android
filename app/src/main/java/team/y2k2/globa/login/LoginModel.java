package team.y2k2.globa.login;

public class LoginModel {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    private String snsKind;
    private String snsId;
    private String name;
    private String profile;
    private boolean notification;



    public LoginModel(String snsKind, String snsId, String name, String profile, boolean notification) {
        this.snsKind = snsKind;
        this.snsId = snsId;
        this.name = name;
        this.profile = profile;
        this.notification = notification;
    }
}
