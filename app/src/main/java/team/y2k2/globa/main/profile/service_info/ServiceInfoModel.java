package team.y2k2.globa.main.profile.service_info;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class ServiceInfoModel {
    private final ArrayList<ServiceInfoItem> items;


    public ServiceInfoModel() {
        items = new ArrayList<>();

        items.add(new ServiceInfoItem(R.string.profile_service_info_1_title, R.string.profile_service_info_1_description));
        items.add(new ServiceInfoItem(R.string.profile_service_info_2_title, R.string.profile_service_info_2_description));
        items.add(new ServiceInfoItem(R.string.profile_service_info_3_title, R.string.profile_service_info_3_description));
        items.add(new ServiceInfoItem(R.string.profile_service_info_4_title, R.string.profile_service_info_4_description));
    }

    public ArrayList<ServiceInfoItem> getItems() {
        return items;
    }
}