package team.y2k2.globa.main;



import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.FragmentMainBinding;
import team.y2k2.globa.main.docs.list.DocsListItemAdapter;
import team.y2k2.globa.main.docs.list.DocsListItemModel;

public class MainFragment extends Fragment {
    FragmentMainBinding binding;
    DocsListItemModel docsListItemModel = new DocsListItemModel();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());

        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewMainTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(inflater.getContext().getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewMainTitle.setText(spanTitle);

        DocsListItemAdapter adapter = new DocsListItemAdapter(docsListItemModel.getItems());
        adapter.notifyDataSetChanged();

        int numColumns = calculateNoOfColumns(binding.getRoot().getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), numColumns);

        binding.recyclerviewMainDocument.setAdapter(adapter);
        binding.recyclerviewMainDocument.setLayoutManager(gridLayoutManager);

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
