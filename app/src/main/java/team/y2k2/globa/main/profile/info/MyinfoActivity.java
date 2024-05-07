package team.y2k2.globa.main.profile.info;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.withdraw.WithdrawActivity;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private ArrayList<MyinfoItem> arrayList;
    private MyinfoAdapter myinfoAdapter;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 리사이클러 뷰 생성
        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(this));
        List<MyinfoItem> itemList = new ArrayList<>();
        itemList.add(new MyinfoItem("이름", "윤영진", R.drawable.arrow_right, new NicknameEditActivity()));
        itemList.add(new MyinfoItem("계정 코드", "someCode", R.drawable.item_docs_frame, null));
        itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_right, null));
        itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_right, new WithdrawActivity()));
        myinfoAdapter = new MyinfoAdapter(itemList);
        binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);

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
                binding.imageviewMyinfoPhoto.setImageURI(imageUri);
            }
        });
        // 이미지 선택 버튼 클릭 이벤트 설정
        binding.buttonMyinfoChangephoto.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(MyinfoActivity.this, Permissions.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Permissions.READ_EXTERNAL_STORAGE);
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

    // 접근 권한 허가 요청 문구
    class Permissions {
        static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    }
}