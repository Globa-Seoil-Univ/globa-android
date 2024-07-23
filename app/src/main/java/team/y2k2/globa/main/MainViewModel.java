package team.y2k2.globa.main;

import static team.y2k2.globa.main.MainModel.REQUEST_CODE_PICK_RECORD;
import static team.y2k2.globa.main.MainModel.REQUEST_CODE_UPLOAD_RECORD;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.upload.DocsUploadActivity;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.main.MainFragment;
import team.y2k2.globa.main.profile.ProfileFragment;
import team.y2k2.globa.main.statistics.StatisticsFragment;

public class MainViewModel extends ViewModel {
    MainActivity activity;
    MainModel model;
    MainFragment mainFragment;
    int currentItem = R.id.item_main_main;


    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public MainViewModel() {
        mainFragment = new MainFragment();
    }

    public boolean viewFragment(int index) {
        if (currentItem == index)
            return true;

        if(index == R.id.item_main_main)
            replaceFragment(mainFragment);
        else if(index == R.id.item_main_statistics)
            replaceFragment(new StatisticsFragment());
        else if(index == R.id.item_main_upload)
            uploadAudio();
        else if(index == R.id.item_main_profile)
            replaceFragment(new ProfileFragment());
        else if(index == R.id.item_main_folder)
            replaceFragment(new FolderFragment());

        return true;
    }

    private void uploadAudio() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mainFragment.getContext());
        bottomSheetDialog.setContentView(R.layout.dialog_upload);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // 탐색할 파일 MIME 타입 설정
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_RECORD);
    }

    public void uploadRecord(Intent data) {
        if (data != null && data.getData() != null) {
            Uri audioUri = data.getData();
            String audioPath = getRealPathFromURI(audioUri);
            String audioName = getFileNameFromURI(audioUri);

            Intent intent = new Intent(activity, DocsUploadActivity.class);
            intent.putExtra("recordPath", audioPath);
            intent.putExtra("recordName", audioName);
            activity.startActivityForResult(intent, REQUEST_CODE_UPLOAD_RECORD);
        }
    }

    private void replaceFragment(Fragment fragment) {
        mainFragment.getFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcv_main, fragment, null)
                .commit();
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
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
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return uri.getLastPathSegment(); // 기본적인 이름 반환
    }
}
