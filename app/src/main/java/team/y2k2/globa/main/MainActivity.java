package team.y2k2.globa.main;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationBarView;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMainBinding;
import team.y2k2.globa.docs.upload.DocsUploadActivity;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.permission.FolderPermissionActivity;
import team.y2k2.globa.main.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PICK_RECORD = 101;
    private ActivityMainBinding binding;
    int currentItem = R.id.item_main_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Bundle bundle = new Bundle();
        bundle.putInt("some_int", 0);
        binding.navigationMainBottom.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

            }
        });


        Log.d("IDXitem", binding.navigationMainBottom.getMenu().getItem(2).getTitle().toString());
        binding.navigationMainBottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int index = item.getItemId();

                if (currentItem == item.getItemId())
                    return true;
                else
                    currentItem = item.getItemId();

                if(index == R.id.item_main_main)
                    replaceFragment(MainFragment.class);
                else if(index == R.id.item_main_statistics) {
                    Intent intent = new Intent(getApplicationContext(), FolderPermissionActivity.class);
                    startActivity(intent);
                }
                else if(index == R.id.item_main_upload)
                    uploadAudio();
                else if(index == R.id.item_main_profile)
                    replaceFragment(ProfileFragment.class);
                else if(index == R.id.item_main_folder)
                    replaceFragment(FolderFragment.class);

                binding.navigationMainBottom.setSelectedItemId(item.getItemId());

                return false;
            }
        });
    }
    private void uploadAudio() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(binding.activityMain.getContext());
        bottomSheetDialog.setContentView(R.layout.dialog_upload);
//                    bottomSheetDialog.show();


        // ACTION_GET_CONTENT - 문서나 사진 등의 파일을 선택하고 앱에 그 참조를 반환하기 위해 요청하는 액션
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // 탐색할 파일 MIME 타입 설정
        startActivityForResult(intent, REQUEST_CODE_PICK_RECORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_RECORD && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri audioUri = data.getData();
                String audioPath = getRealPathFromURI(audioUri);
                String audioName = getFileNameFromURI(audioUri);

                //여기서 선택한 파일에 대한 작업을 수행합니다 (예: 업로드, 처리 등)
                Log.d("AudioInfo", "Selected audio\n path: " + audioPath + "\n name: " + audioName);

                Intent intent = new Intent(this, DocsUploadActivity.class);
                intent.putExtra("recordPath", audioPath);
                intent.putExtra("recordName", audioName);
                startActivity(intent);
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath(); // 기본적인 경로 반환
    }

    private String getFileNameFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return uri.getLastPathSegment(); // 기본적인 이름 반환
    }

    private void replaceFragment(Class fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcv_main, fragment, null)
                .commit();
    }
}
