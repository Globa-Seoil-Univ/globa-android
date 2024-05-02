package team.y2k2.globa.docs;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;

public class DocsActivity extends AppCompatActivity {
    ActivityDocsBinding binding;
    DocsModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

        model = new DocsModel();

        DocsDetailAdapter adapter = new DocsDetailAdapter(model.getItems());

        binding.recyclerviewDocsDetail.setAdapter(adapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        setContentView(binding.getRoot());
    }

}
