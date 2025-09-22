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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.FragmentMainBinding;
import team.y2k2.globa.main.docs.list.*;
import team.y2k2.globa.main.notice.*;
import team.y2k2.globa.main.search.*;
import team.y2k2.globa.notification.*;

public class MainFragment extends Fragment implements View.OnClickListener {
    private Button[] docsFilterButtons;
    private FragmentMainBinding binding;
    private MainFragmentModel viewModel;

    private int currentFilterType = RECORDS_FILTER_CURRENTLY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(MainFragmentModel.class);
        viewModel.setContext(getContext());

        setupUI();
        observeViewModel();
        loadInitialData();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void setupUI() {
        setLogoColor();
        setFilterButtons();
        setOnClickListeners();
        setOnRefreshListener(binding.swiperefreshlayoutMain);
    }

    /**
     * ViewModel의 LiveData를 관찰하여 UI를 업데이트하도록 설정합니다.
     */
    private void observeViewModel() {
        viewModel.getIsRecordsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading) {
                binding.progressbarMainRecordsLoading.setVisibility(View.VISIBLE);
                binding.recyclerviewMainDocument.setVisibility(View.GONE);
            } else {
                binding.progressbarMainRecordsLoading.setVisibility(View.GONE);
                binding.recyclerviewMainDocument.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCurrentlyRecordsLiveData().observe(getViewLifecycleOwner(), this::updateRecordsList);
        viewModel.getMostViewedRecordsLiveData().observe(getViewLifecycleOwner(), this::updateRecordsList);
        viewModel.getSharedRecordsLiveData().observe(getViewLifecycleOwner(), this::updateRecordsList);
        viewModel.getReceivedRecordsLiveData().observe(getViewLifecycleOwner(), this::updateRecordsList);

    }

    /**
     * 프래그먼트가 처음 생성될 때 필요한 초기 데이터를 불러옵니다.
     */
    private void loadInitialData() {
        viewModel.loadPromotionsImage();
        viewModel.loadCurrentlyRecords(); // 기본값으로 최신 기록을 불러옵니다.
        viewModel.loadUnreadNotificationCheck();
    }

    /**
     * 외부(e.g., MainActivity)에서 데이터 새로고침을 요청할 때 호출됩니다.
     * 현재 선택된 필터 타입으로 데이터를 다시 불러옵니다.
     */
    public void refreshData() {
        if (viewModel != null) {
            loadRecordsByFilter(currentFilterType);
        }
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
            if (docsFilterButtons[i] == v) {
                changeButtonDisplay(docsFilterButtons[i]);
                loadRecordsByFilter(i);
                break;
            }
        }
    }

    /**
     * 필터 타입에 따라 ViewModel에 데이터 로딩을 요청합니다.
     * @param filterType 필터 종류 (예: 최신, 많이 본)
     */
    private void loadRecordsByFilter(int filterType) {
        this.currentFilterType = filterType;
        switch (filterType) {
            case RECORDS_FILTER_MOST_VIEWED:
                viewModel.loadMostViewedRecords();
                break;
            case RECORDS_FILTER_SHARED:
                viewModel.loadSharedRecords();
                break;
            case RECORDS_FILTER_RECEIVED:
                viewModel.loadReceivedRecords();
                break;
            case RECORDS_FILTER_CURRENTLY:
            default:
                viewModel.loadCurrentlyRecords();
        }
    }

    /**
     * LiveData로부터 받은 문서 목록으로 RecyclerView를 업데이트합니다.
     * @param items 서버로부터 받은 문서 목록
     */
    private void updateRecordsList(ArrayList<DocsListItem> items) {
        if (items == null) {
            items = new ArrayList<>();
        }

        // 목록이 비어있을 경우, "항목 없음"을 표시하기 위한 아이템을 추가합니다.
        if (items.isEmpty()) {
            items.add(new DocsListItem()); // '항목 없음' 뷰를 위한 빈 아이템
        }

        DocsListItemAdapter adapter = new DocsListItemAdapter(items, getActivity());
        int numColumns = calculateNoOfColumns(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);
    }

    /**
     * LiveData로부터 받은 프로모션 이미지 URL 배열로 ViewPager를 업데이트합니다.
     * @param images 서버로부터 받은 이미지 URL 배열
     */
    private void updatePromotions(String[] images) {
        if (images == null) return;
        ViewPager viewPager = binding.viewpagerMainCarousel;
        NoticeFragmentAdapter noticeAdapter = new NoticeFragmentAdapter(getChildFragmentManager(), images);
        NoticeAutoScrollHandler autoScrollHandler = new NoticeAutoScrollHandler(viewPager);
        viewPager.setAdapter(noticeAdapter);
        autoScrollHandler.startAutoScroll();
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
            loadRecordsByFilter(currentFilterType);
            binding.swiperefreshlayoutMain.setRefreshing(false); // 로딩이 완료되면 LiveData가 UI를 업데이트할 것이므로, 여기서는 바로 숨깁니다.
        });
    }

    public void setLogoColor() {
        if(getContext() == null) return;
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewMainTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewMainTitle.setText(spanTitle);
    }

    public void setOnClickListeners() {
        for (Button docsFilterButton : docsFilterButtons) {
            docsFilterButton.setOnClickListener(this);
        }

        binding.imageButtonMainNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        binding.imageButtonMainSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private int calculateNoOfColumns(Context context) {
        if (context == null) return 1;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // 태블릿과 같은 넓은 화면에서는 2열로 표시
        return (dpWidth >= 600) ? 2 : 1;
    }
}

