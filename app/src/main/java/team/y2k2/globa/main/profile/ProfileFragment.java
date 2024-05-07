package team.y2k2.globa.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.FragmentProfileBinding;
import team.y2k2.globa.main.profile.info.MyinfoActivity;

public class ProfileFragment extends Fragment {
    ProfileModel model;
    FragmentProfileBinding binding;

    public ProfileFragment() {
        model = new ProfileModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems());

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {

            Intent intent = new Intent(binding.getRoot().getContext(), MyinfoActivity.class);
            startActivity(intent);
        });

        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));



        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL) // 외부 접근 시 API_BASE_URL 로 변경
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // API 호출
        ApiService apiService = retrofit.create(ApiService.class);

        SharedPreferences preferences = inflater.getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<UserInfoResponse> call = apiService.requestUserInfo("application/json", accessToken);
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful()) {
                    // API 호출 성공
                    UserInfoResponse userInfo = response.body();
                    // userInfo를 사용하여 필요한 작업 수행
                    Log.d("IMAGETEST", userInfo.getProfile());
                    Log.d("IMAGETEST", userInfo.getName());

                    binding.textviewProfileAccountUsername.setText(userInfo.getName());
                    binding.textviewProfileAccountUsercode.setText(userInfo.getCode());

                    Glide.with(inflater.getContext())
                            .load(userInfo.getProfile()) // 임시로 로드
                            .into(binding.imageviewProfileAccountImage);

                    binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
                    binding.imageviewProfileAccountImage.setClipToOutline(true);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                // API 호출 실패
                // 네트워크 오류 등 예외 처리 로직 구현
                Log.d("LOGTEST", t.getMessage());

            }
        });

        return binding.getRoot();
    }
}
