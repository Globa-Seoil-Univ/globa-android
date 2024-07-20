package team.y2k2.globa.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
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
import team.y2k2.globa.main.profile.info.MyinfoActivity;

public class ProfileFragment extends Fragment {
    ProfileModel model;
    FragmentProfileBinding binding;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    public ProfileFragment() {
        model = new ProfileModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems());

        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        ApiClient apiClient = new ApiClient(getContext());

        UserInfoResponse response = apiClient.requestUserInfo();
        // userInfo를 사용하여 필요한 작업 수행
        //Log.d("IMAGETEST", response.getProfile());
        Log.d("IMAGETEST", response.getName());

        userPreferences(response);
        showLogMessages(response);

        binding.textviewProfileAccountUsername.setText(response.getName());
        binding.textviewProfileAccountUsercode.setText(response.getCode());

        String profile = response.getProfile();

        String httpProfileUrl;
        if(response.getProfile() != null) {
            profileImageRef = storage.getReference().child(profile);
            httpProfileUrl = convertGsToHttps(String.valueOf(profileImageRef));
        } else {
            httpProfileUrl = null;
        }


        Glide.with(inflater.getContext())
                .load(httpProfileUrl) // 임시로 로드
                .error(R.mipmap.ic_launcher)
                .into(binding.imageviewProfileAccountImage);

        binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
        binding.imageviewProfileAccountImage.setClipToOutline(true);

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {

            Intent intent = new Intent(binding.getRoot().getContext(), MyinfoActivity.class);
            intent.putExtra("profile", httpProfileUrl);
            intent.putExtra("name", response.getName());
            intent.putExtra("code", response.getCode());
            intent.putExtra("userId", response.getUserId());
            startActivity(intent);

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

    public static String convertGsToHttps(String gsUrl) {
        if (!gsUrl.startsWith("gs://")) {
            throw new IllegalArgumentException("Invalid gs:// URL");
        }

        // Extract bucket name and path
        int bucketEndIndex = gsUrl.indexOf("/", 5); // Find the end of bucket name
        if (bucketEndIndex == -1 || bucketEndIndex == gsUrl.length() - 1) {
            throw new IllegalArgumentException("Invalid gs:// URL format");
        }

        String bucketName = gsUrl.substring(5, bucketEndIndex);
        String filePath = gsUrl.substring(bucketEndIndex + 1);

        // URL encode the file path
        String encodedFilePath;
        try {
            encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL encoding failed", e);
        }

        // Construct the HTTPS URL
        String httpsUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName +
                "/o/" + encodedFilePath + "?alt=media";

        return httpsUrl;
    }
}
