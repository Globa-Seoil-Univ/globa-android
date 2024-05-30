package team.y2k2.globa.main.folder.inside;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;
    FolderInsideModel model;

    int folderId;
    String folderTitle;

    private final int REQUEST_CODE = 101;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(getLayoutInflater());
        setBundleParams();
        loadFolderInside();

        binding.textviewFolderInsideTitle.setText(folderTitle);

        setPreferences();

        binding.imageviewFolderInsideBack.setOnClickListener(v -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fcv_main, FolderFragment.class, null)
                    .commit();

            getFragmentManager().popBackStack();
        });

        binding.textviewFolderInsideMore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
            intent.putExtra("folderTitle", binding.textviewFolderInsideTitle.getText());
            startActivityForResult(intent, REQUEST_CODE);
        });

        return binding.getRoot();
    }


    public void loadFolderInside() {
        ApiClient apiClient = new ApiClient(getContext());
        FolderInsideRecordResponse response = apiClient.requestGetFolderInside(folderId, 1, 10);

        if (response != null) {
            // 받아온 데이터를 처리하는 로직을 작성합니다.
            model = new FolderInsideModel();
            List<FolderInsideRecord> records = response.getRecords();

            for(FolderInsideRecord record : records) {
                // 각 폴더에 대한 처리 작업 수행
                String recordId = record.getRecordId();
                String title = record.getTitle();
                String path = record.getPath();

                model.addItem(Integer.toString(folderId), recordId, title, path);
            }

            FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(model.getItems());

            binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
            binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        }
    }

    public void setBundleParams() {
        Bundle bundle = getArguments();
        folderId = bundle.getInt("folderId");
        folderTitle = bundle.getString("folderTitle");
    }

    public void setPreferences() {
        preferences = getContext().getSharedPreferences("folderId", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("folderId", folderId);
        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 결과 처리
                if (data != null) {
                    String changedFolderName = data.getStringExtra("changedFolderName");
                    binding.textviewFolderInsideTitle.setText(changedFolderName);
                    // 결과값 사용
                }
            } else if (resultCode == RESULT_CANCELED) {
                // 사용자가 액티비티를 취소한 경우
            }
        }
    }
}
