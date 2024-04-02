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
import team.y2k2.globa.folder.FolderFragment;
import team.y2k2.globa.folder.inside.FolderInsideFragment;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;
import team.y2k2.globa.profile.ProfileFragment;

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
                if (currentItem == item.getItemId())
                    return true;
                else
                    currentItem = item.getItemId();


                if(item.getItemId() == R.id.item_main_main) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fcv_main, MainFragment.class, bundle)
                            .commit();

                }

                else if(item.getItemId() == R.id.item_main_statistics) {
                    Intent intent = new Intent(getApplicationContext(), KeywordDetailActivity.class);
                    startActivity(intent);
                }

                else if(item.getItemId() == R.id.item_main_upload) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(binding.activityMain.getContext());
                    bottomSheetDialog.setContentView(R.layout.dialog_upload);
                    bottomSheetDialog.show();
                }

                else if(item.getItemId() == R.id.item_main_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fcv_main, ProfileFragment.class, bundle)
                            .commit();
                }
                else if(item.getItemId() == R.id.item_main_folder) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fcv_main, FolderFragment.class, bundle)
                            .commit();
                }
                else {

                }
                binding.navigationMainBottom.setSelectedItemId(item.getItemId());

                return false;
            }
        });


    }


}
