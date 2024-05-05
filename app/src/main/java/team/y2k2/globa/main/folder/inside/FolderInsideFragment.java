package team.y2k2.globa.main.folder.inside;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;

    FolderInsideModel model;

    private final int REQUEST_CODE = 101;

    public FolderInsideFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(getLayoutInflater());

        model = new FolderInsideModel();

        FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(model.getItems());

        binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
        binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        binding.imageviewFolderInsideBack.setOnClickListener(v -> {
            // 뒤로가기 코드 생성
            Log.d("BACKKEY", "뒤로가기");
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fcv_main, FolderFragment.class, null)
                    .commit();

            this.getFragmentManager().popBackStack();
        });


        binding.textviewFolderInsideMore.setOnClickListener(v -> {
            Intent intent = new Intent(inflater.getContext(), FolderNameEditActivity.class);
            intent.putExtra("folderName", binding.textviewFolderInsideTitle.getText());
            startActivityForResult(intent, REQUEST_CODE);
        });

        return binding.getRoot();
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
