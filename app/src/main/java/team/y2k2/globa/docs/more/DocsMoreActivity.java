package team.y2k2.globa.docs.more;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.databinding.ActivityDocsMoreBinding;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;

    String title;
    String folderId;
    String recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());

        binding.imagebuttonDocsMoreBack.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");
        recordId = intent.getStringExtra("recordId");

        binding.relativelayoutDocsMoreRename.setOnClickListener(v -> {
            Intent docsRename = new Intent(DocsMoreActivity.this, DocsNameEditActivity.class);
            docsRename.putExtra("title", title);
            docsRename.putExtra("folderId", title);
            docsRename.putExtra("record", title);
            startActivity(docsRename);
        });

        binding.relativelayoutDocsMoreMove.setOnClickListener(v -> {
            Intent docsMove = new Intent(DocsMoreActivity.this, DocsMoveActivity.class);
            docsMove.putExtra("title", title);
            docsMove.putExtra("folderId", folderId);
            docsMove.putExtra("recordId", recordId);
            startActivity(docsMove);
        });

        setContentView(binding.getRoot());
    }
}
