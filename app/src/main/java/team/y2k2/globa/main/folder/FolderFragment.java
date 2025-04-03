package team.y2k2.globa.main.folder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.databinding.FragmentFolderBinding;
import team.y2k2.globa.main.folder.add.FolderAddActivity;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyModel;

public class FolderFragment extends Fragment {
    private final int FOLDER_ADD = 200;
    private FragmentFolderBinding binding;
    private FolderCurrentlyModel currentlyModel;
    private FolderModel model;

    public FolderFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderBinding.inflate(inflater, container, false);

        binding.imageButtonFolderAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FolderAddActivity.class);
            startActivityForResult(intent, FOLDER_ADD);
        });

        loadFolder();
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadFolder();
    }

    public void loadFolder() {
        FolderApiClient folderApiClient = new FolderApiClient();
        FolderResponse response = folderApiClient.requestGetFolders(1, 100);

        model = new FolderModel();
        currentlyModel = new FolderCurrentlyModel();

        if (response == null) {
            Log.d(getClass().getSimpleName(), "response = null");
        }

        if (response != null) {
            for (int i = 0; i < response.getFolders().size(); i++) {
                Folder folder = response.getFolders().get(i);

                model.addItem(folder.getTitle(), folder.getCreatedTime(), Integer.parseInt(folder.getFolderId()));
                currentlyModel.addItem(folder.getTitle(), folder.getCreatedTime(), Integer.parseInt(folder.getFolderId()));

                // 각 폴더에 대한 처리 작업 수행
                Log.d("FOLDER_TEST", folder.getTitle() + " | " + folder.getCreatedTime() + " | " + folder.getFolderId());
            }
        }

        FolderAdapter adapter = new FolderAdapter(model.getItems(), requireActivity());
        FolderCurrentlyAdapter currentlyAdapter = new FolderCurrentlyAdapter(currentlyModel.getItems());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.recyclerviewFolder.setAdapter(adapter);
        binding.recyclerviewFolder.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.recyclerviewFolderCurrently.setAdapter(currentlyAdapter);
        binding.recyclerviewFolderCurrently.setLayoutManager(layoutManager);
    }
}
