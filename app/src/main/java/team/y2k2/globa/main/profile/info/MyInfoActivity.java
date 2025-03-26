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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.ActivityMyInfoBinding;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.main.profile.withdraw.WithdrawActivity;

public class MyInfoActivity extends AppCompatActivity {
    private ActivityMyInfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyinfoViewModel myInfoViewModel;
    private String profile, name, code, userId;
    private String newName, newProfile;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserApiClient apiClient = new UserApiClient();

        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        profile = userInfoResponse.getProfile();
        name = userInfoResponse.getName();
        code = userInfoResponse.getCode();
        userId = userInfoResponse.getUserId();

        binding.buttonMyInfoBack.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.putExtra("newName", newName);
            intent.putExtra("newProfile", newProfile);
            setResult(RESULT_OK, intent);
            finish();
        });

        // 뷰 모델 갖고오기
        myInfoViewModel = new ViewModelProvider(MyInfoActivity.this).get(MyinfoViewModel.class);

        loadUserInfoList(myInfoViewModel);

        changeUserProfileImage(userId);

    }

    // 이미지 클릭시 변경
    public void changeUserProfileImage(String userId) {

        // 이미지 선택 (PhotoPicker Android 14)
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if(uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(this).load(uri)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyInfoPhoto);

                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] fileBytes = null;
                    if (inputStream != null) {
                        fileBytes = IOUtils.toByteArray(inputStream);
                    }

                    RequestBody requestBody = RequestBody.create(fileBytes, MediaType.parse("image/*"));

                    MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", "filename.jpg", requestBody);

                    myInfoViewModel.uploadImage(profilePart, userId);

                } catch (IOException e) {
                    return;
                }

                newProfile = uri.toString();

            } else {
                Toast.makeText(this, "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                Log.d("PhotoPicker", "No media selected");
            }
        });

        // 사진 변경 버튼 클릭 시 PhotoPicker 실행
        binding.buttonMyInfoChangePhoto.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
    }

    // 초기 화면 구성
    public void loadUserInfoList(MyinfoViewModel myInfoViewModel) {
        // 리사이클러뷰 레이아웃 매니저 설정
        binding.recyclerviewMyInfoItems.setLayoutManager(new LinearLayoutManager(MyInfoActivity.this));
        // 리사이클러 뷰에 넣을 아이템 리스트
        List<MyInfoItem> itemList = new ArrayList<>();

        if(profile != null) {
            if(profile.startsWith("http")) {
                Glide.with(this).load(profile)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyInfoPhoto);
            } else {
                StorageReference imageRef = storage.getReference().child(profile);
                Glide.with(this).load(ProfileImage.convertGsToHttps(imageRef.toString()))
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyInfoPhoto);
            }
        } else {
            Glide.with(this).load(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewMyInfoPhoto);
            Log.d("이미지 로드 오류", "profile 값이 null입니다");
        }

        itemList.add(new MyInfoItem(getString(R.string.name), name, R.drawable.arrow_right, new NicknameEditActivity()));
        itemList.add(new MyInfoItem(getString(R.string.profile_account_code), code, R.drawable.item_docs_frame, null));
        itemList.add(new MyInfoItem(getString(R.string.sign_out), "", R.drawable.arrow_right, null));
        itemList.add(new MyInfoItem(getString(R.string.withdraw), "", R.drawable.arrow_right, new WithdrawActivity()));

        // 이름 수정을 위한 registerForActivity 객체 초기화 (어뎁터에서 초기화가 안댐)
        ActivityResultLauncher<Intent> nicknameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.hasExtra("updated_name")) {
                    String updatedName = data.getStringExtra("updated_name");
                    itemList.get(0).setName(updatedName);
                    myinfoAdapter.notifyDataSetChanged();
                    newName = updatedName;
                }
            } else {
                Log.d("이름변경 오류", "registerForActivity 오류");
            }
        });

        // 어뎁터에 아이템 리스트 추가
        myinfoAdapter = new MyinfoAdapter(itemList, nicknameEditLauncher, this);
        // 라시아클러 뷰에 어뎁터 설정
        binding.recyclerviewMyInfoItems.setAdapter(myinfoAdapter);

    }

    public String getUserId() {
        return userId;
    }

}