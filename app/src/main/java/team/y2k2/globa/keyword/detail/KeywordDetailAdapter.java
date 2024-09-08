package team.y2k2.globa.keyword.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import team.y2k2.globa.R;

public class KeywordDetailAdapter extends RecyclerView.Adapter<KeywordDetailAdapter.AdapterViewHolder> {

    KeywordDetailActivity activity;
    ArrayList<KeywordDetailItem> items;

    public KeywordDetailAdapter(ArrayList<KeywordDetailItem> items, KeywordDetailActivity activity) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public KeywordDetailAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword_detail, parent, false);
        return new KeywordDetailAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordDetailAdapter.AdapterViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position + 1));
        holder.tag.setText(items.get(position).getTag());
        holder.description.setText(items.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView tag;
        TextView description;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            index = itemView.findViewById(R.id.textview_keyword_detail_number);
            tag = itemView.findViewById(R.id.textview_keyword_detail_tag);
            description = itemView.findViewById(R.id.textview_keyword_detail_description);
        }
    }
}