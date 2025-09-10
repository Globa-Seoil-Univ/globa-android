package team.y2k2.globa.main.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;

public class SearchDocsAdapter extends RecyclerView.Adapter<SearchDocsAdapter.ViewHolder> {

    private List<SearchDocsItem> items;
    private final Consumer<SearchDocsItem> onDeleteClickListener;

    public SearchDocsAdapter(List<SearchDocsItem> items, Consumer<SearchDocsItem> onDeleteClickListener) {
        this.items = items;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchDocsItem currentItem = items.get(position);
        holder.bind(currentItem, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<SearchDocsItem> newItems) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SearchDiffCallback(this.items, newItems));
        this.items = newItems;
        result.dispatchUpdatesTo(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView title;
        private final TextView datetime;
        private final ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraintlayout_document_item);
            title = itemView.findViewById(R.id.textview_document_title);
            datetime = itemView.findViewById(R.id.textview_document_datetime);
            deleteButton = itemView.findViewById(R.id.imagebutton_delete_history);
        }

        public void bind(SearchDocsItem item, Consumer<SearchDocsItem> onDeleteClickListener) {
            title.setText(item.getTitle());
            datetime.setText(item.getDatetime());

            layout.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DocsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("folderId", item.getFolderId());
                intent.putExtra("recordId", item.getRecordId());
                v.getContext().startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> {
                if(onDeleteClickListener != null) {
                    onDeleteClickListener.accept(item);
                }
            });
        }
    }

    private static class SearchDiffCallback extends DiffUtil.Callback {
        private final List<SearchDocsItem> oldList;
        private final List<SearchDocsItem> newList;

        public SearchDiffCallback(List<SearchDocsItem> oldList, List<SearchDocsItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getRecordId().equals(newList.get(newItemPosition).getRecordId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}