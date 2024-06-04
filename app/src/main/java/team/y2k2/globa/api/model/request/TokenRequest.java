package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("refreshToken")
    private String refreshToken;

    public TokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
