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

    public static final String APP_KEY_KAKAO = "8a572ba277b6059bd51f23fe58474f13";

    /**
     * SNS 계정에 등록된 사용자 개인정보
     */
    String uid;
    String name;
    String profileImageUrl;
    int snsKind;

    public LoginModel(FirebaseUser user, int snsKind) {
        uid = user.getUid();
        name = user.getDisplayName();
        profileImageUrl = user.getPhotoUrl().toString();
        this.snsKind = snsKind;
    }

    public LoginModel(User user, int snsKind) {
        uid = user.getId().toString();
        name = user.getKakaoAccount().getProfile().getNickname();
        profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
        this.snsKind = snsKind;
    }

    public int getSnsKind() {
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
