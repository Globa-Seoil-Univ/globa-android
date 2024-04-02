package team.y2k2.globa.folder.inside;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import team.y2k2.globa.databinding.FragmentFolderInsideBinding;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;

    FolderInsideModel model;

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

        });

        return binding.getRoot();
    }
}
