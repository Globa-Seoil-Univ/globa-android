package team.y2k2.globa.main.profile.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.withdraw.WithdrawActivity;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyinfoViewModel myInfoViewModel;
    private ActivityResultLauncher<Intent> nicknameEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 리사이클러뷰 레이아웃 매니저 설정
        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러 뷰에 넣을 아이템 리스트
        List<MyinfoItem> itemList = new ArrayList<>();

        // 뷰 모델 갖고오기
        myInfoViewModel = new ViewModelProvider(this).get(MyinfoViewModel.class);

        // 헤더 파라미터 2개
        String contentType = "application/json";
        String authorization = "Bearer [사용자 토큰]";

        // getter를 통해 값 가져오기
        myInfoViewModel.getUserInfoResponseLiveData().observe(this, userInfoResponse -> {
            if (userInfoResponse != null) {
                /*
                String profile = userInfoResponse.getProfile();
                String name = userInfoResponse.getName();
                String code = userInfoResponse.getCode();
                int pulbicFolderId = Integer.parseInt(userInfoResponse.getPublicFolderId());
                 */
                itemList.add(new MyinfoItem("이름", userInfoResponse.getName(), R.drawable.arrow_forward, new NicknameEditActivity()));
                itemList.add(new MyinfoItem("계정 코드", userInfoResponse.getCode(), R.drawable.item_docs_frame, null));
                itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_forward, null));
                itemList.add(new MyinfoItem("회월탈퇴", "", R.drawable.arrow_forward, new WithdrawActivity()));
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
        myInfoViewModel.getErrorLiveData().observe(this, errorMessge -> {
            Toast.makeText(getApplicationContext(), errorMessge, Toast.LENGTH_SHORT).show();
        });

        // API 호출 실행
        myInfoViewModel.fetchMyInfo(contentType, authorization);

        /*
        itemList.add(new MyinfoItem("이름", "윤영진", R.drawable.arrow_forward, new NicknameEditActivity()));
        itemList.add(new MyinfoItem("계정 코드", "someCode", R.drawable.item_docs_frame, null));
        itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_forward, null));
        itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_forward, new WithdrawActivity()));
         */

        // 어뎁터에 아이템 리스트 추가
        myinfoAdapter = new MyinfoAdapter(itemList, nicknameEditLauncher);
        // 라시아클러 뷰에 어뎁터 설정
        binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);

    }
}