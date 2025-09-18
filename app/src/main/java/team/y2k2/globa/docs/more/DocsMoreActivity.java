package team.y2k2.globa.docs.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private ActivityResultLauncher<Intent> nameEditLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        docsMoreActivityModel = new ViewModelProvider(this).get(DocsMoreActivityModel.class);
        docsMoreActivityModel.setApiClient(this);

        nameEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // DocsNameEditActivity에서 RESULT_OK 응답을 받았는지 확인
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // "newTitle" 이라는 키로 데이터가 넘어왔는지 확인
                        if (data != null && data.hasExtra("newTitle")) {
                            String newTitle = data.getStringExtra("newTitle");
                            // UI의 텍스트를 새로운 이름으로 업데이트
                            binding.textviewDocsMoreDocsTitle.setText(newTitle);
                            // 내부 변수도 업데이트 (다시 이름 변경을 누를 때를 대비)
                            this.title = newTitle;
                        }
                    }
                }
        );
        initializeUI();
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedTitle", this.title);
        setResult(Activity.RESULT_OK, resultIntent);
        super.finish();
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
            nameEditLauncher.launch(docsRename);
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
        // [수정] folderId와 recordId가 모두 유효한지 확인합니다.
        if (folderId == null || folderId.isEmpty() || recordId == null || recordId.isEmpty()) {
            Toast.makeText(this, "공유할 수 없는 문서입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // [수정] 요청하신 "globa://folders/{folderId}/docs/{docsId}" 형식으로 딥링크를 생성합니다.
        String deepLink = "globa://folders/" + folderId + "/docs/" + recordId;

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