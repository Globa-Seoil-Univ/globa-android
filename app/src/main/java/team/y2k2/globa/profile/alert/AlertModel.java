package team.y2k2.globa.profile.alert;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.profile.alert.AlertItem;

public class AlertModel {
    private final ArrayList<AlertItem> items;


    public AlertModel() {
        items = new ArrayList<>();

        items.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description));
        items.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description));
        items.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description));
    }

    public ArrayList<AlertItem> getItems() {
        return items;
    }
}