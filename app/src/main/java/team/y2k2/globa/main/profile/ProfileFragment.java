package team.y2k2.globa.main.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.FragmentProfileBinding;
import team.y2k2.globa.main.profile.info.MyInfoActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private ProfileModel model;

    // 프래그먼트의 현재 상태를 저장할 멤버 변수
    private String currentName;
    private String currentUserId;
    private String currentProfileImageUrl;
    private String currentUserCode;

    private ActivityResultLauncher<Intent> resultLauncher;
    private boolean cameBackFromEdit = false;
    private String pendingNewName = null;
    private String pendingNewProfileImageUrl = null;

    private Disposable userInfoDisposable;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        model = new ProfileModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                String newNameFromResult = result.getData().getStringExtra("newName");
                String newProfileFromResult = result.getData().getStringExtra("newProfile");

                if (newNameFromResult != null) {
                    this.pendingNewName = newNameFromResult;
                    this.cameBackFromEdit = true;
                }
                if (newProfileFromResult != null) {
                    this.pendingNewProfileImageUrl = newProfileFromResult;
                    this.cameBackFromEdit = true;
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems(), this);
        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
        binding.imageviewProfileAccountImage.setClipToOutline(true);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndDisplayUserData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userInfoDisposable != null && !userInfoDisposable.isDisposed()) {
            userInfoDisposable.dispose();
        }
        binding = null;
    }

    private void loadAndDisplayUserData() {
        if (!isAdded() || binding == null) return;

        if (cameBackFromEdit) {
            handleUserDataAfterEdit();
        } else {
            fetchUserDataFromServer();
        }
    }

    private void handleUserDataAfterEdit() {
        Log.d(TAG, "MyInfoActivity에서 복귀. Pending 데이터 적용.");
        SharedPreferences preferences = requireActivity().getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean persistToPrefs = false;

        if (pendingNewName != null) {
            this.currentName = pendingNewName;
            editor.putString("userName", this.currentName);
            persistToPrefs = true;
        } else {
            this.currentName = preferences.getString("userName", "");
        }

        if (pendingNewProfileImageUrl != null) {
            this.currentProfileImageUrl = pendingNewProfileImageUrl;
            editor.putString("profile", this.currentProfileImageUrl);
            persistToPrefs = true;
        } else {
            this.currentProfileImageUrl = preferences.getString("profile", null);
        }

        if (persistToPrefs) {
            editor.apply();
        }

        this.currentUserId = preferences.getString("userId", "");
        this.currentUserCode = preferences.getString("userCode", "");

        updateUI();

        cameBackFromEdit = false;
        pendingNewName = null;
        pendingNewProfileImageUrl = null;
    }

// ProfileFragment.java

    private void fetchUserDataFromServer() {
        if (!isAdded() || binding == null) return;
        Log.d(TAG, "서버에서 사용자 정보 로드 시작 (RxJava)");

        binding.progressbarProfileLoading.setVisibility(View.VISIBLE);
        binding.relativelayoutProfileAccountUser.setVisibility(View.INVISIBLE); // GONE 대신 INVISIBLE을 사용해 레이아웃이 깨지지 않게 합니다.
        // ▲▲▲▲▲ 수정된 부분 ▲▲▲▲▲

        if (userInfoDisposable != null && !userInfoDisposable.isDisposed()) {
            userInfoDisposable.dispose();
        }

        UserApiClient apiClient = new UserApiClient(getActivity());

        userInfoDisposable = Observable.fromCallable(() -> apiClient.requestUserInfo())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> { // 성공 시
                            if (!isAdded() || binding == null) return;

                            // ▼▼▼▼▼ 수정된 부분 ▼▼▼▼▼
                            // 2. 요청 완료 후: 로딩바를 숨기고 정보 영역을 다시 보여줍니다.
                            binding.progressbarProfileLoading.setVisibility(View.GONE);
                            binding.relativelayoutProfileAccountUser.setVisibility(View.VISIBLE);
                            // ▲▲▲▲▲ 수정된 부분 ▲▲▲▲▲

                            if (response != null) {
                                currentUserId = response.getUserId();
                                currentName = response.getName();
                                currentProfileImageUrl = response.getProfile();
                                currentUserCode = response.getCode();

                                userPreferences(response);
                                updateUI();
                            } else {
                                Log.e(TAG, "API 응답 body가 null입니다.");
                                loadInfoFromPrefsAsFallback();
                            }
                        },
                        error -> { // 실패 시
                            if (!isAdded() || binding == null) return;

                            binding.progressbarProfileLoading.setVisibility(View.GONE);
                            binding.relativelayoutProfileAccountUser.setVisibility(View.VISIBLE);

                            Log.e(TAG, "API 호출 실패", error);
                            Toast.makeText(getContext(), "정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            loadInfoFromPrefsAsFallback();
                        }
                );
    }

    private void updateUI() {
        if (!isAdded() || binding == null) return;

        binding.textviewProfileAccountUsername.setText(currentName);
        binding.textviewProfileAccountUserCode.setText(currentUserCode);
        loadProfileImageUI(currentProfileImageUrl);

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyInfoActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("current_name", currentName);
            intent.putExtra("current_profile_image_url", currentProfileImageUrl);
            intent.putExtra("current_user_code", currentUserCode);
            resultLauncher.launch(intent);
        });
    }

    private void loadInfoFromPrefsAsFallback() {
        if (!isAdded()) return;
        SharedPreferences prefs = requireActivity().getSharedPreferences("account", Activity.MODE_PRIVATE);
        currentName = prefs.getString("userName", "이름");
        currentUserId = prefs.getString("userId", "");
        currentProfileImageUrl = prefs.getString("profile", null);
        currentUserCode = prefs.getString("userCode", "");
        updateUI();
    }

    public void userPreferences(UserInfoResponse response) {
        if (getContext() == null || response == null || !isAdded()) {
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
    }

    private void loadProfileImageUI(String imageUrl) {
        if (!isAdded() || getContext() == null || binding == null) {
            return;
        }
        Context contextForGlide = requireContext();

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.startsWith("http")) {
                Glide.with(contextForGlide).load(imageUrl).into(binding.imageviewProfileAccountImage);
            } else {
                // StorageReference에서 다운로드 URL을 직접 받아와서 String으로 Glide에 전달
                StorageReference imageRef = storage.getReference().child(imageUrl);
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(contextForGlide)
                            .load(uri.toString())
                            .placeholder(R.drawable.profile_user)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase Storage URL 가져오기 실패", e);
                    Glide.with(contextForGlide).load(R.drawable.profile_user).into(binding.imageviewProfileAccountImage);
                });
            }
        } else {
            Glide.with(contextForGlide).load(R.drawable.profile_user).into(binding.imageviewProfileAccountImage);
        }
    }
    public String getUserId() {
        return currentUserId;
    }
}