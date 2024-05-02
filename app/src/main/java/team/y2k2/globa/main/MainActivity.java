package team.y2k2.globa.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMainBinding;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.folder.permission.FolderPermissionActivity;
import team.y2k2.globa.main.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {
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
        startActivity(intent);
    }

    private void replaceFragment(Class fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcv_main, fragment, null)
                .commit();
    }


}
