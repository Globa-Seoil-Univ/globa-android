package team.y2k2.globa.main.folder.inside;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import team.y2k2.globa.R;

public class FolderInsideDocsAdapter extends RecyclerView.Adapter<FolderInsideDocsAdapter.AdapterViewHolder> {
    ArrayList<FolderInsideDocsItem> items;

    public FolderInsideDocsAdapter(ArrayList<FolderInsideDocsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FolderInsideDocsAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new FolderInsideDocsAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderInsideDocsAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView datetime;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_document_title);
            datetime = itemView.findViewById(R.id.textview_document_datetime);
        }
    }
}