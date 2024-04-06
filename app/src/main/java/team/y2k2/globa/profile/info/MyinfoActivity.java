package team.y2k2.globa.profile.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;
import team.y2k2.globa.profile.edit.NicknameEditActivity;
import team.y2k2.globa.withdraw.WithdrawActivity;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private ArrayList<MyinfoItem> arrayList;
    private MyinfoAdapter myinfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(this));

        List<MyinfoItem> itemList = new ArrayList<>();
        itemList.add(new MyinfoItem("이름", "윤영진", R.drawable.arrow_forward, new NicknameEditActivity()));
        itemList.add(new MyinfoItem("계정 코드", "someCode", R.drawable.item_docs_frame, null));
        itemList.add(new MyinfoItem("로그아웃", "", R.drawable.arrow_forward, null));
        itemList.add(new MyinfoItem("회원탈퇴", "", R.drawable.arrow_forward, new WithdrawActivity()));

        myinfoAdapter = new MyinfoAdapter(itemList);
        binding.recyclerviewMyinfoItems.setAdapter(myinfoAdapter);
    }
}