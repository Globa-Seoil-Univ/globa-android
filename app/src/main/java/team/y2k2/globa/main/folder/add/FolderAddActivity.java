package team.y2k2.globa.main.folder.add;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityFolderAddBinding;
import team.y2k2.globa.main.profile.UserInfoResponse;

public class FolderAddActivity extends AppCompatActivity {
    ActivityFolderAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerviewFolderaddAddlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        int[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher};

        FolderAddAdapter adapter = new FolderAddAdapter(this, images);
        binding.recyclerviewFolderaddAddlist.setAdapter(adapter);



        binding.textviewFolderAddConfirm.setOnClickListener(v -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
            String accessToken = "Bearer " + preferences.getString("accessToken", "");

            String uid = preferences.getString("accessToken", "");
            String title = binding.edittextFolderaddInputname.getText().toString();
//            List<ShareTarget> shareTargetList = new ArrayList<>();
//            shareTargetList.add(new ShareTarget(uid, "w"));
            FolderAddRequest folderAddRequest = new FolderAddRequest(title);


            Call<Void> call = apiService.requestInsertFolder("application/json", accessToken, folderAddRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // API 호출 성공
                        response.body();
                        // userInfo를 사용하여 필요한 작업 수행
                        Log.d("LOG_FOLDER_ADD", "폴더 추가 성공");
                        finish();

                    } else {
                        Log.d("LOG_FOLDER_ADD", "폴더 추가 실패: "+response.code() + " | ");

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // API 호출 실패
                    // 네트워크 오류 등 예외 처리 로직 구현
                    Log.d("LOG_FOLDER_ADD", t.getMessage());

                }
            });
        });
    }
}