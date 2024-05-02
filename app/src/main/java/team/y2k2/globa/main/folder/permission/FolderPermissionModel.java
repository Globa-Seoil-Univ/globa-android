package team.y2k2.globa.main.folder.permission;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class FolderPermissionModel {

    private final ArrayList<FolderPermissionItem> items;


    public FolderPermissionModel() {
        items = new ArrayList<>();

        items.add(new FolderPermissionItem("성빈클론", R.drawable.profile_image_4));
        items.add(new FolderPermissionItem("승용클론", R.drawable.profile_image_1));
        items.add(new FolderPermissionItem("영진클론", R.drawable.profile_image_3));
        items.add(new FolderPermissionItem("인태클론", R.drawable.profile_image_2));

    }

    public ArrayList<FolderPermissionItem> getItems() {
        return items;
    }
}
