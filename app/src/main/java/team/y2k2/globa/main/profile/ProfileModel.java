package team.y2k2.globa.main.profile;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.main.profile.alert.AlertActivity;
import team.y2k2.globa.main.profile.help.HelpActivity;
import team.y2k2.globa.main.profile.service_info.ServiceInfoActivity;
import team.y2k2.globa.main.profile.theme.ThemeActivity;

public class ProfileModel {
    private final ArrayList<SettingItem> items;
    public ProfileModel() {
        items = new ArrayList<>();

        items.add(new SettingItem(R.string.profile_alert_setting, R.drawable.alert, new AlertActivity()));
        items.add(new SettingItem(R.string.profile_help,R.drawable.help,new HelpActivity()));
        items.add(new SettingItem(R.string.profile_clean_data,R.drawable.clean_data));
        items.add(new SettingItem(R.string.profile_service_info, R.drawable.service_info,  new ServiceInfoActivity()));
        items.add(new SettingItem(R.string.profile_theme,R.drawable.theme, new ThemeActivity()));
    }

    public ArrayList<SettingItem> getItems() {
        return items;
    }
}