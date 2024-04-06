package team.y2k2.globa.folder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import team.y2k2.globa.databinding.FragmentFolderBinding;
import team.y2k2.globa.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.folder.currently.FolderCurrentlyModel;

public class FolderFragment extends Fragment {
    FragmentFolderBinding binding;
    FolderCurrentlyModel currentlyModel;
    FolderModel model;

    public FolderFragment() { }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderBinding.inflate(getLayoutInflater());

        currentlyModel = new FolderCurrentlyModel();
        model = new FolderModel();

        FolderCurrentlyAdapter currentlyAdapter = new FolderCurrentlyAdapter(currentlyModel.getItems());

        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.recyclerviewFolderCurrently.setAdapter(currentlyAdapter);
        binding.recyclerviewFolderCurrently.setLayoutManager(layoutManager);

        FolderAdapter adapter = new FolderAdapter(model.getItems());

        binding.recyclerviewFolder.setAdapter(adapter);
        binding.recyclerviewFolder.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        return binding.getRoot();
    }
}
