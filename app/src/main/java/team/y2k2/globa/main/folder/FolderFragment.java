package team.y2k2.globa.main.folder;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.FragmentFolderBinding;
import team.y2k2.globa.main.folder.add.FolderAddActivity;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyModel;

public class FolderFragment extends Fragment {
    FragmentFolderBinding binding;
    FolderCurrentlyModel currentlyModel;
    FolderModel model;
    private ApiService apiService;

    private final int FOLDER_ADD = 200;


    public FolderFragment() { }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderBinding.inflate(getLayoutInflater());


        binding.imagebuttonFolderAdd.setOnClickListener(v -> {
            Intent intent = new Intent(binding.getRoot().getContext(), FolderAddActivity.class);
            startActivityForResult(intent, FOLDER_ADD);
        });

        loadFolder();


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadFolder();
    }

    public void loadFolder() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        ApiService apiService = retrofit.create(ApiService.class);

        SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<List<FolderResponse>> call = apiService.requestGetFolders("application/json",accessToken, 1, 10);
        call.enqueue(new Callback<List<FolderResponse>>() {
            @Override
            public void onResponse(Call<List<FolderResponse>> call, Response<List<FolderResponse>> response) {
                if (response.isSuccessful()) {
                    List<FolderResponse> folderResponse = response.body();
                    // 성공적으로 응답을 받았을 때 처리
                    Log.d("FOLDERTEST", "성공");

                    if (folderResponse != null) {
                        // 받아온 데이터를 처리하는 로직을 작성합니다.
                        model = new FolderModel();
                        currentlyModel = new FolderCurrentlyModel();


                        for(int i = 0; i < folderResponse.size(); i++) {
                            FolderResponse folder = folderResponse.get(i);
                            model.addItem(folder.getTitle(), folder.getCreatedTime(), folder.getFolderId());
                            currentlyModel.addItem(folder.getTitle(), folder.getCreatedTime(), folder.getFolderId());

                            // 각 폴더에 대한 처리 작업 수행
                            Log.d("FOLDER_TEST", folder.getTitle()+" | " + folder.getCreatedTime() + " | " + folder.getFolderId());
                        }
                        FolderAdapter adapter = new FolderAdapter(model.getItems());
                        FolderCurrentlyAdapter currentlyAdapter = new FolderCurrentlyAdapter(currentlyModel.getItems());

                        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                        binding.recyclerviewFolder.setAdapter(adapter);
                        binding.recyclerviewFolder.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                        binding.recyclerviewFolderCurrently.setAdapter(currentlyAdapter);
                        binding.recyclerviewFolderCurrently.setLayoutManager(layoutManager);
                    }
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d("FOLDERTEST", "오류");

                }
            }

            @Override
            public void onFailure(Call<List<FolderResponse>> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d("FOLDERTEST", "오류" + t.getMessage());

            }
        });
    }
}
