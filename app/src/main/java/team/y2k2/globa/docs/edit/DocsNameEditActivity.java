package team.y2k2.globa.docs.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.databinding.ActivityDocsNameEditBinding;

public class DocsNameEditActivity extends AppCompatActivity {

    private ActivityDocsNameEditBinding binding;
    private DocsNameEditViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터 바인딩 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_docs_name_edit);

        // Intent에서 데이터 가져오기 및 유효성 검사
        Intent intent = getIntent();
        String recordId;
        String folderId;
        String initialTitle;
        if (intent != null) {
            recordId = intent.getStringExtra("recordId");
            initialTitle = intent.getStringExtra("title");
            folderId = intent.getStringExtra("folderId");
        } else {
            showErrorAndFinish("Intent is null.");
            return;
        }

        if (recordId == null || recordId.isEmpty() ||
                folderId == null || folderId.isEmpty() ||
                initialTitle == null) {
            showErrorAndFinish("필수 데이터가 누락되었습니다."); // 문자열 리소스 사용 권장
            return;
        }

        RecordApiClient apiClient = new RecordApiClient();
        DocsNameEditViewModelFactory factory = new DocsNameEditViewModelFactory(
                initialTitle, recordId, folderId, apiClient
        );
        viewModel = new ViewModelProvider(this, factory).get(DocsNameEditViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.edittextDocsNameInputName.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(32) // MAX_TITLE_LENGTH
        });

        setupObservers();
    }

    /** LiveData 관찰자 설정 */
    private void setupObservers() {
        // 변경 버튼 활성화 상태 관찰
        viewModel.isConfirmEnabled.observe(this, isEnabled -> {
            if (isEnabled != null) {
                binding.textviewDocsNameChangeConfirm.setEnabled(isEnabled);
                updateConfirmButtonAppearance();
            }
        });

        // 내용 변경 여부 관찰
        viewModel.hasTextChanged.observe(this, hasChanged -> {
            if (hasChanged != null) {
                updateConfirmButtonAppearance();
            }
        });

        // 취소 버튼 표시 여부 관찰
        viewModel.isCancelVisible.observe(this, isVisible -> {
            if (isVisible != null) {
                binding.buttonDocsNameCancel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });

        // 뒤로가기 이벤트 관찰
        viewModel.navigateBackEvent.observe(this, event -> {
            if (event != null) {
                finish();
            }
        });

        // 토스트 메시지 이벤트 관찰
        viewModel.showToastEvent.observe(this, event -> {
            if (event != null) {
                String message = event.getContentIfNotHandled();
                if (message != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** 변경 버튼 (TextView)의 외형 (활성화 상태 및 글자 색상) 업데이트 */
    private void updateConfirmButtonAppearance() {
        boolean isEnabled = viewModel.isConfirmEnabled.getValue() != null && viewModel.isConfirmEnabled.getValue();
        boolean hasChanged = viewModel.hasTextChanged.getValue() != null && viewModel.hasTextChanged.getValue();

        int colorResId;
        if (isEnabled && hasChanged) {
            colorResId = R.color.primary;
        } else {
            colorResId = R.color.gray;
        }

        binding.textviewDocsNameChangeConfirm.setTextColor(ContextCompat.getColor(this, colorResId));
    }

    /** 오류 메시지 표시 후 액티비티 종료 */
    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}