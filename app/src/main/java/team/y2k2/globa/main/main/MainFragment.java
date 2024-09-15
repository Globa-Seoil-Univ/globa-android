package team.y2k2.globa.main.main;


import static team.y2k2.globa.main.main.MainFragmentModel.RECORDS_FILTER_CURRENTLY;
import static team.y2k2.globa.main.main.MainFragmentModel.RECORDS_FILTER_MOST_VIEWED;
import static team.y2k2.globa.main.main.MainFragmentModel.RECORDS_FILTER_RECEIVED;
import static team.y2k2.globa.main.main.MainFragmentModel.RECORDS_FILTER_SHARED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.databinding.FragmentMainBinding;
import team.y2k2.globa.main.docs.list.DocsListItemAdapter;
import team.y2k2.globa.main.docs.list.DocsListItemModel;
import team.y2k2.globa.main.notice.NoticeAutoScrollHandler;
import team.y2k2.globa.main.notice.NoticeFragmentAdapter;
import team.y2k2.globa.main.search.SearchActivity;
import team.y2k2.globa.notification.NotificationActivity;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button[] docsFilterButtons;

    Context context;

    FragmentMainBinding binding;

    MainFragmentViewModel viewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        context = getContext();

        viewModel = new MainFragmentViewModel(this.getContext());
        
        setLogoColor();
        setFilterButtons();
        setOnClickListeners();
        setOnRefreshListener(binding.swiperefreshlayoutMain);

        showPromotions();
        showRecords(RECORDS_FILTER_CURRENTLY);

        checkNotification();

        return binding.getRoot();
    }

    public void setFilterButtons() {
        docsFilterButtons = new Button[4];

        docsFilterButtons[RECORDS_FILTER_CURRENTLY] = binding.buttonMainDocsType1;
        docsFilterButtons[RECORDS_FILTER_MOST_VIEWED] = binding.buttonMainDocsType2;
        docsFilterButtons[RECORDS_FILTER_SHARED] = binding.buttonMainDocsType3;
        docsFilterButtons[RECORDS_FILTER_RECEIVED] = binding.buttonMainDocsType4;

        changeButtonDisplay(binding.buttonMainDocsType1);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < docsFilterButtons.length; i++) {
            if (docsFilterButtons[i] != v) continue;

            changeButtonDisplay(docsFilterButtons[i]);
            showRecords(i);
        }
    }

    public void changeButtonDisplay(Button button) {
        for (Button docsFilterButton : docsFilterButtons) {
            docsFilterButton.setBackgroundResource(R.drawable.main_button);
            docsFilterButton.setTextColor(Color.BLACK);
        }

        button.setBackgroundResource(R.drawable.main_button_selected);
        button.setTextColor(Color.WHITE);
    }

    public void setOnRefreshListener(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setOnRefreshListener(() -> {
            showRecords(RECORDS_FILTER_CURRENTLY);
            binding.swiperefreshlayoutMain.setRefreshing(false);
        });
    }

    public void setLogoColor() {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewMainTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(context.getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewMainTitle.setText(spanTitle);
    }

    public void setOnClickListeners() {
        for (Button docsFilterButton : docsFilterButtons) {
            docsFilterButton.setOnClickListener(this);
        }

        binding.imagebuttonMainNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        binding.imagebuttonMainSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), SearchActivity.class);
            startActivity(intent);
        });
    }

    public void showRecords(int buttonFilterType) {
        DocsListItemAdapter adapter;

        switch (buttonFilterType) {
            case RECORDS_FILTER_CURRENTLY:
                adapter = new DocsListItemAdapter(viewModel.getCurrentlyRecords(), getActivity());
                break;
            case RECORDS_FILTER_MOST_VIEWED:
                adapter = new DocsListItemAdapter(viewModel.getMostViewedRecords(), getActivity());
                break;
            case RECORDS_FILTER_SHARED:
                adapter = new DocsListItemAdapter(viewModel.getSharedRecords(), getActivity());
                break;
            case RECORDS_FILTER_RECEIVED:
                adapter = new DocsListItemAdapter(viewModel.getReceivedRecords(), getActivity());
                break;
            default:
                adapter = new DocsListItemAdapter(viewModel.getCurrentlyRecords(), getActivity());
        }

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }

    private void showPromotions() {
        ViewPager viewPager = binding.viewpagerMainCarousel;

        NoticeFragmentAdapter noticeAdapter = new NoticeFragmentAdapter(getChildFragmentManager(), viewModel.getPromotionsImage());
        NoticeAutoScrollHandler autoScrollHandler = new NoticeAutoScrollHandler(viewPager);
        viewPager.setAdapter(noticeAdapter);
        autoScrollHandler.startAutoScroll();
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

    private void checkNotification() {
        viewModel.getUnreadNotificationCheck();
        viewModel.getNotificationCheckLiveData().observe(this, checkResponse -> {
            if(checkResponse != null) {
                if(checkResponse.isHasUnRead()) {
                    Log.d("안 읽은 알림 여부", "안 읽은 알림 있음");
                    binding.linearlayoutMainNotificationCheck.setVisibility(View.VISIBLE);
                } else {
                    Log.d("안 읽은 알림 여부", "안 읽은 알림 없음");
                    binding.linearlayoutMainNotificationCheck.setVisibility(View.GONE);
                }
            } else {
                Log.d("안 읽은 알림 여부", "안 읽은 알림 여부 오류 : checkResponse = null");
            }
        });
    }

}
