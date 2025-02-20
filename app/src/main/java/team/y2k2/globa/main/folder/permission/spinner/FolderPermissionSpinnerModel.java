package team.y2k2.globa.main.folder.permission.spinner;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import team.y2k2.globa.R;

public class FolderPermissionSpinnerModel {

    public String READ = "읽기";
    public String EDIT = "수정";

    public List<String> getOptions(Context context) {
        READ = context.getString(R.string.read);
        EDIT = context.getString(R.string.modify);
        return Arrays.asList(READ,EDIT);
    }

}
