package team.y2k2.globa.main.profile.info;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.fido.fido2.api.common.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;
import team.y2k2.globa.main.MainActivity;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.withdraw.WithdrawActivity;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyinfoViewModel myInfoViewModel;
    private ActivityResultLauncher<Intent> nicknameEditLauncher;
    private String userId;
    private ApiClient apiClient;
    private String profile, name, code;
    private String newName;
    private String newProfile;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiClient = new ApiClient(this);

        binding.buttonMyinfoBack.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            if(newName != null && newProfile != null) {
                resultIntent.putExtra("newName", newName);
                resultIntent.putExtra("newProfile", newProfile);
                setResult(Activity.RESULT_OK, resultIntent);
            } else if(newName != null) {
                resultIntent.putExtra("newName", newName);
                setResult(Activity.RESULT_OK, resultIntent);
            } else if(newProfile != null) {
                resultIntent.putExtra("newProfile", newProfile);
                setResult(Activity.RESULT_OK, resultIntent);
            } else {
                setResult(Activity.RESULT_CANCELED, resultIntent);
            }

            finish();
        });

        // 뷰 모델 갖고오기
        myInfoViewModel = new ViewModelProvider(MyinfoActivity.this).get(MyinfoViewModel.class);

        loadUserInfoList(myInfoViewModel);

        changeUserProfileImage(userId);

    }

    // 이미지 클릭시 변경
    public void changeUserProfileImage(String userId) {

        // 이미지 선택 (PhotoPicker Android 14)
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if(uri != null) {
                Log.d("PhotoPicker", "선택 이미지 URI: " + uri);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] fileBytes = getBytes(inputStream);
                    Log.d(getClass().getSimpleName(), "바이트 파일 크기: " + fileBytes.length);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), fileBytes);

                    MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", "filename.jpg", requestBody);

                    myInfoViewModel.uploadImage(profilePart, userId);

                    Glide.with(this).load(uri)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewMyinfoPhoto);

                    newProfile = uri.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(getClass().getSimpleName(), "이미지 선택 오류: " + e.getMessage());
                }



            } else {
                Toast.makeText(this, "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                Log.d("PhotoPicker", "No media selected");
            }
        });

        // 사진 변경 버튼 클릭 시 PhotoPicker 실행
        binding.buttonMyinfoChangephoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    // 초기 화면 구성
    public void loadUserInfoList(MyinfoViewModel myInfoViewModel) {
        // 리사이클러뷰 레이아웃 매니저 설정
        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(MyinfoActivity.this));
        // 리사이클러 뷰에 넣을 아이템 리스트
        List<MyinfoItem> itemList = new ArrayList<>();

        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();

        profile = userInfoResponse.getProfile();
        name = userInfoResponse.getName();
        code = userInfoResponse.getCode();
        userId = userInfoResponse.getUserId();

        Log.d(getClass().getSimpleName(), "프로필: " + profile);
        Log.d(getClass().getSimpleName(), "이름: " + name);
        Log.d(getClass().getSimpleName(), "유저 코드: " + code);
        Log.d(getClass().getSimpleName(), "아이디: " + userId);

        if(profile != null) {
            if(profile.startsWith("http")) {
                Glide.with(this).load(profile)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyinfoPhoto);
            } else {
                profileRef = storage.getReference().child(profile);
                Glide.with(this).load(ProfileImage.convertGsToHttps(profileRef.toString()))
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyinfoPhoto);
            }
        } else {
            Glide.with(this).load(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(binding.imageviewMyinfoPhoto);
            Log.d("이미지 로드 오류", "profile 값이 null입니다");
        }

        itemList.add(new MyinfoItem("이름", name, R.drawable.arrow_right, new NicknameEditActivity()));
        itemList.add(new MyinfoItem("계정 코드", code, R.drawable.item_docs_frame, null));
        itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_right, null));
        itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_right, new WithdrawActivity()));

        // 이름 수정을 위한 registerForActivity 객체 초기화 (어뎁터에서 초기화가 안댐)
        nicknameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if(data != null && data.hasExtra("updated_name")) {
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
        binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);

    }

    public String getUserId() {
        return userId;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}