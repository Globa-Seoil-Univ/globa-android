package team.y2k2.globa.docs.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoreBinding;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.quiz.conduct.QuizActivity;
import team.y2k2.globa.docs.statistics.DocsStatisticsActivity;
import team.y2k2.globa.main.MainActivity;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;
    String title;
    String folderId;
    String recordId;
    String folderTitle;
    DocsMoreActivityModel docsMoreActivityModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        docsMoreActivityModel = new ViewModelProvider(this).get(DocsMoreActivityModel.class);
        docsMoreActivityModel.setApiClient(this);

        initializeUI();
    }

    private void initializeUI() {
        // 뒤로 가기 버튼
        binding.imageButtonDocsMoreBack.setOnClickListener(v -> finish());

        // 문서 제목, 폴더 ID, 문서 ID, 폴더 제목
        title = getIntent().getStringExtra("title");
        folderId = getIntent().getStringExtra("folderId");
        recordId = getIntent().getStringExtra("recordId");
        folderTitle = getIntent().getStringExtra("folderTitle");

        // 기본 제목 설정
        binding.textviewDocsMoreFolderTitle.setText(folderTitle);
        binding.textviewDocsMoreDocsTitle.setText(title);

        // 이름 변경 버튼
        binding.relativelayoutDocsMoreRename.setOnClickListener(v -> {
            Intent docsRename = new Intent(DocsMoreActivity.this, DocsNameEditActivity.class);
            docsRename.putExtra("title", title);
            docsRename.putExtra("folderId", folderId);
            docsRename.putExtra("recordId", recordId);
            startActivity(docsRename);
        });

        // 문서 삭제 버튼
        binding.relativelayoutDocsMoreDelete.setOnClickListener(v -> showBottomSheetDialog());

        // 시각화 자료 보기 버튼
        binding.relativelayoutDocsMoreStatistics.setOnClickListener(v -> {
            Intent toStatisticsIntent = new Intent(DocsMoreActivity.this, DocsStatisticsActivity.class);
            toStatisticsIntent.putExtra("folderId", folderId);
            toStatisticsIntent.putExtra("recordId", recordId);
            startActivity(toStatisticsIntent);
        });

        // 퀴즈 풀기 버튼
        binding.relativelayoutDocsMoreQuiz.setOnClickListener(v -> {
            Intent toQuizIntent = new Intent(DocsMoreActivity.this, QuizActivity.class);
            toQuizIntent.putExtra("folderId", folderId);
            toQuizIntent.putExtra("recordId", recordId);
            startActivity(toQuizIntent);
        });

        binding.relativelayoutDocsMoreShare.setOnClickListener(v -> shareLink());
    }

    private void shareLink() {
        if (recordId == null || recordId.isEmpty()) {
            Toast.makeText(this, "공유할 수 없는 문서입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String deepLink = "globa://docs/" + recordId;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, deepLink);

        startActivity(Intent.createChooser(shareIntent, "링크 공유"));
    }


    protected void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_docs, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView cancelButton = dialogView.findViewById(R.id.textview_delete_docs_cancel);
        TextView confirmButton = dialogView.findViewById(R.id.textview_delete_docs_confirm);

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            docsMoreActivityModel.deleteDocs(folderId, recordId);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        bottomSheetDialog.show();
    }
}
