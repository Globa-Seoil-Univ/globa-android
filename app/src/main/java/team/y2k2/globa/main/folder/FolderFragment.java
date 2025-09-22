package team.y2k2.globa.main.folder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        if (requestCode == FOLDER_ADD) {
            loadFolder();
        }
    }

    public void loadFolder() {
        binding.progressbarFolderLoading.setVisibility(View.VISIBLE);
        binding.recyclerviewFolder.setVisibility(View.GONE);
        binding.recyclerviewFolderCurrently.setVisibility(View.GONE);
        binding.textviewFolderCurrentlyTitle.setVisibility(View.INVISIBLE);
        binding.textviewFolderTitle.setVisibility(View.INVISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            FolderApiClient folderApiClient = new FolderApiClient();
            FolderResponse response = folderApiClient.requestGetFolders(1, 100);

            FolderModel model = new FolderModel();
            FolderCurrentlyModel currentlyModel = new FolderCurrentlyModel();

            if (response != null && response.getFolders() != null) {
                for (Folder folder : response.getFolders()) {
                    model.addItem(folder.getTitle(), folder.getCreatedTime(), Integer.parseInt(folder.getFolderId()));
                    currentlyModel.addItem(folder.getTitle(), folder.getCreatedTime(), Integer.parseInt(folder.getFolderId()));
                }
            }

            handler.post(() -> {
                binding.progressbarFolderLoading.setVisibility(View.GONE);
                binding.recyclerviewFolder.setVisibility(View.VISIBLE);
                binding.recyclerviewFolderCurrently.setVisibility(View.VISIBLE);
                binding.textviewFolderCurrentlyTitle.setVisibility(View.VISIBLE);
                binding.textviewFolderTitle.setVisibility(View.VISIBLE);

                FolderAdapter adapter = new FolderAdapter(model.getItems(), requireActivity());
                FolderCurrentlyAdapter currentlyAdapter = new FolderCurrentlyAdapter(currentlyModel.getItems());

                LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

                binding.recyclerviewFolder.setAdapter(adapter);
                binding.recyclerviewFolder.setLayoutManager(new LinearLayoutManager(requireContext()));

                binding.recyclerviewFolderCurrently.setAdapter(currentlyAdapter);
                binding.recyclerviewFolderCurrently.setLayoutManager(layoutManager);
            });
        });
    }
}