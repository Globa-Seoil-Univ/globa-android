package team.y2k2.globa.docs.more;

import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoreBinding;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.quiz.quizconduct.QuizActivity;
import team.y2k2.globa.docs.statistics.DocsStatisticsActivity;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;
    String title;
    String folderId;
    String recordId;
    String folderTitle;
    DocsMoreViewModel docsMoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        docsMoreViewModel = new ViewModelProvider(this).get(DocsMoreViewModel.class);

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
            showBottomSheetDialog();
        });


        // 문서 삭제시 액티비티 종료
//        docsMoreViewModel.closeActivities.observe(this, shouldClose -> {
//            if (shouldClose != null && shouldClose) {
//                finish();
//            }
//        });

        binding.relativelayoutDocsMoreStatistics.setOnClickListener(v -> {
            Intent toStatisticsIntent = new Intent(DocsMoreActivity.this, DocsStatisticsActivity.class);
            toStatisticsIntent.putExtra("folderId", folderId);
            toStatisticsIntent.putExtra("recordId", recordId);
            startActivity(toStatisticsIntent);
        });

        binding.relativelayoutDocsMoreQuiz.setOnClickListener(v -> {
            Intent toQuizIntent = new Intent(DocsMoreActivity.this, QuizActivity.class);
            toQuizIntent.putExtra("folderId", folderId);
            toQuizIntent.putExtra("recordId", recordId);
            startActivity(toQuizIntent);
        });
    }

    protected void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_docs, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView cancelButton = dialogView.findViewById(R.id.textview_delete_docs_cancel);
        TextView confirmButton = dialogView.findViewById(R.id.textview_delete_docs_confirm);

        cancelButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // 삭제 처리
            docsMoreViewModel.deleteDocs(folderId, recordId);
            finish();
        });

        bottomSheetDialog.show();

    }

}
