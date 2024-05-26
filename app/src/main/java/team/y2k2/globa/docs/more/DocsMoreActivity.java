package team.y2k2.globa.docs.more;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoreBinding;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;
import team.y2k2.globa.main.MainActivity;

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

        binding.textviewDocsMoreDocsTitle.setText(title);

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

        BottomSheetDialog deleteBottomSheet = new BottomSheetDialog(this);
        deleteBottomSheet.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = deleteBottomSheet.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = deleteBottomSheet.findViewById(R.id.textview_delete_docs_cancel);

        // 버튼 클릭 리스너를 별도의 메서드로 분리
        confirm.setOnClickListener(d2 -> {
            deleteBottomSheet.dismiss();
            Intent newIntent = new Intent(DocsMoreActivity.this, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newIntent);
            finish();
        });
        cancel.setOnClickListener(d2 -> {
            deleteBottomSheet.dismiss();
        });

        binding.relativelayoutDocsMoreDelete.setOnClickListener(v -> {
            deleteBottomSheet.show();

        });

        setContentView(binding.getRoot());
    }
}
