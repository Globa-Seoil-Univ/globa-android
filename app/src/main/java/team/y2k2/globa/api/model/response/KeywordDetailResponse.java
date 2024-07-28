package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.KeywordDetail;

public class KeywordDetailResponse {

    @SerializedName("dictionary")
    private List<KeywordDetail> dictionary;

    public List<KeywordDetail> getDictionary() {
        return dictionary;
    }
}
