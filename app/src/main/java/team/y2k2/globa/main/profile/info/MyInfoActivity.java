package team.y2k2.globa.main.profile.info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.ActivityMyInfoBinding;
import team.y2k2.globa.main.ProfileImage;

public class MyInfoActivity extends AppCompatActivity {
    private ActivityMyInfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyInfoViewModel myInfoViewModel;
    private String newName, newProfile;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: " + uri);
            myInfoViewModel.updateProfileImage(uri.toString());
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                byte[] fileBytes = null;
                if (inputStream != null) {
                    fileBytes = IOUtils.toByteArray(inputStream);
                }

                RequestBody requestBody = RequestBody.create(fileBytes, MediaType.parse("image/*"));

                MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", "filename.jpg", requestBody);

                myInfoViewModel.uploadImage(profilePart, myInfoViewModel.getUserId());

            } catch (IOException e) {
                return;
            }

            newProfile = uri.toString();

        } else {
            Toast.makeText(this, "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
        }
    });
    // 이름 수정을 위한 registerForActivity 객체 초기화 (어뎁터에서 초기화가 안댐)
    ActivityResultLauncher<Intent> nicknameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.hasExtra("updated_name")) {
                String updatedName = data.getStringExtra("updated_name");
                myInfoViewModel.updateName(updatedName);
                myinfoAdapter.notifyDataSetChanged();
                newName = updatedName;
            }
        } else {
            Log.d("이름변경 오류", "registerForActivity 오류");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뷰 모델 초기화
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        binding.setViewModel(myInfoViewModel);
        binding.setLifecycleOwner(this);
        myInfoViewModel.setApiClient(this);

        UserApiClient apiClient = new UserApiClient();

        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        myInfoViewModel.setUserInfo(userInfoResponse);

        initAdapter();
        observeViewModel();
        loadProfileImage(myInfoViewModel.getProfileImage());
    }

    private void initAdapter() {
        myinfoAdapter = new MyinfoAdapter(myInfoViewModel.getItemList(), nicknameEditLauncher, this);
        binding.recyclerviewMyInfoItems.setAdapter(myinfoAdapter);
    }

    private void observeViewModel() {
        myInfoViewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                Intent intent = new Intent();
                intent.putExtra("newName", newName);
                intent.putExtra("newProfile", newProfile);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        myInfoViewModel.getPickImage().observe(this, shouldPickImage -> {
            if (shouldPickImage) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
                myInfoViewModel.resetPickImage();
            }
        });
    }

    public String getUserId() {
        return myInfoViewModel.getUserId();
    }

    public void loadProfileImage(String imageUrl) {
        if (imageUrl != null) {
            if (imageUrl.startsWith("http")) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(binding.imageviewMyInfoPhoto);
            } else {
                StorageReference imageRef = storage.getReference().child(imageUrl);
                Glide.with(this)
                        .load(ProfileImage.convertGsToHttps(imageRef.toString()))
                        .into(binding.imageviewMyInfoPhoto);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewMyInfoPhoto);
            Log.d("이미지 로드 오류", "profile 값이 null입니다");
        }
    }
}