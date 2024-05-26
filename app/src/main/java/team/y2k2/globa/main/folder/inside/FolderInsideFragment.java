package team.y2k2.globa.main.folder.inside;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.databinding.FragmentFolderInsideBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.edit.FolderNameEditActivity;

public class FolderInsideFragment extends Fragment {
    FragmentFolderInsideBinding binding;

    FolderInsideModel model;

    ApiService apiService;

    int folderId;
    String folderTitle;

    private final int REQUEST_CODE = 101;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderInsideBinding.inflate(getLayoutInflater());

        Bundle bundle = getArguments();
        folderId = bundle.getInt("folder_id");
        folderTitle = bundle.getString("folder_title");

        Log.d("FOLDER_INSIDE_ID", String.valueOf(folderId));

        loadFolderInside();

        binding.textviewFolderInsideTitle.setText(folderTitle);
        preferences = getContext().getSharedPreferences("folderid", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("folderid", folderId);
        editor.commit();

        binding.imageviewFolderInsideBack.setOnClickListener(v -> {
            // 뒤로가기 코드 생성
            Log.d("BACKKEY", "뒤로가기");
            getFragmentManager().beginTransaction()
                    .replace(R.id.fcv_main, FolderFragment.class, null)
                    .commit();

            getFragmentManager().popBackStack();
        });


        binding.textviewFolderInsideMore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FolderNameEditActivity.class);
            intent.putExtra("folderName", binding.textviewFolderInsideTitle.getText());
            startActivityForResult(intent, REQUEST_CODE);
        });

        return binding.getRoot();
    }


    public void loadFolderInside() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<FolderInsideRecordResponse> call = apiService.requestGetFolderInside(folderId,"application/json",accessToken, 1, 10);
        call.enqueue(new Callback<FolderInsideRecordResponse>() {
            @Override
            public void onResponse(Call<FolderInsideRecordResponse> call, Response<FolderInsideRecordResponse> response) {
                if (response.isSuccessful()) {
                    FolderInsideRecordResponse folderInsideRecords = response.body();
                    // 성공적으로 응답을 받았을 때 처리
                    Log.d("FOLDERINSIDETEST", "성공" + response);

                    if (folderInsideRecords != null) {
                        // 받아온 데이터를 처리하는 로직을 작성합니다.
                        model = new FolderInsideModel();

                        for(int i = 0; i < folderInsideRecords.getRecords().size(); i++) {
                            // 각 폴더에 대한 처리 작업 수행
                            FolderInsideRecord recordResponse = folderInsideRecords.getRecords().get(i);
                            String recordId = recordResponse.getRecordId();
                            String path = recordResponse.getPath();

                            model.addItem(Integer.toString(folderId), recordId, recordResponse.getTitle(), recordResponse.getPath());

                            Log.d("FOLDERINSIDETEST", recordResponse.getTitle() + " | " + recordResponse.getPath());

                        }

                        FolderInsideDocsAdapter adapter = new FolderInsideDocsAdapter(model.getItems());

                        binding.recyclerviewFolderInsideDocs.setAdapter(adapter);
                        binding.recyclerviewFolderInsideDocs.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                    }
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리

                }
            }

            @Override
            public void onFailure(Call<FolderInsideRecordResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 결과 처리
                if (data != null) {
                    String changedFolderName = data.getStringExtra("changedFolderName");
                    binding.textviewFolderInsideTitle.setText(changedFolderName);
                    // 결과값 사용
                }
            } else if (resultCode == RESULT_CANCELED) {
                // 사용자가 액티비티를 취소한 경우
            }
        }
    }
}
