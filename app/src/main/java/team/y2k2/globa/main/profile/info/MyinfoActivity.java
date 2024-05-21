package team.y2k2.globa.main.profile.info;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
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
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;


    static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserInfoList();


        // 이미지 변경 버튼 동작
        // 갤러리 접근 권한 요청 동작 (menifest 파일에서 허가 받을 수 있지만 이 방식이 권장됨)
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if(isGranted) {
                // 접근 권한 허가 됨
                pickImage();
            } else {
                // 접근 권한 거부 됨
                Toast.makeText(getApplicationContext(), "갤러리 접근이 거부 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        // 이미지 선택을 위한 ActivityResultLauncher 설정
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();

                Glide.with(MyinfoActivity.this)
                        .load(imageUri)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                binding.imageviewMyinfoPhoto.setImageBitmap(resource);

                                sendImageToServer(resource);
                            }
                        });
            }
        });
        // 이미지 선택 버튼 클릭 이벤트 설정
        binding.buttonMyinfoChangephoto.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(MyinfoActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 접근 권한을 못받았을 경우
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
            } else {
                pickImage();
            }
        });

    }
    // 갤러리에서 이미지를 선택하는 메소드
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    private void sendImageToServer(Bitmap bitmap) {
        // Bitmap을 ByteArray로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // ByteArray를 사용하여 뷰모델 호출하여 서버로 전송
        MyinfoViewModel viewModel = new ViewModelProvider(this).get(MyinfoViewModel.class);
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");
        viewModel.uploadImage(byteArray, "user_id", authorization);
    }

    public void loadUserInfoList() {
        // 리사이클러뷰 레이아웃 매니저 설정
        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(MyinfoActivity.this));
        // 리사이클러 뷰에 넣을 아이템 리스트
        List<MyinfoItem> itemList = new ArrayList<>();

        // 뷰 모델 갖고오기
        myInfoViewModel = new ViewModelProvider(MyinfoActivity.this).get(MyinfoViewModel.class);

        // 프리퍼런스 불러오기
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);

        // 헤더 파라미터 2개
        String contentType = "application/json";
        String authorization = "Bearer " + preferences.getString("accessToken", "");

        // getter를 통해 값 가져오기
        myInfoViewModel.getUserInfoResponseLiveData().observe(MyinfoActivity.this, userInfoResponse -> {
            if (userInfoResponse != null) {
                String profile = userInfoResponse.getProfile();
                String name = userInfoResponse.getName();
                String code = userInfoResponse.getCode();
                int folderId = Integer.parseInt(userInfoResponse.getPublicFolderId());

                // 초기 이미지 설정
                profileImageRef = storage.getReference().child(profile);

                // 2024-05-22 에러 발생 지점 주석 처리
//                Glide.with(this).load(profileImageRef)
//                                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
//                                .into(binding.imageviewMyinfoPhoto);

                itemList.add(new MyinfoItem("이름", name, R.drawable.arrow_forward, new NicknameEditActivity()));
                itemList.add(new MyinfoItem("계정 코드", code, R.drawable.item_docs_frame, null));
                itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_forward, null));
                itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_forward, new WithdrawActivity()));

                // 어뎁터에 아이템 리스트 추가
                myinfoAdapter = new MyinfoAdapter(itemList, nicknameEditLauncher);
                // 라시아클러 뷰에 어뎁터 설정
                binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);
            }
        });

        // 이름 수정을 위한 registerForActivity 객체 초기화 (어뎁터에서 초기화가 안댐)
        nicknameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if(data != null && data.hasExtra("updated_name")) {
                    String updatedName = data.getStringExtra("updated_name");
                    itemList.get(0).setName(updatedName);
                    myinfoAdapter.notifyDataSetChanged();
                }
            }
        });

        // 에러 발생
        myInfoViewModel.getErrorLiveData().observe(MyinfoActivity.this, errorMessge -> {
            Toast.makeText(getApplicationContext(), errorMessge, Toast.LENGTH_SHORT).show();
        });

        // API 호출 실행
        myInfoViewModel.fetchMyInfo(contentType, authorization);
    }
}