package team.y2k2.globa.docs.upload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsUploadFolderAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
    List<FolderResponse> items;
    LayoutInflater inflater;
    int dropResource;
    int topResources;

    public DocsUploadFolderAdapter(@NonNull Context context, int resource, @Nullable List<FolderResponse> folders) {
        super(context, resource);
        this.topResources = resource;
        this.items = folders;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createTopView(position, convertView, parent);
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        this.dropResource = resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createDropView(position, convertView, parent);
    }

    private View createDropView(int position, View convertView, ViewGroup parents) {
        final View view = inflater.inflate(dropResource, parents, false);

        TextView title = view.findViewById(R.id.textview_folder_item_title);
        TextView datetime = view.findViewById(R.id.textview_folder_item_datetime);

        title.setText(items.get(position).getTitle());
        datetime.setText(items.get(position).getCreatedTime());

        return view;
    }

    private View createTopView(int position, View convertView, ViewGroup parents) {
        final View view = inflater.inflate(topResources, parents, false);
        TextView title = view.findViewById(R.id.textview_folder_item_title);
        TextView datetime = view.findViewById(R.id.textview_folder_item_datetime);


        title.setText(items.get(position).getTitle());
        datetime.setText(items.get(position).getCreatedTime());


        return view;
    }
}
