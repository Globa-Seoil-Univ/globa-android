package team.y2k2.globa.main.docs.keyword;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class DocsKeywordAdapter extends RecyclerView.Adapter<DocsKeywordAdapter.AdapterViewHolder> {
    ArrayList<DocsKeywordItem> items;

    public DocsKeywordAdapter(ArrayList<DocsKeywordItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DocsKeywordAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new DocsKeywordAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsKeywordAdapter.AdapterViewHolder holder, int position) {
        holder.keyword.setText(items.get(position).getKeyword());
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView keyword;
//        ConstraintLayout layout;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            keyword = itemView.findViewById(R.id.textview_keyword);
        }
    }
}
