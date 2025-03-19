package team.y2k2.globa.main.folder.inside;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;
import team.y2k2.globa.main.folder.permission.FolderPermissionActivity;
import team.y2k2.globa.main.folder.share.FolderShareActivity;

public class FolderInsideFragment extends Fragment {
    private FragmentFolderInsideBinding binding;
    private FolderInsideFragmentModel viewModel;

    private int folderId;
    private String folderTitle;
    private String folderDatetime;

    private ActivityResultLauncher<Intent> nameEditLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        setBundleParams();
        viewModel = new ViewModelProvider(this).get(FolderInsideFragmentModel.class);
        viewModel.setFolderTitle(folderTitle);
        binding.setViewModel(viewModel);
        viewModel.setApiClient(this.getContext());
        setupRecyclerView();
        observeViewModel();
        setupListeners();
        loadFolderInside();
        setPreferences();

        return binding.getRoot();
    }

    private void observeViewModel() {
        viewModel.getFolderInsideRecords().observe(getViewLifecycleOwner(), this::updateRecyclerView);
        viewModel.getDeleteResponseCode().observe(getViewLifecycleOwner(), this::handleDeleteResponse);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::handleErrorMessage);
    }

    private void setBundleParams() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            folderId = bundle.getInt("folderId");
            folderTitle = bundle.getString("folderTitle");
            folderDatetime = bundle.getString("folderDatetime");
        }
    }

    private void setupRecyclerView() {
        binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupListeners() {
        binding.imageviewFolderInsideBack.setOnClickListener(v -> navigateBack());
        binding.textviewFolderInsideMore.setOnClickListener(v -> showBottomSheetDialog());
        nameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleNameEditResult(result.getResultCode(), result.getData()));
    }

    private void loadFolderInside() {
        viewModel.fetchFolderInsideRecords(folderId);
    }

    private void setPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences("folderId", Activity.MODE_PRIVATE);
        preferences.edit().putInt("folderId", folderId).apply();
    }

    private void navigateBack() {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container_view_main, FolderFragment.class, null).commit();
        getParentFragmentManager().popBackStack();
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_folder_more, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogTitle = bottomSheetView.findViewById(R.id.textview_more_folder_title);
        dialogTitle.setText(binding.textviewFolderInsideTitle.getText().toString());
        TextView dialogDatetime = bottomSheetView.findViewById(R.id.textview_more_folder_description);
        dialogDatetime.setText(folderDatetime);

        bottomSheetView.findViewById(R.id.relativelayout_more_rename).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startNameEditActivity();
        });
        bottomSheetView.findViewById(R.id.relativelayout_more_share).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FolderShareActivity.class).putExtra("folderId", folderId));
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.relativelayout_more_authority).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FolderPermissionActivity.class).putExtra("folderId", folderId));
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.relativelayout_more_delete).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDeleteConfirmationDialog();
        });

        bottomSheetDialog.show();
    }

    private void startNameEditActivity() {
        Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
        intent.putExtra("folderTitle", binding.textviewFolderInsideTitle.getText().toString());
        intent.putExtra("folderId", folderId);
        nameEditLauncher.launch(intent);
    }

    private void handleNameEditResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && data.hasExtra("name")) {
            String name = data.getStringExtra("name");
            viewModel.setFolderTitle(name);
            Toast.makeText(getContext(), "이름 변경 완료", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("폴더 이름 변경", "이름변경 취소됨 (RESULT_CANCLED)");
        }
    }

    private void showDeleteConfirmationDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_docs, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.textview_delete_docs_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetView.findViewById(R.id.textview_delete_docs_confirm).setOnClickListener(v -> {
            viewModel.deleteFolder(folderId);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void updateRecyclerView(List<FolderInsideRecord> records) {
        FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(records, this);
        binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
    }

    private void handleDeleteResponse(Integer responseCode) {
        if (responseCode != null && responseCode == 200) {
            navigateBack();
        } else if (responseCode != null) {
            Toast.makeText(getContext(), "폴더 삭제 실패", Toast.LENGTH_SHORT).show();
            Log.d("폴더 삭제 실패", "폴더 삭제 실패 코드 : " + responseCode);
        }
    }

    private void handleErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            Log.e("API Error", errorMessage);
            Toast.makeText(getContext(), "API Error: " + errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public int getFolderId() {
        return folderId;
    }

    public FolderInsideFragmentModel getViewModel() {
        return viewModel;
    }
}