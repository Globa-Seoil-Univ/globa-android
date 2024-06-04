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
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.databinding.FragmentFolderBinding;
import team.y2k2.globa.main.folder.add.FolderAddActivity;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyModel;

public class FolderFragment extends Fragment {
    FragmentFolderBinding binding;
    FolderCurrentlyModel currentlyModel;
    FolderModel model;
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
        ApiClient apiClient = new ApiClient(getContext());

        List<FolderResponse> response = apiClient.requestGetFolders(1, 10);

        model = new FolderModel();
        currentlyModel = new FolderCurrentlyModel();

        for(int i = 0; i < response.size(); i++) {
            FolderResponse folder = response.get(i);
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

    /*
    public void deleteFolder() {
        ApiService apiService = ApiClient.getApiService();

        String folderId = "";

        SharedPreferences preferences = getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");

        apiService.requestDeleteFolder(folderId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    // 삭제 후 갱신 작업 처리
                } else {
                    Log.d(getClass().getName(), "삭제 실패");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getName(), "삭제 요청 실패");
            }
        });
    }
     */

}
