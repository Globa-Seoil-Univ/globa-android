package team.y2k2.globa.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import org.jetbrains.annotations.NotNull;

import team.y2k2.globa.databinding.FragmentProfileBinding;
import team.y2k2.globa.main.profile.info.MyinfoActivity;

public class ProfileFragment extends Fragment {
    ProfileModel model;
    FragmentProfileBinding binding;

    public ProfileFragment() {
        model = new ProfileModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        SettingItemAdapter adapter = new SettingItemAdapter(model.getItems());

        binding.relativelayoutProfileAccountUser.setOnClickListener(v -> {

            Intent intent = new Intent(binding.getRoot().getContext(), MyinfoActivity.class);
            startActivity(intent);
        });

        binding.recyclerviewProfileSetting.setAdapter(adapter);
        binding.recyclerviewProfileSetting.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        return binding.getRoot();
    }
}
