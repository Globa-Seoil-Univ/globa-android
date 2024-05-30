package team.y2k2.globa.docs.more;

import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoreBinding;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;
    String title;
    String folderId;
    String recordId;
    String folderTitle;
//    DocsMoreViewModel docsMoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        docsMoreViewModel = new ViewModelProvider(this).get(DocsMoreViewModel.class);

        binding.imagebuttonDocsMoreBack.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");
        recordId = intent.getStringExtra("recordId");
        folderTitle = intent.getStringExtra("folderTitle");

        binding.textviewDocsMoreFolderTitle.setText(folderTitle);
        binding.textviewDocsMoreDocsTitle.setText(title);

        binding.relativelayoutDocsMoreRename.setOnClickListener(v -> {
            Intent docsRename = new Intent(DocsMoreActivity.this, DocsNameEditActivity.class);
            docsRename.putExtra("title", title);
            docsRename.putExtra("folderId", title);
            docsRename.putExtra("record", title);
            startActivity(docsRename);
        });

//        binding.relativelayoutDocsMoreMove.setOnClickListener(v -> {
//            Intent docsMove = new Intent(DocsMoreActivity.this, DocsMoveActivity.class);
//            docsMove.putExtra("title", title);
//            docsMove.putExtra("folderId", folderId);
//            docsMove.putExtra("recordId", recordId);
//            startActivity(docsMove);
//        });
//
//        BottomSheetDialog deleteBottomSheet = new BottomSheetDialog(this);
//        deleteBottomSheet.setContentView(R.layout.dialog_delete_docs);
//
//        TextView confirm = deleteBottomSheet.findViewById(R.id.textview_delete_docs_confirm);
//        TextView cancel = deleteBottomSheet.findViewById(R.id.textview_delete_docs_cancel);
//
//        // 버튼 클릭 리스너를 별도의 메서드로 분리
//        confirm.setOnClickListener(d2 -> {
//            deleteBottomSheet.dismiss();
//            Intent newIntent = new Intent(DocsMoreActivity.this, MainActivity.class);
//            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(newIntent);
//            finish();
//        });
//        cancel.setOnClickListener(d2 -> {
//            deleteBottomSheet.dismiss();
//        });


        binding.relativelayoutDocsMoreDelete.setOnClickListener(v -> {
            showDialog();
        });


//        // 문서 삭제시 액티비티 종료
//        docsMoreViewModel.closeActivities.observe(this, shouldClose -> {
//            if (shouldClose != null && shouldClose) {
//                finish();
//            }
//        });
    }

    public void showDialog() {
        Dialog dialog = new Dialog(DocsMoreActivity.this);
        dialog.setContentView(R.layout.dialog_delete_docs);
        Button btn_cancel = dialog.findViewById(R.id.textview_delete_docs_cancel);
        Button btn_delete = dialog.findViewById(R.id.textview_delete_docs_confirm);
        btn_cancel.setOnClickListener(v -> {
            dialog.cancel();
        });
        btn_delete.setOnClickListener(v -> {
            String folderId = "";
            String recordId = "";
//            docsMoreViewModel.deleteDocs(folderId, recordId);
            dialog.cancel();
        });

        dialog.show();
    }

}
