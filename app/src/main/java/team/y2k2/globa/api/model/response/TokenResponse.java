package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("grantType")
    private String grantType;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
