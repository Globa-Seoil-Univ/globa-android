package team.y2k2.globa.main.main;

import static team.y2k2.globa.main.main.MainModel.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.FragmentMainBinding;
import team.y2k2.globa.main.docs.list.*;
import team.y2k2.globa.main.notice.*;
import team.y2k2.globa.main.search.*;
import team.y2k2.globa.notification.*;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button[] docsFilterButtons;
    FragmentMainBinding binding;
    MainFragmentModel viewModel;

    int filterType;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(MainFragmentModel.class);
        viewModel.setContext(getContext());

        setLogoColor();
        setFilterButtons();
        setOnClickListeners();
        setOnRefreshListener(binding.swiperefreshlayoutMain);
        showPromotions();
        filterType = RECORDS_FILTER_CURRENTLY;
        showRecords(filterType);
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
            showRecords(filterType);
            binding.swiperefreshlayoutMain.setRefreshing(false);
        });
    }

    public void setLogoColor() {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewMainTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewMainTitle.setText(spanTitle);
    }

    public void setOnClickListeners() {
        for (Button docsFilterButton : docsFilterButtons) {
            docsFilterButton.setOnClickListener(this);
        }

        binding.imageButtonMainNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        binding.imageButtonMainSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), SearchActivity.class);
            startActivity(intent);
        });
    }

    public void showRecords(int buttonFilterType) {
        DocsListItemAdapter adapter;

        filterType = buttonFilterType;

        switch (buttonFilterType) {
            case RECORDS_FILTER_MOST_VIEWED:
                adapter = new DocsListItemAdapter(viewModel.getMostViewedRecords(), getActivity());
                break;
            case RECORDS_FILTER_SHARED:
                adapter = new DocsListItemAdapter(viewModel.getSharedRecords(), getActivity());
                break;
            case RECORDS_FILTER_RECEIVED:
                adapter = new DocsListItemAdapter(viewModel.getReceivedRecords(), getActivity());
                break;
            case RECORDS_FILTER_CURRENTLY:
            default:
                adapter = new DocsListItemAdapter(viewModel.getCurrentlyRecords(), getActivity());
        }

        if (adapter.getItemCount() == 0) {
            ArrayList<DocsListItem> items = new ArrayList<>();
            items.add(new DocsListItem());
            adapter = new DocsListItemAdapter(items, getActivity());
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

        if (dpWidth >= DisplayMetrics.DENSITY_600) {
            return 2;
        } else {
            return 1;
        }
    }

    private void checkNotification() {
        viewModel.getUnreadNotificationCheck();
        viewModel.getNotificationCheckLiveData().observe(getViewLifecycleOwner(), checkResponse -> {});
    }
}