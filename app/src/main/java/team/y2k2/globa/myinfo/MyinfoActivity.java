package team.y2k2.globa.myinfo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyinfoBinding;

public class MyinfoActivity extends AppCompatActivity {
    private ActivityMyinfoBinding binding;
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerviewMyinfoItems.setLayoutManager(new LinearLayoutManager(this));

        List<MainData> itemList = new ArrayList<>();
        itemList.add(new MainData("이름", "윤영진", R.drawable.arrow_forward));
        itemList.add(new MainData("계정 코드", "someCode", R.drawable.item_docs_frame));
        itemList.add(new MainData("로그아웃", "", R.drawable.arrow_forward));
        itemList.add(new MainData("회원탈퇴", "", R.drawable.arrow_forward));

        mainAdapter = new MainAdapter(itemList);
        binding.recyclerviewMyinfoItems.setAdapter(mainAdapter);
    }
}