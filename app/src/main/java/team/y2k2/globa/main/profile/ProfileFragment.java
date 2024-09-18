package team.y2k2.globa.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.FragmentProfileBinding;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.profile.edit.NicknameEditViewModel;
import team.y2k2.globa.main.profile.info.MyinfoActivity;

public class ProfileFragment extends Fragment {
    ProfileModel model;
    FragmentProfileBinding binding;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    private String userId;

    String profile;
    String myName;

    private ActivityResultLauncher<Intent> activityProfileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data != null) {
                        String newName = data.getStringExtra("newName");
                        String newProfile = data.getStringExtra("newProfile");
                        Uri uri = Uri.parse(newProfile);
                        refreshData(newName, uri);
                    }
                }
            }
    );

    public ProfileFragment() {
        model = new ProfileModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems(), this);

        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        ApiClient apiClient = new ApiClient(getContext());

        UserInfoResponse response = apiClient.requestUserInfo();
        // userInfo를 사용하여 필요한 작업 수행
        //Log.d("IMAGETEST", response.getProfile());
        Log.d("IMAGETEST", response.getName());

        userId = response.getUserId();

        userPreferences(response);
        showLogMessages(response);

        myName = response.getName();

        binding.textviewProfileAccountUsername.setText(myName);
        binding.textviewProfileAccountUsercode.setText(response.getCode());

        profile = response.getProfile();

        if(profile != null) {
            if(profile.startsWith("http")) {
                Glide.with(inflater.getContext())
                        .load(profile)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewProfileAccountImage);
            } else {
                profileImageRef = storage.getReference().child(profile);
                Glide.with(inflater.getContext())
                        .load(ProfileImage.convertGsToHttps(profileImageRef.toString()))
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewProfileAccountImage);
            }
        } else {
            Glide.with(inflater.getContext())
                    .load(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewProfileAccountImage);
        }

        binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
        binding.imageviewProfileAccountImage.setClipToOutline(true);

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {

            Intent intent = new Intent(binding.getRoot().getContext(), MyinfoActivity.class);
            activityProfileResultLauncher.launch(intent);

        });

        return binding.getRoot();
    }

    public void userPreferences(UserInfoResponse response) {
        SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", response.getName());
        editor.putString("userId", response.getUserId());
        editor.putString("publicFolderId", response.getPublicFolderId());
        editor.putString("userCode", response.getCode());
        editor.commit();
    }

    public void showLogMessages(UserInfoResponse response) {
        Log.d(getClass().getName(), "프로필 조회 성공");
        ArrayList<String> logs = new ArrayList();
        logs.add("userName      :" + response.getName());
        logs.add("userId        :" + response.getUserId());
        logs.add("publicFolderId:" + response.getPublicFolderId());
        logs.add("userCode      :" + response.getCode());

        for(int i = 0; i < logs.toArray().length; i++) {
            Log.d(getClass().getName(), logs.get(i));
        }

    }

    public String getUserId() {
        return userId;
    }

    private void refreshData(String newName, Uri uri) {
        Log.d(getClass().getSimpleName(), "refreshData() 실행 newName: " + newName);
        Log.d(getClass().getSimpleName(), "refreshData() 실행 newProfile: " + uri);
        if(newName != null && uri != null) {
            binding.textviewProfileAccountUsername.setText(newName);
            Glide.with(requireActivity()).load(uri)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewProfileAccountImage);
        } else if(newName != null) {
            binding.textviewProfileAccountUsername.setText(newName);
            if(profile != null) {
                if(profile.startsWith("http")) {
                    Glide.with(requireActivity())
                            .load(profile)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                } else {
                    profileImageRef = storage.getReference().child(profile);
                    Glide.with(requireActivity())
                            .load(ProfileImage.convertGsToHttps(profileImageRef.toString()))
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                }
            } else {
                Glide.with(requireActivity())
                        .load(R.drawable.profile_user)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewProfileAccountImage);
            }
        } else if(uri != null) {
            binding.textviewProfileAccountUsername.setText(myName);
            Glide.with(requireActivity()).load(uri)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewProfileAccountImage);
        } else {
            binding.textviewProfileAccountUsername.setText(myName);
            if(profile != null) {
                if(profile.startsWith("http")) {
                    Glide.with(requireActivity())
                            .load(profile)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                } else {
                    profileImageRef = storage.getReference().child(profile);
                    Glide.with(requireActivity())
                            .load(ProfileImage.convertGsToHttps(profileImageRef.toString()))
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewProfileAccountImage);
                }
            } else {
                Glide.with(requireActivity())
                        .load(R.drawable.profile_user)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewProfileAccountImage);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
