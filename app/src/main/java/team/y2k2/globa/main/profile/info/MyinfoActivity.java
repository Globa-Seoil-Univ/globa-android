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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;
import team.y2k2.globa.main.MainActivity;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.withdraw.WithdrawActivity;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyinfoViewModel myInfoViewModel;
    private ActivityResultLauncher<Intent> nicknameEditLauncher;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonMyinfoBack.setOnClickListener(v -> {
            finish();
        });

        // 뷰 모델 갖고오기
        myInfoViewModel = new ViewModelProvider(MyinfoActivity.this).get(MyinfoViewModel.class);

        loadUserInfoList(myInfoViewModel);

        userId = getIntent().getStringExtra("userId");

        changeUserProfileImage(userId);

    }

    // 이미지 클릭시 변경
    public void changeUserProfileImage(String userId) {

        // 이미지 선택 (PhotoPicker Android 14)
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if(uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(this).load(uri)
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(binding.imageviewMyinfoPhoto);

                File imageFile = new File(uri.getPath());

                myInfoViewModel.uploadImage(imageFile, userId);

            } else {
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

        String profile = getIntent().getStringExtra("profile");
        String name = getIntent().getStringExtra("name");
        String code = getIntent().getStringExtra("code");

        if(profile != null) {
            Glide.with(this).load(profile)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imageviewMyinfoPhoto);
        } else {
            Glide.with(this).load(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imageviewMyinfoPhoto);
            Log.d("이미지 로드 오류", "profile 값이 null입니다");
        }

        itemList.add(new MyinfoItem("이름", name, R.drawable.arrow_forward, userId, new NicknameEditActivity()));
        itemList.add(new MyinfoItem("계정 코드", code, R.drawable.item_docs_frame, "", null));
        itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_forward, "", null));
        itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_forward, "", new WithdrawActivity()));

        // 이름 수정을 위한 registerForActivity 객체 초기화 (어뎁터에서 초기화가 안댐)
        nicknameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if(data != null && data.hasExtra("updated_name")) {
                    String updatedName = data.getStringExtra("updated_name");
                    itemList.get(0).setName(updatedName);
                    myinfoAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d("이름변경 오류", "registerForActivity 오류");
            }
        });

        // 어뎁터에 아이템 리스트 추가
        myinfoAdapter = new MyinfoAdapter(itemList, nicknameEditLauncher);
        // 라시아클러 뷰에 어뎁터 설정
        binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);

        // API 에러 발생
        myInfoViewModel.getErrorLiveData().observe(MyinfoActivity.this, errorMessge -> {
            if(errorMessge != null) {
                Log.d("이미지 api 오류", "이미지 api Request 실패");
            }
        });

    }

}