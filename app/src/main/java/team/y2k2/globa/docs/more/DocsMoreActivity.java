package team.y2k2.globa.docs.more;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoreBinding;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;
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

        binding.relativelayoutDocsMoreDelete.setOnClickListener(v -> {
            showDialog();
        });


        // 문서 삭제시 액티비티 종료
        docsMoreViewModel.closeActivities.observe(this, shouldClose -> {
            if (shouldClose != null && shouldClose) {
                finish();
            }
        });

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
            docsMoreViewModel.deleteDocs(folderId, recordId);
            dialog.cancel();
        });



        dialog.show();
    }

}
