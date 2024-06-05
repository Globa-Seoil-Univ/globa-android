package team.y2k2.globa.main.folder.inside;

import static team.y2k2.globa.api.ApiClient.apiService;
import static team.y2k2.globa.api.ApiService.API_BASE_URL;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;
import team.y2k2.globa.main.folder.permission.FolderPermissionActivity;
import team.y2k2.globa.main.folder.share.FolderShareActivity;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;
    FolderInsideModel model;

    int folderId;
    String folderTitle;

    private final int REQUEST_CODE = 101;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ActivityResultLauncher<Intent> nameEditLauncher;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(getLayoutInflater());
        setBundleParams();
        loadFolderInside();

        binding.textviewFolderInsideTitle.setText(folderTitle);

        setPreferences();

        nameEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if(data != null && data.hasExtra("name")) {
                    String name = data.getStringExtra("name");
                    binding.textviewFolderInsideTitle.setText(name);
                    Toast.makeText(FolderInsideFragment.this.getContext(), "이름 변경 완료", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.imageviewFolderInsideBack.setOnClickListener(v -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fcv_main, FolderFragment.class, null)
                    .commit();

            getFragmentManager().popBackStack();
        });

        binding.textviewFolderInsideMore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
            intent.putExtra("folderTitle", binding.textviewFolderInsideTitle.getText());
            intent.putExtra("folderId", folderId);
            startActivityForResult(intent, REQUEST_CODE);
        });

        return binding.getRoot();
    }


    public void loadFolderInside() {
        ApiClient apiClient = new ApiClient(getContext());
        FolderInsideRecordResponse response = apiClient.requestGetFolderInside(folderId, 1, 10);

        if (response != null) {
            // 받아온 데이터를 처리하는 로직을 작성합니다.
            model = new FolderInsideModel();
            List<FolderInsideRecord> records = response.getRecords();

            for(FolderInsideRecord record : records) {
                // 각 폴더에 대한 처리 작업 수행
                String recordId = record.getRecordId();
                String title = record.getTitle();
                String path = record.getPath();

                model.addItem(Integer.toString(folderId), recordId, title, path);
            }

            FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(model.getItems());

            binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
            binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        }
    }

    public void setBundleParams() {
        Bundle bundle = getArguments();
        folderId = bundle.getInt("folderId");
        folderTitle = bundle.getString("folderTitle");
    }

    public void setPreferences() {
        preferences = getContext().getSharedPreferences("folderId", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("folderId", folderId);
        editor.commit();
    }

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FolderInsideFragment.this.getContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_folder_more, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogTitle = bottomSheetView.findViewById(R.id.textview_more_folder_title);
        dialogTitle.setText(binding.textviewFolderInsideTitle.getText().toString());

        RelativeLayout nameEditButton = bottomSheetView.findViewById(R.id.relativelayout_more_rename);
        RelativeLayout shareButton = bottomSheetView.findViewById(R.id.relativelayout_more_share);
        RelativeLayout authorityButton = bottomSheetView.findViewById(R.id.relativelayout_more_authority);
        RelativeLayout deleteButton = bottomSheetView.findViewById(R.id.relativelayout_more_delete);

        nameEditButton.setOnClickListener(v -> {
            // 이름 변경 클릭
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
            intent.putExtra("name", binding.textviewFolderInsideTitle.getText().toString());
            intent.putExtra("folderId", folderId);
            nameEditLauncher.launch(intent);
        });
        shareButton.setOnClickListener(v -> {
            Intent toShareIntent = new Intent(getActivity(), FolderShareActivity.class);
            toShareIntent.putExtra("folderId", folderId);
            startActivity(toShareIntent);
            bottomSheetDialog.dismiss();
        });
        authorityButton.setOnClickListener(v -> {
            Intent toPermissionIntent = new Intent(getActivity(), FolderPermissionActivity.class);
            toPermissionIntent.putExtra("folderId", folderId);
            startActivity(toPermissionIntent);
            bottomSheetDialog.dismiss();
        });
        deleteButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showSecondBottomSheetDialog();
        });

        bottomSheetDialog.show();
    }

    public void showSecondBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FolderInsideFragment.this.getContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_folder, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView cancelButton = bottomSheetView.findViewById(R.id.textview_delete_folder_cancel);
        TextView confirmButton = bottomSheetView.findViewById(R.id.textview_delete_folder_confirm);

        cancelButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);

            SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
            String accessToken = "Bearer " + preferences.getString("accessToken", "");

            apiService.requestDeleteFolder(folderId, "application/json", accessToken).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Log.d(getClass().getName(), "폴더 삭제 성공");
                    } else {
                        Log.d(getClass().getName(), "폴더 삭제 실패 : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d(getClass().getName(), "request 실패");
                }
            });

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .remove(FolderInsideFragment.this)
                    .commit();

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

}
