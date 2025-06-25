package team.y2k2.globa.main.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.TextUtils; // TextUtils import 추가
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList; // ArrayList import 확인

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.FragmentProfileBinding;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.profile.info.MyInfoActivity;

public class ProfileFragment extends Fragment {
    private ProfileModel model; // final 제거, 필요시 onCreate에서 Context와 함께 초기화
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private FragmentProfileBinding binding;
    private StorageReference profileImageRef;

    // 프래그먼트의 현재 상태를 저장할 멤버 변수
    private String currentName;
    private String currentUserId;
    private String currentProfileImageUrl;
    private String currentUserCode;

    private ActivityResultLauncher<Intent> resultLauncher;
    private boolean cameBackFromEdit = false;
    private String pendingNewName = null;
    private String pendingNewProfileImageUrl = null;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // ProfileModel은 Context가 필요 없는 현재 구조이므로 여기서 초기화
        model = new ProfileModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate 호출됨");

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d(TAG, "resultLauncher - ActivityResult: resultCode=" + result.getResultCode());
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                String newNameFromResult = result.getData().getStringExtra("newName");
                String newProfileFromResult = result.getData().getStringExtra("newProfile");
                Log.d(TAG, "resultLauncher - 수신 데이터: newName=" + newNameFromResult + ", newProfile=" + newProfileFromResult);

                if (newNameFromResult != null) {
                    this.pendingNewName = newNameFromResult;
                    this.cameBackFromEdit = true;
                }
                if (newProfileFromResult != null) {
                    this.pendingNewProfileImageUrl = newProfileFromResult;
                    this.cameBackFromEdit = true;
                }
                // onResume()에서 loadAndDisplayUserData()가 호출되어 변경사항이 적용될 것임
            } else {
                Log.w(TAG, "resultLauncher - Result not OK or data is null.");
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView 호출됨. cameBackFromEdit 플래그 (View 생성 시점): " + cameBackFromEdit);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // ProfileModel.getItems()는 Context를 받지 않으므로, requireContext() 제거
        // ProfileModel의 getItems()가 Context를 요구한다면 ProfileModel 자체를 수정하거나,
        // getItems() 시그니처를 변경해야 하지만, 현재 제공된 ProfileModel은 그렇지 않음.
        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems(), this);
        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyInfoActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("current_name", currentName); // 현재 이름을 전달
            // 필요하다면 현재 프로필 이미지 URL도 전달
            // intent.putExtra("current_profile_image_url", currentProfileImageUrl);
            if (resultLauncher != null) {
                resultLauncher.launch(intent);
            }
        });

        binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
        binding.imageviewProfileAccountImage.setClipToOutline(true);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume 호출됨. cameBackFromEdit 플래그: " + cameBackFromEdit);
        // 화면이 사용자에게 다시 보여질 때 데이터를 로드하거나 결과값을 반영
        loadAndDisplayUserData();
    }

    private void loadAndDisplayUserData() {
        if (getContext() == null || !isAdded() || binding == null) {
            Log.w(TAG, "loadAndDisplayUserData: Fragment not ready or binding is null.");
            return;
        }
        SharedPreferences preferences = requireActivity().getSharedPreferences("account", Activity.MODE_PRIVATE);

        if (cameBackFromEdit) {
            Log.d(TAG, "loadAndDisplayUserData: MyInfoActivity에서 복귀. Pending 데이터 적용.");
            SharedPreferences.Editor editor = preferences.edit();
            boolean persistToPrefs = false;

            if (pendingNewName != null) {
                this.currentName = pendingNewName;
                editor.putString("userName", this.currentName);
                persistToPrefs = true;
            } else { // pendingNewName이 없으면 기존 SharedPreferences 값을 유지하거나, 프래그먼트 멤버 변수 값 사용
                this.currentName = preferences.getString("userName", this.currentName != null ? this.currentName : "");
            }

            if (pendingNewProfileImageUrl != null) {
                this.currentProfileImageUrl = pendingNewProfileImageUrl;
                editor.putString("profile", this.currentProfileImageUrl);
                persistToPrefs = true;
            } else {
                this.currentProfileImageUrl = preferences.getString("profile", this.currentProfileImageUrl);
            }

            if (persistToPrefs) {
                editor.apply();
                Log.d(TAG, "loadAndDisplayUserData: Pending 데이터 SharedPreferences에 저장됨.");
            }

            // 다른 정보는 현재 SharedPreferences 또는 멤버 변수에서 로드
            this.currentUserId = preferences.getString("userId", this.currentUserId != null ? this.currentUserId : "");
            this.currentUserCode = preferences.getString("userCode", this.currentUserCode != null ? this.currentUserCode : "");

            // UI 업데이트
            binding.textviewProfileAccountUsername.setText(this.currentName);
            binding.textviewProfileAccountUserCode.setText(this.currentUserCode);
            loadProfileImageUI(this.currentProfileImageUrl);

            Log.d(TAG, "loadAndDisplayUserData: UI 업데이트 완료 (Pending). 이름: " + this.currentName);

            // 플래그 초기화
            cameBackFromEdit = false;
            pendingNewName = null;
            pendingNewProfileImageUrl = null;

        } else {
            Log.d(TAG, "loadAndDisplayUserData: 초기 로드 또는 일반 재개. API 호출.");
            UserApiClient apiClient = new UserApiClient();
            UserInfoResponse response = apiClient.requestUserInfo(); // 동기 호출
            if (response != null) {
                Log.d(TAG, "loadAndDisplayUserData: API 응답. 이름: " + response.getName());
                this.currentUserId = response.getUserId();
                this.currentName = response.getName();
                this.currentProfileImageUrl = response.getProfile();
                this.currentUserCode = response.getCode();

                userPreferences(response); // API 응답으로 SharedPreferences 업데이트
                showLogMessages(response);

                binding.textviewProfileAccountUsername.setText(this.currentName);
                binding.textviewProfileAccountUserCode.setText(this.currentUserCode);
                loadProfileImageUI(this.currentProfileImageUrl);
                Log.d(TAG, "loadAndDisplayUserData: UI 업데이트 완료 (API). 이름: " + this.currentName);
            } else {
                Log.e(TAG, "loadAndDisplayUserData: API 응답 null. SharedPreferences에서 폴백 로드.");
                loadInfoFromPrefsAsFallback();
            }
        }
    }

    private void loadInfoFromPrefsAsFallback() {
        if (getContext() == null || !isAdded() || binding == null) {
            Log.w(TAG, "loadInfoFromPrefsAsFallback: Fragment not ready or binding is null.");
            return;
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("account", Activity.MODE_PRIVATE);
        // getString의 두 번째 인자는 기본값입니다. R.string.default_username_placeholder 등을 사용하는 것이 좋습니다.
        this.currentName = prefs.getString("userName", "이름");
        this.currentUserId = prefs.getString("userId", "");
        this.currentProfileImageUrl = prefs.getString("profile", null);
        this.currentUserCode = prefs.getString("userCode", "");

        binding.textviewProfileAccountUsername.setText(this.currentName);
        binding.textviewProfileAccountUserCode.setText(this.currentUserCode);
        loadProfileImageUI(this.currentProfileImageUrl);
        Log.d(TAG, "loadInfoFromPrefsAsFallback: UI 업데이트됨. 이름: " + this.currentName);
    }

    public void userPreferences(UserInfoResponse response) {
        if (getContext() == null || response == null || !isAdded()) {
            Log.e(TAG, "userPreferences: Context, UserInfoResponse가 null이거나 Fragment가 not attached.");
            return;
        }
        SharedPreferences preferences = requireContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", response.getName());
        editor.putString("userId", response.getUserId());
        editor.putString("publicFolderId", response.getPublicFolderId());
        editor.putString("userCode", response.getCode());
        if (response.getProfile() != null && !response.getProfile().isEmpty()) {
            editor.putString("profile", response.getProfile());
        } else {
            editor.remove("profile");
        }
        editor.apply();
        Log.d(TAG, "userPreferences: SharedPreferences 업데이트. 이름: " + response.getName() + ", 프로필: " + response.getProfile());
    }

    public void showLogMessages(UserInfoResponse response) {
        if (response == null) {
            Log.e(TAG, "showLogMessages: UserInfoResponse가 null입니다.");
            return;
        }
        Log.d(getClass().getName(), "======= 프로필 정보 로드/갱신 성공 =======");
        ArrayList<String> logs = new ArrayList<>();
        logs.add("userName      :" + response.getName());
        logs.add("userId        :" + response.getUserId());
        logs.add("publicFolderId:" + response.getPublicFolderId());
        logs.add("userCode      :" + response.getCode());
        logs.add("profileUrl    :" + response.getProfile());

        for (String logMsg : logs) {
            Log.d(getClass().getName(), logMsg);
        }
        Log.d(getClass().getName(), "======================================");
    }

    public String getUserId() {
        return currentUserId;
    }

    private void loadProfileImageUI(String imageUrl) {
        if (!isAdded() || getContext() == null || binding == null) {
            Log.w(TAG, "loadProfileImageUI: Fragment not ready or binding is null.");
            return;
        }
        Context contextForGlide = requireContext();
        Log.d(TAG, "loadProfileImageUI: 이미지 로드 시도: " + imageUrl);

        if (!TextUtils.isEmpty(imageUrl)) { // TextUtils.isEmpty로 null 또는 빈 문자열 체크
            if (imageUrl.startsWith("http")) {
                Glide.with(contextForGlide)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_user)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewProfileAccountImage);
            } else {
                try {
                    StorageReference imageRef = storage.getReference().child(imageUrl);
                    Glide.with(contextForGlide)
                            .load(ProfileImage.convertGsToHttps(imageRef.toString()))
                            .placeholder(R.drawable.profile_user)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "loadProfileImageUI: 잘못된 Firebase Storage 경로: " + imageUrl, e);
                    Glide.with(contextForGlide).load(R.drawable.profile_user).into(binding.imageviewProfileAccountImage);
                }
            }
        } else {
            Log.d(TAG, "loadProfileImageUI: imageUrl이 null이거나 비어있어 기본 이미지 로드.");
            Glide.with(contextForGlide).load(R.drawable.profile_user).into(binding.imageviewProfileAccountImage);
        }
    }
}