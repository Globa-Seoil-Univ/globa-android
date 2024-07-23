package team.y2k2.globa.main.main;


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
import team.y2k2.globa.notification.NotificationActivity;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button[] docsFilterButtons;

    Context context;

    FragmentMainBinding binding;
    DocsListItemModel docsListItemModel = new DocsListItemModel();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        context = getContext();
        
        setLogoColor();
        setFilterButtons();
        setOnClickListeners();
        setOnRefreshListener(binding.swiperefreshlayoutMain);

        changeButtonDisplay(binding.buttonMainDocsType1);

        showPromotions();
        showCurrentlyAddedRecords();

        return binding.getRoot();
    }

    public void setFilterButtons() {
        docsFilterButtons = new Button[4];

        docsFilterButtons[0] = binding.buttonMainDocsType1;
        docsFilterButtons[1] = binding.buttonMainDocsType2;
        docsFilterButtons[2] = binding.buttonMainDocsType3;
        docsFilterButtons[3] = binding.buttonMainDocsType4;
    }

    @Override
    public void onClick(View v) {
        if(v == docsFilterButtons[0]) {
            changeButtonDisplay(docsFilterButtons[0]);
            showCurrentlyAddedRecords();
        }
        else if(v == docsFilterButtons[1]) {
            changeButtonDisplay(docsFilterButtons[1]);
            showMostViewedRecords();
        }
        else if(v == docsFilterButtons[2]) {
            changeButtonDisplay(docsFilterButtons[2]);
            showSharedRecords();
        }
        else if(v == docsFilterButtons[3]) {
            changeButtonDisplay(docsFilterButtons[3]);
            showReceivedRecords();
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
            showCurrentlyAddedRecords();
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
    }

    public void showCurrentlyAddedRecords() {
        ApiClient apiClient = new ApiClient(context);
        RecordResponse recordResponse = apiClient.requestGetRecords(20);

        docsListItemModel = new DocsListItemModel();

        List<Record> records = recordResponse.getRecords();

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            docsListItemModel.addItem(recordId, folderId, title, datetime, keywords);
        }
        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }

    public void showReceivedRecords() {
        ApiClient apiClient = new ApiClient(context);
        RecordResponse recordResponse = apiClient.requestGetRecords(20);
        List<FolderResponse> folderResponse = apiClient.requestGetFolders(1, 20);

        docsListItemModel = new DocsListItemModel();

        List<Record> records = recordResponse.getRecords();

        ArrayList<Integer> folderIds = new ArrayList<>();

        for(FolderResponse folder : folderResponse) {
            folderIds.add(folder.getFolderId());
        }

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            if(! folderIds.contains(Integer.parseInt(folderId))) {
                docsListItemModel.addItem(recordId, folderId, title, datetime, keywords);
            }
        }
        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }

    public void showMostViewedRecords() {
        ApiClient apiClient = new ApiClient(context);
        RecordResponse recordResponse = apiClient.requestGetRecords(20);

        docsListItemModel = new DocsListItemModel();

        // 예제 레코드 리스트 생성 (실제 레코드 리스트는 recordResponse.getRecords()로 가져옴)
        List<Record> records = recordResponse.getRecords();

        // Record 클래스의 카운트 값 기반으로 정렬
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                SharedPreferences preferences1 = context.getSharedPreferences("record_" + r1.getRecordId(), Activity.MODE_PRIVATE);
                int count1 = preferences1.getInt("count", 0);

                SharedPreferences preferences2 = context.getSharedPreferences("record_" + r2.getRecordId(), Activity.MODE_PRIVATE);
                int count2 = preferences2.getInt("count", 0);

                // 내림차순 정렬
                return Integer.compare(count2, count1);
            }
        });

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            docsListItemModel.addItem(recordId, folderId, title, datetime, keywords);
        }

        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }

    public void showSharedRecords() {
        ApiClient apiClient = new ApiClient(context);
        RecordResponse recordResponse = apiClient.requestGetRecords(20);
        List<FolderResponse> folderResponse = apiClient.requestGetFolders(1, 20);

        docsListItemModel = new DocsListItemModel();

        List<Record> records = recordResponse.getRecords();

        for(Record record : records) {
            String recordId = record.getRecordId();
            String folderId = record.getFolderId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();
            List<Keyword> keywords = record.getKeywords();

            for(FolderResponse folder : folderResponse) {
                int myFolderId = folder.getFolderId();
                int targetFolderId = Integer.parseInt(folderId);

                if(myFolderId == targetFolderId) {
                    docsListItemModel.addItem(recordId, folderId, title, datetime, keywords);
                }
            }
        }
        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }



    private void showPromotions() {
        ApiClient apiClient = new ApiClient(context);
        List<NoticeResponse> noticeResponse = apiClient.requestPromotion(3);

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
