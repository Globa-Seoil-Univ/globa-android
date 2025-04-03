package team.y2k2.globa.keyword.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;

public class KeywordDetailAdapter extends RecyclerView.Adapter<KeywordDetailAdapter.KeywordDetailViewHolder> {
    private final Context context;
    private List<KeywordDetailItem> items = new ArrayList<>();

    public KeywordDetailAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<KeywordDetailItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KeywordDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword_detail, parent, false);
        return new KeywordDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordDetailViewHolder holder, int position) {
        KeywordDetailItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class KeywordDetailViewHolder extends RecyclerView.ViewHolder {
        private final TextView numberTextView;
        private final TextView wordTextView;
        private final TextView tagTextView;
        private final TextView descriptionTextView;

        public KeywordDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.textview_keyword_detail_number);
            wordTextView = itemView.findViewById(R.id.textview_keyword_detail_item_word);
            tagTextView = itemView.findViewById(R.id.textview_keyword_detail_tag);
            descriptionTextView = itemView.findViewById(R.id.textview_keyword_detail_description);
        }

        public void bind(KeywordDetailItem item) {
            numberTextView.setText((getAdapterPosition() + 1) + ".");
            wordTextView.setText(item.getKeyword());
            tagTextView.setText(item.getTag());
            descriptionTextView.setText(item.getDescription());
        }
    }
}