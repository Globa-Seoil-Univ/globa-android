package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class NicknameEditRequest {
    @SerializedName("name")
    String name;


    public NicknameEditRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
