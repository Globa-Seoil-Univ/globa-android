package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {

    @SerializedName("requestToken")
    private String refreshToken;

    public TokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
