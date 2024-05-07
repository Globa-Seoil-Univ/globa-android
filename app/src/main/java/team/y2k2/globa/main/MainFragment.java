package team.y2k2.globa.main;



import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.FragmentMainBinding;
import team.y2k2.globa.main.docs.list.DocsListItemAdapter;
import team.y2k2.globa.main.docs.list.DocsListItemModel;
import team.y2k2.globa.main.notice.NoticeAutoScrollHandler;
import team.y2k2.globa.main.notice.NoticeFragmentAdapter;

public class MainFragment extends Fragment implements View.OnClickListener {
    FragmentMainBinding binding;
    DocsListItemModel docsListItemModel = new DocsListItemModel();

    @Override
    public void onClick(View v) {
        if(v == binding.buttonMainDocsType1) {
            changeButtonDisplay(binding.buttonMainDocsType1);
        }
        else if(v == binding.buttonMainDocsType2) {
            changeButtonDisplay(binding.buttonMainDocsType2);
        }
        else if(v == binding.buttonMainDocsType3) {
            changeButtonDisplay(binding.buttonMainDocsType3);
        }
        else if(v == binding.buttonMainDocsType4) {
            changeButtonDisplay(binding.buttonMainDocsType4);
        }
    }

    public void changeButtonDisplay(Button button) {
        binding.buttonMainDocsType1.setBackgroundResource(R.drawable.main_button);
        binding.buttonMainDocsType2.setBackgroundResource(R.drawable.main_button);
        binding.buttonMainDocsType3.setBackgroundResource(R.drawable.main_button);
        binding.buttonMainDocsType4.setBackgroundResource(R.drawable.main_button);

        binding.buttonMainDocsType1.setTextColor(Color.BLACK);
        binding.buttonMainDocsType2.setTextColor(Color.BLACK);
        binding.buttonMainDocsType3.setTextColor(Color.BLACK);
        binding.buttonMainDocsType4.setTextColor(Color.BLACK);

        button.setBackgroundResource(R.drawable.main_button_selected);
        button.setTextColor(Color.WHITE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());

        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewMainTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(inflater.getContext().getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewMainTitle.setText(spanTitle);

        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());
        adapter.notifyDataSetChanged();

        binding.buttonMainDocsType1.setOnClickListener(this);
        binding.buttonMainDocsType2.setOnClickListener(this);
        binding.buttonMainDocsType3.setOnClickListener(this);
        binding.buttonMainDocsType4.setOnClickListener(this);

        changeButtonDisplay(binding.buttonMainDocsType1);

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);


        SharedPreferences preferences = inflater.getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<List<NoticeResponse>> call = apiService.requestPromotion("application/json",accessToken, 3);
        call.enqueue(new Callback<List<NoticeResponse>>() {
            @Override
            public void onResponse(Call<List<NoticeResponse>> call, Response<List<NoticeResponse>> response) {
                if (response.isSuccessful()) {
                    List<NoticeResponse> noticeResponse = response.body();
                    // 성공적으로 응답을 받았을 때 처리
                    ViewPager viewPager = binding.viewpagerMainCarousel;

                    String[] images = new String[noticeResponse.size()];

                    for(int i = 0; i < noticeResponse.size(); i++) {
                        NoticeResponse index = noticeResponse.get(i);
                        images[i] = index.getThumbnail();
                    }

                    NoticeFragmentAdapter noticeAdapter = new NoticeFragmentAdapter(getChildFragmentManager(), images);
                    NoticeAutoScrollHandler autoScrollHandler = new NoticeAutoScrollHandler(viewPager);
                    viewPager.setAdapter(noticeAdapter);
                    autoScrollHandler.startAutoScroll();
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d("IMAGETEST", "오류");

                }
            }

            @Override
            public void onFailure(Call<List<NoticeResponse>> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d("IMAGETEST", "오류" + t.getMessage());

            }
        });

        return binding.getRoot();
    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (dpWidth >= 600) { // 화면 사이즈가 600dp 이상이면
            return 2; // 테블릿의 경우 2 컬럼
        } else {
            return 1; // 모바일의 경우 1 컬럼
        }
    }
}
