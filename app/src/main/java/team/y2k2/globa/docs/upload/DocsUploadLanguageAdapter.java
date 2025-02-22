package team.y2k2.globa.docs.upload;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class DocsUploadLanguageAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
    ArrayList<String> items;
    LayoutInflater inflater;
    int dropResource;
    int topResources;

    public DocsUploadLanguageAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.topResources = resource;
        this.items = new ArrayList<>();
        this.items.add("한국어");
        this.items.add("English");
        this.items.add("日本語");

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return items.get(position).toString();
    }

    public ArrayList<String> getItems() {
        return items;
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
        TextView title = view.findViewById(R.id.textview_language_item_title);
        title.setText(items.get(position));

        return view;
    }

    private View createTopView(int position, View convertView, ViewGroup parents) {
        final View view = inflater.inflate(topResources, parents, false);
        TextView title = view.findViewById(R.id.textview_language_item_title);
        title.setText(items.get(position));

        return view;
    }
}
