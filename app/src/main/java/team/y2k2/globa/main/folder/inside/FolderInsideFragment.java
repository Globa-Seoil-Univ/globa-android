package team.y2k2.globa.main.folder.inside;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;
import team.y2k2.globa.main.folder.permission.FolderPermissionActivity;
import team.y2k2.globa.main.folder.share.FolderShareActivity;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;
    FolderInsideModel model;
    FolderInsideFragmentModel folderInsideFragmentModel;

    int folderId;
    String folderTitle;
    String folderDatetime;

    private final int REQUEST_CODE = 101;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ActivityResultLauncher<Intent> nameEditLauncher;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(getLayoutInflater());
        setBundleParams();
        loadFolderInside();

        folderInsideFragmentModel = new ViewModelProvider(this).get(FolderInsideFragmentModel.class);

        binding.textviewFolderInsideTitle.setText(folderTitle);

        setPreferences();

        nameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.hasExtra("name")) {
                    String name = data.getStringExtra("name");
                    binding.textviewFolderInsideTitle.setText(name);
                    Toast.makeText(FolderInsideFragment.this.getContext(), "이름 변경 완료", Toast.LENGTH_SHORT).show();
                }
            } else if(result.getResultCode() == RESULT_CANCELED) {
                Log.d("폴더 이름 변경", "이름변경 취소됨 (RESULT_CANCLED");
            }
        });

        binding.imageviewFolderInsideBack.setOnClickListener(v -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view_main, FolderFragment.class, null)
                    .commit();

            getFragmentManager().popBackStack();
        });

        binding.textviewFolderInsideMore.setOnClickListener(v -> showBottomSheetDialog());

        return binding.getRoot();
    }

    public void loadFolderInside() {
        ApiClient apiClient = new ApiClient(getContext());
        FolderInsideRecordResponse response = apiClient.requestGetFolderInside(folderId, 1, 100);

        if (response != null) {
            // 받아온 데이터를 처리하는 로직을 작성합니다.
            model = new FolderInsideModel();
            List<FolderInsideRecord> records = response.getRecords();

            for(FolderInsideRecord record : records) {
                // 각 폴더에 대한 처리 작업 수행
                String recordId = record.getRecordId();
                String title = record.getTitle();
                String path = record.getPath();
                String datetime = record.getCreatedTime();
                datetime = datetime.replace("T", "  ");

                model.addItem(Integer.toString(folderId), recordId, title, datetime);
            }

            if(model.getItems().isEmpty()) {
                model.addItem("", "", "", "");
            }

            FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(model.getItems(), this);
            binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
            binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        }
    }

    public void setBundleParams() {
        Bundle bundle = getArguments();
        folderId = bundle.getInt("folderId");
        folderTitle = bundle.getString("folderTitle");
        folderDatetime = bundle.getString("folderDatetime");
    }

    public void setPreferences() {
        preferences = getContext().getSharedPreferences("folderId", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("folderId", folderId);
        editor.commit();
    }

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FolderInsideFragment.this.getContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_folder_more, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogTitle = bottomSheetView.findViewById(R.id.textview_more_folder_title);
        dialogTitle.setText(binding.textviewFolderInsideTitle.getText().toString());
        TextView dialogDatetime = bottomSheetView.findViewById(R.id.textview_more_folder_description);
        dialogDatetime.setText(folderDatetime);

        RelativeLayout nameEditButton = bottomSheetView.findViewById(R.id.relativelayout_more_rename);
        RelativeLayout shareButton = bottomSheetView.findViewById(R.id.relativelayout_more_share);
        RelativeLayout authorityButton = bottomSheetView.findViewById(R.id.relativelayout_more_authority);
        RelativeLayout deleteButton = bottomSheetView.findViewById(R.id.relativelayout_more_delete);

        nameEditButton.setOnClickListener(v -> {
            // 이름 변경 버튼
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
            intent.putExtra("folderTitle", binding.textviewFolderInsideTitle.getText().toString());
            intent.putExtra("folderId", folderId);
            nameEditLauncher.launch(intent);
        });
        shareButton.setOnClickListener(v -> {
            // 공유 버튼
            Intent toShareIntent = new Intent(getActivity(), FolderShareActivity.class);
            toShareIntent.putExtra("folderId", folderId);
            startActivity(toShareIntent);
            bottomSheetDialog.dismiss();
        });
        authorityButton.setOnClickListener(v -> {
            // 권한 관리 버튼
            Intent toPermissionIntent = new Intent(getActivity(), FolderPermissionActivity.class);
            toPermissionIntent.putExtra("folderId", folderId);
            startActivity(toPermissionIntent);
            bottomSheetDialog.dismiss();
        });
        deleteButton.setOnClickListener(v -> {
            // 삭제 버튼
            bottomSheetDialog.dismiss();
            showSecondBottomSheetDialog();
        });

        bottomSheetDialog.show();
    }

    public void showSecondBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FolderInsideFragment.this.getContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_docs, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView cancelButton = bottomSheetView.findViewById(R.id.textview_delete_docs_cancel);
        TextView confirmButton = bottomSheetView.findViewById(R.id.textview_delete_docs_confirm);

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            deleteFolder();

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    private void deleteFolder() {
        folderInsideFragmentModel.deleteFolder(folderId);
        folderInsideFragmentModel.getResponseCodeLiveData().observe(getViewLifecycleOwner(), responseCode -> {
            if(responseCode == 200) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(FolderInsideFragment.this)
                        .commit();

                new Handler().post(() -> {
                    FolderFragment folderFragment = new FolderFragment();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view_main, folderFragment)
                            .addToBackStack(null)
                            .commit();
                });
            } else {
                Toast.makeText(getContext(), "폴더 삭제 실패", Toast.LENGTH_SHORT).show();
                Log.d("폴더 삭제 실패", "폴더 삭제 실패 코드 : " + responseCode);
            }
        });
    }

}
