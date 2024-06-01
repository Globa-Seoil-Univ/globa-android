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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.UserInfoResponse;
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
        Log.d(getClass().getName(), accessToken);

        Call<UserInfoResponse> call = apiService.requestUserInfo("application/json", accessToken);
        Log.d(getClass().getName(), "내 정보 프레그먼트 enqeue 시작");
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 호출 성공
                    UserInfoResponse userInfo = response.body();
                    // userInfo를 사용하여 필요한 작업 수행
//                    Log.d("IMAGETEST", userInfo.getProfile());
//                    Log.d("IMAGETEST", userInfo.getName());

                    userPreferences(response.body());
//                    showLogMessages(response.body());

                    binding.textviewProfileAccountUsername.setText(userInfo.getName());
                    binding.textviewProfileAccountUsercode.setText(userInfo.getCode());


                    Glide.with(inflater.getContext())
                            .load(userInfo.getProfile()) // 임시로 로드
                            .error(R.mipmap.ic_launcher)
                            .into(binding.imageviewProfileAccountImage);

                    binding.imageviewProfileAccountImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    binding.imageviewProfileAccountImage.setBackground(new ShapeDrawable(new OvalShape()));
                    binding.imageviewProfileAccountImage.setClipToOutline(true);
                } else if (response.body() == null) {
                    Log.d(getClass().getName(), "응답 값이 없음 : " + response.message());
                }
                else {
                    Log.d(getClass().getName(), "API 호출 실패");
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                // API 호출 실패
                // 네트워크 오류 등 예외 처리 로직 구현
                Log.d("LOGTEST", "API 호출 실패" + t.getMessage());

            }
        });
        Log.d(getClass().getName(), "프로필 조회 완료");
        return binding.getRoot();
    }

    public void userPreferences(UserInfoResponse response) {
        SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", response.getName());
        editor.putString("userId", response.getUserId());
        editor.putString("publicFolderId", response.getPublicFolderId());
        editor.putString("userCode", response.getCode());
        editor.commit();
    }

    public void showLogMessages(UserInfoResponse response) {
        Log.d(getClass().getName(), "프로필 조회 성공");
        ArrayList<String> logs = new ArrayList();
        logs.add("userName      :" + response.getName());
        logs.add("userId        :" + response.getUserId());
        logs.add("publicFolderId:" + response.getPublicFolderId());
        logs.add("userCode      :" + response.getCode());

        for(int i = 0; i < logs.toArray().length; i++) {
            Log.d(getClass().getName(), logs.get(i));
        }

    }
}
