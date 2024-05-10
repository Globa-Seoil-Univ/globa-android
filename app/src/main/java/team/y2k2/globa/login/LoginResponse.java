package team.y2k2.globa.login;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    // Getter 및 Setter 메서드 등 추가 가능


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
