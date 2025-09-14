package team.y2k2.globa.main.profile.info;

import static com.navercorp.nid.NaverIdLoginSDK.applicationContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMyInfoBinding;

public class MyInfoActivity extends AppCompatActivity {
    private ActivityMyInfoBinding binding;
    private MyinfoAdapter myinfoAdapter;
    private MyInfoViewModel myInfoViewModel;

    private String resultingNewName = null;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = "MyInfoActivity";

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d(TAG, "PhotoPicker - 선택된 URI: " + uri);
                    myInfoViewModel.updateProfileImageUri(uri.toString());

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
                            myInfoViewModel.uploadImage(profilePart);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "PhotoPicker - 파일 처리 중 오류", e);
                        Toast.makeText(this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        myInfoViewModel.fetchAndSetUserInfo();
                    }
                } else {
                    Log.d(TAG, "PhotoPicker - 이미지가 선택되지 않음.");
                    Toast.makeText(this, "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> nicknameEditLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d(TAG, "NicknameEditActivity로부터 성공 결과를 받았습니다. 화면은 onResume에서 갱신됩니다.");
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

        Intent intent = getIntent();
        String initialName = intent.getStringExtra("current_name");
        String initialProfileUrl = intent.getStringExtra("current_profile_image_url");
        String initialUserCode = intent.getStringExtra("current_user_code");
        String initialUserId = intent.getStringExtra("userId");

        myInfoViewModel.setInitialData(initialName, initialProfileUrl, initialUserCode, initialUserId);

        binding.setViewModel(myInfoViewModel);
        binding.setLifecycleOwner(this);

        initRecyclerView();
        observeViewModel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 화면이 다시 활성화되어 사용자 정보를 새로고침합니다.");
        myInfoViewModel.fetchAndSetUserInfo();
    }

    private void initRecyclerView() {
        myinfoAdapter = new MyinfoAdapter(new ArrayList<>(), nicknameEditLauncher, this);
        binding.recyclerviewMyInfoItems.setAdapter(myinfoAdapter);
        binding.recyclerviewMyInfoItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void observeViewModel() {
        myInfoViewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish != null && shouldFinish) {
                // ProfileFragment로 결과를 전달하기 위한 로직 (필요시 활성화)
                // Intent resultIntent = new Intent();
                // setResult(Activity.RESULT_OK, resultIntent);
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

        myInfoViewModel.getItemListLiveData().observe(this, items -> {
            if (items != null) {
                Log.d(TAG, "MyInfoActivity: itemListLiveData 변경 감지, 아이템 개수: " + items.size());
                myinfoAdapter.updateItems(items);
            }
        });

        myInfoViewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getUserId() {
        return myInfoViewModel != null ? myInfoViewModel.getUserId() : null;
    }

    private void loadProfileImageUI(String imageUrl) {
        if (binding == null) return;
        Log.d(TAG, "MyInfoActivity - loadProfileImageUI 호출: " + imageUrl);

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.startsWith("http")) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_user)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyInfoPhoto);
            } else if (imageUrl.startsWith("content://")) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_user)
                        .error(R.drawable.profile_user)
                        .into(binding.imageviewMyInfoPhoto);
            }
            else {
                StorageReference imageRef = storage.getReference().child(imageUrl);
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri.toString())
                            .placeholder(R.drawable.profile_user)
                            .error(R.drawable.profile_user)
                            .into(binding.imageviewMyInfoPhoto);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase Storage URL 가져오기 실패", e);
                    Glide.with(this)
                            .load(R.drawable.profile_user)
                            .into(binding.imageviewMyInfoPhoto);
                });
            }
        } else {
            Log.d(TAG, "loadProfileImageUI: imageUrl이 null이거나 비어있어 기본 이미지 로드.");
            Glide.with(this)
                    .load(R.drawable.profile_user)
                    .into(binding.imageviewMyInfoPhoto);
        }
    }

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
