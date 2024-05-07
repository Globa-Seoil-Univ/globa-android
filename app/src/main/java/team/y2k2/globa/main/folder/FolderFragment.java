package team.y2k2.globa.main.folder;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.FragmentFolderBinding;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyModel;

public class FolderFragment extends Fragment {
    FragmentFolderBinding binding;
    FolderCurrentlyModel currentlyModel;
    FolderModel model;
    private ApiService apiService;


    public FolderFragment() { }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFolderBinding.inflate(getLayoutInflater());
        model = new FolderModel();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        ApiService apiService = retrofit.create(ApiService.class);

        SharedPreferences preferences = inflater.getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<FolderResponse> call = apiService.requestGetFolders("application/json",accessToken, 1, 10);
        call.enqueue(new Callback<FolderResponse>() {
            @Override
            public void onResponse(Call<FolderResponse> call, Response<FolderResponse> response) {
                if (response.isSuccessful()) {
                    FolderResponse folderResponse = response.body();
                    // 성공적으로 응답을 받았을 때 처리
                    Log.d("FOLDERTEST", "성공");

                    for(int i = 0; i < folderResponse.getTotal(); i++) {
                        Folder folder = folderResponse.getFolders().get(i);
                        model.getItems().add(new FolderItem(folder.getTitle(),folder.getCreatedTime()));
                    }

                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d("FOLDERTEST", "오류");

                }
            }

            @Override
            public void onFailure(Call<FolderResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d("FOLDERTEST", "오류" + t.getMessage());

            }
        });



        currentlyModel = new FolderCurrentlyModel();
//        model = new FolderModel();

        FolderCurrentlyAdapter currentlyAdapter = new FolderCurrentlyAdapter(currentlyModel.getItems());

        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.recyclerviewFolderCurrently.setAdapter(currentlyAdapter);
        binding.recyclerviewFolderCurrently.setLayoutManager(layoutManager);

        FolderAdapter adapter = new FolderAdapter(model.getItems());

        binding.recyclerviewFolder.setAdapter(adapter);
        binding.recyclerviewFolder.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        return binding.getRoot();
    }
}
