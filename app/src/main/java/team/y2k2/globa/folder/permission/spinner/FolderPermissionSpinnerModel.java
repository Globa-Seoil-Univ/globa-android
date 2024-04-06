package team.y2k2.globa.folder.permission.spinner;

import java.util.Arrays;
import java.util.List;

public class FolderPermissionSpinnerModel {

    public final String READ = "읽기";
    public final String EDIT = "수정";


    public List<String> getOptions() {
        return Arrays.asList(READ,EDIT);
    }

}
