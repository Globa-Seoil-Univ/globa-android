package team.y2k2.globa.folder.permission.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import team.y2k2.globa.R;

public class FolderPermissionSpinnerAdapter extends ArrayAdapter<String> {

    private List<String> options;

    public FolderPermissionSpinnerAdapter(Context context, List<String> options) {
        super(context, R.layout.item_folder_permission_spinner, options);
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_folder_permission_spinner, parent, false);
        }
        TextView textViewOptionName = convertView.findViewById(R.id.textview_item_folder_permission_spinner);
        textViewOptionName.setText(options.get(position));

        return convertView;
    }
}