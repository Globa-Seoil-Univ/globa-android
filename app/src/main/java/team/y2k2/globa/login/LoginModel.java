package team.y2k2.globa.login;

import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.user.model.User;

public class LoginModel {
    /**
     * SNS 로그인 방식
     */
    public static final int RC_KAKAO = 1001;
    public static final int RC_NAVER = 1002;
    public static final int RC_TWITTER = 1003;
    public static final int RC_GOOGLE = 1004;

    /**
     * SNS 계정에 등록된 사용자 개인정보
     */
    private final String uid;
    private final String name;
    private final String profileImageUrl;
    private final String snsKind;
    String token;

    public LoginModel(FirebaseUser user, int snsKind, String token) {
        uid = user.getUid();
        name = user.getDisplayName();
        profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
        if(snsKind == 1004)
            this.snsKind = "GOOGLE";
        else
            this.snsKind = "KAKAO";
        this.token = token;
    }

    public LoginModel(User user, int snsKind) {
        uid = user.getId().toString();
        name = user.getKakaoAccount().getProfile().getNickname();
        profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
        if(snsKind == 1004)
            this.snsKind = "GOOGLE";
        else
            this.snsKind = "KAKAO";
    }

    public String getSnsKind() {
        return snsKind;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}