package team.y2k2.globa.profile;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.alert.AlertActivity;
import team.y2k2.globa.help.HelpActivity;
import team.y2k2.globa.theme.ThemeActivity;
import team.y2k2.globa.tos.ToSActivity;

public class ProfileModel {
    private final ArrayList<SettingItem> items;


    public ProfileModel() {
        items = new ArrayList<>();

        items.add(new SettingItem(R.string.profile_alert_setting, R.drawable.alert, new AlertActivity()));
        items.add(new SettingItem(R.string.profile_help,R.drawable.help,new HelpActivity()));
        items.add(new SettingItem(R.string.profile_clean_data,R.drawable.clean_data));
        items.add(new SettingItem(R.string.profile_tos, R.drawable.tos,  new ToSActivity()));
        items.add(new SettingItem(R.string.profile_theme,R.drawable.theme, new ThemeActivity()));
    }

    public ArrayList<SettingItem> getItems() {
        return items;
    }
}