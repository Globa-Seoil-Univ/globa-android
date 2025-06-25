package team.y2k2.globa.main.profile.info;

import static com.navercorp.nid.NaverIdLoginSDK.applicationContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // TextUtils 추가
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList; // ArrayList 추가
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyInfoBinding;
import team.y2k2.globa.main.ProfileImage;

public class MyInfoActivity extends AppCompatActivity {
    private ActivityMyInfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyInfoViewModel myInfoViewModel;

    private String resultingNewName = null;
    // resultingNewProfileUrl은 ViewModel의 LiveData를 통해 관리되므로 Activity 멤버 변수 불필요

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = "MyInfoActivity";

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d(TAG, "PhotoPicker - 선택된 URI: " + uri);
                    myInfoViewModel.updateProfileImageUri(uri.toString()); // ViewModel에 URI 전달 (UI 미리보기용)

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        byte[] fileBytes = null;
                        if (inputStream != null) {
                            fileBytes = IOUtils.toByteArray(inputStream);
                            inputStream.close();
                        }

                        if (fileBytes != null) {
                            String mimeType = getContentResolver().getType(uri);
                            RequestBody requestBody = RequestBody.create(fileBytes, MediaType.parse(mimeType != null ? mimeType : "image/*"));
                            MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", "profile_image.jpg", requestBody);
                            myInfoViewModel.uploadImage(profilePart, myInfoViewModel.getUserId()); // ViewModel 통해 업로드
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "PhotoPicker - 파일 처리 중 오류", e);
                        Toast.makeText(this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        // 오류 시 ViewModel의 LiveData를 통해 이전 서버 값으로 복원 시도
                        myInfoViewModel.fetchAndSetUserInfo(); // 또는 특정 메서드로 이전 이미지 값 복원
                    }
                } else {
                    Log.d(TAG, "PhotoPicker - 이미지가 선택되지 않음.");
                    Toast.makeText(this, "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> nicknameEditLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("updated_name")) {
                        String updatedName = data.getStringExtra("updated_name");
                        Log.d(TAG, "NicknameEditActivity 결과 수신: " + updatedName);

                        resultingNewName = updatedName; // ProfileFragment로 전달할 값 업데이트
                        myInfoViewModel.updateNameFromEdit(updatedName); // ViewModel 업데이트 -> LiveData가 itemListLiveData를 갱신 -> Observer가 어댑터 갱신
                    }
                } else {
                    Log.d(TAG, "NicknameEditActivity 결과가 OK가 아님 또는 데이터 없음.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        myInfoViewModel.initialize(getApplicationContext());

        binding.setViewModel(myInfoViewModel);
        binding.setLifecycleOwner(this);

        initRecyclerView();
        observeViewModel();

        myInfoViewModel.fetchAndSetUserInfo();
    }

    private void initRecyclerView() {
        // 어댑터는 초기에 빈 리스트로 설정하고, LiveData를 통해 업데이트 받음
        myinfoAdapter = new MyinfoAdapter(new ArrayList<>(), nicknameEditLauncher, this);
        binding.recyclerviewMyInfoItems.setAdapter(myinfoAdapter);
        binding.recyclerviewMyInfoItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void observeViewModel() {
        myInfoViewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish != null && shouldFinish) {
                Intent resultIntent = new Intent();
                if (resultingNewName != null) {
                    resultIntent.putExtra("newName", resultingNewName);
                }
                String finalProfileUrl = myInfoViewModel.getProfileImageLiveData().getValue();
                if (finalProfileUrl != null) {
                    resultIntent.putExtra("newProfile", finalProfileUrl);
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        myInfoViewModel.getPickImage().observe(this, shouldPick -> {
            if (shouldPick != null && shouldPick) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
                myInfoViewModel.resetPickImage();
            }
        });

        myInfoViewModel.getProfileImageLiveData().observe(this, imageUrl -> {
            Log.d(TAG, "MyInfoActivity: 프로필 이미지 LiveData 변경 감지 -> " + imageUrl);
            loadProfileImageUI(imageUrl);
        });

        // *** itemListLiveData 관찰 ***
        myInfoViewModel.getItemListLiveData().observe(this, items -> {
            if (items != null) {
                Log.d(TAG, "MyInfoActivity: itemListLiveData 변경 감지, 아이템 개수: " + items.size());
                myinfoAdapter.updateItems(items); // 어댑터에 새 리스트 전달 및 UI 갱신
            }
        });

        myInfoViewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                // myInfoViewModel.doneShowError(); // ViewModel에 추가하여 Toast 중복 방지
            }
        });
    }

    public String getUserId() {
        // ViewModel이 초기화되지 않았을 수 있으므로 null 체크
        return myInfoViewModel != null ? myInfoViewModel.getUserId() : null;
    }

    private void loadProfileImageUI(String imageUrl) {
        if (binding == null) return;
        Log.d(TAG, "MyInfoActivity - loadProfileImageUI 호출: " + imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.startsWith("http")) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewMyInfoPhoto);
            } else if (imageUrl.startsWith("content://")) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewMyInfoPhoto);
            }
            else {
                try {
                    StorageReference imageRef = storage.getReference().child(imageUrl);
                    Glide.with(this).load(ProfileImage.convertGsToHttps(imageRef.toString())).placeholder(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewMyInfoPhoto);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "loadProfileImageUI: 잘못된 Firebase Storage 경로: " + imageUrl, e);
                    Glide.with(this).load(R.drawable.profile_user).into(binding.imageviewMyInfoPhoto);
                }
            }
        } else {
            Log.d(TAG, "loadProfileImageUI: imageUrl이 null이거나 비어있어 기본 이미지 로드.");
            Glide.with(this).load(R.drawable.profile_user).into(binding.imageviewMyInfoPhoto);
        }
    }

    // 이 메서드는 이제 ViewModel에서 itemListLiveData를 통해 itemList가 관리되므로 직접적인 호출은 불필요.
    // 다만, nicknameEditLauncher에서 특정 아이템만 갱신하기 위해 위치를 찾을 때는 필요할 수 있음.
    // 그러나 LiveData를 사용하면 전체 리스트가 갱신되므로, 이 메소드의 필요성은 줄어듦.
    // 만약 notifyItemChanged를 계속 사용하고 싶다면, ViewModel의 itemListLiveData.getValue()를 사용해야 함.
    private int findNameItemPosition() {
        if (applicationContext == null || myInfoViewModel == null || myInfoViewModel.getItemListLiveData().getValue() == null) {
            Log.w(TAG, "findNameItemPosition: Context, ViewModel, 또는 itemListLiveData의 값이 null입니다.");
            return -1;
        }
        String nameTitle = applicationContext.getString(R.string.name);
        List<MyInfoItem> items = myInfoViewModel.getItemListLiveData().getValue();
        if (items == null) return -1;

        for (int i = 0; i < items.size(); i++) {
            MyInfoItem currentItem = items.get(i);
            if (currentItem != null && nameTitle.equals(currentItem.getTitle())) {
                return i;
            }
        }
        Log.w(TAG, "findNameItemPosition: itemList에서 '" + nameTitle + "' 항목을 찾지 못했습니다.");
        return -1;
    }
}